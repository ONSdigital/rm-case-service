SELECT cto.actionplanid
  FROM collectionexercise.casetypeoverride AS cto
       INNER JOIN collectionexercise.collectionexercise AS ce
       ON ce.id = '{0}'
  WHERE ce.exercisepk = cto.exercisefk
    AND cto.sampleunittypefk = 'B';