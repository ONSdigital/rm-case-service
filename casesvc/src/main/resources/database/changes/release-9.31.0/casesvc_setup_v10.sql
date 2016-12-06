--updates for behavioural insights casetypes
UPDATE casesvc.sample SET name = 'C2EO331BIE', description = 'component 2 no sex id online first 331 england beavioural insights' , addresscriteria = 'SAMPLE = C2EO331BIE' WHERE name = 'C2EO321E';
UPDATE casesvc.sample SET name = 'C2EO332BIE', description = 'component 2 no sex id online first 332 england beavioural insights' , addresscriteria = 'SAMPLE = C2EO332BIE' WHERE name = 'C2EO322E';
UPDATE casesvc.casetype SET name = 'HC2EO331BIE', description = 'component 2 no sex id online first 331 england beavioural insights' WHERE name = 'HC2EO321E';
UPDATE casesvc.casetype SET name = 'HC2EO332BIE', description = 'component 2 no sex id online first 332 england beavioural insights' WHERE name = 'HC2EO322E';
UPDATE casesvc.actionplanmapping SET actionplanid = 55 WHERE actionplanid = 12 ;
UPDATE casesvc.actionplanmapping SET actionplanid = 56 WHERE actionplanid = 13 ;

--correct space in sample table (CTPA - 957)
UPDATE casesvc.sample SET addresscriteria = 'SAMPLE = SHOUSING' WHERE name = 'SHOUSING';

--make category table consistent with hyphens (CTPA- 937)
UPDATE casesvc.category SET shortdescription = 'Field Complaint - Escalated', longdescription = 'Field Complaint - Escalated' WHERE name = 'FIELD_COMPLAINT_ESCALATED';
UPDATE casesvc.category SET shortdescription = 'Field Emergency - Escalated', longdescription = 'Field Emergency - Escalated' WHERE name = 'FIELD_EMERGENCY_ESCALATED';


