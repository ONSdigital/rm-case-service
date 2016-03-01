See Case Frame Service in the WIKI : http://192.168.10.11/Case_Frame_Service

## To build
./mvnw clean install checkstyle:checkstyle


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement-caseframesvc sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


## To test regions
curl http://localhost:8171/regions/ -v -X GET
200 [{"regionCode":"E12000001","regionName":"North East"}...


