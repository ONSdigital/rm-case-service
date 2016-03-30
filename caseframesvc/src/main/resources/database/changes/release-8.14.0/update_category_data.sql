set schema 'caseframe';
UPDATE category SET generatedactiontype = 'ESC_GENERAL' WHERE name = 'General Enquiry - Escalated';
UPDATE category SET generatedactiontype = 'ESC_COMPLAINT' WHERE name = 'Complaint - Escalated';
UPDATE category SET generatedactiontype = 'ESC_SURVEY' WHERE name = 'Survey Enquiry - Escalated';
