package com.project.LightWeightAnalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.arrow.flight.FlightClient;
import org.apache.arrow.flight.FlightStream;
import org.apache.arrow.flight.Location;
import org.apache.arrow.flight.Ticket;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;


public class QueryManager { 
	String tmstmp = null; 
	Timestamp timeStamp = null;
	Timestamp timeStamp2 = null;
	private String modelarUrl = "leviathan.imsi.athenarc.gr";
	private Integer modelarPort = 9999;
	int updateWindowMinutes = -1;
	int slidingWindowSize = -1;
	String databaseTableName = null;
	int variablename = -1;
	String queryType = null;
	LocalDateTime timestamp = null;
	int countThreashold = -1;
	Location location = null;
	RootAllocator rootAllocator = null;
	FlightClient flightClient = null;
	Ticket ticket = null;
	FlightStream flightStream = null;
	
	
	//constructor
	QueryManager(String parameterFilePath){
		readParameterFile(parameterFilePath);
		location = Location.forGrpcInsecure(modelarUrl, modelarPort);
		rootAllocator = new RootAllocator();
		flightClient = FlightClient.builder().location(location).allocator(rootAllocator).build();
	}
		
	
    //read and split the parameter file
    void readParameterFile(String parameterFilePath)  {
		File file = new File(parameterFilePath);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		
		try {
			while ((line = br.readLine()) != null) {
				line = line.replaceAll(" ","");
				line = line.replaceAll("\t","");
				String[] splitLine = line.split("=");
				
				String parameterName = splitLine[0]; 
				
				switch (parameterName) {
					case "updateWindowMinutes":
						updateWindowMinutes = Integer.parseInt(splitLine[1]);
						break;
					case "slidingWindowSize":
						slidingWindowSize = Integer.parseInt(splitLine[1]);
						break;
					case "databaseTableName":
						databaseTableName = splitLine[1];
						break;
					case "variableName":
						variablename = Integer.parseInt(splitLine[1]);
						break;
					case "timestamp":
						 timestamp = LocalDateTime.parse(splitLine[1]).atZone(ZoneOffset.UTC).toLocalDateTime();
						 timeStamp = Timestamp.valueOf(timestamp);
						 
						 break;
					case "countThreashold":
						countThreashold = Integer.parseInt(splitLine[1]);
						break;
					case "executionQuery":
						queryType = splitLine[1];
						queryType = queryType.toLowerCase();
						if (!queryType.equals("min") && !queryType.equals("max") && !queryType.equals("count") && !queryType.equals("avg")) {
							System.err.println("Wrong operator. " + queryType +" does not exist");
						}
						break;
					default:
						System.err.println("Parameter : " + parameterName +" does not exist");
				}				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
	//get records for update window
    FlightStream getUpdates(Timestamp timeStampStart, Timestamp timeStampEnd) {
		String sql = "SELECT value, timestamp FROM " +  databaseTableName + " WHERE tid = " + variablename +" AND timestamp >= '" + timeStampStart +"' AND timestamp < '" + timeStampEnd +"' ";
		ticket = new Ticket(sql.getBytes());
		flightStream = flightClient.getStream(ticket);
		
		return flightStream;
	}
    
	
    //query execution
	void executeQuery()  {
		switch (this.queryType) {
			case "min":
				this.executeMIN();
				break;
			case "max":
				this.executeMAX();
				break;
			case "count":
				this.executeCOUNT(countThreashold);
				break;
			case "avg":
				this.executeAVG();
				break;
		}
	}
	
	
	//Shift right one window update the sliding window
	float shiftRightWindow(float[] slidingWindow) {
		float delMin ;		
		delMin = slidingWindow[slidingWindow.length-1];

		for (int i = slidingWindow.length -2 ; i >= 0 ; i-- ) {
			slidingWindow[i+1] = slidingWindow [i];
		}
		
		slidingWindow[0] = Float.MAX_VALUE;

		return delMin;
	}
		
	
	//Calculate MIN in the update window	
	float calculateMIN(ArrayList<Float> q) {
		float MIN = Float.MAX_VALUE;
		
		for(float s : q) {
			if ( s != Float.MAX_VALUE ) {
				if ( MIN > s) {
					MIN = s;
				}	
			}
		}
		
		return MIN;
	}
	
	
	//Calculate MIN in the sliding window
	float calculateMIN(float[] q) {
		float MIN = Float.MAX_VALUE;
		
		for(float s : q) {
			if ( s != Float.MAX_VALUE ) {
				if ( MIN > s) {
					MIN = s;
				}	
			}
		}
		return MIN;
	}

	
	//execute MIN query
	void executeMIN() {
		float[] slidingWindow = new float[slidingWindowSize];
		float overallMIN = Float.MAX_VALUE;
		int initialIndex = 0;
		boolean running = true;
		ArrayList<Float> updateWindowList = new ArrayList<Float>();
		float updateWindowMIN = 0.0f;
		float delMIN = 0.0f;
		FlightStream flightStreamRes;
		VectorSchemaRoot vsr;
		int rowCount;

		timeStamp2 = new Timestamp (timeStamp.getNanos());//update the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));	

		for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
			
			flightStreamRes = this.getUpdates(timeStamp, this.timeStamp2); //get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        for (int row = 0; row < rowCount; row++) {//put all the records in the update window list		        	
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
		        }
				
		        updateWindowMIN = calculateMIN(updateWindowList);//calculate MIN inside the update window
				slidingWindow[initialIndex] = updateWindowMIN ;//save the update window in the sliding window
				
				if (overallMIN > updateWindowMIN) {//check and update the overallMIN
					overallMIN = updateWindowMIN;
				}
			}

			else {//no records
				slidingWindow[initialIndex] = Float.MAX_VALUE;
			}
					
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (running) {//infinitive loop
			
			if (overallMIN == Float.MAX_VALUE) {//fire the event
				System.out.println("MIN = empty window");
			}
			else {
				System.out.println("MIN = " + overallMIN);
			}
					
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);

			if (flightStream.next()) {	
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        for (int row = 0; row < rowCount; row++) {   	
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
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
				slidingWindow[0] = Float.MAX_VALUE;
				
				if (delMIN == overallMIN) {//check and update overallMIN
					overallMIN = calculateMIN(slidingWindow);
				}	
				
			}
			
			updateWindowList.clear();//clear the update window list
			timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	
	//Calculate MAX in the update window
	float calculateMAX(ArrayList<Float> q) {
		float MAX = Float.MIN_VALUE;
		
		for( float s : q) {
			if ( s != Float.MIN_VALUE ) {
				if ( MAX < s) {
					MAX = s;
				}	
			}
		}
		
		return MAX;
	}
	
	
	//Calculate MAX in the sliding window
	float calculateMAX(float[] q) {
		float MAX = Float.MIN_VALUE;
		
		for(float s : q) {
			if ( s != Float.MIN_VALUE ) {
				if ( MAX < s) {
					MAX = s;
				}	
			}
		}
		return MAX;
	}
	
	
	//execute MAX query
	void executeMAX() {
		float[] slidingWindow = new float[slidingWindowSize];
		float overallMAX = -Float.MIN_VALUE;
		int initialIndex = 0;
		boolean running = true;
		ArrayList<Float> updateWindowList = new ArrayList<Float>();
		float updateWindowMAX = 0.0f;
		float delMAX = 0.0f;
		FlightStream flightStreamRes;
		VectorSchemaRoot vsr;
		int rowCount;		

		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//update the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));	
		
		for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
			
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        for (int row = 0; row < rowCount; row++) {//put all the records in the update window list	
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
		        }
				
		        updateWindowMAX = calculateMAX(updateWindowList);//calculate MAX inside the update window
				slidingWindow[initialIndex] = updateWindowMAX ;//save the update window in the sliding window
				
				if (overallMAX < updateWindowMAX) {//check and update the overallMAX
					overallMAX = updateWindowMAX;
				}
			}

			else {//no records
				slidingWindow[initialIndex] = -Float.MIN_VALUE;
			}			
					
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (running) {//infinitive loop
			
			if (overallMAX == -Float.MIN_VALUE) {//fire the event
				System.out.println("MAX = empty window");
			}
			else {
				System.out.println("ΜΑΧ = " + overallMAX);
			}
					
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window

			if (flightStream.next()) {//we have records	
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        for (int row = 0; row < rowCount; row++) { //put all the records in the update window list 	
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
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
				slidingWindow[0] = -Float.MIN_VALUE;
				
				if (delMAX == overallMAX) {//check and update overallMAX
					overallMAX = calculateMAX(slidingWindow);
				}	
				
			}
			
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	
	//Calculate COUNT in the update window
	int calculateCOUNT(double thresholdValue, ArrayList<Float> q) {
		int counter = 0;
		
		for(Float s : q) {
			if (s!= Float.MAX_VALUE) {
				if (s > thresholdValue) {
					counter++;
				}
			}
		}	
		return counter;
	}
	
	
	//Execute COUNT query
	void executeCOUNT(double thresholdValue) {
		float[] slidingWindow = new float[slidingWindowSize];
		float overallCOUNT = 0.0f;
		int initialIndex = 0;
		boolean running = true;
		ArrayList<Float> updateWindowList = new ArrayList<Float>();
		float updateWindowCOUNT = 0.0f;
		float delCOUNT = 0.0f;
		FlightStream flightStreamRes;
		VectorSchemaRoot vsr;
		int rowCount;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//update the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));	
		
		for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time	
			
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
				
		        for (int row = 0; row < rowCount; row++) {//put all the records in the update window list
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
				}
				
				updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);//calculate COUNT inside the update window
				slidingWindow[initialIndex] = updateWindowCOUNT ;//save the update window in the sliding window
				overallCOUNT += updateWindowCOUNT;// update the overallCOUNT
				
			}
			else {//no records
				slidingWindow[initialIndex] = Float.MAX_VALUE;
			}
			
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
		}
		
		while (running) {//infinitive loop
			
			System.out.println("COUNT = " + overallCOUNT);//fire the event			
			
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        for (int row = 0; row < rowCount; row++) { //put all the records in the update window list
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
				}
				
				updateWindowCOUNT = calculateCOUNT(thresholdValue,updateWindowList);//calculate COUNT inside the update window
				delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
				slidingWindow[0] = updateWindowCOUNT;//save the update window in the sliding window
				
				if (delCOUNT != Float.MAX_VALUE) {//update overallCOUNT after the deletion
					overallCOUNT -= delCOUNT;
				}
				
				overallCOUNT += updateWindowCOUNT;	//update overallCOUNT after the insertion
				
			}
			else {//no records
				delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
				slidingWindow[0] = Float.MAX_VALUE;
				
				if (delCOUNT != Float.MAX_VALUE) {//update overallCOUNT after the deletion
					overallCOUNT -= delCOUNT;
				}
				
			}
			
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	//Calculate AVG in the update window
	float calculateAVG(ArrayList<Float> q) {
		float avg = 0.0f;
		int counter = 0;
		
		for(float s : q) {
			if (s!= Float.MAX_VALUE) {
				avg += s;
				counter++;
			}
		}
		
		return avg/counter;
	}
	
	
	//Execute AVG query
	void executeAVG() {
		float[] slidingWindow = new float[slidingWindowSize];
		float overallAVG = 0.0f;
		int initialIndex = 0;
		boolean running = true;
		ArrayList<Float> updateWindowList = new ArrayList<Float>();
		float updateWindowAVG = 0.0f;
		float delAVG = 0.0f;
		int counter = 0;
		FlightStream flightStreamRes;
		VectorSchemaRoot vsr;
		int rowCount;
		
		this.timeStamp2 = new Timestamp (timeStamp.getNanos());//update the timestamps
		timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));	
	
		for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {// fill the sliding window for the first time
			
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        
		        for (int row = 0; row < rowCount; row++) {//put all the records in the update window list
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
				}
				
				updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG inside the update window
				slidingWindow[initialIndex] = updateWindowAVG ;	//save the update window in the sliding window			
				overallAVG += updateWindowAVG; //Update overallAVG
				counter ++;//update the counter of the update windows inside the sliding window
				
			}
			else {//no records
				slidingWindow[initialIndex] = Float.MAX_VALUE;
			}
			
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
		}	
		
		while (running) {//infinitive loop
			
			if (counter == 0) {//fire the event
				System.out.println("AVG = empty window");
			}
			else {
				System.out.println("AVG = " + overallAVG/counter);
			}
						
			flightStreamRes = this.getUpdates(timeStamp, timeStamp2);//get records for the update window
			
			if (flightStream.next()) {//we have records
				vsr = flightStream.getRoot();
		        rowCount = vsr.getRowCount();
		        
		        for (int row = 0; row < rowCount; row++) { //put all the records in the update window list
		        	updateWindowList.add(((Float4Vector) vsr.getVector("value")).get(row));
				}
				
				updateWindowAVG = calculateAVG(updateWindowList);//calculate AVG inside the update window
				delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
				slidingWindow[0] = updateWindowAVG;//save the update window in the sliding window
				
				if (delAVG != Float.MAX_VALUE) {//update overallAVG after the deletion
					overallAVG -= delAVG;
				}
				
				overallAVG += updateWindowAVG;//update overallAVG after the insertion
				counter++;//update the counter of the update windows inside the sliding window		
				
			}
			else {//no records
				delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
				slidingWindow[0] = Float.MAX_VALUE;
				
				if (delAVG != Float.MAX_VALUE) {//update overallAVG after the deletion
					overallAVG -= delAVG;
				}
				
				if (counter > 0 ) {//update the counter of the update windows inside the sliding window
					counter --;
				}	
				
			}
			
			updateWindowList.clear();//clear the update window list
			this.timeStamp.setTime(this.timeStamp2.getTime());//update the timestamps
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.MINUTES.toMillis(this.updateWindowMinutes));
			
			try {
				flightStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
