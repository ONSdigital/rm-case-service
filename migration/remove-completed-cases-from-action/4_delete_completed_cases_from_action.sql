delete from action.case
WHERE id in (SELECT case_id FROM action.temp_cases)
