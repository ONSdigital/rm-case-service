--update sample addresscriteria to use correct category values

update caseframe.sample
set addresscriteria = 'addresstype = ''CE'' and category = ''HOTEL'''
where name = 'Hotels Guest Houses';
update caseframe.sample
set addresscriteria = 'addresstype = ''CE'' and category = ''CARE HOME'''
where name = 'Care Homes';
