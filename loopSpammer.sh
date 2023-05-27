#!/usr/local/bin/bash

# Configure URL to the GRA client HTTP server (the "gateway")

readarray -t array < baseSpamDifferentUrl.txt

for i in {1..100}
do
  eval "curl ${array[1 + $RANDOM % ${#array[@]} - 1]}"
  sleep 0.2
  #echo curl "${array[1 + ($RANDOM % ${#array[@]} - 1 )]}"
done
