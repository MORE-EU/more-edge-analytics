package com.project.LightWeightAnalytics;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class AggregatesInfo {
	RandomAccessFile rAccessFile = null;
	String variableName = null;
	long filePointer = 0;
	ArrayList<QueryType> queries = null;
	
	
	AggregatesInfo(String variableName){
		this.variableName = variableName;
		queries = new ArrayList<QueryType>();
	}
	
	//getters
	public RandomAccessFile getrAccessFile() {
		return rAccessFile;
	}
	
	public long getFilePointer() {
		return filePointer;
	}
	
	public ArrayList<QueryType> getQueries() {
		return queries;
	}
	
	
	public String getVariableName() {
		return variableName;
	}


	//setters
	public void setrAccessFile(RandomAccessFile rAccessFile) {
		this.rAccessFile = rAccessFile;
	}
	
	public void setFilePointer(long filePointer) {
		this.filePointer = filePointer;
	}
	

	public void setQueries(ArrayList<QueryType> queries) {
		this.queries = queries;
	}	
	
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
}
