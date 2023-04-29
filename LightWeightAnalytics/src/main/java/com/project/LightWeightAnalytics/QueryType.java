package com.project.LightWeightAnalytics;
import java.util.ArrayList;


public interface QueryType {
	
	//getters
	float getThreshold();
	float getOverallValue();
	float[] getSlidingWindow();
	String getAggregateName();
	boolean isFLAGThreashold();
	void setFLAGThreashold(boolean fLAGThreashold);


	//setters
	void setThreshold(float threshold);
	void setOverallValue(float overallValue);
	void setSlidingWindow(float[] slidingWindow);
	void setAggregateName(String aggregateName);
	
	//shift the sliding window to the right
	float shiftRightWindow(float[] slidingWindow);
	
	//calculate the aggregate in the arrrayList
	float calculate(ArrayList<Float> q);
	
	//calculate the aggregate in the sliding window
	float calculate(float[] q);
	
	//fill the sliding window for the first time
	float fillSlidingWindow(int initialIndex, ArrayList<Float> bufferRecords);
	
	//run the aggregate
	float run(ArrayList<Float> bufferRecords);
	
	//check if the comparison is accurate
	float checkThreshold();
}
