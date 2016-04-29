--new entry into category table for category of 'UNDELIVERABLE' - same as refusal so will close the case.

INSERT INTO caseframe.category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Undeliverable', NULL, true, true, 'collect-csos, collect-admins', NULL);
