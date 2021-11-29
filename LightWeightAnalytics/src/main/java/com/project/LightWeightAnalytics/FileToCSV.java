package com.project.LightWeightAnalytics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class FileToCSV {

		Connection conn = null;
		Statement stmt = null; 
		String inputFile = null;
	
		FileToCSV(Connection conn, String filePath){
			this.conn = conn;
			this.inputFile = filePath;
		}
		
		
	  int trasformCSV(){
		  FileInputStream fis = null;
		  String line = null;
		  String insertQuery = null;
		  
		  try {
			  fis = new FileInputStream(inputFile);
		  } catch (FileNotFoundException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return 1;
		  }       
		
		  Scanner sc=new Scanner(fis);   
		  
		  line = sc.nextLine();
		  while(sc.hasNextLine()){  
			  //System.out.println(sc.nextLine());
			  line = sc.nextLine();
			  String []splitLine = line.split(",");
			  
			  try {
				stmt = conn.createStatement();
				insertQuery = "INSERT INTO inaccess (TMSTMP,MOD_TEMP,POWER,IRRADIANCE) VALUES('" + splitLine[0] + "'," + splitLine[1] + "," + splitLine[2] +","+ splitLine[3] +")";
				stmt.executeUpdate(insertQuery); 
				insertQuery = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			  
		  }  
		  sc.close();
		  try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return 0;
	  }

}
