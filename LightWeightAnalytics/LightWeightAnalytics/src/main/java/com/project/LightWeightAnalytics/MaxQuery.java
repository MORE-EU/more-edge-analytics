package com.project.LightWeightAnalytics;
import java.util.ArrayList;

public class MaxQuery implements QueryType {
	float threshold = Float.MAX_VALUE;
	float overallValue = -Float.MAX_VALUE;
	float[] slidingWindow = null;
	String aggregateName = "max";
	boolean FLAGThreashold = false;
	String thresholdOperator = null;
	
	
	//constructor with threshold
	MaxQuery (float threshold, int slidingWindowSize, String thresholdOperator){
		this.threshold = threshold;
		this.slidingWindow = new float[slidingWindowSize];
		this.FLAGThreashold = true;
		this.thresholdOperator = thresholdOperator;
	}
	
	
	//constructor without threshold
	MaxQuery (int slidingWindowSize){
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
	
	@Override
	public boolean isFLAGThreashold() {
		return FLAGThreashold;
	}
	
	
	//setters
	@Override
	public void setThreshold(float threshold) {
		this.threshold = threshold;
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

	@Override
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

	
	//calculate MAX in the arrrayList
	@Override
	public float calculate(ArrayList<Float> q) {
		float MAX = -Float.MAX_VALUE;
		
		for( float s : q) {
			if ( s != -Float.MAX_VALUE ) {
				//System.out.println("Max = " + MAX + " < " + "s = " + s);
				if ( MAX < s) {
					//System.out.println("edwwwwwwwwww");
					MAX = s;
				}	
			}
		}
		
		return MAX;
	}

	
	//calculate MAX in the sliding window
	@Override
	public float calculate(float[] q) {
		float MAX = -Float.MAX_VALUE;
		
		for(float s : q) {
			if ( s != -Float.MAX_VALUE ) {
				if ( MAX < s) {
					MAX = s;
				}	
			}
		}
		return MAX;
	}

	
	//fill the sliding window for the first time
	@Override
	public float fillSlidingWindow(int initialIndex, ArrayList<Float> bufferRecords) {
		Float updateWindowMAX = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowMAX = calculate(bufferRecords);//calculate MIN inside the update window
			slidingWindow[initialIndex] = updateWindowMAX ;//save the update window in the sliding window
			

			
			if (overallValue < updateWindowMAX) {//check and update the overallMAX
				overallValue = updateWindowMAX;
			}
			
		}
		else {
			slidingWindow[initialIndex] = -Float.MAX_VALUE;;
		}
		
		return overallValue;
	}

	
	//run the MAX query
	@Override
	public float run(ArrayList<Float> bufferRecords) {
		Float updateWindowMAX = 0.0f;
		float delMAX = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowMAX = calculate(bufferRecords);//calculate MIN inside the update window
			delMAX = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = updateWindowMAX;//save the update window in the sliding window
			
			if (overallValue < updateWindowMAX) {//check and update the overallMAX
				overallValue = updateWindowMAX;
			}
			else {
				if (delMAX == overallValue) {
					overallValue = calculate(slidingWindow);
				}
			}			
		}
		else {
			delMAX = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = -Float.MAX_VALUE;
			
			if (delMAX == overallValue) {//check and update overallMAX
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
