# Case Service
This repository contains the Case service. This microservice is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/) and has the following responsibilities:

* Receive and act upon Case Creation messages on a Rabbit queue
* Receive CaseEvent instructions via RESTful requests and act accordingly
* As a result of CaseEvents, call one or more of IAC Service, CollectionExercise Service, Action Service, Party Service, and orchestrate survey collection exercise cases

* to run casesvc

      cd code/rm-sample-service
      mvn clean install
      cd samplesvc
      ./mvnw spring-boot:run


## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
