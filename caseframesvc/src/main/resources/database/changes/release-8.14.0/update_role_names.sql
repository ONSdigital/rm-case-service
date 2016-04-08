set schema 'caseframe';
UPDATE category SET role = 'collect-csos, collect-admins' WHERE manual = true;
UPDATE category SET role = 'collect-escalate, collect-admins' WHERE name = 'Pending';
UPDATE category SET role = 'collect-escalate, collect-admins' WHERE name = 'Closed';
COMMIT;
