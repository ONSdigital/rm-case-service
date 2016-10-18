--data to insert setup data based on version 2 of the setup data supplied by the business

set schema 'casesvc';

delete from actionplanmapping;
delete from samplecasetypeselector;
delete from casetype;
delete from sample;

--sample

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

--casetype

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


-- actionplanmapping

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


--samplecasetypeselector

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



