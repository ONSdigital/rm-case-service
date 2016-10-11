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

    - To start with default credentials:
        ./mvnw spring-boot:run

    - To start with specific credentials:
        ./mvnw spring-boot:run -Dsecurity.user.name=tiptop -Dsecurity.user.password=override


#######################################################################################################################
## Curl tests
#######################################################################################################################
## To test the health endpoint without credentials
curl http://localhost:8241/mgmt/health -v -X GET
401 {"timestamp":1476221152687,"status":401,"error":"Unauthorized","message":"Full authentication is required to access this resource","path":"/mgmt/health"}


## To test the health endpoint
curl http://localhost:8241/mgmt/health -v -X GET -u admin:ctp
200 {"status":"UP","exportScheduler":{"status":"UP","exportInfo":{"lastRunTime":"11/10/2016 22:27:00","callTimes":["11/10/2016 22:23:30","11/10/2016 22:24:00","11/10/2016 22:24:30","11/10/2016 22:25:00","11/10/2016 22:25:30","11/10/2016 22:26:00","11/10/2016 22:26:30","11/10/2016 22:27:00"]}},"jms":{"status":"UP","provider":"ActiveMQ"},"diskSpace":{"status":"UP","total":30335164416,"free":6906839040,"threshold":10485760},"mongo":{"status":"UP","version":"3.2.9"},"refreshScope":{"status":"UP"}}


## To retrieve a DocumentContent that does NOT exist
curl http://localhost:8141/content/donotexist  -u admin:ctp -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160917013702454","message":"ContentDocument not found for name donotexist"}}


## To store an empty DocumentContent
cd /home/centos/code/responsemanagement-service/actionexporter/src/test/resources/templates/freemarker
curl http://localhost:8141/content/curltest -u admin:ctp -v -X POST -F file=@curltest_emptytemplate.ftl
500 {"error":{"code":"SYSTEM_ERROR","timestamp":"20160917034936583","message":"Issue storing ContentDocument. It appears to be empty."}}


## To store a valid FreeMarker template
cd /home/centos/code/responsemanagement-service/actionexporter/src/test/resources/templates/freemarker
curl http://localhost:8141/content/curltest -u admin:ctp -v -X POST -F file=@curltest_validtemplate.ftl
201 and Location: http://localhost:8141/content/curltest/curltest and {"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}


## To retrieve a DocumentContent that does exist
curl http://localhost:8141/content/curltest -u admin:ctp -v -X GET
200 {"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}


## To retrieve all DocumentContents
curl http://localhost:8141/content/ -u admin:ctp -v -X GET
200 [{"name":"curltest","content":"=================================  File for the Printer ==================================ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode<#list actionRequests as actionRequest>\t${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}, ${actionRequest.address.line1}, ${actionRequest.address.townName}, ${actionRequest.address.postcode}</#list>","dateModified":1474080646781}]


## To template using an existing template
curl http://localhost:8141/manualtest/curltest -u admin:ctp -v -X GET
200


## To template using a NON existing template
curl http://localhost:8141/manualtest/random -u admin:ctp -v -X GET
500 {"timestamp":1474094170845,"status":500,"error":"Internal Server Error","message":"Internal Server Error","path":"/manualtest/random"}
