See Case Service in the WIKI : http://192.168.10.11/Case_Frame_Service


## To build
./mvnw clean site
    - unit test coverage reports at /casesvc/target/site/cobertura/index.html


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/casesvc sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


########################################################################
## To test addresses for uprn
########################################################################
curl http://localhost:8171/addresses/12345 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112002223","message":"No addresses found for uprn 12345"}}


curl http://localhost:8171/addresses/ -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112102928","message":"HTTP 404 Not Found"}}


curl http://localhost:8171/addresses/uprn/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912105745550","message":"java.lang.NumberFormatException: For input string: \"uprn\""}}


########################################################################
## To test addresses for postcode
########################################################################
curl http://localhost:8171/addresses/postcode/PO141DT -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112330942","message":"No addresses found for postcode PO141DT"}}


curl http://localhost:8171/addresses/postcode/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912112421853","message":"java.lang.NumberFormatException: For input string: \"postcode\""}}


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
