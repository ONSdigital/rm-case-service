# Case Service API
This page documents the Case service API endpoints. These endpoints will be secured using HTTP basic authentication initially. All endpoints return an `HTTP 200 OK` status code except where noted otherwise.

## Get Case Group
* `GET /casegroups/9a5f2be5-f944-41f9-982c-3517cfcfef3c` will return the details of the case group with an ID of `9a5f2be5-f944-41f9-982c-3517cfcfef3c`.

### Example JSON  Response
```json
{
  "id": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
  "collectionExerciseID": "dab9db7f-3aa0-4866-be20-54d72ee185fb",
  "partyID": "3b136c4b-7a14-4904-9e01-13364dd7b972"
}
```

An `HTTP 404 Not Found` status code is returned if the case group with the specified ID could not be found.

## List Cases for Case Group
* `GET /cases/casegroupid/9a5f2be5-f944-41f9-982c-3517cfcfef3c` will return a list of cases for the case group with an ID of `9a5f2be5-f944-41f9-982c-3517cfcfef3c`.

### Example JSON  Response
```json
[
  {
    "id": "7bc5d41b-0549-40b3-ba76-42f6d4cf3fdb",
    "caseGroupID": "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
    "collectionInstrumentID": "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e",
    "partyID": "3b136c4b-7a14-4904-9e01-13364dd7b972",
    "actionPlanID": "5381731e-e386-41a1-8462-26373744db86",
    "sampleUnitRef": "0123456789",
    "sampleUnitType": "B",
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

*More APIs to follow...*