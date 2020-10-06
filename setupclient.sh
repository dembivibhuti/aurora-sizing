#!/bin/sh

echo "Start Client"

sudo yum update -y
sudo yum install git -y
git version

cd /home/ec2-user
wget https://golang.org/dl/go1.15.2.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.15.2.linux-amd64.tar.gz
echo 'export PATH=$PATH:/usr/local/go/bin' > ~/.profile
echo 'export PATH=$PATH:/usr/local/go/bin' > /home/ec2-user/.profile
export PATH=$PATH:/usr/local/go/bin

git clone https://github.com/somnath67643/aurora-sizing.git

cd /home/ec2-user/aurora-sizing/clientgo
export GOCACHE=/home/ec2-user/gocache
make clean
make

./testclient -sa 172.31.222.242:8080

echo "Run Complete"

