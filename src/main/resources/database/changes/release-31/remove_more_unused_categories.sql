DELETE FROM casesvc.caseevent WHERE category_fk = 'LANGUAGE_DIFFICULTIES';
DELETE FROM casesvc.category WHERE category_pk = 'LANGUAGE_DIFFICULTIES';

DELETE FROM casesvc.caseevent WHERE category_fk = 'TOO_BUSY';
DELETE FROM casesvc.category WHERE category_pk = 'TOO_BUSY';

DELETE FROM casesvc.caseevent WHERE category_fk = 'NON_RESIDENTIAL_ADDRESS';
DELETE FROM casesvc.category WHERE category_pk = 'NON_RESIDENTIAL_ADDRESS';