# aurora-sizing

Install maven and jdk 8
https://docs.aws.amazon.com/neptune/latest/userguide/iam-auth-connect-prerq.html

Compile
mvn clean formatter:format compile package

For Creating / Recreating the Schema
mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dexec.mainClass="org.anonymous.boot.SchemaUtil"

Run the DB Client
mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dexec.mainClass="org.anonymous.boot.Program"

 ## ------  OR -------
 java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-jar aurora-cli-1.0-SNAPSHOT.jar

## --- Run in H2 Mode
mvn exec:java -Dexec.mainClass="org.anonymous.boot.Program"

Run the GRPC Server
mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dport=8080 \
-Dexec.mainClass="org.anonymous.server.GrpcServer"

Run the GRPC Client
mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dhost=localhost \
-Dport=8080 \
-Dexec.mainClass="org.anonymous.client.Client"



For Github Upload
- sudo yum -y update      # Install the latest system updates.
- sudo yum -y install git # Install Git.
- git --version           # Confirm Git was installed.

- git config --global user.name "USER_NAME"
- git config --global user.email EMAIL_ADDRESS

- git clone https://github.com/somnath67643/aurora-sizing.git OR git pull
- cd aurora-sizing

- git add --all # not required

- git commit -m "Initial Commit"
- git push
- 

SSL
 //Download the Combined Root and Intermidiate certificates
wget https://s3.amazonaws.com/rds-downloads/rds-ca-2019-root.pem

// Convert to .der format
openssl x509 -in rds-ca-2019-root.pem -out rds-ca-2019-root.pem.der -outform der

//create a new trust store
keytool -keystore aurora-postgresql.jks -alias aurora-postgresql -import -file rds-ca-2019-root.pem.der -storepass aurora-postgresql -noprompt

//Build and Intall psql in Amz. Linux 2018.3
sudo yum install -y gcc readline-devel zlib-devel
wget https://ftp.postgresql.org/pub/source/v10.4/postgresql-10.4.tar.gz
tar -xf postgresql-10.4.tar.gz
cd postgresql-10.4
./configure --with-openssl
make -C src/bin
sudo make -C src/bin install
make -C src/include
sudo make -C src/include install
make -C src/interfaces
sudo make -C src/interfaces install
make -C doc
sudo make -C doc install

vi ~/.bash_profile
export PATH="/usr/local/pgsql/bin:$PATH"

//Connect
export PGPASSWORD='postgres'
psql -h database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com -p 5432 "dbname=postgres user=postgres sslrootcert=rds-ca-2019-root.pem sslmode=verify-full"


// For SSL Connection pass the trustStore as Env arg 

mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Djavax.net.ssl.trustStore=aurora-postgresql.jks \
-Djavax.net.ssl.trustStorePassword=aurora-postgresql \
-Dexec.mainClass="org.anonymous.boot.Program"

// Go Client Build
// To recompile grpc stubs
make proto

// Just build go
make


// IAM Authentication ( assuming IAM Authentication is turned on Aurora )
// Create user in DB from psql
// In case recreating then :  DROP ROLE mwuser

CREATE USER mwuser WITH LOGIN; 
GRANT rds_iam TO mwuser;

//Ensure delete the 'credential' and other aws profile files, we dont want the SDK to get confused
            
mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=mwuser \
-Drds.region=us-east-1 \
-DdataSource.databaseName=postgres \
-DdataSource.currentSchema=public \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Djavax.net.ssl.trustStore=aurora-postgresql.jks \
-Djavax.net.ssl.trustStorePassword=aurora-postgresql \
-Dexec.mainClass="org.anonymous.boot.Program"

// IAM Auth. testing with psql client

export RDHOST="database-2.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com"

Create AWS Profile
aws configure --profile dbuser
AWS Access Key ID [None]: xxxxxxxxxxxxx
AWS Secret Access Key [None]: xxxxxxxxxxxxxxxxxx
Default region name [None]: eu-west-1
Default output format [None]: json


export PGPASSWORD="$(aws --profile dbuser rds generate-db-auth-token \
--hostname $RDSHOST \
--port 5432 \
--region us-east-1 \
--username mwuser)"

// in case u have deleted the default profile files, then the below would also work as it will fall back to the EC2 Service Role
aws rds generate-db-auth-token \
--hostname $RDSHOST \
--port 5432 \
--region us-east-1 \
--username mwuser

psql -h "$RDHOST" -p 5432 "dbname=postgres user=mwuser sslrootcert=rds-ca-2019-root.pem sslmode=verify-full"



// Go Client Build
// To recompile grpc stubs
make proto

// Just build go
make

// To expand the EBS Disk Size 
https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/recognize-expanded-volume-linux.html





