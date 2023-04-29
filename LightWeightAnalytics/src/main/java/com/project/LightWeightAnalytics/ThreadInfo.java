package com.project.LightWeightAnalytics;
import java.sql.Timestamp;
import java.util.Map;

public class ThreadInfo {
	Timestamp timeStamp = null;
	int updateWindowMinutes = -1;
	int slidingWindowSize = -1;
	Map <String,AggregatesInfo> aggregatesQueries = null;
	int id = -1;
	
	
	//constructor
	ThreadInfo(Map <String,AggregatesInfo> aggregatesQueries, int slidingWindowSize, int updateWindowMinutes, Timestamp timeStamp, int id){
		this.aggregatesQueries = aggregatesQueries;
		this.slidingWindowSize = slidingWindowSize;
		this.updateWindowMinutes = updateWindowMinutes;
		this.timeStamp = timeStamp;
		this.id = id;
	}
	
	
	//getters
	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	
	public int getUpdateWindowMinutes() {
		return updateWindowMinutes;
	}

	
	public int getSlidingWindowSize() {
		return slidingWindowSize;
	}
	
	
	public Map<String, AggregatesInfo> getAggregatesQueries() {
		return aggregatesQueries;
	}

	
	public int getId() {
		return id;
	}


	//setters
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	public void setUpdateWindowMinutes(int updateWindowMinutes) {
		this.updateWindowMinutes = updateWindowMinutes;
	}
	
	
	public void setSlidingWindowSize(int slidingWindowSize) {
		this.slidingWindowSize = slidingWindowSize;
	}
	
	
	public void setAggregatesQueries(Map<String, AggregatesInfo> aggregatesQueries) {
		this.aggregatesQueries = aggregatesQueries;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}
