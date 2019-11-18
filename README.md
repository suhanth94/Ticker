# Ticker

# Running Instructions

This assignment implement the below three API's:

POST /ticks

<Sample Input>
  
{
"instrument": "JPM.CC",
"price": 115.82,
"timestamp": 1574093319000
}

Make sure the timestamp is always entered in milliseconds.

GET /statistics

GET /statistics/{instrument_identifier} 


Steps to run the application from IDE:

1. Import project into any IDE like Intellij using pom.xml
2. Run App from Intellij Configuration straightaway
3. (Or) Go to SolactiveAssignmentApplication File -> Right Click -> Run

From command line:

1. Build the project using pom.xml - > mvn clean install
2. Run the application -> java -jar target/assignment-0.0.1-SNAPSHOT.jar

Once the app is run, use any REST client like POSTMAN for testing the above API's

# Implementation Details

1. A dequeue is maintain which uses sliding window algorithm to push and poll ticks based on the timestamps.
2. Statisctics like avg, count are updated with every entry push and entry removal concurrently by reentrant lock
3. For computation of min and max, seperate dequeues are maintained and these are updated with every push and poll ticks.
4. Cleanup is performed every time statistics endpoint is called to delete older/expired entries.
5. Add tick is only performed if it satisfies the timestamp condition and returns 201, else returns 204 staright from the controller.
6. At any point of time, the statistics operation are constant time as we are dealing only with the current sliding window.

# Improvements 

1. For now, for the third API which is about retreiving instrument specific statistics, it can improved by maintaining a map of dequeues like did for the global dequeue which would be much more quicker in time complexity, assuming that we are okay with having more space complexity.

# Overall

This challenge was very interesting, exiciting and thought provoking in terms of choosing which data structures would suit the best and be optimal. Overall, it was fun and great learning. 

