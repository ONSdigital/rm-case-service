update casesvc.casegroup cg
set surveyid = ct.surveyid
from casesvc.case_temp ct
where cg.collectionexerciseid = ct.collectionexerciseid