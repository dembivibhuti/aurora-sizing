#!/bin/sh

if [ -z "$2" ]
then
     NO_OF_CLIENTS=1
else
     NO_OF_CLIENTS="$2"
fi

if [ -z "$3" ]
then
     MODE=1
else
     MODE="$3"
fi

echo "Starting $NO_OF_CLIENTS Instances of Client"
echo "With Mode = $MODE '1 = Lookup and Get 2 = Index Lookup and Get'"

for i in $(seq 1 $NO_OF_CLIENTS)
do
./testclient -sa $1 -mode $MODE -promscrapeport :$((9090 + $i)) > out.log &
done

read -p "Press any key to stop"
pkill -P $$

# FOR CLEAN UP: for pid in $(ps -ef | grep "testclient" | awk '{print $2}'); do kill -9 $pid; done