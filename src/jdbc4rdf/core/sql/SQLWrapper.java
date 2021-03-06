package jdbc4rdf.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import jdbc4rdf.core.config.Config;



public class SQLWrapper<T extends Config> {


	protected final T conf;
	
	public Connection conn;
	
	protected final boolean AUTOCOMMIT = false;
	
	
	final static Logger logger = Logger.getLogger(SQLWrapper.class);
	
	/**
	 * Initialize the SQL wrapper class
	 * @param conf Connection configuration container
	 */
	public SQLWrapper(T confIn) {
		
		// store configuration
		this.conf = confIn;
		
		// Try to load the driver class
		try {
			Class.forName(conf.getDriver().getDriverClass());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			conn = init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}


	protected ResultSet runQuery(Statement stmt, String query) throws SQLException {
		
		boolean hasResult = stmt.execute(query);
		
		if (hasResult) {
			return stmt.getResultSet();
		} else {
			return null;
		}
	}
	
	
	protected Connection init() throws SQLException {

		final String host = conf.getHost();
		final String db = conf.getDbName();
		final String dbuser = conf.getUser();
		final String dbpw = conf.getPw();
		
		String connectionUri = conf.getDriver().getJDBCUri(host, db, conf.getUriSuffix());
		
		logger.info("Connecting with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		logger.info("Connection URI:\n" + connectionUri);
		
		
		Connection conn = null;
		try {
			// jdbc:hive2://localhost:10000/default", "hive", ""
			//conn = DriverManager.getConnection("jdbc:hive2://" + host + ":" + PORT + "/" + db, dbuser, dbpw);
			conn = DriverManager.getConnection(connectionUri, dbuser, dbpw);
			
			conn.setAutoCommit(AUTOCOMMIT);
			
		} catch (SQLException sqle) {
			logger.error("Unable to create connection", sqle);
		}


		return conn;
	}


	@Deprecated
	protected void rollback(Connection conn) {
		
		// rollback is not possible for autocommit=true
		if (AUTOCOMMIT) return;
		
		try {
			if (conn != null) {
				if (!conn.isClosed()) {
					logger.debug("Calling rollback");
					conn.rollback();
				} else {
					logger.debug("Connection already closed");
				}

			} else {
				logger.debug("Can't rollback connection, because Connection object is null");
			}
		} catch (Exception e) {
			logger.error("Unable to rollback connection", e);
		}

	}


	protected void close(AutoCloseable ac) {
		try {
			if (ac != null) {
				// also commit here?
				ac.close();
			} else {
				logger.warn("close() "
						+ "AutoClosable \"ac\" can not be "
						+ "closed because it is already null");
			}
		} catch (Exception e) {
			logger.error("Unable to close connection", e);
		}
	}


	
	

	
	protected ArrayList<String[]> storeResultSet(ResultSet rs) throws SQLException {
		ArrayList<String[]> rtable = new ArrayList<String[]>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int ccount = rsmd.getColumnCount();
		
		/*
		 * TEST:
		 * SELECT preis as pxy
		 * ColumnName = preis
		 * ColumnLabel = pxy
		 * SELECT preis
		 * ColumnName = preis
		 * ColumnLabel = preis
		 */
		
		// header
		String[] head = new String[ccount];
		for (int i = 1; i <= ccount; i++) { 
			head[i-1] = rsmd.getColumnLabel(i);
		}
		rtable.add(head);
		
		// table content
		while (rs.next()) {
			String[] row = new String[ccount];
		    for (int i = 1; i <= ccount; i++) {
		    	row[i-1] = rs.getString(i);
		    }
		    rtable.add(row);
		}
		
		// close the result set
		close(rs);

		return rtable;
	}
	



}
