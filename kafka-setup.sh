#!/bin/bash
#Script for the setup on is4gen.2xlarge
sudo mkdir ~/Downloads

sudo apt update
sudo apt install -y openjdk-17-jre-headless

sudo curl "https://downloads.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz" -o ~/Downloads/kafka.tgz
mkdir ~/kafka && cd ~/kafka
tar -xvzf ~/Downloads/kafka.tgz --strip 1

sudo mkdir /logs
sudo mkfs.ext4 /dev/nvme1n1
sudo mount /dev/nvme1n1 /logs
cd  /logs && sudo mkdir kafka zookeeper
sudo chown -R  `whoami` kafka zookeeper







