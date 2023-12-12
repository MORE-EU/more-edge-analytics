package com.project.LightWeightAnalytics;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.protobuf.InvalidProtocolBufferException;

import proto.CsvRow;
import proto.Message;


public class QueryExecution implements Runnable{
	ArrayList<ThreadInfo> threadData = null;
	int thread_id = -1;
	MqttClient client = null;
	//String broker_address = "tcp://broker.hivemq.com:1883";
	String broker_address = "tcp://172.17.0.7:1883";
	int qos = 1;
	String topic = "/mqttstreamer/testtopic";
	Map <String,Integer> variableIndex = null;
	

	//Constructor
	QueryExecution(ArrayList<ThreadInfo> threadData, Map <String,Integer> variableIndex, int thread_id){
		this.threadData = threadData;
		this.thread_id = thread_id;
		this.variableIndex = variableIndex;
	}
	
	
	@Override
	public void run() {
		getDataFromMQTT();
	}

	
	void getDataFromMQTT() {
		
		String publisherId = UUID.randomUUID().toString();
		try {
			
			//create connection with MQTT client
			client = new MqttClient(broker_address, publisherId);
			MqttConnectOptions options = new MqttConnectOptions();
			
			// setup callback
			client.setCallback(new MqttCallback() {

				public void connectionLost(Throwable cause) {
					System.out.println("connectionLost: " + cause.getMessage());
				}

				public void messageArrived(String topic, MqttMessage message) {
					try {
						
						//get the messsage
						Message records = Message.parseFrom(message.getPayload());
											
						//get the values
						List<CsvRow> recRows = records.getCsvRowsList();
						List<Float> recordsFloat = new ArrayList<Float>();
						
						//trasform values from double to float
						for (int i = 0 ; i < recRows.size() ; i ++) {
							List<Double> rec = recRows.get(i).getValuesList();
							for (Double d : rec) {
								recordsFloat.add(d.floatValue());
							}
						}

						//put the data to the sliding windows	
						executeQuery(recordsFloat);
							
						
					} catch (InvalidProtocolBufferException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}        
				}

				public void deliveryComplete(IMqttDeliveryToken token) {
					System.out.println("deliveryComplete---------" + token.isComplete());
				}

			});
			client.connect(options);
			client.subscribe(topic, qos);	           
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	//query execution
	void executeQuery(List<Float> records) {
		int initialIndex = 0;
		ArrayList<Float> bufferRec = null;
		int slidingWindowSize = -1;
		int updateWindowMinutes = -1;
		Map <String,AggregatesInfo> aggregatesQueries = null;						
		
		//execute sequential the queries
		for (int j = 0 ; j < threadData.size(); j++ ) {
			
			ThreadInfo threadInfo = threadData.get(j);
			
			//get important info about the query
			slidingWindowSize = threadInfo.getSlidingWindowSize();
			updateWindowMinutes = threadInfo.getUpdateWindowMinutes();
			aggregatesQueries = threadInfo.getAggregatesQueries();
			initialIndex = threadInfo.getInitialIndex();
				
			if (initialIndex >= 0) { // fill sliding window
				
				//update sliding window per variable queries
				for (String variable: aggregatesQueries.keySet()) {
					bufferRec = new ArrayList<Float>();

					//System.out.println("variable = " + variable + "\t thread id = " + thread_id);
					if (records.size() == variableIndex.size()) { //if sends only one row
						bufferRec.add(records.get(variableIndex.get(variable)));
					}
					else { // if sends more than one row
						for (int i = variableIndex.get(variable) ; i < records.size() ; i = i + variableIndex.size() ) {
							bufferRec.add(records.get(i));	
						}
					}
								
					//fill the sliding window to all queries, which are related to a variable 
					AggregatesInfo aggInfo = aggregatesQueries.get(variable);
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {
						aggInfo.getQueries().get(i).fillSlidingWindow(initialIndex, bufferRec);
					}
				}
				
				threadInfo.setInitialIndex(initialIndex-1);

			}
			else {
							
				AggregatesInfo aggInfo = null;
				int logicAndCounter = 0;
				int numberOfQueries = 0;
				
				//check the correctness of the query
				for (String variable: aggregatesQueries.keySet()) {

					aggInfo = aggregatesQueries.get(variable);
					numberOfQueries += aggInfo.getQueries().size();
									
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {			
						if (aggInfo.getQueries().get(i).isFLAGThreashold() == false) {
							if ( aggInfo.getQueries().get(i).getOverallValue() != Float.MAX_VALUE && aggInfo.getQueries().get(i).getOverallValue() != -Float.MAX_VALUE  ) {
								logicAndCounter++;
							}
						}
						else {
							float overallValue = 0.0f;
							if ( (overallValue = aggInfo.getQueries().get(i).checkThreshold()) != Float.MAX_VALUE) {
								logicAndCounter ++;
							}
						}
					}		
				}
				
				//print the alarm
				if (logicAndCounter == numberOfQueries) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
					LocalDateTime now = LocalDateTime.now();  

					String alert = "Alert: " + dtf.format(now) + "\t query_id = " + threadInfo.getId(); 
					
					for (String variable: aggregatesQueries.keySet()) {
						aggInfo = aggregatesQueries.get(variable);
						alert = alert + ("\t" + variable +" = "+ + aggInfo.getQueries().get(0).getOverallValue())  ;
					}
					System.out.println(alert);
				}
				
				//update sliding window per variable queries
				for (String variable: aggregatesQueries.keySet()) {
					bufferRec = new ArrayList<Float>();

					if (records.size() == variableIndex.size()) { //if sends only one row
						bufferRec.add(records.get(variableIndex.get(variable)));
					}
					else { // if sends more than one row
						for (int i = variableIndex.get(variable) ; i < records.size() ; i = i + variableIndex.size()  ) {
							bufferRec.add(records.get(i));	
						}
					}
					
					//fill the sliding window to all queries, which are related to a variable 
					aggInfo = aggregatesQueries.get(variable);
					for (int i = 0 ; i < aggInfo.getQueries().size() ; i++ ) {
						aggInfo.getQueries().get(i).run(bufferRec);
					}
				}	
			}		
		}	
	}
}
