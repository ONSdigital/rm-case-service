[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0c2913652fd94878b4c61838b54db11e)](https://www.codacy.com/app/zekizeki/rm-case-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-case-service&amp;utm_campaign=Badge_Grade) [![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/casesvc.svg)]()
[![Build Status](https://travis-ci.org/ONSdigital/rm-case-service.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-case-service)
[![codecov](https://codecov.io/gh/ONSdigital/rm-case-service/branch/master/graph/badge.svg)](https://codecov.io/gh/ONSdigital/rm-case-service)

# Case Service
This repository contains the Case service. This microservice is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It manages cases, where a case represents an expected response from an sample unit such as a business or a household. Every sample unit in the survey sample must have at least one associated case. Each case can have multiple questionnaires associated with it, but it must have at least one. Each questionnaire has a question set and a Unique Access Code (UAC). Interesting things that happen during the life cycle of a case are recorded as case events. Case life cycle transitions are published as JMS messages for interested parties to subscribe to.


# Inbound process

1. CaseCreation message arrives from the collection exercise service on the Case.CaseDelivery queue and is processed by the 
case-creation-inbound-flow.xml where it is validated against the XSD and sent to the case transformed channel.
1. This channel is processed by the CaseCreationReceiver class.
1. The CaseCreationReceiver calls the CaseService to create the initial case.
1. The CaseService then creates a Case Group based on the SampleUnit parent and saves it to the database
1. To do this it has to obtain the survey id so it calls back to the collection exercise service with the collection 
exercise id in order to swap it for a survey id 
1. The CaseService then creates a child case for every SampleUnit child, saving each one to the database as it goes.
1. All these child cases are created with a status of SAMPLED_INIT ready for the IAC scheduler.
1. Finally it sends out some case created events to the event publisher.

## Outbound Process

1. The CaseDistributionScheduler runs on a scheduled job every 0.5 seconds 
1. It retrieves all cases in SAMPLED_INIT or REPLACEMENT_INIT state
1. Once it has a count of these it sends a request to the IAC service to generate IAC codes for these cases
1. It then updates all Cases with a generated IAC code (unless one fails in which case its left in the database for the next run)
1. Publish an event to the event publisher (unsure what this is for?) 
1. It adds an IAC case audit record
1. Prepares a case notification to inform the action service that a new case is ready.
1. The CaseNotificationPublisher publish this to the rabbit queue Case.LifecycleEvents via the case-notification-outbound-flow.xml
where it is read by action service.
 

### Receipt Process
Cases are receipted by SDX-Gateway via the Case.Responses via the case-receipt-inbound flow for some reason
the inbound channel is always offline. This creates a final case event stating that the response has been received.

### Report Scheduling

The ReportScheduler is driven by the report cron expression. It creates a lock on redis to ensure only
one instance can run at any given time. This scheduler executes the CaseReportService which subsequently 
runs two stored procedures - generate case event reports and generate response chasing reports. These two
procedures run nightly at 11pm and populate the report table.  

## This service calls other service rest API

Collection Exercise - get collection exercise - used to obtain the survey id for a specific collection exercise id
IAC service - generates an IAC code for a case. This allows a respondent to enroll into the collection exercise. 

## Micro Service Interactions 
This service calls other services via Rabbit:

1. action-service - Case.LifecycleEvents (aka case notifications)

The REST API is called by:

1. ras-frontstage
    - /cases/{case_id}
    - /cases/iac/{enrolment_code}
    - /cases/partyid/{party_id}
    - /cases/{case_id}/events
    - /categories
1. action-service
    - /cases/{caseid}
    - /cases/{caseid}/events
    - /cases/{caseid}/iac
    - /casegroups/{caseGroupId}
1. response-operations-ui
    - /cases/{case_id}?iac=true
    - /cases/{case_id}/events
    - /cases/casegroupid/{case_group_id}
    - /cases/partyid/{business_party_id}
    - /cases/partyid/{business_party_id}
    - /cases/{case_id}/iac
    - /cases/{case_id}/events
1. ras-party
    - /cases/iac/{enrolment_code}
    - /cases/{case_id}/events
    - /cases/casegroupid/{case_group_id}
    - /casegroups/partyid/{business_id}

## This service is passed messages from
1. collection-exercise via the Case.CaseDelivery
1. sdx-gateway via the Case.Responses queue

## Improvements 
Suggested improvements can be found here:
[Improvements](IMPROVEMENTS.md)

## Running

There are two ways of running this service

* The easiest way is via docker (https://github.com/ONSdigital/ras-rm-docker-dev)
* Alternatively running the service up in isolation
    ```bash
    cp .maven.settings.xml ~/.m2/settings.xml  # This only needs to be done once to set up mavens settings file
    mvn clean install
    mvn spring-boot:run
    ```

# Code Styler
To use the code styler please goto this url (https://github.com/google/google-java-format) and follow the Intellij instructions or Eclipse depending on what you use

## API
Open API spec can be found~~~~ [here](API.yaml)

## To test
See curlTests.txt under /test/resources

## Swagger Specifications
To view the Swagger Specifications for the Sample Service, run the service and navigate to http://localhost:8171/swagger-ui.html.

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics) 
