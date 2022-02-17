#!/bin/bash
for i in {1..10}
do
name="poc-$i"
partition=30
echo "Creating $name"
./bin/kafka-topics --bootstrap-server localhost:9092 --create --topic $name --replication-factor 2 --partitions $partition --config  min.insync.replicas=2
done
echo "Done creating"