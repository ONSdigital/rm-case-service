#Adding surveyid to Case-Groups

The scripts in this folder are used to add the surveyid to the case groups table

##Scripts

Getting all executed collection exercises with survey ids

`psql "{COLLEX_POSTGRES_URI}" -f 1_get-collection-exercises.sql > temp_collex.csv`

Creating temporary table

`psql "{CASE_POSTGRES_URI}" -f 2_create-temp-table.sql`

importing temporary table

`psql "{CASE_POSTGRES_URI}" -f 3_import-temp-table.sql`

Adding survey id to the case groups table

`psql "{CASE_POSTGRES_URI}" -f 4_add-surveyid-to-case-groups.sql`

Dropping temporary table

`psql "{CASE_POSTGRES_URI}" -f 5_drop-temp-table.sql`