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
-- TOC entry 3380 (class 0 OID 52848)
-- Dependencies: 288
-- Data for Name: respondenttype; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO respondenttype (respondenttype) VALUES ('H');
INSERT INTO respondenttype (respondenttype) VALUES ('I');


--
-- TOC entry 3377 (class 0 OID 52827)
-- Dependencies: 282
-- Data for Name: casetype; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1, 'HHT1E', NULL, 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (2, 'HHT1W', NULL, 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (3, 'HHT2E', NULL, 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (4, 'HHT2W', NULL, 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (5, 'HHT3E', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (6, 'HHT3W', NULL, 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (7, 'HHT4E', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (8, 'HHT4W', NULL, 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (9, 'HHT5E', NULL, 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (10, 'HHT5W', NULL, 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (11, 'HHT6E', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (12, 'HHT6W', NULL, 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (13, 'HHT7E', NULL, 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (14, 'HHT7W', NULL, 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (15, 'HHT8E', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (16, 'HHT8W', NULL, 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (17, 'HHT9E', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (18, 'HHT10', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (19, 'HHT11', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (20, 'HHT12', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (21, 'HHT13', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (22, 'HHT14', NULL, 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (23, 'IRT1E', NULL, 'I1S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (24, 'IRT1W', NULL, 'I2S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (25, 'IRT2E', NULL, 'I1S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (26, 'IRT2W', NULL, 'I2S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (27, 'IRT3E', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (28, 'IRT3W', NULL, 'I2', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (29, 'IRT4E', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (30, 'IRT4W', NULL, 'I2', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (31, 'IRT5E', NULL, 'I1S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (32, 'IRT5W', NULL, 'I2S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (33, 'IRT6E', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (34, 'IRT6W', NULL, 'I2', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (35, 'IRT7E', NULL, 'I1S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (36, 'IRT7W', NULL, 'I2S', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (37, 'IRT8E', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (38, 'IRT8W', NULL, 'I2', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (39, 'IRT9E', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (40, 'IRT10', NULL, 'I2', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (41, 'IRT11', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (42, 'IRT12', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (43, 'IRT13', NULL, 'I1', 'I');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (44, 'IRT14', NULL, 'I1', 'I');


--
-- TOC entry 3375 (class 0 OID 52797)
-- Dependencies: 272
-- Data for Name: actionplanmapping; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (1, 1, 1, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (2, 2, 2, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (3, 3, 3, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (4, 4, 4, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (5, 5, 5, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (6, 6, 6, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (7, 7, 7, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (8, 8, 8, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (9, 9, 9, true, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (10, 10, 10, true, 'PAPER', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (11, 11, 11, true, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (12, 12, 12, true, 'PAPER', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (13, 13, 13, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (14, 14, 14, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (15, 15, 15, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (16, 16, 16, true, 'ONLINE', 'WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (17, 17, 17, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (18, 18, 18, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (19, 19, 19, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (20, 20, 20, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (21, 21, 21, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (22, 22, 22, true, 'ONLINE', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (23, 148, 1, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (24, 146, 1, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (25, 147, 1, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (26, 151, 2, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (27, 149, 2, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (28, 150, 2, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (29, 154, 2, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (30, 152, 2, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (31, 153, 2, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (32, 139, 3, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (33, 137, 3, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (34, 138, 3, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (35, 142, 4, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (36, 140, 4, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (37, 141, 4, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (38, 145, 4, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (39, 143, 4, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (40, 144, 4, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (41, 130, 5, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (42, 128, 5, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (43, 129, 5, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (44, 133, 6, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (45, 131, 6, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (46, 132, 6, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (47, 136, 6, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (48, 134, 6, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (49, 135, 6, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (50, 121, 7, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (51, 119, 7, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (52, 120, 7, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (53, 124, 8, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (54, 122, 8, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (55, 123, 8, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (56, 127, 8, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (57, 125, 8, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (58, 126, 8, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (59, 203, 9, false, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (60, 204, 9, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (61, 201, 9, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (62, 202, 9, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (63, 207, 10, false, 'PAPER', 'W ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (64, 208, 10, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (65, 205, 10, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (66, 206, 10, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (67, 211, 10, false, 'PAPER', 'W WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (68, 212, 10, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (69, 209, 10, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (70, 210, 10, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (71, 184, 11, false, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (72, 185, 11, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (73, 182, 11, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (74, 183, 11, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (75, 188, 12, false, 'PAPER', 'W ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (76, 189, 12, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (77, 186, 12, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (78, 187, 12, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (79, 192, 12, false, 'PAPER', 'W WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (80, 193, 12, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (81, 190, 12, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (82, 191, 12, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (83, 196, 13, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (84, 194, 13, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (85, 195, 13, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (86, 199, 14, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (87, 197, 14, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (88, 198, 14, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (89, 200, 14, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (90, 214, 14, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (91, 213, 14, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (92, 172, 15, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (93, 170, 15, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (94, 171, 15, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (95, 175, 16, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (96, 173, 16, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (97, 174, 16, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (98, 178, 16, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (99, 176, 16, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (100, 177, 16, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (101, 181, 17, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (102, 179, 17, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (103, 180, 17, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (104, 163, 18, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (105, 161, 18, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (106, 162, 18, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (107, 166, 19, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (108, 164, 19, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (109, 165, 19, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (110, 160, 20, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (111, 158, 20, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (112, 159, 20, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (113, 157, 21, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (114, 155, 21, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (115, 156, 21, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (116, 169, 22, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (117, 167, 22, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (118, 168, 22, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (119, 52, 23, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (120, 50, 23, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (121, 51, 23, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (122, 55, 24, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (123, 53, 24, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (124, 54, 24, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (125, 58, 24, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (126, 56, 24, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (127, 57, 24, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (128, 43, 25, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (129, 41, 25, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (130, 42, 25, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (131, 46, 26, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (132, 44, 26, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (133, 45, 26, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (134, 49, 26, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (135, 47, 26, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (136, 48, 26, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (137, 34, 27, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (138, 32, 27, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (139, 33, 27, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (140, 37, 28, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (141, 35, 28, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (142, 36, 28, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (143, 40, 28, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (144, 38, 28, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (145, 39, 28, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (146, 25, 29, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (147, 23, 29, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (148, 24, 29, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (149, 28, 30, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (150, 26, 30, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (151, 27, 30, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (152, 31, 30, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (153, 29, 30, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (154, 30, 30, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (155, 109, 31, false, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (156, 110, 31, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (157, 107, 31, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (158, 108, 31, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (159, 113, 32, false, 'PAPER', 'W ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (160, 114, 32, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (161, 111, 32, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (162, 112, 32, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (163, 117, 32, false, 'ONLINE', 'W WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (164, 118, 32, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (165, 115, 32, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (166, 116, 32, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (167, 87, 33, false, 'PAPER', 'ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (168, 88, 33, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (169, 85, 33, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (170, 86, 33, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (171, 91, 34, false, 'PAPER', 'W ENGLISH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (172, 92, 34, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (173, 89, 34, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (174, 90, 34, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (175, 95, 34, false, 'ONLINE', 'W WELSH', 'POST');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (176, 96, 34, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (177, 93, 34, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (178, 94, 34, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (179, 100, 35, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (180, 98, 35, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (181, 99, 35, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (182, 103, 36, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (183, 101, 36, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (184, 102, 36, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (185, 106, 36, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (186, 104, 36, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (187, 105, 36, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (188, 75, 37, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (189, 73, 37, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (190, 74, 37, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (191, 78, 38, false, 'ONLINE', 'W ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (192, 76, 38, false, 'ONLINE', 'W ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (193, 77, 38, false, 'ONLINE', 'W ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (194, 81, 38, false, 'ONLINE', 'W WELSH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (195, 79, 38, false, 'ONLINE', 'W WELSH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (196, 80, 38, false, 'ONLINE', 'W WELSH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (197, 84, 39, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (198, 82, 39, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (199, 83, 39, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (200, 67, 40, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (201, 65, 40, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (202, 66, 40, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (203, 70, 41, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (204, 68, 41, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (205, 69, 41, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (206, 64, 42, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (207, 62, 42, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (208, 63, 42, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (209, 61, 43, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (210, 59, 43, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (211, 60, 43, false, 'ONLINE', 'ENGLISH', 'LETTER');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (212, 72, 44, false, 'ONLINE', 'ENGLISH', 'SMS');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (213, 97, 44, false, 'ONLINE', 'ENGLISH', 'EMAIL');
INSERT INTO actionplanmapping (actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel) VALUES (214, 71, 44, false, 'ONLINE', 'ENGLISH', 'LETTER');


--
-- TOC entry 3376 (class 0 OID 52824)
-- Dependencies: 281
-- Data for Name: casestate; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO casestate (state) VALUES ('ACTIONABLE');
INSERT INTO casestate (state) VALUES ('INACTIONABLE');
INSERT INTO casestate (state) VALUES ('SAMPLED_INIT');
INSERT INTO casestate (state) VALUES ('REPLACEMENT_INIT');


--
-- TOC entry 3378 (class 0 OID 52830)
-- Dependencies: 283
-- Data for Name: category; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACTION_COMPLETED', 'Action Completed', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACTION_CREATED', 'Action Created', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACTION_UPDATED', 'Action Updated', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ADDRESS_DETAILS_INCORRECT', 'Address Details Incorrect', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'CASE_CREATED', 'Case Created', NULL, false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'CLASSIFICATION_INCORRECT', 'Classification Incorrect', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'GENERAL_COMPLAINT', 'General Complaint', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'GENERAL_COMPLAINT_ESCALATED', 'General Complaint - Escalated', NULL, true, 'collect-csos, collect-admins', 'ComplaintEscalation', 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'GENERAL_ENQUIRY', 'General Enquiry', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'GENERAL_ENQUIRY_ESCALATED', 'General Enquiry - Escalated', NULL, true, 'collect-csos, collect-admins', 'GeneralEscalation', 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'INCORRECT_ESCALATION', 'Incorrect Escalation', NULL, true, 'collect-escalate, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'MISCELLANEOUS', 'Miscellaneous', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'FIELD_EMERGENCY_ESCALATED', 'Field Emergency Escalated', NULL, true, 'collect-escalate, collect-admins', 'FieldEmergencyEscalation', 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'PENDING', 'Pending', NULL, true, 'collect-escalate, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'FIELD_COMPLAINT_ESCALATED', 'Field Complaint Escalated', NULL, true, 'collect-escalate, collect-admins', 'FieldComplaintEscalation', 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TECHNICAL_QUERY', 'Technical Query', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ACCESSIBILITY_MATERIALS', 'Accessibility Materials', NULL, true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'PAPER_QUESTIONNAIRE_RESPONSE', 'Paper Questionnaire Response', 'DEACTIVATED', false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'ONLINE_QUESTIONNAIRE_RESPONSE', 'Online Questionnaire Response', 'DISABLED', false, NULL, NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'REFUSAL', 'Refusal', 'DEACTIVATED', true, 'collect-csos, collect-escalate, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'UNDELIVERABLE', 'Undeliverable', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'HOUSEHOLD_REPLACEMENT_IAC_REQUESTED', 'Household Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'HOUSEHOLD_PAPER_REQUESTED', 'Household Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'INDIVIDUAL_RESPONSE_REQUESTED', 'Individual Response Requested', NULL, true, 'collect-csos, collect-admins', NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'INDIVIDUAL_REPLACEMENT_IAC_REQUESTED', 'Individual Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'INDIVIDUAL_PAPER_REQUESTED', 'Individual Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL);
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_SOMALI', 'Somali', NULL, true, 'collect-csos, collect-admins', 'QGSOM', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_BENGALI', 'Bengali', NULL, true, 'collect-csos, collect-admins', 'QGBEN', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_SPANISH', 'Spanish', NULL, true, 'collect-csos, collect-admins', 'QGSPA', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_POLISH', 'Polish', NULL, true, 'collect-csos, collect-admins', 'QGPOL', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_CANTONESE', 'Cantonese', NULL, true, 'collect-csos, collect-admins', 'QGCAN', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_MANDARIN', 'Mandarin', NULL, true, 'collect-csos, collect-admins', 'QGMAN', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_PUNJABI_SHAHMUKI', 'Punjabi – Shahmuki', NULL, true, 'collect-csos, collect-admins', 'QGSHA', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_LITHUANIAN', 'Lithuanian', NULL, true, 'collect-csos, collect-admins', 'QGLIT', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_PUNJABI_GURMUKHI', 'Punjabi – Gurmukhi', NULL, true, 'collect-csos, collect-admins', 'QGGUR', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_TURKISH', 'Turkish', NULL, true, 'collect-csos, collect-admins', 'QGTUR', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_ARABIC', 'Arabic', NULL, true, 'collect-csos, collect-admins', 'QGARA', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_PORTUGUESE', 'Portuguese', NULL, true, 'collect-csos, collect-admins', 'QGPOR', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_URDU', 'Urdu', NULL, true, 'collect-csos, collect-admins', 'QGURD', 'translation');
INSERT INTO category ( name, description, eventtype, manual, role, generatedactiontype, "group") VALUES ( 'TRANSLATION_GUJARATI', 'Gujarati', NULL, true, 'collect-csos, collect-admins', 'QGGUJ', 'translation');


--
-- TOC entry 3383 (class 0 OID 52860)
-- Dependencies: 292
-- Data for Name: survey; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO survey (survey) VALUES ('2017 TEST');


--
-- TOC entry 3381 (class 0 OID 52854)
-- Dependencies: 290
-- Data for Name: sample; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (1, 'COM1T1E', NULL, 'SAMPLE = COM1T1E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (2, 'COM1T1W', NULL, 'SAMPLE = COM1T1W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (3, 'COM1T2E', NULL, 'SAMPLE = COM1T2E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (4, 'COM1T2W', NULL, 'SAMPLE = COM1T2W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (5, 'COM1T3E', NULL, 'SAMPLE = COM1T3E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (6, 'COM1T23W', NULL, 'SAMPLE = COM1T23W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (7, 'COM1T4E', NULL, 'SAMPLE = COM1T4E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (8, 'COM1T4W', NULL, 'SAMPLE = COM1T4W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (9, 'COM2T5E', NULL, 'SAMPLE = COM2T5E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (10, 'COM2T5W', NULL, 'SAMPLE = COM2T5W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (11, 'COM2T6E', NULL, 'SAMPLE = COM2T6E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (12, 'COM2T6W', NULL, 'SAMPLE = COM2T6W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (13, 'COM2T7E', NULL, 'SAMPLE = COM2T7E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (14, 'COM2T7W', NULL, 'SAMPLE = COM2T7W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (15, 'COM2T8E', NULL, 'SAMPLE = COM2T8E', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (16, 'COM2T8W', NULL, 'SAMPLE = COM2T8W', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (17, 'COM2T9', NULL, 'SAMPLE = COM2T9', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (18, 'COM2T10', NULL, 'SAMPLE = COM2T10', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (19, 'COM2T11', NULL, 'SAMPLE = COM2T11', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (20, 'COM2T12', NULL, 'SAMPLE = COM2T12', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (21, 'COM2T13', NULL, 'SAMPLE = COM2T13', '2017 TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES (22, 'COM2T14', NULL, 'SAMPLE = COM2T14', '2017 TEST');


--
-- TOC entry 3382 (class 0 OID 52857)
-- Dependencies: 291
-- Data for Name: samplecasetypeselector; Type: TABLE DATA; Schema: forphil; Owner: postgres
--

INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (1, 1, 1, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (2, 2, 2, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (3, 3, 3, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (4, 4, 4, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (5, 5, 5, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (6, 6, 6, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (7, 7, 7, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (8, 8, 8, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (9, 9, 9, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (10, 10, 10, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (11, 11, 11, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (12, 12, 12, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (13, 13, 13, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (14, 14, 14, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (15, 15, 15, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (16, 16, 16, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (17, 17, 17, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (18, 18, 18, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (19, 19, 19, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (20, 20, 20, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (21, 21, 21, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (22, 22, 22, 'H');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (23, 1, 23, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (24, 2, 24, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (25, 3, 25, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (26, 4, 26, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (27, 5, 27, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (28, 6, 28, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (29, 7, 29, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (30, 8, 30, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (31, 9, 31, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (32, 10, 32, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (33, 11, 33, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (34, 12, 34, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (35, 13, 35, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (36, 14, 36, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (37, 15, 37, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (38, 16, 38, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (39, 17, 39, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (40, 18, 40, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (41, 19, 41, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (42, 20, 42, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (43, 21, 43, 'I');
INSERT INTO samplecasetypeselector (samplecasetypeselectorid, sampleid, casetypeid, respondenttype) VALUES (44, 22, 44, 'I');


