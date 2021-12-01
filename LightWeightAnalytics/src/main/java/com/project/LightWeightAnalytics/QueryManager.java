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

//jdbc:h2:~/tmp/h2dbs/moredb//
//java -cp bin/h2-1.4.199.jar org.h2.tools.Shell


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
	
	void closeConnection() {
		
		try {
			this.conn.close(); ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	QueryManager( int slidingWindowSize){
		this.slidingWindowSize = slidingWindowSize;
		Timestamp sqlTimestamp = Timestamp.valueOf(this.tmstmp);
		this.timeStamp = sqlTimestamp;

		query = query + this.tableName;
		
		System.out.println("query = " + query);
	
	}
	
	void executeMIN() {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMIN = Double.MAX_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMIN = 0.0;
		double delMIN = 0.0;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {
				
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowMIN = calculateMIN(updateWindowList);
					slidingWindow[initialIndex] = updateWindowMIN ;
					
					if (overallMIN > updateWindowMIN) {
						overallMIN = updateWindowMIN;
					}
				}
				else {
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			
			while (running) {
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				System.out.println("query2 = " + query2);
				
				if (overallMIN == Double.MAX_VALUE) {
					System.out.println("MIN = empty window");
				}
				else {
					System.out.println("MIN = " + overallMIN);
				}
				
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowMIN = calculateMIN(updateWindowList);
					System.out.println("updateWindowMIN = " + updateWindowMIN);
					delMIN = shiftRightWindow(slidingWindow);
					slidingWindow[0] = updateWindowMIN;
					if (overallMIN > updateWindowMIN) {
						overallMIN = updateWindowMIN;
					}
					else {
						if (delMIN == overallMIN) {
							overallMIN = calculateMIN(slidingWindow);
						}
					}
					
				}
				else {
					delMIN = shiftRightWindow(slidingWindow);
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delMIN == overallMIN) {
						overallMIN = calculateMIN(slidingWindow);
					}		
				}
				
				
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
	
	
	double shiftRightWindow(double[] slidingWindow) {
		double delMin = 0.0;
		
		delMin = slidingWindow[slidingWindow.length-1];

		for (int i = slidingWindow.length -2 ; i >= 0 ; i-- ) {
			slidingWindow[i+1] = slidingWindow [i];
		}
		slidingWindow[0] = Double.MAX_VALUE;

		return delMin;
	}
	
	
	void executeMAX() {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallMAX = -Double.MIN_VALUE;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowMAX = 0.0;
		double delMAX = 0.0;

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {
				
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowMAX = calculateMAX(updateWindowList);
					slidingWindow[initialIndex] = updateWindowMAX ;
					
					if (overallMAX < updateWindowMAX) {
						overallMAX = updateWindowMAX;
					}
				}
				else {
					slidingWindow[initialIndex] = -Double.MIN_VALUE;
				}
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			
			while (running) {
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				System.out.println("query2 = " + query2);
				
				if (overallMAX == -Double.MIN_VALUE) {
					System.out.println("MAX = empty window");
				}
				else {
					System.out.println("MAX = " + overallMAX);
				}
				
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowMAX = calculateMAX(updateWindowList);
					
					System.out.println("updateWindowMAX = " + updateWindowMAX);
					delMAX = shiftRightWindow(slidingWindow);
					slidingWindow[0] = updateWindowMAX;
					if (overallMAX < updateWindowMAX) {
						overallMAX = updateWindowMAX;
					}
					else {
						if (delMAX == overallMAX) {
							overallMAX = calculateMAX(slidingWindow);
						}
					}
					
				}
				else {
					delMAX = shiftRightWindow(slidingWindow);
					slidingWindow[0] = -Double.MIN_VALUE;
					
					if (delMAX == overallMAX) {
						overallMAX = calculateMAX(slidingWindow);
					}		
				}
				
				
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {
				
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowAVG = calculateAVG(updateWindowList);
					slidingWindow[initialIndex] = updateWindowAVG ;				
					
					overallAVG += updateWindowAVG;
					counter ++;
				}
				else {
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			
			while (running) {
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				System.out.println("query2 = " + query2);
				
				if (counter == 0) {
					System.out.println("COUNT = empty window");
				}
				else {
					System.out.println("COUNT = " + overallAVG/counter + "\t overallAVG = " + overallAVG + "\tcounter = " + counter);
				}
				
				
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowAVG = calculateAVG(updateWindowList);
					
					delAVG = shiftRightWindow(slidingWindow);
					slidingWindow[0] = updateWindowAVG;
					
					if (delAVG != Double.MAX_VALUE) {
						overallAVG -= delAVG;
					}
					overallAVG += updateWindowAVG;
					counter++;
					
				}
				else {
					delAVG = shiftRightWindow(slidingWindow);
					slidingWindow[0] = Double.MAX_VALUE;
					
					if (delAVG != Double.MAX_VALUE) {
						overallAVG -= delAVG;
					}
					if (counter > 0 ) {
						counter --;
					}
					
				}
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	void executeCOUNT(double thresholdValue) {
		double[] slidingWindow = new double[slidingWindowSize];
		double overallCOUNT= 0.0;
		int initialIndex = 0;
		boolean running = true;
		String query2 = null;
		ArrayList<Double> updateWindowList = new ArrayList<Double>();
		double updateWindowCOUNT = 0.0;
		double delCOUNT = 0.0;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
		
		try {
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {
				
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);
					slidingWindow[initialIndex] = updateWindowCOUNT ;
					
					overallCOUNT += updateWindowCOUNT;
				}
				else {
					slidingWindow[initialIndex] = Double.MAX_VALUE;
				}
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
			
			
			while (running) {
				stmt = conn.createStatement();
				query2 = query + " WHERE TMSTMP >= '" + this.timeStamp+"' and TMSTMP < '" + timeStamp2 +"'";
				System.out.println("query2 = " + query2);
				
				if (overallCOUNT == 0.0) {
					System.out.println("COUNT = empty window");
				}
				else {
					System.out.println("COUNT = " + overallCOUNT);
				}
				
				ResultSet rs = stmt.executeQuery(query2);
				if (rs.next() != false) {
					updateWindowList.add(rs.getDouble(4));
					while ( rs.next()) {
						updateWindowList.add(rs.getDouble(4));
					}
					updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);
					
					System.out.println("updateWindowCOUNT = " + updateWindowCOUNT);
					delCOUNT = shiftRightWindow(slidingWindow);
					slidingWindow[0] = updateWindowCOUNT;
					
					if (delCOUNT != Double.MAX_VALUE) {
						overallCOUNT -= delCOUNT;
					}
					overallCOUNT += updateWindowCOUNT;
					
				}
				else {
					delCOUNT = shiftRightWindow(slidingWindow);
					slidingWindow[0] = Double.MAX_VALUE;
					if (delCOUNT != Double.MAX_VALUE) {
						overallCOUNT -= delCOUNT;
					}		
				}
				
				
				updateWindowList.clear();
				this.timeStamp.setTime(this.timeStamp2.getTime());
				timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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

}
