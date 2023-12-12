package com.project.LightWeightAnalytics;
import java.io.IOException;



public class App {

	public static void main(String[] args) throws IOException {
	
		String parameterFile = args[0];//path of the parameter file
		int numOfthreads = Integer.parseInt(args[1]);//single or multi threaded
		
		//call queryManager
		QueryManager qManager = new QueryManager(parameterFile, numOfthreads);
	}
}
