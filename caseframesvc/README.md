See Case Frame Service in the WIKI : http://192.168.10.11/Case_Frame_Service


## To build
./mvnw clean install


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/caseframesvc sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


## To test regions
curl http://localhost:8171/regions/ -v -X GET
200 [{"regionCode":"E12000001","regionName":"North East"}...


## To test addresses
curl http://localhost:8171/addresses/postcode/PO141DT -v -X GET
200 long json


## To test categories
curl http://localhost:8171/categories/ -v -X GET
curl http://localhost:8171/categories/?role=collect-cso -v -X GET


## To test case events
# Create a case - valid json
curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/1 -v -X PUT -d "{\"type\":\"REGION\", \"code\":\"E12000008\"}"
204

# Create a case - bad json
curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/1 -v -X PUT -d "{\"badtype\":\"REGION\", \"code\":\"E12000008\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160704143808891","message":"Provided json is incorrect."}}
