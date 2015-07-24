package com.twitternlg.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


public class DatabaseManager {
 
	private static final String DB_DRIVER = "org.postgresql.Driver";
	private static final String DB_NAME = "nlglog";
	
	//deployment
	private static final String DB_USER = "dotrural";
	private static final String DB_PASSWORD = "sj00awesome";
	private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/"+DB_NAME;

  
	private static Connection dbConnection = null;
	private static Statement statement=null;
	
	private static DatabaseManager instance = null;

	protected DatabaseManager() {
		  // Exists only to defeat instantiation.
	}
	  
	//singleton initialization
	public static DatabaseManager getInstance() {
		if(instance == null) {
			instance = new DatabaseManager();
			}
		return instance;
		}
	
	public static Date convertJavaDateToSqlDate(java.util.Date date) {
	    return new Date(date.getTime());
	}
	
	private static java.sql.Timestamp getCurrentTimestamp(){
		java.util.Date today = new java.util.Date();
	    	return new java.sql.Timestamp(today.getTime());
	 }
	/*
	 * get most recent message for the recipient
	 */
	public static void getLog(String recipient){
		
		String WHERE_CLAUSE = "WHERE recipient="+recipient;
		String query = "SELECT * FROM time_log "+WHERE_CLAUSE;
		
		ResultSet resultset = DBFetch(query);
		
		//List<List<String>> results = new ArrayList<>();  // List of list, one per row
		
		//List<HashMap<String,Object>> results = convertResultSetToList(resultset);

		//return results;
	}
	
	/*
	 * get most recent message for the recipient
	 */
	public static void insertLog(String recipient, String message){
		
		Timestamp timestamp= getCurrentTimestamp();
		
		String query = "INSERT INTO time_log (recipient, message, timestamp) VALUES('"+recipient+"','"+message+"','"+timestamp+"')";
		
		DBInsert(query);
		
	}
	
	private static ResultSet DBFetch(String query) {
		//String query = "SELECT * from time_log WHERE recipient='twitterhandle'";
		System.out.println("Query to fetch: "+query);

		ResultSet results=null;
 
		try {
			if(dbConnection==null || dbConnection.isClosed())
				dbConnection = getDBConnection();
			
			if(statement==null || statement.isClosed())
				statement = dbConnection.createStatement();
 
			//System.out.println(query);
 
			// execute insert SQL stetement
			results = statement.executeQuery(query);
		      
			//System.out.println("results results results results");
 
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Failed for query: "+query);
 
		}
		return results;
 
	}
	

	private static void DBInsert(String query) {
		//String query = "INSERT INTO member"+ " (member_ref, name) " + "VALUES"+ "(1,'test')";
 
		System.out.println("insert: "+query);
		
		try {
			if(dbConnection==null || dbConnection.isClosed())
				dbConnection = getDBConnection();
			
			if(statement==null || statement.isClosed())
				statement = dbConnection.createStatement();
 
			//System.out.println(query);
 
			// execute insert SQL stetement
			statement.executeUpdate(query);
 
			//System.out.println("Record is inserted into DBUSER table!");
 
			if (statement != null) {
				statement.close();
			}
 
			if (dbConnection != null) {
				dbConnection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Failed for query: "+query);
 
		}
 
	}
 
	/*
	 * returns MySQL Database connection
	 */
	private static Connection getDBConnection() {
		 
		dbConnection = null;
 
		try {
 
			Class.forName(DB_DRIVER);
 
		} catch (ClassNotFoundException e) {
 
			System.out.println(e.getMessage());
 
		}
 
		try {
 
			dbConnection = DriverManager.getConnection(
                               DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;
 
		} catch (SQLException e) {
 
			System.out.println(e.getMessage());
 
		}
 
		return dbConnection;
 
	}
}