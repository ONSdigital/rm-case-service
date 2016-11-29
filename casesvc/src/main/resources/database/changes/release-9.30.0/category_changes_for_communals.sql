--Changes to category to distinguish between communal and household individuals

--drop constraint on caseevent referencing category
ALTER TABLE casesvc.caseevent DROP CONSTRAINT category_fkey;

UPDATE casesvc.category
SET name = 'H_INDIVIDUAL_RESPONSE_REQUESTED'
,longdescription = 'Household Individual Response Requested'
,shortdescription = 'Household Individual Response Requested'
WHERE name = 'INDIVIDUAL_RESPONSE_REQUESTED';

UPDATE casesvc.category
SET name = 'H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED'
,longdescription = 'Household Individual Replacement IAC Requested'
,shortdescription = 'Household Individual Replacement IAC Requested'
WHERE name = 'INDIVIDUAL_REPLACEMENT_IAC_REQUESTED';

UPDATE casesvc.category
SET name = 'H_INDIVIDUAL_PAPER_REQUESTED'
,longdescription = 'Household Individual Paper Requested'
,shortdescription = 'Household Individual Paper Requested'
WHERE name = 'INDIVIDUAL_PAPER_REQUESTED';

INSERT INTO casesvc.category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) 
VALUES ('C_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED', 'Communal Individual Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Communal Individual Replacement IAC Requested', 'CI', 'CI');

--add constraint on caseevent referencing category
ALTER TABLE ONLY casesvc.caseevent ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES casesvc.category(name);
