--add column to decide casetype for initial case generation
ALTER TABLE casesvc.samplecasetypeselector ADD COLUMN isdefault boolean;

UPDATE casesvc.samplecasetypeselector SET isdefault = TRUE where respondenttype = 'H';

UPDATE casesvc.samplecasetypeselector SET isdefault = FALSE where respondenttype = 'HI';
