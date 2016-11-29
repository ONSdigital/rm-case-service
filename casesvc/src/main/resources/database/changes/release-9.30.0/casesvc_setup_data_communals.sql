--setup data for hotels, university halls of residence and sheltered housing

--questionset
INSERT INTO casesvc.questionset(questionset) VALUES ('HOTEL');

--sample

INSERT INTO casesvc.sample(sampleid, name, description, addresscriteria, survey ) VALUES (23, 'SHOUSING', 'sheltered housing','SAMPLE = SHOUSING ', '2017 TEST');
INSERT INTO casesvc.sample(sampleid, name, description, addresscriteria, survey ) VALUES (24, 'HOTEL', 'hotel','SAMPLE = HOTEL', '2017 TEST');
INSERT INTO casesvc.sample(sampleid, name, description, addresscriteria, survey ) VALUES (25, 'UNIVERSITY', 'university','SAMPLE = UNIVERSITY', '2017 TEST');

--casetype
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (29, 'SHOUSING', 'SHOUSING','H1', 'H');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (30, 'HOTEL', 'HOTEL','HOTEL', 'C');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (31, 'UNIVERSITY', 'UNIVERSITY','I1', 'CI');


--ACTIONPLANMAPPING

INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (81,50, 29,true,'ONLINE','ENGLISH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (82,53, 29,false,'ONLINE','ENGLISH','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (83,51, 30,true,'ONLINE','ENGLISH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (84,52, 31,true,'ONLINE','ENGLISH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (85,54, 31,false,'ONLINE','ENGLISH','SMS' );


--SAMPLECASETYPESELECTOR

INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (45,23, 29,'H', TRUE);
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (46,23, 26,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (47,24, 30,'C', TRUE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (48,25, 31,'CI', TRUE );
