UPDATE casesvc.category
SET oldcasesampleunittypes = 'B,BI,H'
WHERE categorypk IN ('COLLECTION_INSTRUMENT_DOWNLOADED', 'COLLECTION_INSTRUMENT_ERROR',
'UNSUCCESSFUL_RESPONSE_UPLOAD', 'SUCCESSFUL_RESPONSE_UPLOAD');
