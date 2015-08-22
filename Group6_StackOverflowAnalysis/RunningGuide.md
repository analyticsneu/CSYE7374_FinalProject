# Running Document 

## Part 1:  Real Time Analysis. 
###1. Run Web Crawler. 
    1. Navigate to crawler project.
    2. pip install scrapy
    3. download and start Mongodb.
    4. scrapy crawler stack.

###2. Run Front end server and kafka producer.
    1.go to bigdataanylytic project, mvn package.
    2.java -jar "name of generated jar file"
    
###3. Start Kafka Broker.
     bin/start-kafka.

###4. Start spark streaming processer.
     bin/spark-submit --jars lib/spark-streaming-kafka-assembly_2.10-1.4.0.jar examples/src/main/python/streaming/kafka_wordDetector.py localhost:2181 topic1

 <br />
##Part 2: OLAP
1. Download and start HBase.
2. Start final project using java -jar command.
 <br />

##Part 3: Benchmark
1. Run one of script we have uploaded.
2. Start Timer to count the elapse.
