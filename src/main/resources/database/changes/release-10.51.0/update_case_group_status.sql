UPDATE casesvc.casegroup AS CG
SET status = 'NOTSTARTED';

UPDATE casesvc.casegroup AS CG
SET status = 'INPROGRESS'
FROM (select * from casesvc.caseevent
		inner join casesvc.case on casepk = casefk
     		where categoryfk='COLLECTION_INSTRUMENT_DOWNLOADED') AS CE
WHERE CG.casegrouppk = CE.casegroupfk;

UPDATE casesvc.casegroup AS CG
SET status = 'COMPLETE'
FROM (select * from casesvc.caseevent
		inner join casesvc.case on casepk = casefk
     		where categoryfk='SUCCESSFUL_RESPONSE_UPLOAD') AS CE
WHERE CG.casegrouppk = CE.casegroupfk;