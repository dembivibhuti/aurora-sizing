#!/bin/sh

if [ -z "$2" ]
then
     NO_OF_CLIENTS=1
else
     NO_OF_CLIENTS="$2"
fi

echo "Starting $NO_OF_CLIENTS Instances of Client"

for i in $(seq 1 $NO_OF_CLIENTS)
do
./testclient -sa $1 $NO_OF_CLIENTS > out.log &
done

read -p "Press any key to stop"
pkill -P $$