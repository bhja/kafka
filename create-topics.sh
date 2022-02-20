#!/bin/bash
for i in {1..10}
do
name="test-$i"
partition=30
echo "Creating $name"
./bin/kafka-topics.sh --bootstrap-server ec2-54-159-82-133.compute-1.amazonaws.com:9092 --create --topic $name --replication-factor 2 --partitions $partition --config  min.insync.replicas=2


done
echo "Done creating"
