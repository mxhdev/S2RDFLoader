package jdbc4rdf.core.config;

import java.util.logging.Logger;

public enum DBDRIVER {
	MYSQL, HIVE, SPARK;


	private static Logger logger =  Logger.getLogger(String.valueOf(DBDRIVER.class));
	
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
	public static final String SPARK_DRIVER = "org.apache.hive.jdbc.HiveDriver";
	
	
	public static DBDRIVER getDefaultDriver() {
		return MYSQL;
	}
	
	
	public static DBDRIVER detectDriver(String dstr) {
		if ((dstr.equalsIgnoreCase("mysql")) ||
				dstr.equalsIgnoreCase(MYSQL_DRIVER)) {
			return MYSQL;
		} else if ((dstr.equalsIgnoreCase("hive")) ||
				dstr.equalsIgnoreCase(HIVE_DRIVER)) {
			return HIVE;
		} else if (dstr.equalsIgnoreCase("spark")) {
			return SPARK; 
		}else {
			logger.warning("Couldn't detect driver " + dstr);
			return null;
		}
		
	}
	
	public String getDriverClass() {
		return DBDRIVER.getDriverClass(this);
	}
	
	public static String getDriverClass(DBDRIVER d) {
		if (d.equals(MYSQL)) {
			return MYSQL_DRIVER; 
		} else if (d.equals(HIVE)) {
			return HIVE_DRIVER;
		} else if (d.equals(SPARK)) {
			return SPARK_DRIVER;
		} else {
			logger.warning("Unknown driver " + d.toString() + " - returning null");
			return null;
		}
	}
	
	
	
	public String getJDBCUri(String host, String db) {
		String uri = "";
		
		
		if (this.equals(HIVE)) {
			// https://cwiki.apache.org/confluence/display/Hive/HiveServer2+Clients
			// example: jdbc:hive2://localhost:10000/dbxy
			// user name , password needed in uri ?
			uri = "jdbc:hive2://" + host + ":" + 10000 + "/" + db;
		} else if (this.equals(MYSQL)) {
			// https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
			// https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html
			// http://stackoverflow.com/questions/26307760/mysql-and-jdbc-with-rewritebatchedstatements-true
			// example: localhost:3310/dbxy
			uri = "jdbc:mysql://" + host + ":" + 3306 + "/" + db + "?useSSL=false&rewriteBatchedStatements=true";
		} else if (this.equals(SPARK)) {
			// default port of thrift server is 10000 changed here to 10001 so that hive and spark thrift server can
			// be run in parallel!
			uri = "jdbc:hive2://" + host + ":" + 10001 + "/" + db;
		}
		
		return uri;
	}
	
	
	public static String getDriverList() {
		return "mysql, hive";
	}
}
