package com.project.LightWeightAnalytics;
import java.sql.Timestamp;
import java.util.Map;

public class ThreadInfo {
	int updateWindowMinutes = -1;
	int slidingWindowSize = -1;
	Map <String,AggregatesInfo> aggregatesQueries = null;
	int id = -1;
	int initialIndex = -1;
	
	
	//constructor
	ThreadInfo(Map <String,AggregatesInfo> aggregatesQueries, int slidingWindowSize, int updateWindowMinutes, int id){
		this.aggregatesQueries = aggregatesQueries;
		this.slidingWindowSize = slidingWindowSize;
		this.updateWindowMinutes = updateWindowMinutes;
		this.id = id;
		this.initialIndex = this.slidingWindowSize -1;
	}
	
	
	//getters
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

	public int getInitialIndex() {
		return initialIndex;
	}


	//setters	
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
	
	public void setInitialIndex(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	
}
