--change status to state on case and questionnaire table



ALTER TABLE caseframe.case RENAME COLUMN status TO state;

ALTER TABLE caseframe.questionnaire RENAME COLUMN status TO state;
