package com.project.LightWeightAnalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;



public class QueryManager { 
	String tmstmp = null; 
	Timestamp timeStamp = null;
	int updateWindowMinutes = -1;
	int slidingWindowSize = -1;
	int numberOfQueries = -1;
	String databaseTableName = null;
	int variablename = -1;	
	LocalDateTime timeStampLocal = null;
	ExecutorService executor = null;
	int []threashold = null;
	String[] queryType = null;
	int numOfthreads = -1;
	
	Map <String,AggregatesInfo> aggregatesQueries = null;
	Map <String,String> variableFilePaths = null;
	
	ArrayList<ArrayList<ThreadInfo>> allThreadData = null;
	
	
	//constructor
	QueryManager(String parameterFilePath, int numOfthreads){
		this.numOfthreads = numOfthreads;
		executor = Executors.newFixedThreadPool(numOfthreads);
		
		//put all input files in a LinkedHashMap
		variableFilePaths = new LinkedHashMap<String,String>();
    	variableFilePaths.put("activepower", "/home/jimakos/MoreDataset/activepower.csv");
    	variableFilePaths.put("nacelledirection", "/home/jimakos/MoreDataset/nacelledirection.csv");
    	variableFilePaths.put("pitchangle", "/home/jimakos/MoreDataset/pitchangle.csv");
    	variableFilePaths.put("rotorspeed", "/home/jimakos/MoreDataset/rotorspeed.csv");
    	variableFilePaths.put("winddirection", "/home/jimakos/MoreDataset/winddirection.csv");
    	variableFilePaths.put("windspeed", "/home/jimakos/MoreDataset/windspeed.csv");
		
    	//read the parameter file
		readParameterFile(parameterFilePath);
		
	}
		
	
    //read and split the parameter file
    void readParameterFile(String parameterFilePath)  {
    	allThreadData = new ArrayList<ArrayList<ThreadInfo>>();
    	ArrayList<ThreadInfo> threadData = new ArrayList<ThreadInfo>();
    	
    	aggregatesQueries = new LinkedHashMap<String,AggregatesInfo>();
    	AggregatesInfo aggInfo = null;
    			
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
		QueryExecution queryExe = null;
		boolean FLAGQueryReady = false;
		
		int counter = 0; 
		int numOfQueriesPerThread = -1;
		int threadId = 0;
		int queryId = 0;
		
		try {
			//read all file
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					
					line = line.replaceAll("\t","");
					String[] splitLine = line.split(":", 2);
					splitLine[0] =  splitLine[0].replaceAll(" ","");
					String parameterName = splitLine[0]; 
					
					if ( parameterName.equals("timestamp")) {
						splitLine[1] = splitLine[1].replaceFirst(" ", "");
					}
					else {
						splitLine[1] = splitLine[1].replaceAll(" ", "");
					}
					
					//parse the query
					switch (parameterName) {
						case "numOfqueries":
							numberOfQueries = Integer.parseInt(splitLine[1]); 
							numOfQueriesPerThread = numberOfQueries/numOfthreads;
							break;
					
						case "updateWindowMinutes":
							updateWindowMinutes = Integer.parseInt(splitLine[1]);
							break;
						case "slidingWindowSize":
							slidingWindowSize = Integer.parseInt(splitLine[1]);
							break;
						case "timestamp":
							timeStamp = Timestamp.valueOf(splitLine[1]);
							 break;
						case "executionQuery":		
							String[] parseAggregates = null;
							if ( !splitLine[1].contains("&&") ) {
								parseAggregates =  new String[1];
								parseAggregates[0] = splitLine[1];
							}
							else {
								parseAggregates = splitLine[1].split("&&");
							}
							
							for ( int i = 0 ; i < parseAggregates.length ; i ++ ) {
								String[] analyzeAggregates = parseAggregates[i].split(",");
								
								String variableName = null;
								String aggregate = null;
								String threshold = null;
								String thresholdOperator =null ;
								
								if ( analyzeAggregates.length == 2) {
									variableName = analyzeAggregates[0];
									aggregate = analyzeAggregates[1];
								}
								else {
									variableName = analyzeAggregates[0];
									aggregate = analyzeAggregates[1];
									threshold = analyzeAggregates[3];
									thresholdOperator = analyzeAggregates[2] ;
								}
								
								
								switch (aggregate) {
									case "min":
										if ( (aggInfo = aggregatesQueries.get(variableName)) == null) {
											aggInfo = new AggregatesInfo(variableName);
											if (threshold == null) {
												aggInfo.getQueries().add(new MinQuery(slidingWindowSize));
												aggregatesQueries.put(variableName, aggInfo);
											}
											else {
												aggInfo.getQueries().add(new MinQuery(Float.parseFloat(threshold),slidingWindowSize,thresholdOperator ));
												aggregatesQueries.put(variableName, aggInfo);
											}
										}
										else {
											if (threshold == null) {
												aggInfo.getQueries().add(new MinQuery(slidingWindowSize));
											}
											else {
												aggInfo.getQueries().add(new MinQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
											}												
										}
										break;
									case "max":
										if ( (aggInfo = aggregatesQueries.get(variableName)) == null) {
											aggInfo = new AggregatesInfo(variableName);
											if (threshold == null) {
												aggInfo.getQueries().add(new MaxQuery(slidingWindowSize));
												aggregatesQueries.put(variableName, aggInfo);
											}
											else {
												aggInfo.getQueries().add(new MaxQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
												aggregatesQueries.put(variableName, aggInfo);
											}
										}
										else {
											if (threshold == null) {
												aggInfo.getQueries().add(new MaxQuery(slidingWindowSize));
											}
											else {
												aggInfo.getQueries().add(new MaxQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
											}												
										}
										break;
									case "count":
										if ( (aggInfo = aggregatesQueries.get(variableName)) == null) {
											aggInfo = new AggregatesInfo(variableName);
											if (threshold == null) {
												System.err.println("Aggregate Count must have a threshold");
												System.exit(0);
											}
											else {
												aggInfo.getQueries().add(new CountQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
												aggregatesQueries.put(variableName, aggInfo);
											}
										}
										else {
											if (threshold == null) {
												System.err.println("Aggregate Count must have a threshold");
												System.exit(0);
											}
											else {
												aggInfo.getQueries().add(new CountQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
											}												
										}
										break;
									case "avg":
										if ( (aggInfo = aggregatesQueries.get(variableName)) == null) {
											aggInfo = new AggregatesInfo(variableName);
											if (threshold == null) {
												aggInfo.getQueries().add(new AvgQuery(slidingWindowSize));
												aggregatesQueries.put(variableName, aggInfo);
											}
											else {
												aggInfo.getQueries().add(new AvgQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
												aggregatesQueries.put(variableName, aggInfo);
											}
										}
										else {
											if (threshold == null) {
												aggInfo.getQueries().add(new AvgQuery(slidingWindowSize));
											}
											else {
												aggInfo.getQueries().add(new AvgQuery(Float.parseFloat(threshold),slidingWindowSize, thresholdOperator));
											}												
										}
										break;				
								}
							}

							FLAGQueryReady = true;

							break;
						default:
							System.err.println("Parameter : " + parameterName +" does not exist");
					}
					
				}
				
				//run the query
				if (FLAGQueryReady == true) {			
					ThreadInfo threadInfo = new ThreadInfo(aggregatesQueries, slidingWindowSize, updateWindowMinutes, timeStamp, queryId);
									
					if ( counter < numOfQueriesPerThread ) {
						threadData.add(threadInfo);
						counter ++;					
					}
					else if ( threadId < numOfthreads -1) {
						allThreadData.add(threadData);		
						threadData = new ArrayList<ThreadInfo>();
						threadData.add(threadInfo);				
						threadId ++;
						counter = 1;
					}
					else if (threadId == numOfthreads -1){
						threadData.add(threadInfo);
						counter ++;					
					}
					
			    	aggInfo = null;
					FLAGQueryReady = false;
					queryId ++;
					
					aggregatesQueries = new LinkedHashMap<String,AggregatesInfo>();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		allThreadData.add(threadData);
		
		//start the threads
		for ( int  i = 0 ; i < allThreadData.size(); i++) {
			//System.out.println("i = " + i );
			queryExe = new QueryExecution(allThreadData.get(i),variableFilePaths, i); 
			executor.execute(queryExe);
		}
			

				
		 executor.shutdown();  
		
		 //wait until all threads finished
		 while (!executor.isTerminated()) {   }  
		  
	}
}
