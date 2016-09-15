-- The data in this file populates the casetype, questionset and sample tables with data based on v 1.2 of the actionplans
-- This is NOT THE FINAL DATA and will be subject to change


SET SCHEMA 'casesvc';

DELETE from CASESVC.SAMPLE;
DELETE from CASESVC.CASETYPE;
DELETE from CASESVC.QUESTIONSET;

--
-- Data for Name: questionset; Type: TABLE DATA; Schema: casesvc; Owner: role_connect
--

INSERT INTO questionset (questionset, description) VALUES ('HH', 'Households');
INSERT INTO questionset (questionset, description) VALUES ('CE', 'Communal Establishments');
INSERT INTO questionset (questionset, description) VALUES ('H1S', 'Household questionnaire for England with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('H2S', 'Household questionnaire for Wales (in English) with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('H2WS', 'Household questionnaire for Wales (in Welsh) with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I1S', 'Individual questionnaire for England with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I2S', 'Individual questionnaire for Wales ( in English) with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I2WS', 'Individual questionnaire for Wales ( in Welsh) with sexual id');
INSERT INTO questionset (questionset, description) VALUES ('H1', 'Household questionnaire for England without sexual id');
INSERT INTO questionset (questionset, description) VALUES ('H2', 'Household questionnaire for Wales ( in English) without sexual id');
INSERT INTO questionset (questionset, description) VALUES ('H2W', 'Household questionnaire for Wales ( in Welsh) without sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I1', 'Individual questionnaire for England without sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I2', 'Individual questionnaire for Wales ( in English ) without sexual id');
INSERT INTO questionset (questionset, description) VALUES ('I2W', 'Individual questionnaire for Wales ( in Welsh ) without sexual id');


--
-- Data for Name: casetype; Type: TABLE DATA; Schema: casesvc; Owner: role_connect
--

INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (1, 'treatment_1e', 'treatment 1 (england)', 1, 'H1S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (2, 'treatment_1w', 'treatment 1 (wales)', 2, 'H2S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (3, 'treatment_2e', 'treatment 2 (england)', 3, 'H1S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (4, 'treatment_2w', 'treatment 2 (wales)', 4, 'H2S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (5, 'treatment_3e', 'treatment 3 (england)', 5, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (6, 'treatment_3w', 'treatment 3 (wales)', 6, 'H2');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (7, 'treatment_4e', 'treatment 4 (england)', 7, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (8, 'treatment_4w', 'treatment 4 (wales)', 8, 'H2');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (9, 'treatment_5e', 'treatment 5 (england)', 9, 'H1S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (10, 'treatment_5w', 'treatment 5 (wales)', 10, 'H2S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (11, 'treatment_6e', 'treatment 6 (england)', 11, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (12, 'treatment_6w', 'treatment 6 (wales)', 12, 'H2');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (13, 'treatment_7e', 'treatment 7 (england)', 13, 'H1S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (14, 'treatment_7w', 'treatment 7 (wales)', 14, 'H2S');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (15, 'treatment_8e', 'treatment 8 (england)', 15, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (16, 'treatment_8w', 'treatment 8 (wales)', 16, 'H2');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (17, 'treatment_9', 'treatment 9', 17, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (18, 'treatment_10', 'treatment 10', 18, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (19, 'treatment_11', 'treatment 11', 19, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (20, 'treatment_12', 'treatment 12', 20, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (21, 'treatment_13', 'treatment 13', 21, 'H1');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (22, 'treatment_14', 'treatment 14', 22, 'H1');


--
-- Data for Name: sample; Type: TABLE DATA; Schema: casesvc; Owner: role_connect
--


INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (1, 'treatment_1e', 'treatment 1 (england)', 'category = ''1'' and region11cd like ''E%''', 1, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (2, 'treatment_1w', 'treatment 1 (wales)', 'category = ''1'' and region11cd like ''W%''', 2, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (3, 'treatment_2e', 'treatment 2 (england)', 'category = ''2'' and region11cd like ''E%''', 3, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (4, 'treatment_2w', 'treatment 2 (wales)', 'category = ''2'' and region11cd like ''W%''', 4, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (5, 'treatment_3e', 'treatment 3 (england)', 'category = ''3'' and region11cd like ''E%''', 5, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (6, 'treatment_3w', 'treatment 3 (wales)', 'category = ''3'' and region11cd like ''W%''', 6, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (7, 'treatment_4e', 'treatment 4 (england)', 'category = ''4'' and region11cd like ''E%''', 7, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (8, 'treatment_4w', 'treatment 4 (wales)', 'category = ''4'' and region11cd like ''W%''', 8, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (9, 'treatment_5e', 'treatment 5 (england)', 'category = ''5'' and region11cd like ''E%''', 9, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (10, 'treatment_5w', 'treatment 5 (wales)', 'category = ''5'' and region11cd like ''W%''', 10, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (11, 'treatment_6e', 'treatment 6 (england)', 'category = ''6'' and region11cd like ''E%''', 11, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (12, 'treatment_6w', 'treatment 6 (wales)', 'category = ''6'' and region11cd like ''W%''', 12, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (13, 'treatment_7e', 'treatment 7 (england)', 'category = ''7'' and region11cd like ''E%''', 13, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (14, 'treatment_7w', 'treatment 7 (wales)', 'category = ''7'' and region11cd like ''W%''', 14, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (15, 'treatment_8e', 'treatment 8 (england)', 'category = ''8'' and region11cd like ''E%''', 15, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (16, 'treatment_8w', 'treatment 8 (wales)', 'category = ''8'' and region11cd like ''W%''', 16, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (17, 'treatment_9', 'treatment 9', 'category = ''9'' and region11cd like ''E%''', 17, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (18, 'treatment_10', 'treatment 10', 'category = ''10'' and region11cd like ''E%''', 18, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (19, 'treatment_11', 'treatment 11', 'category = ''11'' and region11cd like ''E%''', 19, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (20, 'treatment_12', 'treatment 12', 'category = ''12'' and region11cd like ''E%''', 20, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (21, 'treatment_13', 'treatment 13', 'category = ''13'' and region11cd like ''E%''', 21, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (22, 'treatment_14', 'treatment 14', 'category = ''14'' and region11cd like ''E%''', 22, 1);

