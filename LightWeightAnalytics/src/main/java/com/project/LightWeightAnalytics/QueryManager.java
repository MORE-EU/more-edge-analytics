package com.project.LightWeightAnalytics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class QueryManager {
	Connection conn = null;
	Statement stmt = null; 
	int updateWindowMinutes = 60;
	int slidingWindowSize = -1;
	String query = "SELECT * FROM ";
	String tableName = "tableName"; // User must define the name of the table
	String tmstmp = "2014-01-01 11:00:00"; 
	Timestamp timeStamp = null;
	Timestamp timeStamp2 = null;
	String jdbcURL = null;
	String jdbcUsername = null;
	String jdbcPassword = null;
	
	
	QueryManager( int slidingWindowSize){ // Query Manager Constructor
		this.slidingWindowSize = slidingWindowSize;
		Timestamp sqlTimestamp = Timestamp.valueOf(this.tmstmp);
		this.timeStamp = sqlTimestamp;

		query = query + this.tableName;
	}
	
	
	void createConnection(String jdbcURL, String jdbcUsername , String jdbcPassword) { //create connection with the database
		this.jdbcURL = jdbcURL;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
		
		try {
			this.conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	void closeConnection() { //close connection with the database	
		try {
			this.conn.close(); ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	ResultSet getUpdates(Timestamp timeStamp, Timestamp timeStamp2) { //get records between these two timestamps 
		String query2 = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement();
			query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
			rs = stmt.executeQuery(query2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	
	double shiftRightWindow(double[] slidingWindow) { // sliding window to the right
		double delMin = 0.0;
		
		delMin = slidingWindow[slidingWindow.length-1];

		for (int i = slidingWindow.length -2 ; i >= 0 ; i-- ) {
			slidingWindow[i+1] = slidingWindow [i];
		}
		slidingWindow[0] = Double.MAX_VALUE;

		return delMin;
	}
	
	
	double calculateMAX(ArrayList<Double> q) {//calculate MAX in the update window
		double MAX = -Double.MIN_VALUE;
		
		for(Double s : q) {
			if ( s != -Double.MIN_VALUE ) {
				if ( MAX < s) {
					MAX = s;
				}	
			}
		}
		
		return MAX;
	}
	
	
	double calculateMAX(double[] q) { //calculate MAX in the sliding window
		double MAX = -Double.MIN_VALUE;
		
		for(Double s : q) {
			if ( s != -Double.MIN_VALUE ) {
				if ( MAX < s) {
					MAX = s;
				}	
			}
		}
		
		return MAX;
	}
	
	
	double calculateMIN(ArrayList<Double> q) {//calculate MIN in the update window
		double MIN = Double.MAX_VALUE;
		
		for(Double s : q) {
			if ( s != Double.MAX_VALUE ) {
				if ( MIN > s) {
					MIN = s;
				}	
			}
		}
		
		return MIN;
	}
	
	
	double calculateMIN(double[] q) { //calculate MIN in the sliding window
		double MIN = Double.MAX_VALUE;
		
		for(Double s : q) {
			if ( s != Double.MAX_VALUE ) {
				if ( MIN > s) {
					MIN = s;
				}	
			}
		}
		
		return MIN;
	}
	
	
	double calculateAVG(ArrayList<Double> q) { //calculate the average value inside the window update
		double avg = 0.0;
		int counter = 0;
		
		for(Double s : q) {
			if (s!= Double.MAX_VALUE) {
				avg += s;
				counter++;
			}
		}
		
		return avg/counter;
	}
	
	
	int calculateCOUNT(double thresholdValue, ArrayList<Double> q) { // calculate count in the update window
		int counter = 0;
		
		for(Double s : q) {
			if (s!= Double.MAX_VALUE) {
				if (s > thresholdValue) {
					counter++;
				}
			}
		}	
		return counter;
	}	
	

	void executeMIN() {// MIN query execution
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMIN = Double.MAX_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMIN = 0.0;
		double delMIN = 0.0;
		ResultSet rs;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//initialize the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
				rs = getUpdates(this.timeStamp, timeStamp2);// get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowMIN = calculateMIN(updateWindowList); //calculate MIN in the update window
					slidingWindow[initialIndex] = updateWindowMIN ;//put MIN in the sliding window
					
					if (overallMIN > updateWindowMIN) {//check and update overallMIN
						overallMIN = updateWindowMIN;
					}
				}
				else {// no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}			
			
			while (running) {//infinite loop
				if (overallMIN == Double.MAX_VALUE) {
					System.out.println("MIN = empty window");
				}
				else {
					System.out.println("MIN = " + overallMIN);
				}
				
				rs = getUpdates(this.timeStamp, timeStamp2);//get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowMIN = calculateMIN(updateWindowList);//calculate MIN in the update window
					delMIN = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = updateWindowMIN;//put MIN in the sliding window
					if (overallMIN > updateWindowMIN) {//check and update the overallMIN
						overallMIN = updateWindowMIN;
					}
					else {
						if (delMIN == overallMIN) {
							overallMIN = calculateMIN(slidingWindow);
						}
					}
					
				}
				else {//no records
					delMIN = shiftRightWindow(slidingWindow);
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delMIN == overallMIN) {//check and update the overallMIN
						overallMIN = calculateMIN(slidingWindow);
					}		
				}
				updateWindowList.clear();// clear the update window
				
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	void executeMAX() {//MAX query execution
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMAX = -Double.MIN_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMAX = 0.0;
		double delMAX = 0.0;
		ResultSet rs;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//initialize the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
				
				rs = getUpdates(this.timeStamp, timeStamp2);// get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowMAX = calculateMAX(updateWindowList);//calculate MAX in the update window
					slidingWindow[initialIndex] = updateWindowMAX ;//put MAX in the sliding window
					
					if (overallMAX < updateWindowMAX) {//check and update overallMAX
						overallMAX = updateWindowMAX;
					}
				}
				else {// no records
					slidingWindow[initialIndex] = -Double.MIN_VALUE;
				}
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			while (running) {//infinite loop
				if (overallMAX == -Double.MIN_VALUE) {
					System.out.println("MAX = empty window");
				}
				else {
					System.out.println("MAX = " + overallMAX);
				}
				
				rs = getUpdates(this.timeStamp, timeStamp2);//get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowMAX = calculateMAX(updateWindowList);//calculate MAX in the update window
					delMAX = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = updateWindowMAX;//put MAX in the sliding window
					if (overallMAX < updateWindowMAX) {//check and update the overallMAX
						overallMAX = updateWindowMAX;
					}
					else {
						if (delMAX == overallMAX) {
							overallMAX = calculateMAX(slidingWindow);
						}
					}		
				}
				else {//no records
					delMAX = shiftRightWindow(slidingWindow);
					slidingWindow[0] = -Double.MIN_VALUE;				
					if (delMAX == overallMAX) {//check and update the overallMAX
						overallMAX = calculateMAX(slidingWindow);
					}		
				}
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	void executeAVG() {// AVG query execution
		double[] slidingWindow = new double[slidingWindowSize];
		double overallAVG = 0.0;
		int initialIndex = 0;
		boolean running = true;
		double overallSUMOfAVGS = 0.0;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowAVG = 0.0;
		double delAVG = 0.0;
		int counter = 0;
		ResultSet rs;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//initialize the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {	// fill the sliding window for the first time		
				
				rs = getUpdates(this.timeStamp, timeStamp2);// get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG in the update window
					slidingWindow[initialIndex] = updateWindowAVG ;	//put AVG in the sliding window							
					overallAVG += updateWindowAVG;// update overallAVG
					counter ++; //update number of values in the sliding window
				}
				else {// no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}		
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}			
			
			while (running) {//infinite loop
				if (counter == 0) {
					System.out.println("COUNT = empty window");
				}
				else {
					System.out.println("COUNT = " + overallAVG/counter + "\t overallAVG = " + overallAVG + "\tcounter = " + counter);
				}
				rs = getUpdates(this.timeStamp, timeStamp2);//get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG in the update window
					delAVG = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = updateWindowAVG;//put AVG in the sliding window
					
					if (delAVG != Double.MAX_VALUE) {// update overallAVG
						overallAVG -= delAVG;
					}
					overallAVG += updateWindowAVG;
					counter++;//update number of values in the sliding window				
				}
				else {// no records
					delAVG = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delAVG != Double.MAX_VALUE) {// update overallAVG
						overallAVG -= delAVG;
					}
					if (counter > 0 ) {//update number of values in the sliding window
						counter --;
					}				
				}			
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	void executeCOUNT(double thresholdValue) { //COUNT query execution
		double[] slidingWindow = new double[slidingWindowSize];
		double overallCOUNT= 0.0;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowCOUNT = 0.0;
		double delCOUNT = 0.0;
		ResultSet rs;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//initialize the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {	// fill the sliding window for the first time	
				rs = getUpdates(this.timeStamp, timeStamp2);// get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);//calculate COUNT in the update window
					slidingWindow[initialIndex] = updateWindowCOUNT ;		//put COUNT in the sliding window		
					overallCOUNT += updateWindowCOUNT;// update overallCOUNT
				}
				else {//no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}			
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
						
			while (running) {//infinite loop
				
				if (overallCOUNT == 0.0) {
					System.out.println("COUNT = empty window");
				}
				else {
					System.out.println("COUNT = " + overallCOUNT);
				}
				
				rs = getUpdates(this.timeStamp, timeStamp2);//get records
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					while ( rs.next()) {//fill the update window
						updateWindowList.add(rs.getDouble("columnName")); // User must define the column Name in the table
					}
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);	//calculate COUNT in the update window			
					delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = updateWindowCOUNT;//put COUNT in the sliding window
					
					if (delCOUNT != Double.MAX_VALUE) {//update overallCOUNT
						overallCOUNT -= delCOUNT;
					}
					overallCOUNT += updateWindowCOUNT;	
				}
				else {//no records
					delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window to the right
					slidingWindow[0] = Double.MAX_VALUE;//put COUNT in the sliding window
					if (delCOUNT != Double.MAX_VALUE) {//update overallCOUNT
						overallCOUNT -= delCOUNT;
					}		
				}				
				updateWindowList.clear();// clear the update window
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
