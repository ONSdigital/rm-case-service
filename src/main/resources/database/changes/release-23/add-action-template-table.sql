CREATE TABLE action_template (
    name varchar (40) UNIQUE NOT NULL,
    description varchar(350) NOT NULL,
    event_tag_mapping varchar(100) NOT NULL,
    handler varchar CHECK (handler = 'EMAIL' OR handler = 'LETTER'),
    prefix varchar (100));

INSERT INTO action_template
    (name, description, event_tag_mapping, handler, prefix)
    VALUES
    ('BSSNE', 'Survey Reminder Notification', 'go_live', 'EMAIL', NULL),
    ('BSNL', 'Business Survey Notification Letter', 'mps', 'LETTER', 'BSNOT'),
    ('BSNE', 'Business Survey Notification Email', 'go_live', 'EMAIL', NULL),
    ('BSRL', 'Business Survey Reminder Letter', 'reminder', 'LETTER', 'BSREM'),
    ('BSRE', 'Business Survey Reminder Email', 'reminder', 'EMAIL', NULL),
    ('BSNUE', 'Business Survey Nudge Email', 'nudge', 'EMAIL', NULL);