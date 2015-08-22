# CSYE7374_Final_Group2

## Deployment Instruction:
### Environment required:
Spark
```shell
$ wget http://www.apache.org/dyn/closer.cgi/spark/spark-1.4.1/spark-1.4.1-bin-hadoop2.6.tgz
$ tar -xvzf spark-1.4.1-bin-hadoop2.6.tgz
$ export SPARK_HOME=...
$ export PATH=$PATH:$SPARK_HOME/bin
```
Kafka
```shell
$ wget https://www.apache.org/dyn/closer.cgi?path=/kafka/0.8.2.1/kafka_2.10-0.8.2.1.tgz
$ tar -xvzf kafka_2.10-0.8.2.1.tgz
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic weather
```
Memsql
```shell
$ wget http://download.memsql.com/memsql-ops-4.0.34/memsql-ops-4.0.34.tar.gz
$ tar -xvzf memsql-ops-4.0.34.tar.gz
$ cd memsql-ops-4.0.34
$ sudo ./install.sh
```
And some basic tools: sbt, maven, pip

### Start Application
DB bootstrap (as example from my config)
```
python ClimateDataSQLLoader.py ec2-52-20-252-81.compute-1.amazonaws.com /home/ubuntu/data/ghcnd_hcn /home/ubuntu/data/ghcnd-stations.txt
```
build java crawler, inside maven console of project CSYE7374_Final_Kafka
```
assembly:assembly
```
start java crawler and kafka producer
```
$ java -cp CSYE7374_Final_Kafka-1.0-jar-with-dependencies.jar ClimateKafkaProducer ec2-52-20-252-81.compute-1.amazonaws.com:3306
```
build spark scala project, inside sbt console of project CSYE7374_Final_Spark
```
assembly
```
start spark scala application
```
$ spark-submit --class ClimateStreaming --master local[4] CSYE7374_Final_Spark-assembly-1.0.jar ec2-52-20-252-81.compute-1.amazonaws.com:3306
```
Start data ingestion daemon:
```
python ClimateScheduler.py
```
Build WAR package of Web UI, inside maven console of project CSYE7374_Final_UI
```
war:war
```
Deploy war package to YOUR tomcat
Access web page UI

### Application Monitoring
Monitor database status:
```
http://ec2-52-20-252-81.compute-1.amazonaws.com:9000
```
Monitor Spark Cluster
```
http://ec2-52-21-30-155.compute-1.amazonaws.com:4040
```
