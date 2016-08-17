## To build
./mvnw clean install


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/actionsvc 
sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


## To test action plans
curl http://localhost:8151/actionplans -v -X GET
200 [{"actionPlanId":1,"surveyId":1,"name":"HH","description":"Household Action Plan","createdBy":"SYSTEM","lastRunDateTime":null},{"actionPlanId":2,"surveyId":1,"name":"CH","description":"Care Home Action Plan","createdBy":"SYSTEM","lastRunDateTime":null},{"actionPlanId":3,"surveyId":1,"name":"HGH","description":"Hotel and Guest House Action Plan","createdBy":"SYSTEM","lastRunDateTime":null}]

curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8151/actionplans/1 -v -X PUT -d "{\"description\":\"philippe2testing\"}"
200 {"actionPlanId":1,"surveyId":1,"name":"HH","description":"philippe2testing","createdBy":"SYSTEM","lastRunDateTime":null}


## To test action plan jobs
curl http://localhost:8151/actionplans/1/jobs -v -X GET
204 No Content

curl http://localhost:8151/actionplans/jobs/1 -v -X GET
404 {"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160801120637483","message":"ActionPlanJob not found for id 1"}}

curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8151/actionplans/1/jobs -v -X POST -d "{\"createdBy\":\"philippeb\"}"
{"error":{"code":"RESOURCE_NOT_FOUND","timestamp":"20160801120711682","message":"ActionPlan not found for id 1"}}

curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8151/actionplans/1/jobs -v -X POST -d "{\"created\":\"philippeb\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20160801120749337","message":"Provided json is incorrect."}}
