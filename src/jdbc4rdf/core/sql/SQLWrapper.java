package jdbc4rdf.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jdbc4rdf.core.config.Config;



public abstract class SQLWrapper {


	protected Config conf;
	
	/**
	 * Initialize the SQL wrapper class
	 * @param conf Connection configuration container
	 */
	public SQLWrapper(Config conf) {
		// Try to load the driver class
		try {
			Class.forName(conf.getDriver().getDriverClass());
		} catch (ClassNotFoundException e) {
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
	
	
	
	public void runSql() {
		
		Connection conn = null;
		//Statement stmt = null;
		try {
			// init
			conn = init();
			
			// do import
			loadData(conn);
			
		} catch (Exception e) {
			rollback(conn);
		} finally {
			close(conn);
		}
	}
	
	
	protected abstract void loadData(Connection conn) throws Exception;

	
	protected Connection init() throws SQLException {

		final String host = conf.getHost();
		final String db = conf.getDbName();
		final String dbuser = conf.getUser();
		final String dbpw = conf.getPw();
		
		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		final int PORT = 10000;
		
		Connection conn = null;
		try {
			// jdbc:hive2://localhost:10000/default", "hive", ""
			conn = DriverManager.getConnection("jdbc:hive2://" + host + ":" + PORT + "/" + db, dbuser, dbpw);
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		}


		return conn;
	}



	protected void rollback(Connection conn) {
		try {
			if (conn != null) {
				if (!conn.isClosed()) {
					System.out.println("calling rollback!");
					conn.rollback();
				} else {
					System.out.println("Connection already closed");
				}

			} else {
				System.out.println("Connection is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	protected void close(AutoCloseable ac) {
		try {
			if (ac != null) {
				// also commit here?
				ac.close();
			} else {
				System.out.println("[WARNING] close() "
						+ "AutoClosable \"ac\" can not be "
						+ "closed because it is already null");
			}
		} catch (Exception e) {
			e.printStackTrace();
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