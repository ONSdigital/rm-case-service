## To build
./mvnw clean install


## To run
    - Prerequisite:
        - sudo mongod --dbpath /var/lib/mongodb
    - ./mvnw spring-boot:run


## To test
curl http://localhost:8241/mgmt/health -v -X GET
200 {"status":"UP","exportScheduler":{"status":"UP","exportInfo":{"lastRunTime":"09/09/2016 15:42:30","callTimes":["09/09/2016 15:40:30","09/09/2016 15:41:00","09/09/2016 15:41:30","09/09/2016 15:42:00","09/09/2016 15:42:30"]}},"jms":{"status":"UP","provider":"ActiveMQ"},"diskSpace":{"status":"UP","total":30335164416,"free":8084832256,"threshold":10485760},"refreshScope":{"status":"UP"}}
