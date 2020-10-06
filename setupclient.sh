#!/bin/sh

echo "Start Client"

git clone https://github.com/somnath67643/aurora-sizing.git

cd /home/ec2-user/environment/aurora-sizing/clientgo

make clean
make

./testclient

echo "Run Complete"

