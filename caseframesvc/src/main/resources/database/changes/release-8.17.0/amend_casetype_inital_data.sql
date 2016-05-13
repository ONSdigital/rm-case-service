--changes to correct initial data in casetype so that correct actionplain is associated
--with each casetype

update caseframe.casetype 
set actionplanid = 3 where name = 'HGH';

update caseframe.casetype
set actionplanid = 2 where name = 'CH';
