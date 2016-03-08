
--drop action and actionplan table due to these now being in the actionservice

set schema 'caseframe';

DROP TABLE action CASCADE;

DROP TABLE actionplan CASCADE;
