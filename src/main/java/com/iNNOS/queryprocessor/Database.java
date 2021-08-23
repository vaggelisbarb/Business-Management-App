package com.iNNOS.queryprocessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static Database database;
	private Connection myConn;
	
	private String DATABASE = "jdbc:mysql://ux49x9yq3z51wpf3:9m67kekunbxniMoku01A@bmzi3dvfuyxea4mmpx1a-mysql.services.clever-cloud.com:3306/bmzi3dvfuyxea4mmpx1a";
	private String USER = "ux49x9yq3z51wpf3";
	private String PASSWORD = "9m67kekunbxniMoku01A"; 
	

	public Database() {}
	
	
	/** Singleton Pattern.
	 * 	Public static method for getting the Database instance.
	 */
	public static Database getDatabaseInstance() {
		if (database == null)
			database = new Database();
		return database;
	}
	
	
	public void connect() {
		try {
			myConn = DriverManager.getConnection(DATABASE, USER, PASSWORD);
			System.out.println("Connected to Cloud Database succesfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		if (myConn != null)
			try {
				myConn.close();
				System.out.println("Disconnected from Cloud Database");
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	
	public Statement getStatement() {
		Statement stm = null;
		try {
			stm = myConn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return stm;
	}
	
	public Connection getConnection() {
		return this.myConn;
	}
	
}
