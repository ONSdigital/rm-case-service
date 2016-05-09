-- Function: caseframe.iac_generator()

-- DROP FUNCTION caseframe.iac_generator();

CREATE OR REPLACE FUNCTION caseframe.iac_generator(OUT iac character)
  RETURNS character AS
$BODY$
DECLARE
v_errmess text;
BEGIN
    iac := SUBSTRING(lpad((ltrim(to_char((abs((select pseudo_encrypt from caseframe.pseudo_encrypt(currval

('caseframe.qidseq'::regclass))))),'99999999999999999999'))),20,'0'),11,10);  


EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE AN IAC CODE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.iac_generator');         
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.iac_generator()
  OWNER TO postgres;

