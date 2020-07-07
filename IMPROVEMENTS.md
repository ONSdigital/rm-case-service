1. Remove the /cases endpoint as this retrieves every single case in the database and in production 
we have 1.8 million at last count. Also for each case it retrieves it calls back to the database to 
get the case group. This could result in 1.8 million queries.
1. Investigate the case endpoint /partyid/{partyId} and sort out the filtering that happens in code 
when you provide the max_cases_per_survey parameter
1. Investigate if we can remove one of /case/{id}?caseevents=true or /case/{id}/caseevents as they appear
to do the same thing put have been implemented twice.
1. Do we need the case-iac-endpoint as the same information is available via the case endpoint?
1. Remove caseCreation.xsd as it isn't used and has been replaced by SampleUnitNotification.xsd
1. Is the response table needed? It appears to be written to but never read from?
1. Is the report table needed? If not, we can remove the two stored procedures that run nightly.
1. The message log table can be deleted if we remove the stored procedures.
1. Do we need the event publisher? What is that queue used for?