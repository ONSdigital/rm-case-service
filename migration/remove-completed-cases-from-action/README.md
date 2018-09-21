## Delete completed cases from action service
This folder contains scripts to delete all cases from the action service that have completed case group statuses.

### Scripts
Export the case ids of cases that have a completed case group status but are in state actionable to a csv file

```
psql "{CASE_POSTGRES_URI}" -f 1_get_case_ids_of_completed_actionable_cases_to_csv.sql > temp_cases.csv
```

Create temporary table for case ids in action service
```
psql "{ACTION_POSTGRES_URI}" -f 2_create_temporary_table_in_action.sql
```

Copy case ids from the csv to temporary table
```
psql "{ACTION_POSTGRES_URI}" -c "COPY actionsvc.temp_cases(case_id) FROM STDIN WITH CSV;" < temp_cases.csv
```

Delete completed cases from action service
```
psql "{ACTION_POSTGRES_URI}" -f 4_delete_completed_cases_from_action.sql
```