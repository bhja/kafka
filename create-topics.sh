#!/bin/bash
for i in {1..10}
do
name="poc-$i"
partition=30
echo "Creating $name"
./bin/kafka-topics.sh --bootstrap-server ec2-34-207-137-113.compute-1.amazonaws.com:9092 --create --topic $name --replication-factor 2 --partitions $partition --config  min.insync.replicas=2
done
echo "Done creating"
