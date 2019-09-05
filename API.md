# Case Service API
This page documents the Case service API endpoints. Apart from the Service Information endpoint, all these endpoints are secured using HTTP basic authentication. All endpoints return an `HTTP 200 OK` status code except where noted otherwise.

## Case Events
For the endpoints that return the details of a case:

* `GET /cases/<case_id>`
* `GET /cases/partyid/<party_id>`
* `GET /cases/iac/<iac>`

&mdash;an optional `caseevents` boolean query parameter can be used to specify that the JSON response include an array of case events associated with the case. For example:

* `GET /cases/partyid/{partyId}?caseevents=true`

If this query parameter is omitted these case events **will not** be returned with the case details. In this scenario case events for a case can be retrieved separately using the `GET /cases/<case_id>/events` endpoint. The JSON examples provided for the three endpoints mentioned above include the case events for illustration purposes.

## Unique Access Codes
For the endpoints that return the details of a case:

* `GET /cases/<case_id>`
* `GET /cases/partyid/<party_id>`
* `GET /cases/iac/<iac>`

&mdash;an optional `iac` boolean query parameter can be used to specify that the JSON response include the unique access code associated with the case. For example:

* `GET /cases/partyid/{partyId}?iac=true?max_cases_per_survey=<n>`

If the iac query parameter is omitted the unique access code **will not** be returned with the case details. The JSON examples provided for the three endpoints mentioned above include the code for illustration purposes.

The max_cases_per_survey is an optional parameter. If not present then all cases for that party will be returned
If present as an integer then only the most recent <n> cases per survey will 
be returned. 

## Service Information
* `GET /info` will return information about this service, collated from when it was last built.

### Example JSON Response
```json
{
    "name": "casesvc",
    "version": "10.42.0",
    "origin": "git@github.com:ONSdigital/rm-case-service.git",
    "commit": "a7abf0734b7cbac9ccb83882cc9aad91446ee8c8",
    "branch": "master",
    "built": "2017-07-12T09:38:30Z"
}
```

## Get Case Group
* `GET /casegroups/{casegroupId}` will return the details of the case group with the given ID.

### Example JSON Response
```json
{
  "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
  "collectionExerciseId": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
  "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
  "sampleUnitRef": "0123456789",
  "sampleUnitType": "B"
}
```

An `HTTP 404 Not Found` status code is returned if the case group with the specified ID could not be found.

## List Case Groups for Party
* `GET /casegroups/partyid/{partyId}` will return a list of case groups with the given party ID.

### Example JSON Response
```json
[
  {
    "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
    "collectionExerciseId": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "sampleUnitRef": "0123456789",
    "sampleUnitType": "B",
    "status": "NOTSTARTED"
  },
  {
    "id": "2d31f300-246d-11e8-b467-0ed5f89f718b",
    "collectionExerciseId": "24535ac6-246d-11e8-b467-0ed5f89f718b",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "sampleUnitRef": "0123456789",
    "sampleUnitType": "B",
    "status": "NOTSTARTED"
  }
]
```

An `HTTP 204 No Content` status code is returned if there are no case groups found with the specified party ID.

## Find Case Group Transition by Collection Exercise and Sample Unit Reference
* `GET /casegroups/transitions/{collectionExerciseId}/{ruRef}` will return a list of case group transitions with the given collection exercise ID and sample unit reference.

### Example JSON Response
```json
[
   {
        {
            "COLLECTION_INSTRUMENT_DOWNLOADED":"INPROGRESS",
            "EQ_LAUNCH":"INPROGRESS",
            "SUCCESSFUL_RESPONSE_UPLOAD":"COMPLETE",
            "COMPLETED_BY_PHONE":"COMPLETEDBYPHONE",
            "NO_LONGER_REQUIRED":"NOLONGERREQUIRED",
            "PRIVACY_DATA_CONFIDENTIALITY_CONCERNS":"REFUSAL",
            "LEGITIMACY_CONCERNS":"REFUSAL",
            "OTHER_OUTRIGHT_REFUSAL":"REFUSAL",
            "ILL_AT_HOME":"OTHERNONRESPONSE",
            "IN_HOSPITAL":"OTHERNONRESPONSE",
            "PHYSICALLY_OR_MENTALLY_UNABLE":"OTHERNONRESPONSE",
            "LANGUAGE_DIFFICULTIES":"OTHERNONRESPONSE",
            "LACK_OF_COMPUTER_INTERNET_ACCESS":"OTHERNONRESPONSE",
            "TOO_BUSY":"OTHERNONRESPONSE",
            "OTHER_CIRCUMSTANTIAL_REFUSAL":"OTHERNONRESPONSE",
            "COMPLY_IN_DIFFERENT_COLLECTION_MODE":"OTHERNONRESPONSE",
            "REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT":"OTHERNONRESPONSE",
            "NO_TRACE_OF_ADDRESS":"UNKNOWNELIGIBILITY",
            "WRONG_ADDRESS":"UNKNOWNELIGIBILITY",
            "VACANT_OR_EMPTY":"NOTELIGIBLE",
            "NON_RESIDENTIAL_ADDRESS":"NOTELIGIBLE",
            "ADDRESS_OCCUPIED_NO_RESIDENT":"NOTELIGIBLE",
            "COMMUNAL_ESTABLISHMENT_INSTITUTION":"NOTELIGIBLE",
            "DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS":"NOTELIGIBLE",
            "NO_PERSON_IN_ELIGIBLE_AGE_RANGE":"NOTELIGIBLE",
            "DECEASED":"NOTELIGIBLE"
        }
   }
]
```

## List Cases for Case Group
* `GET /cases/casegroupid/{casegroupId}` will return a list of cases for the case group with the given case group ID.

### Example JSON Response
```json
[
  {
    "id": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
    "collectionInstrumentId": "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "actionPlanId": "5381731e-e386-41a1-8462-26373744db86",
    "sampleUnitType": "BI",
    "state": "INACTIONABLE",
    "createdBy": "SYSTEM",
    "createdDateTime": "2017-05-15T10:00:00Z",
    "responses": [
      {
        "inboundChannel": "ONLINE",
        "dateTime": "2017-05-17T16:15:20Z"
      }
    ]
  }
]
```

An `HTTP 404 Not Found` status code is returned if the case group with the specified ID could not be found.

## Get Case
* `GET /cases/{caseId}` will return the details of the case with the given case ID.

### Example JSON Response
```json
{
  "id": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
  "collectionInstrumentId": "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e",
  "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
  "actionPlanId": "5381731e-e386-41a1-8462-26373744db86",
  "caseRef":"1000000000000001",
  "iac": "fb747cq725lj",
  "sampleUnitType": "BI",
  "state": "INACTIONABLE",
  "createdBy": "SYSTEM",
  "createdDateTime": "2017-05-15T10:00:00Z",
  "responses": [
    {
      "inboundChannel": "ONLINE",
      "dateTime": "2017-05-17T16:15:20Z"
    }
  ],
  "caseGroup": {
    "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
    "collectionExerciseId": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "sampleUnitRef": "0123456789",
    "sampleUnitType": "B",
  },
  "caseEvents": [
    {
      "description": "Initial creation of case",
      "category": "CASE_CREATED",
      "subCategory": null,
      "createdBy": "SYSTEM",
      "createdDateTime": "2017-02-22T14:16:50Z"
    },
    {
      "description": "Create Household Visit",
      "category": "ACTION_CREATED",
      "subCategory": "HouseholdCreateVisit",
      "createdBy": "SYSTEM",
      "createdDateTime": "2017-04-10T08:48:49Z"    
    }
  ]
}
```

An `HTTP 404 Not Found` status code is returned if the case with the specified ID could not be found.

## Find Cases by Sample Unit Id
* `GET /cases/sampleunitids` will return the details of the case with the given sample unit ID.

### Example JSON Response
```json
{
  [
    {
        "state":"ACTIONABLE",
        "id":"15654d71-678b-481c-ac4a-8865699315de",
        "actionPlanId":"ac146769-b1ee-4097-b478-7f2954311517",
        "collectionInstrumentId":"08a27195-bccf-472c-bf3e-ccd7dc550274",
        "partyId":"08b70ee4-2219-48c7-a8e5-efcb02490627",
        "sampleUnitId":"cf8bf16f-6e14-47d2-b3d5-da0cb5466844",
        "iac":null,"caseRef":"1000000000000001",
        "createdBy":"SYSTEM",
        "sampleUnitType":"B",
        "createdDateTime":"2019-05-23T14:50:49.997Z",
        "caseGroup":
        {
            "collectionExerciseId":"aae2efaa-7070-4a7b-ac3d-3d932669528a",
            "id":"c9ad1cee-d4ca-4b73-8551-2c5cc10afea5",
            "partyId":"08b70ee4-2219-48c7-a8e5-efcb02490627",
            "sampleUnitRef":"49900000001",
            "sampleUnitType":"B",
            "caseGroupStatus":"NOTSTARTED",
            "surveyId":
            "cb8accda-6118-4d3b-85a3-149e28960c54"
        },
        "responses":[],
        "caseEvents":null
    }
  ]
}
```

An `HTTP 404 Not Found` status code is returned if the sample unit with the specified ID could not be found.

## List Cases for Party
* `GET /cases/partyid/{partyId}` will return a list of cases with the given party ID.

### Example JSON Response
```json
[
  {
    "id": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
    "collectionInstrumentId": "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "actionPlanId": "5381731e-e386-41a1-8462-26373744db86",
    "caseRef":"1000000000000001",
    "iac": "fb747cq725lj",
    "sampleUnitType": "BI",
    "state": "INACTIONABLE",
    "createdBy": "SYSTEM",
    "createdDateTime": "2017-05-15T10:00:00Z",
    "responses": [
      {
        "inboundChannel": "ONLINE",
        "dateTime": "2017-05-17T16:15:20Z"
      }
    ],
    "caseGroup": {
      "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
      "collectionExerciseId": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
      "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
      "sampleUnitRef": "0123456789",
      "sampleUnitType": "B",
      "caseGroupStatus": "INPROGRESS"
    },
    "caseEvents": [
      {
        "description": "Initial creation of case",
        "category": "CASE_CREATED",
        "subCategory": null,
        "createdBy": "SYSTEM",
        "createdDateTime": "2017-02-22T14:16:50Z"
      },
      {
        "description": "Create Household Visit",
        "category": "ACTION_CREATED",
        "subCategory": "HouseholdCreateVisit",
        "createdBy": "SYSTEM",
        "createdDateTime": "2017-04-10T08:48:49Z"    
      }
    ]
  }
]
```

An `HTTP 204 No Content` status code is returned if there are no cases found with the specified party ID.

## Find Cases by Survey ID
* `GET /cases/surveyid/{surveyId}` will return a list of cases with the given survey ID.

### Example JSON Response
```json
[
]
```

NOTE: For some reason, even when a valid survey ID is entered, the endpoint just returns a blank json message. Since no error message is generated, it can be assumed that the endpoint works, but is most likely redundant.

## Get Case by Unique Access Code
* `GET /cases/iac/{iac}` will return the details of the case with the unique access code provided.

### Example JSON Response
```json
{
  "id": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
  "collectionInstrumentId": "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e",
  "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
  "actionPlanId": "5381731e-e386-41a1-8462-26373744db86",
  "caseRef":"1000000000000001",
  "iac": "fb747cq725lj",
  "sampleUnitType": "BI",
  "state": "INACTIONABLE",
  "createdBy": "SYSTEM",
  "createdDateTime": "2017-05-15T10:00:00Z",
  "responses": [
    {
      "inboundChannel": "ONLINE",
      "dateTime": "2017-05-17T16:15:20Z"
    }
  ],
  "caseGroup": {
    "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
    "collectionExerciseId": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
    "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "sampleUnitRef": "0123456789",
    "sampleUnitType": "B",
    "caseGroupStatus": "INPROGRESS"
  },
  "caseEvents": [
    {
      "description": "Initial creation of case",
      "category": "CASE_CREATED",
      "subCategory": null,
      "createdBy": "SYSTEM",
      "createdDateTime": "2017-02-22T14:16:50Z"
    },
    {
      "description": "Create Household Visit",
      "category": "ACTION_CREATED",
      "subCategory": "HouseholdCreateVisit",
      "createdBy": "SYSTEM",
      "createdDateTime": "2017-04-10T08:48:49Z"    
    }
  ]
}
```

An `HTTP 404 Not Found` status code is returned if the case with the specified unique access code could not be found.

## List Case Events for Case
* `GET /cases/{caseId}/events` will return a list of case events for the case with given case ID.

&mdash;an optional `category` string query parameter can be used to specify that the JSON response include matching events with the case for said case id. For example:

* `GET /cases/{caseId}/events?category=OFFLINE_RESPONSE_PROCESSED`

If this query parameter is omitted all events for said case ID will be returned.

### Example JSON Response
```json
[
  {
    "description": "Initial creation of case",
    "category": "CASE_CREATED",
    "subCategory": null,
    "createdBy": "SYSTEM",
    "createdDateTime": "2017-02-22T14:16:50Z"
  },
  {
    "description": "Create Household Visit",
    "category": "ACTION_CREATED",
    "subCategory": "HouseholdCreateVisit",
    "createdBy": "SYSTEM",
    "createdDateTime": "2017-04-10T08:48:49Z"    
  }
]
```

An `HTTP 404 Not Found` status code is returned if the case with the specified ID could not be found. An `HTTP 204 No Content` status code is returned if there are no case events for the case with the specified ID.

## Create Case Event
* `POST /cases/{caseId}/events` will create a case event for the case with the given case ID.

**Required parameters:** `description` as the description of the case event, `category` as the category of the case event and `createdBy` as the creator of the case event.

*Optional parameters:* `subCategory` as additional free text to describe the case event, `partyId` as the ID of the party to create a new case for, as a side effect of a case creation event.


### Example JSON Request
```json
{
  "description": "Initial creation of case",
  "category": "CASE_CREATED",
  "subCategory": null,
  "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
  "createdBy": "Fred Bloggs"
}
```

### Example JSON Response
```json
{
  "caseId": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
  "description": "Initial creation of case",
  "category": "CASE_CREATED",
  "subCategory": null,
  "partyId": "3b136c4b-7a14-4904-9e01-13364dd7b972",
  "createdBy": "Fred Bloggs",
  "createdDateTime": "2017-04-10T08:48:49Z"  
}
```

An `HTTP 201 Created` status code is returned if the case event creation was a success. An `HTTP 404 Not Found` status code is returned if the case with the specified ID could not be found. An `HTTP 400 Bad Request` is returned if any of the required parameters are missing.

## List Categories
* `GET /categories` will return a list of categories available when creating a case event.

 ### Example JSON Response
```json
[
  {
    "name": "CASE_CREATED",
    "shortDescription": "Case Created",
    "longDescription": "Case Created",
    "role": null,
    "group": null
  },
  {
    "name": "GENERAL_ENQUIRY",
    "shortDescription": "General Enquiry",
    "longDescription": "General Enquiry",
    "role": "collect-csos, collect-admins",
    "group": "general"
  }
]
```

An `HTTP 204 No Content` status code is returned if there are no available categories.

## Get Category
* `GET /categories/name/{categoryName}` will return the details of the category with the name provided.

### Example JSON Response
```json
{
  "name": "CASE_CREATED",
  "shortDescription": "Case Created",
  "longDescription": "Case Created",
  "role": null,
  "group": null
}
```

An `HTTP 404 Not Found` status code is returned if the category with the specified name could not be found.

## Get IAC by Case ID
* `GET /cases/{caseId}/iac` will return the IAC corresponding to the specified case ID.

### Example JSON Response
```json
[
    {
        "iac":"wkbgygjrzbvw"
    }
]

An `HTTP 404 Not Found` status code is returned if the case with the specified ID could not be found.