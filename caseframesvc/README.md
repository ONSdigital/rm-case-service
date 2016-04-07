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


## To test categories
curl http://localhost:8171/categories/ -v -X GET
curl http://localhost:8171/categories/?role=collect-cso -v -X GET


## To test case events
curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8171/cases/1/events -v -X POST -d "{\"category\":\"General Enquiry - Escalated\", \"description\":\"manualcurltest\", \"createdBy\":\"philippeb\"}"
Submitted at 12:26 and got:
200 {"createdDateTime":"2016-04-04T11:26:06.961+0000","caseEventId":766,"caseId":1,"category":"General Enquiry - Escalated","subCategory":null,"createdBy":"philippeb","description":"manualcurltest"}

In DB: we have stored:
    - select * from caseframe.caseevent where caseeventid = 766;
    - 766;1;"manualcurltest";"philippeb";"2016-04-04 11:26:06.961+00";"General Enquiry - Escalated";""

