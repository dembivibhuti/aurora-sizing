# aurora-sizing

mvn clean formatter:format compile install

mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dexec.mainClass="org.anonymous.boot.Program"

 ## ------  OR -------
 java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-jar aurora-cli-1.0-SNAPSHOT.jar


For Github Upload
- sudo yum -y update      # Install the latest system updates.
- sudo yum -y install git # Install Git.
- git --version           # Confirm Git was installed.

- git config --global user.name "USER_NAME"
- git config --global user.email EMAIL_ADDRESS

- git clone https://github.com/somnath67643/aurora-sizing.git
- cd aurora-sizing

- git add --all # not required

- git commit -m "Initial Commit"
- git push








