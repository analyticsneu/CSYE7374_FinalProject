1) Add install-apache-zeppelin-on-amazon-emr.sh script in custom action of Bootstrap Action step while creating an EMR
	(Script obtained from https://gist.github.com/andershammar/224e1077021d0ea376dd)

2) Tunnelling for accessing Apache Zepplin in EMR

ssh -i <.pem key> -N -L 4040:<EC2 Instance>:4040 -L 8080:<EC2 Instance>:8080 -L 8081:<EC2 Instance>:8081 hadoop@<EMR master Instance>



3) Config to be added to Zeppelin-env.sh

export ZEPPELIN_JAVA_OPTS="-Dspark.executor.memory=12g -Dspark.cores.max=16 -Dspark.akka.frameSize=2047 -Dspark.driver.maxResultSize=6g"
export ZEPPELIN_MEM="-Xmx8096m -XX:MaxPermSize=4096m"

4) Restart Zeppelin