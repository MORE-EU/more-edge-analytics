package com.project.LightWeightAnalytics;
import java.util.ArrayList;

public class CountQuery implements QueryType {
	float threshold = Float.MAX_VALUE;
	float overallValue = 0.0f;
	float[] slidingWindow = null;
	String aggregateName = "count";
	String thresholdOperator = null;
	
	
	//constructor
	CountQuery (float threshold, int slidingWindowSize, String thresholdOperator){
		this.threshold = threshold;
		this.slidingWindow = new float[slidingWindowSize];
		this.thresholdOperator = thresholdOperator;
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
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
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

	
	//calculate COUNT in the arrrayList
	@Override
	public float calculate(ArrayList<Float> q) {		
		int counter = 0;
  		
  		for(Float s : q) {
  			if (s!= Float.MAX_VALUE) {
  				switch (thresholdOperator) {
	  				case ">":
	  					if (s > threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case "<":
	  					if (s < threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case "=":
	  					if (s == threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case "=>":
	  					if (s >= threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case ">=":
	  					if (s >= threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case "=<":
	  					if (s <= threshold ) {
	  						counter++;
	  					}
	  					break;
	  				case "<=":
	  					if (s <= threshold ) {
	  						counter++;
	  					}
	  				break;
	  			}
  			}
  		}	
  		return counter;
	}

	
	@Override
	public float calculate(float[] q) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	//fill the sliding window for the first time
	@Override
	public float fillSlidingWindow(int initialIndex, ArrayList<Float> bufferRecords) {
		float updateWindowCOUNT = 0.0f;
		
		if (bufferRecords.size() > 0 ) {//we have records
			
			updateWindowCOUNT = calculate(bufferRecords);//calculate COUNT inside the update window
			slidingWindow[initialIndex] = updateWindowCOUNT ;//save the update window in the sliding window
			overallValue += updateWindowCOUNT;// update the overallCOUNT
			
		}
		else {//no records
			slidingWindow[initialIndex] = Float.MAX_VALUE;
		}
		
		return overallValue;
	}

	
	//run the COUNT query
	@Override
	public float run(ArrayList<Float> bufferRecords) {
		float updateWindowCOUNT = 0.0f;
		float delCOUNT = 0.0f;
		
		if (bufferRecords.size() > 0) {//we have records
			
			updateWindowCOUNT = calculate(bufferRecords);//calculate COUNT inside the update window
			delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = updateWindowCOUNT;//save the update window in the sliding window
			
			if (delCOUNT != Float.MAX_VALUE) {//update overallCOUNT after the deletion
				overallValue -= delCOUNT;
			}
			
			overallValue += updateWindowCOUNT;	//update overallCOUNT after the insertion
			
		}
		else {//no records
			delCOUNT = shiftRightWindow(slidingWindow);//shift the sliding window right for one position
			slidingWindow[0] = Float.MAX_VALUE;
			
			if (delCOUNT != Float.MAX_VALUE) {//update overallCOUNT after the deletion
				overallValue -= delCOUNT;
			}
		}
		
		return overallValue;
	}
	

	//COUNT query does not have threshold
	@Override
	public float checkThreshold() {
		// TODO Auto-generated method stub
		return this.overallValue;
	}

}
