UPDATE casesvc.category
SET oldcasesampleunittypes='BI,H'
WHERE categorypk='SUCCESSFUL_RESPONSE_UPLOAD';

UPDATE casesvc.category
SET oldcasesampleunittypes='B,H'
WHERE categorypk='ACCESS_CODE_AUTHENTICATION_ATTEMPT';

UPDATE casesvc.category
SET oldcasesampleunittypes='B,BI,H'
WHERE categorypk='ACTION_CANCELLATION_CREATED'
OR categorypk='COMPLETED_BY_PHONE'
OR categorypk='NO_LONGER_REQUIRED'
OR categorypk='ACTION_CANCELLATION_COMPLETED'
OR categorypk='EQ_LAUNCH'
OR categorypk='CASE_CREATED'
OR categorypk='ACTION_COMPLETED'
OR categorypk='ACTION_UPDATED'
OR categorypk='ACTION_CREATED';