# Case Service
<<<<<<< HEAD
This repository contains the Case service. This microservice is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It manages cases, where a case represents an expected response from an sample unit such as a business or a household. Every sample unit in the survey sample must have at least one associated case. Each case can have multiple questionnaires associated with it, but it must have at least one. Each questionnaire has a question set and a Unique Access Code (UAC). Interesting things that happen during the life cycle of a case are recorded as case events. Case life cycle transitions are published as JMS messages for interested parties to subscribe to.

* Receive and act upon Case Creation messages on a Rabbit queue
* Receive CaseEvent instructions via RESTful requests and act accordingly
* As a result of CaseEvents, call one or more of IAC Service, CollectionExercise Service, Action Service, Party Service, and orchestrate survey collection exercise cases

* to run casesvc

      cd code/rm-sample-service
      mvn clean install
      cd samplesvc
      ./mvnw spring-boot:run


## API
See [API.md](https://github.com/ONSdigital/rm-case-service/blob/master/API.md) for API documentation.

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
