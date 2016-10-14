See Case Service in the WIKI : http://192.168.10.11/Case_Frame_Service


## To build
./mvnw clean site
    - unit test coverage reports at /casesvc/target/site/cobertura/index.html


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/casesvc sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


########################################################################
## To test GET addresses for uprn
########################################################################
curl http://localhost:8171/addresses/12345 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112002223","message":"No addresses found for uprn 12345"}}


curl http://localhost:8171/addresses/ -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112102928","message":"HTTP 404 Not Found"}}


curl http://localhost:8171/addresses/uprn/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912105745550","message":"java.lang.NumberFormatException: For input string: \"uprn\""}}


########################################################################
## To test GET addresses for postcode
########################################################################
curl http://localhost:8171/addresses/postcode/PO141DT -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912112330942","message":"No addresses found for postcode PO141DT"}}


curl http://localhost:8171/addresses/postcode/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912112421853","message":"java.lang.NumberFormatException: For input string: \"postcode\""}}


########################################################################
## To test GET cases for uprn
########################################################################
curl http://localhost:8171/cases/uprn/12345 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160913110903751","message":"Case not found for UPRN 12345"}}


curl http://localhost:8171/cases/uprn/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912113023803","message":"java.lang.NumberFormatException: For input string: \"uprn\""}}


curl http://localhost:8171/cases/uprn/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912113126838","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


########################################################################
## To test GET cases for questionnaire
########################################################################
curl http://localhost:8171/cases/questionnaire/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160913110957453","message":"Case not found for questionnaire id 123"}}


curl http://localhost:8171/cases/questionnaire/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912114715980","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


curl http://localhost:8171/cases/questionnaire/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912114759613","message":"java.lang.NumberFormatException: For input string: \"questionnaire\""}}


########################################################################
## To test POST cases
########################################################################
TODO To be implemented


########################################################################
## To test GET cases for caseid
########################################################################
curl http://localhost:8171/cases/1234 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912130955077","message":"Case not found for id 1234"}}


curl http://localhost:8171/cases/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912131031021","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


curl http://localhost:8171/cases/ -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912131107198","message":"HTTP 404 Not Found"}}


########################################################################
## To test GET /cases/actionplan/{actionPlanId}
########################################################################
curl http://localhost:8171/cases/actionplan/1 -v -X GET
200 [1,2,3,4,5,6,7,8,9...


curl http://localhost:8171/cases/actionplan/4 -v -X GET
204


curl http://localhost:8171/cases/actionplan/abc -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912132229086","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


curl http://localhost:8171/cases/actionplan/ -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912132322589","message":"java.lang.NumberFormatException: For input string: \"actionplan\""}}


########################################################################
## To test GET /cases/{caseid}/events
## I created cases directly in pgAdmin with: select casesvc.generate_cases(1, 'REGION', 'E12000001')
########################################################################
curl http://localhost:8171/cases/123/events -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912153148643","message":"Case not found for id 123"}}


curl http://localhost:8171/cases/abc/events -v -X GET
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912153252321","message":"java.lang.NumberFormatException: For input string: \"abc\""}}


curl http://localhost:8171/cases/1/events -v -X GET
204


########################################################################
## POST /cases/{caseid}/events
########################################################################
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/cases/1/events -v -X POST -d "{\"type\":\"LA\", \"code\":\"E07000163\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912160946534","message":"Provided json is incorrect."}}


curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/cases/1/events -v -X POST -d "{\"description\":\"mytest\", \"category\":\"Complaint - Escalated\", \"subCategory\":\"ABC\", \"createdBy\":\"philippe\"}"
200 {"createdDateTime":"2016-09-12T15:28:25.681+0000","caseEventId":763,"caseId":1,"category":"Complaint - Escalated","subCategory":"ABC","createdBy":"philippe","description":"mytest"}


curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/cases/1234/events -v -X POST -d "{\"description\":\"mytest\", \"category\":\"Complaint - Escalated\", \"subCategory\":\"ABC\", \"createdBy\":\"philippe\"}"
404 curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/cases/1234/events -v -X POST -d "{\"description\":\"mytest\", \"category\":\"Complaint - Escalated\", \"subCategory\":\"ABC\", \"createdBy\":\"philippe\"}"


########################################################################
## GET /questionnaires/iac/{iac}
########################################################################
curl http://localhost:8171/questionnaires/iac/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912163255378","message":"Cannot find Questionnaire for iac 123"}}


########################################################################
## GET /questionnaires/case/{caseid}
########################################################################
curl http://localhost:8171/questionnaires/case/1234 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160914095343962","message":"Questionnaire not found for case id 1234"}}


########################################################################
## PUT /questionnaires/{questionnaireid}/response
########################################################################
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/questionnaires/1234/response -v -X PUT -d "{\"description\":\"mytest\", \"category\":\"Complaint - Escalated\", \"subCategory\":\"ABC\", \"createdBy\":\"philippe\"}"
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160914101955632","message":"Questionnaire not found for questionnaire id 1234"}}


########################################################################
## GET /questionsets
########################################################################
curl http://localhost:8171/questionsets/ -v -X GET
200 [{"questionSet":"HH","description":"Households"},{"questionSet":"CE","description":"Communal Establishments"}]


########################################################################
## GET /questionsets/{questionset}
########################################################################
curl http://localhost:8171/questionsets/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912165259103","message":"QuestionSet not found for id 123"}}


########################################################################
## GET /casetypes
########################################################################
curl http://localhost:8171/casetypes/ -v -X GET
200 [{"caseTypeId":1,"name":"HH","description":"Household","actionPlanId":1,"questionSet":"HH"},{"caseTypeId":2,"name":"HGH","description":"Hotel Guest House Bed and Breakfast","actionPlanId":3,"questionSet":"CE"},{"caseTypeId":3,"name":"CH","description":"Care Home","actionPlanId":2,"questionSet":"CE"}]


########################################################################
## GET /casetypes/{casetypeid}
########################################################################
curl http://localhost:8171/casetypes/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912165705088","message":"CaseType not found for id 123"}}


########################################################################
## GET /categories
########################################################################
curl http://localhost:8171/categories/ -v -X GET
200 Long json


########################################################################
## GET /samples
########################################################################
curl http://localhost:8171/samples/ -v -X GET
200 Long json


########################################################################
## GET /samples/{sampleid}
########################################################################
curl http://localhost:8171/samples/123 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912170200153","message":"Sample not found for id 123"}}


########################################################################
## PUT /samples/{sampleid}
########################################################################
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/1234 -v -X PUT -d "{\"description\":\"mytest\", \"category\":\"Complaint - Escalated\", \"subCategory\":\"ABC\", \"createdBy\":\"philippe\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160912170335864","message":"Provided json is incorrect."}}


curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/1234 -v -X PUT -d "{\"type\":\"LA\", \"code\":\"E07000163\"}"
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160914110133205","message":"Sample not found for id 1234"}}


curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/1 -v -X PUT -d "{\"type\":\"LA\", \"code\":\"E07000163\"}"
200 {"sampleId":1,"name":"Residential","description":"Households","addressCriteria":"addresstype = 'HH'","caseTypeId":1,"surveyId":1}


curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/samples/20 -v -X PUT -d "{\"type\":\"REGION\", \"code\":\"E12000005\"}"
200 {"sampleId":20,"name":"COM2T12","description":null,"addressCriteria":"SAMPLE = COM2T12","survey":"2017 TEST","sampleCaseTypes":[{"caseTypeId":20,"respondentType":"H"},{"caseTypeId":42,"respondentType":"I"}]}


########################################################################
## GET /surveys
########################################################################
curl http://localhost:8171/surveys/ -v -X GET
200 [{"surveyid":1,"name":"2016 Test","description":"2016 Census Test"}]


########################################################################
## GET /surveys/{surveyid}
########################################################################
curl http://localhost:8171/surveys/1 -v -X GET
200 {"surveyid":1,"name":"2016 Test","description":"2016 Census Test"}


curl http://localhost:8171/surveys/10 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160912171344249","message":"Survey not found for id 10"}}
