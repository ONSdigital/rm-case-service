UPDATE casesvc.reporttype SET reporttype = UPPER(reporttype);

DELETE FROM casesvc.reporttype where reporttype = 'HELPLINE';