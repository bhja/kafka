
## STEPS FOR SETUP ON E2.

Update the server.properties with the broker and zookeeper IP address as configured
Update the zookeeper.properties with the servers in the cluster.
Create a file with only the broker.id value in the path of dataDir. In the one here that path is 
/logs/zookeeper

Example -> echo 30 > /logs/zookeeper/myid. 

Login to the instance and run kafka-setup.sh 

Move server.properties and zookeeper.properties to the ~/kafka/config/
Create the myid file in the dataDir path as explained above. 

Start zookeeper 
~/kafka/bin/zookeeper-server-start.sh -daemon ~/kafka/config/zookeeper.properties

Start kakfka broker

~/kafka/bin/kafka-server-start.sh -daemon ~/kafka/config/server.properties


Run the maven build and start the spring boot app. 

Submit a request to run the producer. 
REQUEST JSON 
{
"logIt": false,
"maxSeconds":30,
"topic":"test"
}

Set the type to "parallel" or "bounded" to run it in multi threaded context.

curl -X POST -H"Content-Type: application/json;charset=UTF-8" -H"Accept: application/json;charset=UTF-8" --data @./single.json "http://3.81.233.27:8080/producer"