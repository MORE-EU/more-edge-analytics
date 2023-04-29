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
import java.util.Map;


public class QueryManagerSingleThread { 
	String tmstmp = null; 
	Timestamp timeStamp = null;
	Timestamp timeStamp2 = null;
	int updateWindowMinutes = -1;
	int slidingWindowSize = -1;
	String databaseTableName = null;
	int variablename = -1;	
	LocalDateTime timeStampLocal = null;
	int []threashold = null;
	String[] queryType = null;
	
	Map <String,AggregatesInfo> aggregatesQueries = null;
	Map <String,String> variableFilePaths = null;
	ArrayList<ThreadInfo> data = null;
	
	
	//constructor
	QueryManagerSingleThread(String parameterFilePath){
		
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
    	data = new ArrayList<ThreadInfo>();
    	
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
		boolean FLAGQueryReady = false;
		int queryId = 0;
		
		
		try {
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
					
					switch (parameterName) {
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
				
				if (FLAGQueryReady == true) {
					ThreadInfo threadInfo = new ThreadInfo(aggregatesQueries, slidingWindowSize, updateWindowMinutes, timeStamp, queryId);
					data.add(threadInfo);
			    	aggInfo = null;
			    	queryId ++;
					FLAGQueryReady = false;
					aggregatesQueries = new LinkedHashMap<String,AggregatesInfo>();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
