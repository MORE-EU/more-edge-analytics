package com.project.LightWeightAnalytics;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

import org.h2.tools.Server;

//mvn compile
//mvn exec:java -Dexec.mainClass=com.project.UDF.App


public class App {
  public static void main(String[] args) {
    
    Server server = null;
    String jdbcURL = "jdbc:h2:tcp://localhost:9092/~/tmp/h2dbs/moredb";
    String jdbcUsername = "dtsi";
    String jdbcPassword = "123456";
    String filePath = "/home/jimakos/eclipse-workspace/UDF/first_array.csv";
        
    
	try {
		// start the TCP Server
		server = Server.createTcpServer(args).start();
		System.out.println("Connect to Server");
	
		QueryManager qManager = new QueryManager(5);
		
		qManager.createConnection(jdbcURL, jdbcUsername, jdbcPassword);
		qManager.executeMIN();
		qManager.executeMAX();
		qManager.executeCOUNT(200.0);
		//qManager.executeAVG();
		qManager.closeConnection();
		
		// stop the TCP Server
	    server.stop();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

   
    
  }
 
  
}
