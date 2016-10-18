See Case Service in the WIKI : http://192.168.10.11/Case_Frame_Service


## To build
./mvnw clean site
    - unit test coverage reports at /casesvc/target/site/cobertura/index.html


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/casesvc sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


## To test
See curlTests.txt under /test/resources


about to invoke the event creation...
org.springframework.integration