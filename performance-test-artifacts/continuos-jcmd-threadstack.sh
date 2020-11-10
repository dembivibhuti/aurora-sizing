#!/bin/sh

while true; echo "-------------------------- START $(date +"%m-%d-%Y-%T")-------------------------" >> thread.dump; do jcmd $1 Thread.print >> thread.dump; echo "-------------------------- END $(date +"%m-%d-%Y-%T")-------------------------" >> thread.dump; sleep 60; done
