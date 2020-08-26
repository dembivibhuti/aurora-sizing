# aurora-sizing


         ___        ______     ____ _                 _  ___  
        / \ \      / / ___|   / ___| | ___  _   _  __| |/ _ \ 
       / _ \ \ /\ / /\___ \  | |   | |/ _ \| | | |/ _` | (_) |
      / ___ \ V  V /  ___) | | |___| | (_) | |_| | (_| |\__, |
     /_/   \_\_/\_/  |____/   \____|_|\___/ \__,_|\__,_|  /_/ 
 ----------------------------------------------------------------- 


Hi there! Welcome to AWS Cloud9!

To get started, create some files, play with the terminal,
or visit https://docs.aws.amazon.com/console/cloud9/ for our documentation.

Happy coding!


mvn clean formatter:format compile install

mvn exec:java -DdataSourceClassName=org.postgresql.ds.PGSimpleDataSource \
-DdataSource.user=postgres \
-DdataSource.password=postgres \
-DdataSource.databaseName=postgres \
-DdataSource.portNumber=5432 \
-DdataSource.roserverName=database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-DdataSource.rwserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com \
-Dexec.mainClass="org.anonymous.boot.Program"


For Github Upload
sudo yum -y update      # Install the latest system updates.
sudo yum -y install git # Install Git.
git --version           # Confirm Git was installed.

git config --global user.name "USER_NAME"
git config --global user.email EMAIL_ADDRESS

git clone https://github.com/somnath67643/aurora-sizing.git
cd aurora-sizing

#git add --all # not required




