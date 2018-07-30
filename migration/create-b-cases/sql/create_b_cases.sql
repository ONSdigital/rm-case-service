INSERT INTO casesvc."case" (casepk, id, casegroupfk, casegroupid, partyid, sampleunittype, collectioninstrumentid, statefk, actionplanid, createdby, createddatetime, optlockversion)
SELECT nextval('casesvc.caseseq'), gen_random_uuid(), cg.casegrouppk, cg.id, cg.partyid, 'B', bicases.collectioninstrumentid, 'INACTIONABLE', null, 'MIGRATION_SCRIPT', now(), 1
  FROM casesvc.casegroup AS cg
       INNER JOIN (
          SELECT DISTINCT ON (casegroupfk) casegroupfk, collectioninstrumentid
            FROM casesvc."case"
           WHERE sampleunittype = 'BI'
       ) AS bicases
       ON cg.casegrouppk = bicases.casegroupfk
    LEFT JOIN (
        SELECT casegroupfk
          FROM casesvc."case"
         WHERE sampleunittype = 'B') AS bcases
    ON cg.casegrouppk = bcases.casegroupfk
WHERE bcases IS NULL;