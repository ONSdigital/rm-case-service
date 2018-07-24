INSERT INTO casesvc.case (
  casepk,
  id,
  casegroupfk,
  casegroupid,
  partyid,
  sampleunittype,
  collectioninstrumentid,
  statefk,
  actionplanid,
  createdby,
  createddatetime,
  optlockversion
)
SELECT
  nextval('casesvc.caseseq'),
  gen_random_uuid(),
  cg.casegrouppk,
  cg.id,
  cg.partyid,
  'B',
  bicases.collectioninstrumentid,
  'INACTIONABLE',
  cto.actionplanid,
  'MIGRATION_SCRIPT',
  now(),
  1
FROM casesvc.casegroup as cg

-- Retrieve the collectioninstrumentid from a BI case for each casegroup
INNER JOIN
  (
    SELECT DISTINCT ON (casegroupfk)
      casegroupfk, collectioninstrumentid
    FROM casesvc."case"
    WHERE sampleunittype = 'BI'
  ) as bicases
  ON cg.casegrouppk = bicases.casegroupfk

-- Couple of inner joins to get the actionplanid from casetypeoverride table
INNER JOIN (
             SELECT id, exercisepk
             FROM collectionexercise.collectionexercise
           ) as ce
  ON ce.id = cg.collectionexerciseid
INNER JOIN (
             SELECT exercisefk, sampleunittypefk, actionplanid
             FROM collectionexercise.casetypeoverride
           ) as cto
  ON ce.exercisepk = cto.exercisefk AND cto.sampleunittypefk = 'B'

-- Only add row for casegroup if no B cases exists for it
LEFT JOIN (
            SELECT casegroupfk
            FROM casesvc."case"
            WHERE sampleunittype = 'B'
          ) as bcases
  ON cg.casegrouppk = bcases.casegroupfk
WHERE bcases IS NULL;