UPDATE caseframe.category set closecase=NULL where caseframe.category.name LIKE 'Closed';

UPDATE caseframe.category set role='collect-csos, collect-escalate, collect-admins' where caseframe.category.name LIKE 'Refusal';

INSERT INTO caseframe.category VALUES ('IncorrectEscalation', 'Incorrect Escalation', NULL, true, 'collect-escalate, collect-admins', NULL);