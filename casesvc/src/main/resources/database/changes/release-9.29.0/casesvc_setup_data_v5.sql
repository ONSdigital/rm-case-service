--drop constraints on case table for actionplanmapping and casetype

ALTER TABLE casesvc."case"  DROP CONSTRAINT actionplanmappingid_fkey;

ALTER TABLE casesvc."case"  DROP CONSTRAINT casetypeid_fkey;

--delete from actionplanmapping

DELETE FROM casesvc.actionplanmapping WHERE casetypeid IN  (SELECT casetypeid FROM casesvc.casetype WHERE respondenttype = 'I');

--delete from samplecasetypeselector

DELETE FROM casesvc.samplecasetypeselector where respondenttype = 'I';

--delete from casetype

DELETE FROM  casesvc.casetype WHERE respondenttype = 'I';

--drop constraint on category for respondenttype

ALTER TABLE casesvc.category  DROP CONSTRAINT newcaserespondenttype_fkey;
ALTER TABLE casesvc.category DROP CONSTRAINT oldcaserespondenttype_fkey;

--delete from respondenttype and add new categories

DELETE FROM casesvc.respondenttype where respondenttype = 'I';
INSERT INTO casesvc.respondenttype VALUES ('HI');
INSERT INTO casesvc.respondenttype VALUES ('C');
INSERT INTO casesvc.respondenttype VALUES ('CI');

--update category table with new respondenttype

UPDATE casesvc.category SET newcaserespondenttype = 'HI' WHERE newcaserespondenttype = 'I';
UPDATE casesvc.category SET oldcaserespondenttype = 'HI' where oldcaserespondenttype = 'I';

--add constraint to category for respondenttype
ALTER TABLE casesvc.category
  ADD CONSTRAINT newcaserespondenttype_fkey FOREIGN KEY (newcaserespondenttype)
      REFERENCES casesvc.respondenttype (respondenttype);

ALTER TABLE casesvc.category
  ADD CONSTRAINT oldcaserespondenttype_fkey FOREIGN KEY (oldcaserespondenttype)
      REFERENCES casesvc.respondenttype (respondenttype);

--remove redundant questionsets
DELETE FROM casesvc.questionset WHERE questionset IN ('H2W','H2WS','I2','I2S','I2W','I2WS');

--insert into casetype new casetypes
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (23, 'I1S', 'individual questionnaire with sex id england','I1S', 'HI');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (24, 'I1SO', 'individual questionnaire online only with sex id england','I1S', 'HI');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (25, 'I1', 'individual questionnaire no sex id england','I1', 'HI');
INSERT INTO casesvc.casetype(casetypeid, name, description, questionset, respondenttype ) VALUES (26, 'I1O', 'individual questionnaire online only no sex id england','I1', 'HI');

--insert into actionplanmapping
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (23,20, 1,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (24,26, 1,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (25,21, 3,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (26,27, 3,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (27,23, 5,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (28,26, 5,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (29,24, 7,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (30,27, 7,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (31,22, 9,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (32,28, 9,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (33,25, 11,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (34,28, 11,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (35,22, 13,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (36,28, 13,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (37,25, 15,false,'PAPER','English','PRINTER' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (38,28, 15,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (39,28, 17,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (40,28, 18,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (41,28, 19,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (42,28, 20,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (43,28, 21,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (44,28, 22,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (45,17, 23,false,'PAPER','English','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (46,19, 23,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (47,19, 24,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (48,18, 25,false,'PAPER','English','POST' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (49,19, 25,false,'ONLINE','English','SMS' );
INSERT INTO casesvc.actionplanmapping(actionplanmappingid, actionplanid, casetypeid, isdefault, inboundchannel, variant, outboundchannel ) VALUES (50,19, 26,false,'ONLINE','English','SMS' );


--insert into samplecasetypeselector

INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (23,1, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (24,2, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (25,3, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (26,4, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (27,5, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (28,6, 26,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (29,7, 23,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (30,9, 23,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (31,11, 25,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (32,13, 25,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (33,15, 23,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (34,17, 25,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (35,19, 23,'HI' );
INSERT INTO casesvc.samplecasetypeselector(samplecasetypeselectorid, sampleid, casetypeid, respondenttype ) VALUES (36,21, 25,'HI' );


--resinstate constraints on case table for actionplanmapping and casetype
ALTER TABLE casesvc."case"
  ADD CONSTRAINT actionplanmappingid_fkey FOREIGN KEY (actionplanmappingid)
      REFERENCES casesvc.actionplanmapping (actionplanmappingid);
      
ALTER TABLE casesvc."case"
  ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid)
      REFERENCES casesvc.casetype (casetypeid) ;
