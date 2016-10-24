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


## To test
See curlTests.tx under /test/resources
