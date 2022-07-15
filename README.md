# Case Service
This repository contains the Case service. This microservice is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It manages cases, where a case represents an expected response from an sample unit such as a business or a household. Every sample unit in the survey sample must have at least one associated case. Each case can have multiple questionnaires associated with it, but it must have at least one. Each questionnaire has a question set and a Unique Access Code (UAC). Interesting things that happen during the life cycle of a case are recorded as case events. Case life cycle transitions are published as JMS messages for interested parties to subscribe to.


# Inbound process
   1. CaseCreation message arrives from the sample service on the pubsub input channel `caseCreationChannel`.
   2. This channel is processed by the CaseCreationReceiver class which includes validation fo the message.
   3. The CaseCreationReceiver calls the CaseService to create the initial case.
   4. The CaseService then creates a Case Group based on the SampleUnit parent and saves it to the database.
   5. To do this it has to obtain the survey id, so it calls back to the collection exercise service with the collection
      exercise id in order to swap it for a survey id
   6. The CaseService then creates a child case for every SampleUnit child, saving each one to the database as it goes.
   7. All these child cases are created with a status of SAMPLED_INIT ready for the IAC scheduler. 
   8. Finally, it sends out some case created events to the event publisher.

## Outbound Process

   1. The CaseDistributionScheduler runs on a scheduled job every 0.5 seconds 
   2. It retrieves all cases in SAMPLED_INIT or REPLACEMENT_INIT state
   3. Once it has a count of these it sends a request to the IAC service to generate IAC codes for these cases
   4. It then updates all Cases with a generated IAC code (unless one fails in which case its left in the database for the next run)
   5. Publish an event to the event publisher (unsure what this is for?) 
   6. It adds an IAC case audit record
   7. Prepares a case notification to inform the action service that a new case is ready.
   8. The CaseNotificationPublisher publish this to action via pub sub

## Improvement Process
   As a part of the improvement process the outbound process will be replaced by merging action to case service. 
   Currently, the improvement is in progress and is feature flag off. As a part of this improvement collection exercise
   service will now call `/process-event` endpoint in case service rather than action service to process the events.

### Action Process/ Event Process
   1. Collection exercise service gives a call to `/process-event` endpoint which is an async call.
   2. Which calls `ProcessCaseActionService`.
   3. `ProcessCaseActionService` then calls two async services i.e.`ProcessEmailActionService` and 
      `ProcessLetterActionService`.
   4. `ProcessEmailActionService` and `ProcessLetterActionService` calls back collection exercise service with the
       collection exercise id in order to swap it for a survey id and then the survey service to confirm the correctness.
       The services also calls party service to retried party information required for emails and letters.
   5. `ProcessEmailActionService` then uses `NotifyEmailService` to publish PubSub email messages via `notifyEmailChannel`.
   6. `ProcessLetterActionService` uses `UploadObjectGCS` to upload the created file to the GCP and then uses `NotifyLetterService`
       to send letter message to PubSub via `printFileChannel`.
   7. `/retry-event` follows the same process to retry first fail operation for event processing which is trigger by 
      kubernetes cron job.
   8. `/action-template` is provided to create new action template.
 

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
Survey Service - to confirm survey associated to the collection exercise for event processing.
Party Service - to gather party information required for letters and emails event.

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
2. action-service
    - /cases/{caseid}
    - /cases/{caseid}/events
    - /cases/{caseid}/iac
    - /casegroups/{caseGroupId}
3. response-operations-ui
    - /cases/{case_id}?iac=true
    - /cases/{case_id}/events
    - /cases/casegroupid/{case_group_id}
    - /cases/partyid/{business_party_id}
    - /cases/partyid/{business_party_id}
    - /cases/{case_id}/iac
    - /cases/{case_id}/events
4. ras-party
    - /cases/iac/{enrolment_code}
    - /cases/{case_id}/events
    - /cases/casegroupid/{case_group_id}
    - /casegroups/partyid/{business_id}
5. action-event
   - `/process-event`
   - `/retry-event`
   - `/action-template`

## This service is passed messages from
1. collection-exercise via the Case.CaseDelivery
1. sdx-gateway via the Case.Responses queue

## Improvements 
Suggested improvements can be found here:
[Improvements](IMPROVEMENTS.md)

## Running
* Run
    ```bash
    cp .maven.settings.xml ~/.m2/settings.xml  # This only needs to be done once to set up mavens settings file
    mvn clean install
    mvn spring-boot:run
    ```

# Code Styler
To use the code styler please goto this url (https://github.com/google/google-java-format) and follow the Intellij instructions or Eclipse depending on what you use

## API
Open API spec can be found [here](API.yaml)

## Integration tests
Use the command 'mvn clean install' this will run the tests in docker.

Note: You may need to a service account and key can be found locally in your environment and use the command 
"export GOOGLE_APPLICATION_CREDENTIALS='/[PATH]/[NAME_OF_KEY].json'" (remove the double quotes)

## To test
See curlTests.txt under /test/resources

## Issues with schema startup

There is currently an issue with the SQL scripts due to upgrading to Spring Boot 2.6.6. You might see an error about not being able to create `iac.iac` on the first start - this will only appear on the first run and should be fine on future runs.

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics) 
