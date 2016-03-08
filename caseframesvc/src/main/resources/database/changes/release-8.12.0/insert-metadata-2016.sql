set schema 'caseframe';

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = caseframe, pg_catalog;

DELETE FROM sample;

DELETE FROM survey;

DELETE FROM casetype;


INSERT INTO survey VALUES (1, '2016 Test', '2016 Census Test');

INSERT INTO casetype VALUES (1, 'HH', 'Household', 1, 'HH');
INSERT INTO casetype VALUES (2, 'HGH', 'Hotel Guest House Bed and Breakfast', 2, 'CE');
INSERT INTO casetype VALUES (3, 'CH', 'Care Home', 3, 'CE');

INSERT INTO sample VALUES (1, 'Residential', 'Households', 'addresstype = ''HH''', 1, 1);
INSERT INTO sample VALUES (2, 'Hotels Guest Houses', 'Hotels Guest Houses Bed and Breakfasts', 'addresstype = ''CE'' and estabtype in (''CH'')', 2, 1);
INSERT INTO sample VALUES (3, 'Care Homes', 'Care Homes', 'addresstype = ''CE'' and estabtype in (''RI01'')', 3, 1);


