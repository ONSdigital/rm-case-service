UPDATE casesvc.category
SET eventtype = 'ACTIONPLAN_CHANGED', newcasesampleunittype = null
WHERE categorypk = 'RESPONDENT_ENROLED';