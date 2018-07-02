ALTER TABLE ONLY casesvc.category DROP manual;

INSERT INTO casesvc.category(name, longdescription, eventtype, role, generatedactiontype, "group", shortdescription, newCaseSampleUnitType, oldCaseSampleUnitType) VALUES ('RESPONDENT_ENROLLED', 'Respondent Enrolled', 'DEACTIVATED', null, null, null, 'Respondent Enrolled', 'BI', 'B');
INSERT INTO casesvc.category(name, longdescription, eventtype, role, generatedactiontype, "group", shortdescription, newCaseSampleUnitType, oldCaseSampleUnitType) VALUES ('INDIVIDUAL_RESPONSE_REQUESTED', 'Individual Response Requested', 'ACTIVATED', 'TBD', 'TBD', 'TBD', 'Individual Response Requested', 'H', 'HI');
