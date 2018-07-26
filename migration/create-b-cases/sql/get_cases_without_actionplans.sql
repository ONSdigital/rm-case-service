SELECT c.id, cg.collectionexerciseid
FROM casesvc."case" as c
INNER JOIN casesvc.casegroup as cg
  ON c.casegroupfk = cg.casegrouppk
WHERE actionplanid IS NULL
AND c.sampleunittype = 'B';