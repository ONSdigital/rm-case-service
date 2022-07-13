--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 14.4

--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;

--
-- Data for Name: casegroup; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: casestate; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO casesvc.casestate (state_pk) VALUES ('ACTIONABLE');
INSERT INTO casesvc.casestate (state_pk) VALUES ('INACTIONABLE');
INSERT INTO casesvc.casestate (state_pk) VALUES ('REPLACEMENT_INIT');
INSERT INTO casesvc.casestate (state_pk) VALUES ('SAMPLED_INIT');


--
-- Data for Name: case; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: case_action_audit_event; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: case_action_event_request; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: case_action_template; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNL', 'Business Survey Notification Letter', 'mps', 'LETTER', 'BSNOT');
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNE', 'Business Survey Notification Email', 'go_live', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRL', 'Business Survey Reminder Letter', 'reminder', 'LETTER', 'BSREM');
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRE', 'Business Survey Reminder Email', 'reminder', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRL', 'Business Survey Reminder Letter', 'reminder2', 'LETTER', 'BSREM');
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRE', 'Business Survey Reminder Email', 'reminder2', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRL', 'Business Survey Reminder Letter', 'reminder3', 'LETTER', 'BSREM');
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSRE', 'Business Survey Reminder Email', 'reminder3', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNUE', 'Business Survey Nudge Email', 'nudge_email_0', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNUE', 'Business Survey Nudge Email', 'nudge_email_1', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNUE', 'Business Survey Nudge Email', 'nudge_email_2', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNUE', 'Business Survey Nudge Email', 'nudge_email_3', 'EMAIL', NULL);
INSERT INTO casesvc.case_action_template (type, description, event_tag, handler, prefix) VALUES ('BSNUE', 'Business Survey Nudge Email', 'nudge_email_4', 'EMAIL', NULL);


--
-- Data for Name: category; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--

INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('RESPONDENT_ENROLED', 'Respondent Enroled', 'Respondent Enroled', 'ACTIONPLAN_CHANGED', NULL, NULL, NULL, 'B', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('NO_ACTIVE_ENROLMENTS', 'No active enrolments', 'No active enrolments remaining for case', 'ACTIONPLAN_CHANGED', NULL, NULL, NULL, 'B', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('RESPONDENT_ACCOUNT_CREATED', 'Account created for respondent', 'Account created for respondent', 'ACCOUNT_CREATED', NULL, NULL, NULL, 'B', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('SECURE_MESSAGE_SENT', 'Secure Message Sent', 'Secure Message Sent', NULL, NULL, NULL, NULL, 'BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('VERIFICATION_CODE_SENT', 'Verification Code Sent', 'Verification Code Sent', NULL, NULL, NULL, NULL, 'B', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('RESPONDENT_EMAIL_AMENDED', 'Respondent Email Amended', 'Respondent Email Amended', NULL, NULL, NULL, NULL, 'BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('GENERATE_ENROLMENT_CODE', 'Generate enrolment code', 'Generate enrolment code', NULL, NULL, NULL, NULL, 'B', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACCESS_CODE_AUTHENTICATION_ATTEMPT', 'Access Code authentication attempted', 'Access Code authentication attempted', NULL, NULL, NULL, NULL, 'B,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACTION_CREATED', 'Action Created', 'Action Created', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACTION_COMPLETED', 'Action Completed', 'Action Completed', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACTION_UPDATED', 'Action Updated', 'Action Updated', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('CASE_CREATED', 'Case Created', 'Case Created', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', 'Action Cancellation Completed', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', 'Action Cancellation Created', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('COMPLETED_BY_PHONE', 'Completed By Phone', 'Completed By Phone', 'DEACTIVATED', NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('NO_LONGER_REQUIRED', 'No Longer Required', 'No Longer Required', 'DEACTIVATED', NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('EQ_LAUNCH', 'Collection Instrument Launched', 'Collection Instrument Launched', NULL, NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('COLLECTION_INSTRUMENT_DOWNLOADED', 'Collection Instrument Downloaded', 'Collection Instrument Downloaded', NULL, NULL, NULL, NULL, 'B,BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('UNSUCCESSFUL_RESPONSE_UPLOAD', 'Unsuccessful Response Upload', 'Unsuccessful Response Upload', NULL, NULL, NULL, NULL, 'B,BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('COLLECTION_INSTRUMENT_ERROR', 'Collection Instrument Error', 'Collection Instrument Error', NULL, NULL, NULL, NULL, 'B,BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('SUCCESSFUL_RESPONSE_UPLOAD', 'Successful Response Upload', 'Successful Response Upload', 'DISABLED', NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('DISABLE_RESPONDENT_ENROLMENT', 'Disable Respondent Enrolment', 'Disable Respondent Enrolment', 'DISABLED', NULL, NULL, NULL, 'BI', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('OFFLINE_RESPONSE_PROCESSED', 'Offline Response Processed', 'Offline Response Processed', 'DISABLED', NULL, NULL, NULL, 'B,BI,H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('PRIVACY_DATA_CONFIDENTIALITY_CONCERNS', '411', 'Privacy/Data Security/Confidentiality concerns', 'DEACTIVATED', NULL, NULL, NULL, 'H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('PHYSICALLY_OR_MENTALLY_UNABLE', '531', 'Physically or mentally unable/incompetent', 'DEACTIVATED', NULL, NULL, NULL, 'H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('LACK_OF_COMPUTER_INTERNET_ACCESS', '571', 'Lack of computer or internet access', 'DEACTIVATED', NULL, NULL, NULL, 'H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('COMPLY_IN_DIFFERENT_COLLECTION_MODE', '581', 'Willing to comply in a different collection mode', 'DEACTIVATED', NULL, NULL, NULL, 'H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('ADDRESS_OCCUPIED_NO_RESIDENT', '750', 'Address occupied, but no resident household/resident(s)', 'DEACTIVATED', NULL, NULL, NULL, 'H', NULL, NULL);
INSERT INTO casesvc.category (category_pk, short_description, long_description, event_type, role, generated_action_type, "group", old_case_sample_unit_types, new_case_sample_unit_type, recalc_collection_instrument) VALUES ('COMPLETED_TO_NOTSTARTED', 'Changed from completed back to not started', 'Changed from completed back to not started', 'ACTIVATED', NULL, NULL, NULL, 'B, BI', NULL, NULL);


--
-- Data for Name: caseevent; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: casegroupstatusaudit; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--



--
-- Data for Name: caseiacaudit; Type: TABLE DATA; Schema: casesvc; Owner: postgres
--


--
-- Name: case_action_audit_event_id_seq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.case_action_audit_event_id_seq', 1, false);


--
-- Name: case_action_event_request_id_seq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.case_action_event_request_id_seq', 1, false);


--
-- Name: caseeventseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.caseeventseq', 1, false);


--
-- Name: casegroupseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.casegroupseq', 1, false);


--
-- Name: casegroupstatusauditseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.casegroupstatusauditseq', 1, false);


--
-- Name: caseiacauditseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.caseiacauditseq', 1, false);


--
-- Name: caserefseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.caserefseq', 1000000000000001, false);


--
-- Name: caseseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.caseseq', 1, false);


--
-- Name: messagelogseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.messagelogseq', 1, false);


--
-- Name: reportpkseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.reportpkseq', 1, false);


--
-- Name: responseseq; Type: SEQUENCE SET; Schema: casesvc; Owner: postgres
--

SELECT pg_catalog.setval('casesvc.responseseq', 1, false);


--
-- PostgreSQL database dump complete
--

