\copy (
  SELECT ce.id AS collectionexerciseid, cto.actionplanid
    FROM collectionexercise.casetypeoverride AS cto
         INNER JOIN (
           SELECT id, exercisepk
             FROM collectionexercise.collectionexercise
         ) AS ce
         ON ce.exercisepk = cto.exercisefk
   WHERE cto.sampleunittypefk = 'B'
) TO STDOUT WITH CSV;