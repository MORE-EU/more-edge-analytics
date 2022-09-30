package com.project.LightWeightAnalytics;


public class App {
	public static void main(String[] args) {
    	    
		String dataBaseTableName = args[0];
    
		QueryManager qManager = new QueryManager(dataBaseTableName);
		qManager.executeQuery();
    
	}
}
