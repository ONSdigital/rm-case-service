UPDATE casesvc.category
SET oldcasesampleunittypes = 'H,'||oldcasesampleunittypes
WHERE categorypk = 'OFFLINE_RESPONSE_PROCESSED'
