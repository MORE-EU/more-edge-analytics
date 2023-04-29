package com.project.LightWeightAnalytics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QueryExecution implements Runnable{
	ArrayList<ThreadInfo> threadData = null;
	Timestamp timeStamp = null;
	Timestamp timeStamp2 = null;
	int thread_id = -1;


	//Constructor
	QueryExecution(ArrayList<ThreadInfo> threadData, Map <String,String> variableFilePaths, int thread_id){
		this.threadData = threadData;
		this.thread_id = thread_id;
		
		for ( int i = 0 ; i < threadData.size() ; i ++) {
			Map <String,AggregatesInfo> aggregatesQueries = threadData.get(i).getAggregatesQueries();
			RandomAccessFile rAccessFile = null;
			for (String name: aggregatesQueries.keySet()) {
				try {
					rAccessFile = new RandomAccessFile(variableFilePaths.get(name),"r");
					//System.out.println("rAccessFile = " + rAccessFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				aggregatesQueries.get(name).setrAccessFile(rAccessFile);
			}
		}
	}
	
	

	@Override
	public void run() {
		this.executeQuery();
	}


	//query execution
	void executeQuery() {
		int initialIndex = 0;
		boolean running = true;
		ArrayList<Float> bufferRec = null;
		int slidingWindowSize = -1;
		int updateWindowMinutes = -1;
		Map <String,AggregatesInfo> aggregatesQueries = null;
		int endOfProgram = 0;
		
		
		for (int j = 0 ; j < threadData.size(); j++ ) {
			
			ThreadInfo threadInfo = threadData.get(j);
			
			timeStamp = threadInfo.getTimeStamp();
			slidingWindowSize = threadInfo.getSlidingWindowSize();
			updateWindowMinutes = threadInfo.getUpdateWindowMinutes();
			aggregatesQueries = threadInfo.getAggregatesQueries();
			
		
			timeStamp2 = new Timestamp (timeStamp.getNanos());
			timeStamp2.setTime(this.timeStamp.getTime() + TimeUnit.SECONDS.toMillis(updateWindowMinutes));	
			
			for (initialIndex = slidingWindowSize -1; initialIndex >= 0; initialIndex-- ) {
				
				for (String variable: aggregatesQueries.keySet()) {
					AggregatesInfo aggInfo = aggregatesQueries.get(variable);
					bufferRec = getRecordsFromFiles(aggInfo, this.timeStamp, this.timeStamp2);
					
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {
						aggInfo.getQueries().get(i).fillSlidingWindow(initialIndex, bufferRec);
					}
					
				}
				
				timeStamp.setTime(timeStamp2.getTime());
				timeStamp2.setTime(timeStamp.getTime() + TimeUnit.SECONDS.toMillis(updateWindowMinutes));
			}
			threadInfo.setTimeStamp(timeStamp);		
		}
		
		
		while (running) {
			
			for (int j = 0 ; j < threadData.size(); j++ ) {
				
				ThreadInfo threadInfo = threadData.get(j);
				
				timeStamp = threadInfo.getTimeStamp();
				timeStamp2.setTime(timeStamp.getTime() + TimeUnit.SECONDS.toMillis(updateWindowMinutes));
				slidingWindowSize = threadInfo.getSlidingWindowSize();
				updateWindowMinutes = threadInfo.getUpdateWindowMinutes();
				aggregatesQueries = threadInfo.getAggregatesQueries();
				
				AggregatesInfo aggInfo = null;
				int logicAndCounter = 0;
				int numberOfQueries = 0;
				
				for (String variable: aggregatesQueries.keySet()) {

					aggInfo = aggregatesQueries.get(variable);
					numberOfQueries += aggInfo.getQueries().size();
									
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {			
						if (aggInfo.getQueries().get(i).isFLAGThreashold() == false) {
							if ( aggInfo.getQueries().get(i).getOverallValue() != Float.MAX_VALUE && aggInfo.getQueries().get(i).getOverallValue() != -Float.MAX_VALUE  ) {
								logicAndCounter++;
							}
						}
						else {
							float overallValue = 0.0f;
							if ( (overallValue = aggInfo.getQueries().get(i).checkThreshold()) != Float.MAX_VALUE) {
								logicAndCounter ++;
							}
						}
					}
					
				}
				
				if (logicAndCounter == numberOfQueries) {
					System.out.println("Alert: "+this.timeStamp +" - " +this.timeStamp2+"\t query_id = " + threadInfo.getId());
				}
				
				
				//stop if file ends
				try {
					if (aggInfo.getrAccessFile().readLine() == null) {
						endOfProgram ++;
					}
					
					if ( endOfProgram == threadData.size()) {
						return;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
				
				for (String variable: aggregatesQueries.keySet()) {
					aggInfo = aggregatesQueries.get(variable);
					bufferRec = getRecordsFromFiles(aggInfo, this.timeStamp, this.timeStamp2);	
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {					
						aggInfo.getQueries().get(i).run(bufferRec);
					}
					
				}	
				
				timeStamp.setTime(timeStamp2.getTime());//update the timestamps
				threadInfo.setTimeStamp(timeStamp);
			}
		}
	}
	
	//get records for update window
    ArrayList<Float> getRecordsFromFiles(AggregatesInfo aggInfo, Timestamp timeStampStart, Timestamp timeStampEnd){
    	ArrayList<Float> buffer = new ArrayList<Float>();
    	
    	
    	String record = null;
    	long fdTemp = aggInfo.filePointer;

    	try {
    		aggInfo.getrAccessFile().seek(aggInfo.filePointer);
    		
			while ((record = aggInfo.getrAccessFile().readLine()) != null) {
				String[] arrRecord = record.split(",");
				 
				Timestamp timeStampTemp = Timestamp.valueOf(arrRecord[0]);
				
				if (timeStampTemp.compareTo(timeStampEnd) <= 0) {
					fdTemp = aggInfo.getrAccessFile().getFilePointer();
					buffer.add(Float.valueOf(arrRecord[1]));
				}
				else {
					aggInfo.setFilePointer(fdTemp);
					break;
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return buffer;
    }
}
