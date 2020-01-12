#!/bin/bash

echo '----------------------------------'
echo 'verifying data base server is up!'
echo '----------------------------------'

until nc -z -v -w30 "db_trader" 3306
do
  echo "Waiting for database connection..."
  # wait for 5 seconds before check again
  sleep 5
done

echo '----------------------------------'
echo 'data base server is up'
echo '----------------------------------'

echo 'starting trader-server'

java -Djava.security.egd=file:/dev/./urandom -jar /trader/server/trader.jar

