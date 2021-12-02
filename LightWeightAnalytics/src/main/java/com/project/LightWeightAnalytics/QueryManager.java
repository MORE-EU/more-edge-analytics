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
	String tableName = null;
	String columnName = null; 
	String tmstmp = "2014-01-01 11:00:00"; 
	Timestamp timeStamp = null;
	Timestamp timeStamp2 = null;
	String jdbcURL = null;
	String jdbcUsername = null;
	String jdbcPassword = null;
	
	
	//Constructor of the Query Manager
	QueryManager( int slidingWindowSize, String tableName, String columnName){ 
		this.slidingWindowSize = slidingWindowSize;
		this.tableName = tableName;
		this.columnName = columnName;
		Timestamp sqlTimestamp = Timestamp.valueOf(this.tmstmp);
		this.timeStamp = sqlTimestamp;

		query = query + this.tableName;
	}
	
	
	//Create database connection
	void createConnection(String jdbcURL, String jdbcUsername , String jdbcPassword) {
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
	
	
	//Close database connection
	void closeConnection() { 
		try {
			this.conn.close(); ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	//Get records for update window
	ResultSet getUpdates(Timestamp timeStampStart, Timestamp timeStampEnd) {
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
	
	
	//Calculate MIN in the update window
	double calculateMIN(ArrayList<Double> q) {
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
	
	
	//Calculate MIN in the sliding window
	double calculateMIN(double[] q) {
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
	
	
	//Shift right one window update the sliding window
	double shiftRightWindow(double[] slidingWindow) {
		double delMin = 0.0;
		
		delMin = slidingWindow[slidingWindow.length-1];

		for (int i = slidingWindow.length -2 ; i >= 0 ; i-- ) {
			slidingWindow[i+1] = slidingWindow [i];
		}
		slidingWindow[0] = Double.MAX_VALUE;

		return delMin;
	}
	
	
	//Calculate MAX in the update window
	double calculateMAX(ArrayList<Double> q) {
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
	
	
	//Calculate MAX in the sliding window
	double calculateMAX(double[] q) {
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
	
	
	//Calculate COUNT in the update window
	int calculateCOUNT(double thresholdValue, ArrayList<Double> q) {
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
	
	
	//Calculate AVG in the update window
	double calculateAVG(ArrayList<Double> q) {
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


	//Execute MIN query
	void executeMIN() {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMIN = Double.MAX_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMIN = 0.0;
		double delMIN = 0.0;
		ResultSet rs = null;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
				
				rs = getUpdates(this.timeStamp,timeStamp2); //get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowMIN = calculateMIN(updateWindowList);//calculate MIN inside the update window
					slidingWindow[initialIndex] = updateWindowMIN ;//save the update window in the sliding window
					
					if (overallMIN > updateWindowMIN) {//check and update the overallMIN
						overallMIN = updateWindowMIN;
					}
					
				}
				else {//no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}

			while (running) {//infinitive loop
				
				if (overallMIN == Double.MAX_VALUE) {//fire the event
					System.out.println("MIN = empty window");
				}
				else {
					System.out.println("MIN = " + overallMIN);
				}
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowMIN = calculateMIN(updateWindowList);//calculate MIN inside the update window
					delMIN = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = updateWindowMIN;//save the update window in the sliding window
					
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
					delMIN = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delMIN == overallMIN) {//check and update overallMIN
						overallMIN = calculateMIN(slidingWindow);
					}	
					
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//Execute MAX query
	void executeMAX() {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMAX = -Double.MIN_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMAX = 0.0;
		double delMAX = 0.0;
		ResultSet rs = null;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {	// fill the sliding window for the first time
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowMAX = calculateMAX(updateWindowList);//calculate MAX inside the update window
					slidingWindow[initialIndex] = updateWindowMAX ;//save the update window in the sliding window
					
					if (overallMAX < updateWindowMAX) {//check and update the overallMAX
						overallMAX = updateWindowMAX;
					}
					
				}
				else {//no records
					slidingWindow[initialIndex] = -Double.MIN_VALUE;
				}		
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}		
			
			while (running) {//infinitive loop
				
				if (overallMAX == -Double.MIN_VALUE) {//fire the event
					System.out.println("MAX = empty window");
				}
				else {
					System.out.println("MAX = " + overallMAX);
				}	
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowMAX = calculateMAX(updateWindowList);//calculate MAX inside the update window
					delMAX = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = updateWindowMAX;//save the update window in the sliding window
					
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
					delMAX = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = -Double.MIN_VALUE;
					
					if (delMAX == overallMAX) {//check and update overallMAX
						overallMAX = calculateMAX(slidingWindow);
					}	
					
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Execute AVG query
	void executeAVG() {
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
		ResultSet rs = null;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG inside the update window
					slidingWindow[initialIndex] = updateWindowAVG ;	//save the update window in the sliding window			
					overallAVG += updateWindowAVG; //Update overallAVG
					counter ++;//update the counter of the update windows inside the sliding window
				}
				else {//no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}	
			
			while (running) {//infinitive loop
				
				if (counter == 0) {//fire the event
					System.out.println("COUNT = empty window");
				}
				else {
					System.out.println("COUNT = " + overallAVG/counter);
				}
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG inside the update window
					delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = updateWindowAVG;//save the update window in the sliding window
					
					if (delAVG != Double.MAX_VALUE) {//update overallAVG after the deletion
						overallAVG -= delAVG;
					}
					
					overallAVG += updateWindowAVG;//update overallAVG after the insertion
					counter++;//update the counter of the update windows inside the sliding window
				}
				else {//no records
					delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delAVG != Double.MAX_VALUE) {//update overallAVG after the deletion
						overallAVG -= delAVG;
					}
					
					if (counter > 0 ) {//update the counter of the update windows inside the sliding window
						counter --;
					}
					
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Execute COUNT query
	void executeCOUNT(double thresholdValue) {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallCOUNT= 0.0;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowCOUNT = 0.0;
		double delCOUNT = 0.0;
		ResultSet rs = null;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);//calculate COUNT inside the update window
					slidingWindow[initialIndex] = updateWindowCOUNT ;//save the update window in the sliding window
					overallCOUNT += updateWindowCOUNT;// update the overallCOUNT
				}
				else {//no records
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			while (running) {//infinitive loop
				

				System.out.println("COUNT = " + overallCOUNT);//fire the event
				
				rs = getUpdates(this.timeStamp,timeStamp2);//get records for the update window
				
				if (rs.next() != false) {//we have records
					updateWindowList.add(rs.getDouble(this.columnName));
					
					while ( rs.next()) {//put all the records in the update window list
						updateWindowList.add(rs.getDouble(this.columnName));
					}
					
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);//calculate COUNT inside the update window
					delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = updateWindowCOUNT;//save the update window in the sliding window
					
					if (delCOUNT != Double.MAX_VALUE) {//update overallCOUNT after the deletion
						overallCOUNT -= delCOUNT;
					}
					
					overallCOUNT += updateWindowCOUNT;	//update overallCOUNT after the insertion
				}
				else {//no records
					delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delCOUNT != Double.MAX_VALUE) {//update overallCOUNT after the deletion
						overallCOUNT -= delCOUNT;
					}
					
				}
				
				updateWindowList.clear();//clear the update window list
				this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
