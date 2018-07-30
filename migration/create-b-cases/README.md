# Create Missing B Cases Scripts
The scripts contained in the folder are to be used to create B cases in the case service for the casegroups that have no B cases

We require information from both the case and collection exercise databases.
For this reason a temporary table will have to be created in the case service from collection exercise data.

## Instructions
#### Create temporary actionplanid table
The cases we create will need the correct actionplanid's.
We currently find the correct actionplanid using the casetypeoverride table in the collection exercise service.
A temporary table will be created in the case service so we have access to this table

Execute the `select-temp-cto-table.sql` script to a csv file to retrieve the required data.
Then simply import this table into the case database with name `temp_cto` and headers `collectionexerciseid` and `actionplanid`.

#### Create new B cases
Once the temporary table has been created simply execute the `create-b-cases.sql` script to create missing B cases.