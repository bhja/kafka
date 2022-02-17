#!/bin/bash

mdkir ~/Downloads
sudo apt update
sudo apt install -y openjdk-17-jre-headless
curl "https://downloads.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz" -o ~/Downloads/kafka.tgz
mkdir ~/kafka && cd ~/kafka
tar -xvzf ~/Downloads/kafka.tgz strip 1



