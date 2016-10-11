## To build
./mvnw clean install


## To run
    - Prerequisites:
        - Start MongoDB:
            - sudo mongod --dbpath /var/lib/mongodb
        - Start ActiveMQ:
            - sudo /sbin/service rabbitmq-server stop
            - cd /opt/apache-activemq-5.13.3/bin
            - ./activemq console
    - ./mvnw spring-boot:run


#######################################################################################################################
## Curl tests
#######################################################################################################################
## To test the health endpoint
curl http://localhost:8241/mgmt/health -v -X GET
200 {"status":"UP","exportScheduler":{"status":"UP","exportInfo":{"lastRunTime":"14/09/2016 13:34:00","callTimes":["14/09/2016 13:27:00","14/09/2016 13:27:30","14/09/2016 13:28:00","14/09/2016 13:28:30","14/09/2016 13:29:00","14/09/2016 13:29:30","14/09/2016 13:30:00","14/09/2016 13:30:30","14/09/2016 13:31:00","14/09/2016 13:31:30","14/09/2016 13:32:00","14/09/2016 13:32:30","14/09/2016 13:33:00","14/09/2016 13:33:30","14/09/2016 13:34:00"]}},"jms":{"status":"UP","provider":"ActiveMQ"},"diskSpace":{"status":"UP","total":30335164416,"free":8425631744,"threshold":10485760},"mongo":{"status":"UP","version":"3.2.9"},"refreshScope":{"status":"UP"}}


## To retrieve a DocumentContent that does NOT exist
curl http://localhost:8141/content/donotexist -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160917013702454","message":"ContentDocument not found for name donotexist"}}


## To store an empty DocumentContent
cd /home/centos/code/responsemanagement-service/actionexporter/src/test/resources/templates/freemarker
curl http://localhost:8141/content/curltest -v -X POST -F file=@curltest_emptytemplate.ftl
500 {"error":{"code":"SYSTEM_ERROR","timestamp":"20160917034936583","message":"Issue storing ContentDocument. It appears to be empty."}}


## To store a valid FreeMarker template
cd /home/centos/code/responsemanagement-service/actionexporter/src/test/resources/templates/freemarker
curl http://localhost:8141/content/curltest -v -X POST -F file=@curltest_validtemplate.ftl
TODO Should be 201 not 200 {"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}


## To retrieve a DocumentContent that does exist
curl http://localhost:8141/content/curltest -v -X GET
200 {"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}


## To retrieve all DocumentContents
curl http://localhost:8141/content/ -v -X GET
200 [{"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}]


## To template using an existing template
curl http://localhost:8141/manualtest/curltest -v -X GET
200


## To template using a NON existing template
curl http://localhost:8141/manualtest/random -v -X GET
500 {"timestamp":1474094170845,"status":500,"error":"Internal Server Error","message":"Internal Server Error","path":"/manualtest/random"}
