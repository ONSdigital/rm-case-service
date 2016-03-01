
set schema 'caseframe';

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = caseframe, pg_catalog;




INSERT INTO survey VALUES (1, '2016_TEST', '2016 Census Test');


INSERT INTO questionset VALUES ('HH', 'Households');
INSERT INTO questionset VALUES ('CE', 'Communal Establishments');


INSERT INTO actionplan VALUES (1, 'HH_APL1', 'Household Action Plan 1', 'IAC letter');
INSERT INTO actionplan VALUES (3, 'HGH_APL1', 'Care Home Action Plan 1', 'IAC letter');
INSERT INTO actionplan VALUES (2, 'HGH_APL1', 'Hotel and Guest House Action Plan 1', 'visit');



INSERT INTO casetype VALUES (1, 'HH', 'Household', 1, 'HH');
INSERT INTO casetype VALUES (3, 'CH', 'Care Home', 3, 'CE');
INSERT INTO casetype VALUES (2, 'HGH', 'Hotel Guest House Bed and Breakfast', 2, 'CE');




INSERT INTO sample VALUES (1, 'Residential', 'Households', 'addresstype = ''HH''', 1, 1);
INSERT INTO sample VALUES (3, 'Care_Homes', 'Care Homes', 'addresstype = ''CE'' and estabtype in (''22'')', 3, 1);
INSERT INTO sample VALUES (2, 'Hotels_Guest_Houses', 'Hotels Guest Houses Bed and Breakfasts', 'addresstype = ''CE'' and estabtype in (''25'')', 2, 1);




