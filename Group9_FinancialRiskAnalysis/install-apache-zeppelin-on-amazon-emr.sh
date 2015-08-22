#!/bin/bash -ex

# Install Git
sudo yum -y install git

# Install Maven
wget -P /tmp http://apache.mirrors.spacedump.net/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
sudo mkdir /opt/apache-maven
sudo tar -xvzf /tmp/apache-maven-3.3.3-bin.tar.gz -C /opt/apache-maven

cat <<EOF >> /home/hadoop/.bashrc

# Maven
export MAVEN_HOME=/opt/apache-maven/apache-maven-3.3.3
export PATH=\$MAVEN_HOME/bin:\$PATH
EOF

source /home/hadoop/.bashrc

# Install Zeppelin
git clone https://github.com/apache/incubator-zeppelin.git /home/hadoop/zeppelin
cd /home/hadoop/zeppelin
mvn clean package -Pspark-1.3 -Dhadoop.version=2.4.0 -Phadoop-2.4 -Pyarn -DskipTests

# Configure Zeppelin
SPARK_DEFAULTS=/home/hadoop/spark/conf/spark-defaults.conf

declare -a ZEPPELIN_JAVA_OPTS
if [ -f $SPARK_DEFAULTS ]; then
    ZEPPELIN_JAVA_OPTS=("${ZEPPELIN_JAVA_OPTS[@]}" \
        $(grep spark.executor.instances $SPARK_DEFAULTS | awk '{print "-D" $1 "=" $2}'))
    ZEPPELIN_JAVA_OPTS=("${ZEPPELIN_JAVA_OPTS[@]}" \
        $(grep spark.executor.cores $SPARK_DEFAULTS | awk '{print "-D" $1 "=" $2}'))
    ZEPPELIN_JAVA_OPTS=("${ZEPPELIN_JAVA_OPTS[@]}" \
        $(grep spark.executor.memory $SPARK_DEFAULTS | awk '{print "-D" $1 "=" $2}'))
    ZEPPELIN_JAVA_OPTS=("${ZEPPELIN_JAVA_OPTS[@]}" \
        $(grep spark.default.parallelism $SPARK_DEFAULTS | awk '{print "-D" $1 "=" $2}'))
fi
echo "${ZEPPELIN_JAVA_OPTS[@]}"

cp conf/zeppelin-env.sh.template conf/zeppelin-env.sh
cat <<EOF >> conf/zeppelin-env.sh

export MASTER=yarn-client
export HADOOP_CONF_DIR=$HADOOP_CONF_DIR
export ZEPPELIN_SPARK_USEHIVECONTEXT=false
export ZEPPELIN_JAVA_OPTS="${ZEPPELIN_JAVA_OPTS[@]}"
EOF

cat <<'EOF' > 0001-Add-Amazon-EMR-jars-to-Zeppelin-classpath.patch
From 5bad22dd3681305f081233cbecea5a55bf3dcc7d Mon Sep 17 00:00:00 2001
From: Anders Hammar <anders.hammar@gmail.com>
Date: Wed, 24 Jun 2015 15:09:02 +0200
Subject: [PATCH] Add Amazon EMR jars to Zeppelin classpath

---
 bin/common.sh | 2 ++
 1 file changed, 2 insertions(+)

diff --git a/bin/common.sh b/bin/common.sh
index 8087e9d..69e09d4 100644
--- a/bin/common.sh
+++ b/bin/common.sh
@@ -86,6 +86,8 @@ function addJarInDir(){
 
 if [[ ! -z "${SPARK_HOME}" ]] && [[ -d "${SPARK_HOME}" ]]; then
   addJarInDir "${SPARK_HOME}"
+  addJarInDir "${SPARK_HOME}/classpath/emr"
+  addJarInDir "${SPARK_HOME}/classpath/emrfs"
 fi
 
 if [[ ! -z "${HADOOP_HOME}" ]] && [[ -d "${HADOOP_HOME}" ]]; then
-- 
1.8.2.2

EOF
git config user.email "you@example.com"
git config user.name "Your Name"
git am 0001-Add-Amazon-EMR-jars-to-Zeppelin-classpath.patch

# Start the Zeppelin daemon
bin/zeppelin-daemon.sh start