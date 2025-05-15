#!/bin/bash
nohup java -jar /home/runner/.m2/repository/org/wiremock/wiremock-standalone/3.9.2/wiremock-standalone-3.9.2.jar --port 18002 > wiremock.log 2>&1 &
echo $! > wiremock.pid