package com.project.LightWeightAnalytics;
import java.util.ArrayList;

public class MinQuery implements QueryType{
	float threshold = Float.MAX_VALUE;
	float overallValue = Float.MAX_VALUE;
	float[] slidingWindow = null;
	String aggregateName = "min";
	boolean FLAGThreashold = false;
	String thresholdOperator = null;
	
	
	//constructor with threshold
	MinQuery (float threshold, int slidingWindowSize, String thresholdOperator){
		this.threshold = threshold;
		this.slidingWindow = new float[slidingWindowSize];
		this.FLAGThreashold = true;
		this.thresholdOperator = thresholdOperator;
	}
	
	
	//constructor without threshold
	MinQuery (int slidingWindowSize){
		this.slidingWindow = new float[slidingWindowSize];
		this.FLAGThreashold = false;
	}
	

	//getters
	@Override
	public float getThreshold() {
		return threshold;
	}

	@Override
	public float getOverallValue() {
		return overallValue;
	}
	
	@Override
	public float[] getSlidingWindow() {
		return slidingWindow;
	}
	
	@Override
	public String getAggregateName() {
		return aggregateName;
	}
	
	public boolean isFLAGThreashold() {
		return FLAGThreashold;
	}


	//setters
	@Override
	public void setThreshold(float threashold) {
		this.threshold = threashold;
	}

	@Override
	public void setOverallValue(float overallValue) {
		this.overallValue = overallValue;
	}

	@Override
	public void setSlidingWindow(float[] slidingWindow) {
		this.slidingWindow = slidingWindow;
	}
	
	@Override
	public void setAggregateName(String aggregateName) {
		this.aggregateName = aggregateName;
	}
	
	public void setFLAGThreashold(boolean fLAGThreashold) {
		FLAGThreashold = fLAGThreashold;
	}


	//shift the sliding window to the right
	@Override
	public float shiftRightWindow(float[] slidingWindow) {
  		float delMin ;		
  		delMin = slidingWindow[slidingWindow.length-1];

  		for (int i = slidingWindow.length -2 ; i >= 0 ; i-- ) {
  			slidingWindow[i+1] = slidingWindow [i];
  		}
  		
  		slidingWindow[0] = Float.MAX_VALUE;

  		return delMin;
  	}
	

	//calculate MIN in the arrrayList
	@Override
	public float calculate (ArrayList<Float> q) {
  		float MIN = Float.MAX_VALUE;
  		
  		for(float s : q) {
  			if ( s != Float.MAX_VALUE ) {
  				if ( MIN > s) {
  					MIN = s;
  				}	
  			}
  		}
  		
  		return MIN;
  	}

	
	//calculate MIN in the sliding windows
	@Override
	public float calculate(float[] q) {
  		float MIN = Float.MAX_VALUE;
  		
  		for(float s : q) {
  			if ( s != Float.MAX_VALUE ) {
  				if ( MIN > s) {
  					MIN = s;
  				}	
  			}
  		}
  		return MIN;
  	}

	
	//fill the sliding window for the first time
	@Override
	public float fillSlidingWindow(int initialIndex, ArrayList<Float> bufferRecords) {
		Float updateWindowMIN = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records

			updateWindowMIN = calculate(bufferRecords);//calculate MIN inside the update window
			slidingWindow[initialIndex] = updateWindowMIN ;//save the update window in the sliding window
				
			if (overallValue > updateWindowMIN) {//check and update the overallMIN
				overallValue = updateWindowMIN;
			}
			
		}
		else {
			slidingWindow[initialIndex] = Float.MAX_VALUE;
		}
		
		return overallValue;	
	}
	

	//run the MIN query
	@Override
	public float run(ArrayList<Float> bufferRecords) {
		Float updateWindowMIN = 0.0f;
		float delMIN = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowMIN = calculate(bufferRecords);//calculate MIN inside the update window
			delMIN = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = updateWindowMIN;//save the update window in the sliding window
			
			if (overallValue > updateWindowMIN) {//check and update the overallMIN
				overallValue = updateWindowMIN;
			}
			else {
				if (delMIN == overallValue) {
					overallValue = calculate(slidingWindow);
				}
			}
			
		}
		else {
			delMIN = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = Float.MAX_VALUE;
			
			if (delMIN == overallValue) {//check and update overallMIN
				overallValue = calculate(slidingWindow);
			}	
		}

		return overallValue;
	}


	//check if the comparison is accurate
	@Override
	public float checkThreshold() {
		
		switch (thresholdOperator) {
			case ">":
				if (overallValue > threshold ) {
					return this.overallValue;
				}
				break;
			case "<":
				if (overallValue < threshold ) {
					return this.overallValue;
				}
				break;
			case "=":
				if (overallValue == threshold ) {
					return this.overallValue;
				}
				break;
			case "=>":
				if (overallValue >= threshold ) {
					return this.overallValue;
				}
				break;
			case ">=":
				if (overallValue >= threshold ) {
					return this.overallValue;
				}
				break;
			case "=<":
				if (overallValue <= threshold ) {
					return this.overallValue;
				}
				break;
			case "<=":
				if (overallValue <= threshold ) {
					return this.overallValue;
				}
				break;
		}
			
		return Float.MAX_VALUE;
	}
}
