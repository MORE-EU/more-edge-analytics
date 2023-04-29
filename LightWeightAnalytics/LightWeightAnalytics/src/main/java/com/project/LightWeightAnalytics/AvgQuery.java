package com.project.LightWeightAnalytics;
import java.util.ArrayList;

public class AvgQuery implements QueryType{
	float threshold = Float.MAX_VALUE;
	float overallValue = 0.0f;
	float[] slidingWindow = null;
	String aggregateName = "avg";
	boolean FLAGThreashold = false;
	int counter = -1;
	String thresholdOperator = null;
	
	
	//constructor with threshold
	AvgQuery (float threshold, int slidingWindowSize, String thresholdOperator){
		this.threshold = threshold;
		this.slidingWindow = new float[slidingWindowSize];
		this.FLAGThreashold = true;
		this.counter = 0;
		this.thresholdOperator = thresholdOperator;
	}
	
	
	//constructor without threshold
	AvgQuery (int slidingWindowSize){
		this.slidingWindow = new float[slidingWindowSize];
		this.FLAGThreashold = false;
		this.counter = 0;
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
	public boolean isFLAGThreashold() {
		return FLAGThreashold;
	}

	@Override
	public float[] getSlidingWindow() {
		return slidingWindow;
	}
	
	@Override
	public String getAggregateName() {
		return aggregateName;
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
	public void setFLAGThreashold(boolean fLAGThreashold) {
		FLAGThreashold = fLAGThreashold;
	}

	@Override
	public void setSlidingWindow(float[] slidingWindow) {
		this.slidingWindow = slidingWindow;
	}

	@Override
	public void setAggregateName(String aggregateName) {
		this.aggregateName = aggregateName;	
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

	
	//calculate AVG in the arrrayList
	@Override
	public float calculate(ArrayList<Float> q) {
		float avg = 0.0f;
  		int counterInsideWindow = 0;
  		
  		for(float s : q) {
  			if (s!= Float.MAX_VALUE) {
  				avg += s;
  				counterInsideWindow++;
  			}
  		}
  		  		
  		return avg/counterInsideWindow;
	}
	
	
	@Override
	public float calculate(float[] q) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	//fill the sliding window for the first time
	@Override
	public float fillSlidingWindow(int initialIndex, ArrayList<Float> bufferRecords) {
		Float updateWindowAVG = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowAVG = calculate(bufferRecords);//calculate AVG inside the update window
			slidingWindow[initialIndex] = updateWindowAVG ;	//save the update window in the sliding window			
			overallValue += updateWindowAVG; //Update overallAVG
			counter ++;//update the counter of the update windows inside the sliding window
		}
		else {//no records
			slidingWindow[initialIndex] = Float.MAX_VALUE;
		}
			
		return overallValue;
	}
	
	
	//run the AVG query
	@Override
	public float run(ArrayList<Float> bufferRecords) {
		Float updateWindowAVG = 0.0f;
		float delAVG = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowAVG = calculate(bufferRecords);//calculate AVG inside the update window
			delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = updateWindowAVG;//save the update window in the sliding window
				
			
			if (delAVG != Float.MAX_VALUE) {//update overallAVG after the deletion
				overallValue -= delAVG;
			}
			else if (updateWindowAVG != Float.MAX_VALUE) {
				counter++;
			}
			
			overallValue += updateWindowAVG;//update overallAVG after the insertion
			
		}
		else {//no records
			delAVG = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = Float.MAX_VALUE;
			
			if (delAVG != Float.MAX_VALUE) {//update overallAVG after the deletion
				overallValue -= delAVG;
			}
			
			if (counter > 0 ) {//update the counter of the update windows inside the sliding window
				counter --;
			}	
			
		}
		return overallValue;
	}
	

	//check if the comparison is accurate  
	@Override
	public float checkThreshold() {
		float tempAvg = this.overallValue/this.counter;		
		switch (thresholdOperator) {
			case ">":
				if (tempAvg > threshold ) {
					return tempAvg;
				}
				break;
			case "<":
				if (tempAvg < threshold ) {
					return tempAvg;
				}
				break;
			case "=":
				if (tempAvg == threshold ) {
					return tempAvg;
				}
				break;
			case "=>":
				if (tempAvg >= threshold ) {
					return tempAvg;
				}
				break;
			case ">=":
				if (tempAvg >= threshold ) {
					return tempAvg;
				}
				break;
			case "=<":
				if (tempAvg <= threshold ) {
					return tempAvg;
				}
				break;
			case "<=":
				if (tempAvg <= threshold ) {
					return tempAvg;
				}
			break;
		}
		return Float.MAX_VALUE;
	}

}
