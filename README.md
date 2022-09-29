# more-edge-analytics

### * Light Weight Analytics
####    &emsp;&emsp;  * Installation
        For the installation of the module both Java and maven are required. The light weight analytics module is available 
        in the MORE GitHub repo. To use them, the user needs to clone the respective GitHub repository and switch to the 
        specific branch: 
        
            * git clone [Github](https://github.com/MORE-EU/more-edge-analytics.git)   
        
            * go to folder LightWeightAnalytics 
            
####    &emsp;&emsp;  * Parameter file:
        User can set the arguments for our techniques via parameter file. With more details, the parameters are:
        
            -  **updateWindowMinutes** : this variable defines the update frequency of the update window in minutes 
            
            * slidingWindowSize: this variable defines the size of the sliding window 
            
            * timestamp: this variable is responsible about the value of the timestamp, corresponding to the start time 
            of the query 
            
            * databaseTableName: this variable defines the name of the table in the ModelarDB, that the data are stored 
            
            * variableName:  this variable defines the name of the attribute, where the aggregate functions will be applied 
            
            * countThreashold: this variable defines the threshold for the COUNT query
            
            * executionQuery: This variable defines the type of the execution query. The choices are min, max, avg and count

  
