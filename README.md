# Ticker

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


Steps to run the application:

1. Import project into any editor like Intellij using pom.xml
2. Run App from Intellij Configuration

From command line:

1. Build the project using pom.xml - > mvn clean install
2. Run the application -> java -jar target/assignment-0.0.1-SNAPSHOT.jar

Implementation Details:

1. A dequeue is maintain which uses sliding window algorithm to push and poll ticks based on the timestamps.
2. Statisctics like avg, count are updated with every entry push and entry removal concurrently by reentrant lock
3. For computation of min and max, seperate dequeues are maintained and these are updated with every push and poll ticks.
4. Cleanup is performed every time statistics endpoint is called to delete older/expired entries.
5. Add tick is only performed if it satisfies the timestamp condition and returns 201, else returns 204 staright from the controller.
6. At any point of time, the statistics operation are constant time as we are dealing only with the current sliding window.

