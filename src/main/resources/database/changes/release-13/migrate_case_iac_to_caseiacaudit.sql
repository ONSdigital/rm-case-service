INSERT INTO casesvc.caseiacaudit (casefk, iac, createddatetime)
SELECT c.casepk, c.iac, c.createddatetime
FROM casesvc."case" AS c
WHERE c.iac NOTNULL
  AND NOT
          EXISTS(
            SELECT 1
            FROM casesvc.caseiacaudit AS cia
            WHERE cia.casefk = c.casepk
              AND cia.iac = c.iac
              );
