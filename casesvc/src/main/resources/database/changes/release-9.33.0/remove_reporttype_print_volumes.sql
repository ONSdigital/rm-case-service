-- Remove print volumes report from casesvc as the print volume report is part of the actionexporter service

DELETE FROM casesvc.reporttype
WHERE reporttype = 'PRINT_VOLUMES';