SELECT c.id, cg.collectionexerciseid
  FROM casesvc."case" AS c
       INNER JOIN casesvc.casegroup AS cg
       ON c.casegroupfk = cg.casegrouppk
  WHERE actionplanid IS NULL
    AND c.sampleunittype = 'B';