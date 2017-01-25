set schema 'casesvc';

--survey
INSERT INTO survey(survey) VALUES ('DTM TEST');

--sample
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1001, 'DTMTESTH1', 'DTM test for H1', 'SAMPLE = DTMTESTH1', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1002, 'DTMTESTH1S', 'DTM test for H1S', 'SAMPLE = DTMTESTH1S', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1003, 'DTMTESTH2', 'DTM test for H2', 'SAMPLE = DTMTESTH2', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1004, 'DTMTESTH2S', 'DTM test for H2S', 'SAMPLE = DTMTESTH2S', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1005, 'DTMTESTSHOUSING', 'DTM test for SHOUSING', 'SAMPLE = DTMTESTSHOUSING', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1006, 'DTMTESTUNI', 'DTM test for UNIVERSITY', 'SAMPLE = DTMTESTUNI', 'DTM TEST');
INSERT INTO sample (sampleid, name, description, addresscriteria, survey) VALUES ( 1007, 'DTMTESTHOTEL', 'DTM test for HOTEL', 'SAMPLE = DTMTESTHOTEL', 'DTM TEST');

--casetype

INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1001, 'DTMTESTH1', 'DTM H1 test', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1002, 'DTMTESTH1S', 'DTM H1S test', 'H1S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1003, 'DTMTESTH2', 'DTM H2 test', 'H2', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1004, 'DTMTESTH2S', 'DTM H2S test', 'H2S', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1005, 'DTMTESTSHOUSING', 'DTM Shousing test', 'H1', 'H');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1006, 'DTMTESTUNI', 'DTM University test', 'I1', 'CI');
INSERT INTO casetype (casetypeid, name, description, questionset, respondenttype) VALUES (1007, 'DTMTESTHOTEL', 'DTM Hotel test', 'HOTEL', 'C');

--samplecasetypeselector

INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1001,1001, 1001,'H', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1002,1002, 1002,'H', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1003,1003, 1003,'H', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1004,1004, 1004,'H', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1005,1005, 1005,'H', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1006,1006, 1006,'CI', TRUE);
INSERT INTO samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (1007,1007, 1007,'C', TRUE);

--actionplanmapping

INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1001,0, 1001,true,'ONLINE','ENGLISH','POST' );
INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1002,0, 1002,true,'ONLINE','ENGLISH','POST' );
INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1003,0, 1003,true,'ONLINE','ENGLISH','POST' );
INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1004,0, 1004,true,'ONLINE','ENGLISH','POST' );
INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1005,0, 1005,true,'ONLINE','ENGLISH','POST' );
INSERT INTO actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1006,0, 1006,true,'ONLINE','ENGLISH','POST' );
INSERT INTO  actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (1007,0, 1007,true,'ONLINE','ENGLISH','POST' );

