package com.project.LightWeightAnalytics;
import java.io.IOException;


public class App {

	public static void main(String[] args) throws IOException {
	
		
		String parameterFile = args[0];//path of the parameter file
		int numOfthreads = Integer.parseInt(args[1]);//single or multi threaded
		
		//single thread		
		if (numOfthreads == 0 || numOfthreads == 1 ) {
			QueryManagerSingleThread qManager = new QueryManagerSingleThread(parameterFile);	
		}
		//multi thread
		else {
			QueryManager qManager = new QueryManager(parameterFile, numOfthreads);
		}	
	}
}
