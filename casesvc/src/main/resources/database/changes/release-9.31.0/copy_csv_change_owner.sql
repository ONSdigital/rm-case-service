-- Function: casesvc.copy_csv(character varying, character varying, character varying, character varying)
-- DROP FUNCTION casesvc.copy_csv(character varying, character varying, character varying, character varying);

-- Drop old function
DROP FUNCTION casesvc.copy_csv(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION casesvc.copy_csv(p_sql character varying, p_directory character varying, p_filename character varying, p_header character varying)
  RETURNS boolean AS
$BODY$
DECLARE    
BEGIN
 EXECUTE 'COPY (' || p_sql || ') TO ''' || p_directory || p_filename || '.csv''' || 'DELIMITER '','' CSV ' || UPPER(p_header);

                    
 PERFORM casesvc.logmessage(p_messagetext := 'Command - ' || 'COPY (' || p_sql || ') TO ' || p_directory || p_filename || '.csv DELIMITER '','' CSV ' || UPPER(p_header)
                           ,p_jobid := 0   
                           ,p_messagelevel := 'INFO'
                           ,p_functionname := 'casesvc.copy_csv');  
 RETURN true;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION casesvc.copy_csv(character varying, character varying, character varying, character varying)
  OWNER TO postgres;
