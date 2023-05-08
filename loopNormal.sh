#!/bin/bash

# Configure URL to the GRA client HTTP server (the "gateway")

readarray -t array < baseStressTest.txt

for i in {1..100}
do
  eval "curl ${array[1 + $RANDOM % ${#array[@]} - 1]}"
  sleep $(($RANDOM % 5))
  #echo curl "${array[1 + ($RANDOM % ${#array[@]} - 1 )]}"
done
