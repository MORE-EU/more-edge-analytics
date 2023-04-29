## more-edge-analytics
(Initial integrated version of the Platform)

### **Light Weight Analytics**

 
**1. Installation**


&emsp; For the installation of the module both Java and maven are required. The light weight analytics module is available 
in the MORE GitHub repo. To use them, the user needs to clone the respective GitHub repository and switch to the 
specific branch: 

* git clone https://github.com/MORE-EU/more-edge-analytics.git  
* go to folder LightWeightAnalytics 
---           
            
            
**2. Parameter file**:

&emsp; User can set the arguments for our techniques via parameter file. The first line should contain the number of the queries (numOfqueries). With more details, the parameters are:
        
* **updatedwindowminute** : this variable defines the update frequency of the update window in minutes 
            
* **slidingWindowSize** : this variable defines the size of the sliding window 
            
* **executionQuery**: This variable defines the execution query. Depending user choice about having or not a threshold, we have two different templates to write a query: variavbleName,operator,comparison,threshold or variavbleName,operator. For example, the query that finds the minimum values of rotor speed that are smaller than 50, it will be written: rotor speed,min,<,200. If we have the same query without the threshold, the query will be rotor speed, min. In case of conjunctive queries, we separate the queries using ‘&&’ as a separator. For example, the query that finds the minimum value of rotor speed is bigger than 20 and the average wind speed is smaller than 50, it will be written: rotor speed,min,<,200 && wind speed,avg,<,50.
 
 There is an example of how the parameter file should be formatted within the project.
 
 ---
 
 **3. Run**:

&emsp; After downloading the source code and have prepared your parameter file, you are ready to execute the code. The module is a maven java project, so the user must navigate to the folder more-edge-analytics/LightWeigtAnalytics to use the maven commands. Maven command for module execution: 

* **mvn install** :This command will compile, test and package your Java project and even install your built .jar file into your local Maven repository. 
* **java -jar target/LightWeightAnalytics-final.jar path of the parameter and file the number of threads (with space between them)** : Command for the execution
* ** mvn clean** : it deletes all previously compiled Java .class files and resources (like. properties) in your project. Your build will start from a clean slate. 
