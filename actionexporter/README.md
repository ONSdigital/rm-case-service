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


## To retrieve a FreeMarker template that does not exist
curl http://localhost:8141/freemarker/donotexist -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160917013702454","message":"FreeMarker template not found for name donotexist"}}


## To store a FreeMarker template
cd /home/centos/code/responsemanagement-service/actionexporter/src/test/resources/templates/freemarker
curl http://localhost:8141/freemarker/curltest -v -X POST -F file=@curltest.ftl
204


## To retrieve a FreeMarker template that does exist
curl http://localhost:8141/freemarker/curltest -v -X GET
TODO


## Steps TODO
    - get templates from db: follow http://www.nurkiewicz.com/2010/01/writing-custom-freemarker-template.html
