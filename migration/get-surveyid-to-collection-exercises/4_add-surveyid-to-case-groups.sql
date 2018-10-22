update casesvc.casegroup cg
set surveyid = ct.survey_uuid
from casesvc.case_temp ct
where cg.collectionexerciseid = ct.collectionexerciseid