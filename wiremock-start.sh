#!/bin/bash
nohup java -jar wiremock-standalone-3.9.2.jar --port 18002 > wiremock.log 2>&1 &
echo $! > wiremock.pid