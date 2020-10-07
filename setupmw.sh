#!/bin/sh

rm -rf ~/aurora-sizing

sudo yum update -y
sudo yum install git -y
git version

git clone https://github.com/somnath67643/aurora-sizing.git

sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
sudo yum install -y java-1.8.0-devel
#sudo /usr/sbin/alternatives --config java
#sudo /usr/sbin/alternatives --config javac

sudo /usr/sbin/alternatives --set java  /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
sudo /usr/sbin/alternatives --set javac /usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/javac

cd ~/aurora-sizing

wget https://s3.amazonaws.com/rds-downloads/rds-ca-2019-root.pem
openssl x509 -in rds-ca-2019-root.pem -out rds-ca-2019-root.pem.der -outform der
keytool -keystore aurora-postgresql.jks -alias aurora-postgresql -import -file rds-ca-2019-root.pem.der -storepass aurora-postgresql -noprompt
rm rds-ca-2019-root.pem.der

mvn clean formatter:format compile package

nohup java \
-server \
-Xms8g \
-Xmx8g \
-DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DmaximumPoolSize=2000 \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Djavax.net.ssl.trustStore=aurora-postgresql.jks \
-Djavax.net.ssl.trustStorePassword=aurora-postgresql \
-Dport=8080 \
-jar ~/aurora-sizing/target/aurora-sizing-1.0-SNAPSHOT.jar &


