## more-edge-analytics

### **Light Weight Analytics**

 
**1. Installation**


&emsp; For the installation of the module both Java and maven are required. The light weight analytics module is available 
in the MORE GitHub repo. To use them, the user needs to clone the respective GitHub repository and switch to the 
specific branch: 

* git clone https://github.com/MORE-EU/more-edge-analytics.git  
* go to folder LightWeightAnalytics 
---           
            
            
**2. Parameter file**:

&emsp; User can set the arguments for our techniques via parameter file. With more details, the parameters are:
        
* **updatedwindowminute** : this variable defines the update frequency of the update window in minutes 
            
* **slidingWindowSize** : this variable defines the size of the sliding window 
            
* **timestamp** : this variable is responsible about the value of the timestamp, corresponding to the start time 
            of the query 
            
* **databaseTableName**: this variable defines the name of the table in the ModelarDB, that the data are stored 
            
* **variableName**:  this variable defines the name of the attribute, where the aggregate functions will be applied 
            
* **countThreashold**: this variable defines the threshold for the COUNT query
            
* **executionQuery**: This variable defines the type of the execution query. The choices are min, max, avg and coun
 ---
 
 **3. Run**:

&emsp; After downloading the source code and have prepared your parameter file, you are ready to execute the code. The module is a maven java project, so the user must navigate to the folder more-edge-analytics/LightWeigtAnalytics to use the maven commands. Maven command for module execution: 

* **mvn compile** : It compiles the source code, converts the .java files to .class and stores the classes in target/classes folder. It is necessary to execute this command to continue the process.
* **mvn install** :This step installs the packaged code to the local Maven repository. This command is optional.
* **mvn exec:java -Dexec.mainClass=com.project.LightWeightAnalytics.App -Dexec.args="path of the parameter file"** : Command for the execution
