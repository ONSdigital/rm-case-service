UPDATE casesvc.category
SET shortdescription='Action Created', longdescription='Action Created', eventtype=NULL, "role"=NULL, generatedactiontype=NULL, "group"=NULL, oldcasesampleunittypes='B,BI,H', newcasesampleunittype=NULL, recalccollectioninstrument=NULL
WHERE categorypk='ACTION_CREATED' OR categorypk='ACTION_UPDATED' OR categorypk='ACTION_COMPLETED' OR categorypk='CASE_CREATED';
UPDATE casesvc.category
SET shortdescription='Action Created', longdescription='Action Created', eventtype=NULL, "role"=NULL, generatedactiontype=NULL, "group"=NULL, oldcasesampleunittypes='BI,H', newcasesampleunittype=NULL, recalccollectioninstrument=NULL
WHERE categorypk='SUCCESSFUL_RESPONSE_UPLOAD';