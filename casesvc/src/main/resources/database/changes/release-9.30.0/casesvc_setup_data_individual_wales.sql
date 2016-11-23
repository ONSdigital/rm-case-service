--set up data for Welsh Individual Forms

-- questionset
INSERT INTO casesvc.questionset(questionset) VALUES ('I2');
INSERT INTO casesvc.questionset(questionset) VALUES ('I2S');

--casetype
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (27, 'I2S', 'individual questionnaire with sex id wales','I2S', 'HI');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (28, 'I2', 'individual questionnaire no sex id wales','I2', 'HI');

--actionplanmapping
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (51,29, 27,false,'PAPER','ENGLISH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (52,30, 27,false,'PAPER','WELSH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (53,34, 27,false,'ONLINE','WELSH','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (54,31, 28,false,'PAPER','ENGLISH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (55,32, 28,false,'PAPER','WELSH','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (56,34, 28,false,'ONLINE','WELSH','SMS' );

--samplecasetypeselector
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (37,12, 28,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (38,14, 28,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (39,18, 28,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (40,22, 28,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (41,8, 27,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (42,10, 27,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (43,16, 27,'HI', FALSE );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype, isdefault ) VALUES (44,20, 27,'HI', FALSE );
