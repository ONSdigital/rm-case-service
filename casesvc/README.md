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


########################################################################
## To test cases for uprn
########################################################################
curl http://localhost:8171/cases/uprn/12345 -v -X GET
204 No content
TODO: differ from spec which says 404 UPRN not found
TODO: Server-side, it does a select by uprn on table casesvc.case.


curl http://localhost:8171/cases/uprn/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912113023803","message":"java.lang.NumberFormatException: For input string: \"uprn\""}}


curl http://localhost:8171/cases/uprn/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912113126838","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


########################################################################
## To test cases for questionnaire
########################################################################
curl http://localhost:8171/cases/questionnaire/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912114223773","message":"Case not found for id 123"}}
TODO Spec says Questionnaire not found


curl http://localhost:8171/cases/questionnaire/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912114715980","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


curl http://localhost:8171/cases/questionnaire/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912114759613","message":"java.lang.NumberFormatException: For input string: \"questionnaire\""}}






