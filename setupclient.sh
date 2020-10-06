#!/bin/sh

echo "Start Client"

sudo yum update -y
sudo yum install git -y
git version

wget https://golang.org/dl/go1.15.2.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.15.2.linux-amd64.tar.gz
echo 'export PATH=$PATH:/usr/local/go/bin' > ~/.profile

git clone https://github.com/somnath67643/aurora-sizing.git

cd ~/aurora-sizing/clientgo

make clean
make

./testclient

echo "Run Complete"

