--inserts setup data into casesvc for 2017 test based on v2 of the setup data provided by the business area.

set schema 'casesvc';

INSERT INTO questionset (questionset) VALUES ('H1S');
INSERT INTO questionset (questionset) VALUES ('H2S');
INSERT INTO questionset (questionset) VALUES ('H2WS');
INSERT INTO questionset (questionset) VALUES ('I1S');
INSERT INTO questionset (questionset) VALUES ('I2S');
INSERT INTO questionset (questionset) VALUES ('I2WS');
INSERT INTO questionset (questionset) VALUES ('H1');
INSERT INTO questionset (questionset) VALUES ('H2');
INSERT INTO questionset (questionset) VALUES ('H2W');
INSERT INTO questionset (questionset) VALUES ('I1');
INSERT INTO questionset (questionset) VALUES ('I2');
INSERT INTO questionset (questionset) VALUES ('I2W');


--
-- TOC entry 3582 (class 0 OID 54634)
-- Dependencies: 377
-- Data for Name: respondenttype; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO respondenttype (respondenttype) VALUES ('H');
INSERT INTO respondenttype (respondenttype) VALUES ('I');


--
-- TOC entry 3579 (class 0 OID 54613)
-- Dependencies: 371
-- Data for Name: casetype; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1, 'HC1SO331D4E', 'component 1 sex id online first 331 day 4 england household', 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (2, 'HC1SO331D4W', 'component 1 sex id online first 331 day 4 wales household', 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (3, 'HC1SO331D10E', 'component 1 sex id online first 331 day 10 england household', 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (4, 'HC1SO331D10W', 'component 1 sex id online first 331 day 10 wales household', 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (5, 'HC1EO331D4E', 'component 1 no sex id online first 331 day 4 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (6, 'HC1EO331D4W', 'component 1 no sex id online first 331 day 4 wales household', 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (7, 'HC1EO331D10E', 'component 1 no sex id online first 331 day 10 england ousehold', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (8, 'HC1EO331D10W', 'component 1 no sex id online first 331 day 10 wales household', 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (9, 'HC2SP331E', 'component 2 sex id paper first 331 england household', 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (10, 'HC2SP331W', 'component 2 sex id paper first 331 wales household', 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (11, 'HC2EP331E', 'component 2 no sex id paper first 331 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (12, 'HC2EP331W', 'component 2 no sex id paper first 331 wales household', 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (13, 'HC2SO331E', 'component 2 sex id online first 331 england household', 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (14, 'HC2SO331W', 'component 2 sex id online first 331 wales household', 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (15, 'HC2EO331E', 'component 2 no sex id online first 331 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (16, 'HC2EO331W', 'component 2 no sex id online first 331 wales househhold', 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (17, 'HC2EO332E', 'component 2 no sex id online first 332 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (18, 'HC2EO321E', 'component 2 no sex id online first 321 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (19, 'HC2EO322E', 'component 2 no sex id online first 322 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (20, 'HC2EO300E', 'component 2 no sex id online first 300 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (21, 'HC2EO200E', 'component 2 no sex id online first 200 england household', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (22, 'HC2EO331ADE', 'component 2 no sex id online first 331 assisted digital england household', 'H1', 'H');


--
-- TOC entry 3577 (class 0 OID 54583)
-- Dependencies: 361
-- Data for Name: actionplanmapping; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (1, 1, 1, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (2, 1, 5, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (3, 2, 2, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (4, 2, 6, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (5, 3, 3, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (6, 3, 7, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (7, 4, 4, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (8, 4, 8, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (9, 5, 9, true, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (10, 6, 10, true, 'PAPER', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (11, 7, 11, true, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (12, 8, 12, true, 'PAPER', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (13, 9, 13, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (14, 9, 15, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (15, 10, 14, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (16, 10, 16, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (17, 11, 17, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (18, 12, 18, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (19, 13, 19, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (20, 14, 20, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (21, 15, 21, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (22, 16, 22, true, 'ONLINE', 'ENGLISH', 'POST');


--
-- TOC entry 3578 (class 0 OID 54610)
-- Dependencies: 370
-- Data for Name: casestate; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO casestate (state) VALUES ('ACTIONABLE');
INSERT INTO casestate (state) VALUES ('INACTIONABLE');
INSERT INTO casestate (state) VALUES ('SAMPLED_INIT');
INSERT INTO casestate (state) VALUES ('REPLACEMENT_INIT');


--
-- TOC entry 3580 (class 0 OID 54616)
-- Dependencies: 372
-- Data for Name: category; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', NULL, false, NULL, NULL, NULL, 'Action Cancellation Completed', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', NULL, false, NULL, NULL, NULL, 'Action Cancellation Created', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACTION_COMPLETED', 'Action Completed', NULL, false, NULL, NULL, NULL, 'Action Completed', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACTION_CREATED', 'Action Created', NULL, false, NULL, NULL, NULL, 'Action Created', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACTION_UPDATED', 'Action Updated', NULL, false, NULL, NULL, NULL, 'Action Updated', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ADDRESS_DETAILS_INCORRECT', 'Address Details Incorrect', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Address Details Incorrect', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('CASE_CREATED', 'Case Created', NULL, false, NULL, NULL, NULL, 'Case Created', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('CLASSIFICATION_INCORRECT', 'Classification Incorrect', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Classification Incorrect', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('GENERAL_COMPLAINT', 'General Complaint', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Complaint', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('GENERAL_ENQUIRY', 'General Enquiry', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Enquiry', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('MISCELLANEOUS', 'Miscellaneous', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Miscellaneous', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TECHNICAL_QUERY', 'Technical Query', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Technical Query', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ACCESSIBILITY_MATERIALS', 'Accessibility Materials', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Accessibility Materials', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('PAPER_QUESTIONNAIRE_RESPONSE', 'Paper Questionnaire Response', 'DEACTIVATED', false, NULL, NULL, NULL, 'Paper Questionnaire Response', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ONLINE_QUESTIONNAIRE_RESPONSE', 'Online Questionnaire Response', 'DISABLED', false, NULL, NULL, NULL, 'Online Questionnaire Response', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('UNDELIVERABLE', 'Undeliverable', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Undeliverable', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_SOMALI', 'Somali Translation', NULL, true, 'collect-csos, collect-admins', 'QGSOM', 'translation', 'Somali', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_BENGALI', 'Bengali Translation', NULL, true, 'collect-csos, collect-admins', 'QGBEN', 'translation', 'Bengali', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_SPANISH', 'Spanish Translation', NULL, true, 'collect-csos, collect-admins', 'QGSPA', 'translation', 'Spanish', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_POLISH', 'Polish Translation', NULL, true, 'collect-csos, collect-admins', 'QGPOL', 'translation', 'Polish', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_CANTONESE', 'Cantonese Translation', NULL, true, 'collect-csos, collect-admins', 'QGCAN', 'translation', 'Cantonese', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_MANDARIN', 'Mandarin Translation', NULL, true, 'collect-csos, collect-admins', 'QGMAN', 'translation', 'Mandarin', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_PUNJABI_SHAHMUKI', 'Punjabi (Shahmuki) Translation', NULL, true, 'collect-csos, collect-admins', 'QGSHA', 'translation', 'Punjabi (Shahmuki)', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_LITHUANIAN', 'Lithuanian Translation', NULL, true, 'collect-csos, collect-admins', 'QGLIT', 'translation', 'Lithuanian', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('FIELD_COMPLAINT_ESCALATED', 'Field Complaint Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FC_ESCALATION', 'general', 'Field Complaint Escalated', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('FIELD_EMERGENCY_ESCALATED', 'Field Emergency Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FE_ESCALATION', 'general', 'Field Emergency Escalated', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('GENERAL_COMPLAINT_ESCALATED', 'General Complaint - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GC_ESCALATION', 'general', 'General Complaint - Escalated', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('GENERAL_ENQUIRY_ESCALATED', 'General Enquiry - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GE_ESCALATION', 'general', 'General Enquiry - Escalated', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('INCORRECT_ESCALATION', 'Incorrect Escalation', NULL, true, 'collect-field-escalate, collect-general-escalate, collect_admins', NULL, 'general', 'Incorrect Escalation', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('PENDING', 'Pending', NULL, true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Pending', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('REFUSAL', 'Refusal', 'DEACTIVATED', true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Refusal', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_PUNJABI_GURMUKHI', 'Punjabi (Gurmukhi) Translation', NULL, true, 'collect-csos, collect-admins', 'QGGUR', 'translation', 'Punjabi (Gurmukhi)', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_TURKISH', 'Turkish Translation', NULL, true, 'collect-csos, collect-admins', 'QGTUR', 'translation', 'Turkish', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_ARABIC', 'Arabic Translation', NULL, true, 'collect-csos, collect-admins', 'QGARA', 'translation', 'Arabic', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_PORTUGUESE', 'Portuguese Translation', NULL, true, 'collect-csos, collect-admins', 'QGPOR', 'translation', 'Portuguese', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_URDU', 'Urdu Translation', NULL, true, 'collect-csos, collect-admins', 'QGURD', 'translation', 'Urdu', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('TRANSLATION_GUJARATI', 'Gujarati Translation', NULL, true, 'collect-csos, collect-admins', 'QGGUJ', 'translation', 'Gujarati', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('CLOSE_ESCALATION', 'Close Escalation', NULL, true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Close Escalation', NULL, NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('INDIVIDUAL_RESPONSE_REQUESTED', 'Individual Response Requested', NULL, true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Response Requested', 'I', 'H');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('INDIVIDUAL_REPLACEMENT_IAC_REQUESTED', 'Individual Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Replacement IAC Requested', 'I', 'I');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('INDIVIDUAL_PAPER_REQUESTED', 'Individual Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Paper Requested', 'I', 'I');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('HOUSEHOLD_REPLACEMENT_IAC_REQUESTED', 'Household Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Replacement IAC Requested', 'H', 'H');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('HOUSEHOLD_PAPER_REQUESTED', 'Household Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Paper Requested', 'H', 'H');


--
-- TOC entry 3585 (class 0 OID 54649)
-- Dependencies: 382
-- Data for Name: survey; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO survey (survey) VALUES ('2017 TEST');


--
-- TOC entry 3583 (class 0 OID 54643)
-- Dependencies: 380
-- Data for Name: sample; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (1, 'C2EO332E', 'component 2 no sex id online first 332 england', 'SAMPLE = C2EO332E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (2, 'C2EO321E', 'component 2 no sex id online first 321 england', 'SAMPLE = C2EO321E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (3, 'C2EO322E', 'component 2 no sex id online first 322 england', 'SAMPLE = C2EO322E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (4, 'C2EO300E', 'component 2 no sex id online first 300 england', 'SAMPLE = C2EO300E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (5, 'C2EO200E', 'component 2 no sex id online first 200 england', 'SAMPLE = C2EO200E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (6, 'C2EO331ADE', 'component 2 no sex id online first 331 assisted digital england', 'SAMPLE = C2EO331ADE', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (7, 'C1SO331D4E', 'component 1 sex id online first 331 day 4 england', 'SAMPLE = C1SO331D4E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (8, 'C1SO331D4W', 'component 1 sex id online first 331 day 4 wales', 'SAMPLE = C1SO331D4W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (9, 'C1SO331D10E', 'component 1 sex id online first 331 day 10 england', 'SAMPLE = C1SO331D10E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (10, 'C1SO331D10W', 'component 1 sex id online first 331 day 10 wales', 'SAMPLE = C1SO331D10W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (11, 'C1EO331D4E', 'component 1 no sex id online first 331 day 4 england', 'SAMPLE = C1EO331D4E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (12, 'C1EO331D4W', 'component 1 no sex id online first 331 day 4 wales', 'SAMPLE = C1EO331D4W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (13, 'C1EO331D10E', 'component 1 no sex id online first 331 day 10 england', 'SAMPLE = C1EO331D10E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (14, 'C1EO331D10W', 'component 1 no sex id online first 331 day 10 wales', 'SAMPLE = C1EO331D10W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (15, 'C2SP331E', 'component 2 sex id paper first 331 england', 'SAMPLE = C2SP331E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (16, 'C2SP331W', 'component 2 sex id paper first 331 wales', 'SAMPLE = C2SP331W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (17, 'C2EP331E', 'component 2 no sex id paper first 331 england', 'SAMPLE = C2EP331E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (18, 'C2EP331W', 'component 2 no sex id paper first 331 wales', 'SAMPLE = C2EP331W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (19, 'C2SO331E', 'component 2 sex id online first 331 england', 'SAMPLE = C2SO331E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (20, 'C2SO331W', 'component 2 sex id online first 331 wales', 'SAMPLE = C2SO331W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (21, 'C2EO331E', 'component 2 no sex id online first 331 england', 'SAMPLE = C2EO331E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (22, 'C2EO331W', 'component 2 no sex id online first 331 wales', 'SAMPLE = C2EO331W', '2017 TEST');


--
-- TOC entry 3584 (class 0 OID 54646)
-- Dependencies: 381
-- Data for Name: samplecasetypeselector; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (1, 7, 1, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (2, 8, 2, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (3, 9, 3, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (4, 10, 4, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (5, 11, 5, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (6, 12, 6, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (7, 13, 7, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (8, 14, 8, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (9, 15, 9, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (10, 16, 10, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (11, 17, 11, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (12, 18, 12, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (13, 19, 13, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (14, 20, 14, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (15, 21, 15, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (16, 22, 16, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (17, 1, 17, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (18, 2, 18, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (19, 3, 19, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (20, 4, 20, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (21, 5, 21, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (22, 6, 22, 'H');




