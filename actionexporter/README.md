## To build
./mvnw clean install


## To run
    - Prerequisite:
        - sudo mongod --dbpath /var/lib/mongodb
    - ./mvnw spring-boot:run


## To test
curl http://localhost:8241/mgmt/health -v -X GET
200 {"status":"UP","exportScheduler":{"status":"UP","exportInfo":{"lastRunTime":"14/09/2016 13:34:00","callTimes":["14/09/2016 13:27:00","14/09/2016 13:27:30","14/09/2016 13:28:00","14/09/2016 13:28:30","14/09/2016 13:29:00","14/09/2016 13:29:30","14/09/2016 13:30:00","14/09/2016 13:30:30","14/09/2016 13:31:00","14/09/2016 13:31:30","14/09/2016 13:32:00","14/09/2016 13:32:30","14/09/2016 13:33:00","14/09/2016 13:33:30","14/09/2016 13:34:00"]}},"jms":{"status":"UP","provider":"ActiveMQ"},"diskSpace":{"status":"UP","total":30335164416,"free":8425631744,"threshold":10485760},"mongo":{"status":"UP","version":"3.2.9"},"refreshScope":{"status":"UP"}}
