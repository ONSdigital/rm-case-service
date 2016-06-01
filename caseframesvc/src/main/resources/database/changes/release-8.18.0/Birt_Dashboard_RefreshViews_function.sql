-- Function: caseframe.refresh_materialised_views()
-- DROP FUNCTION caseframe.refresh_materialised_views();

CREATE OR REPLACE FUNCTION caseframe.refresh_materialised_views()
  RETURNS integer AS
$BODY$BEGIN
	/* Add any other materialised views here */
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_DAY;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_PERC;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_SECTOR;

	RETURN 1;
END$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.refresh_materialised_views()
  OWNER TO postgres;
COMMENT ON FUNCTION caseframe.refresh_materialised_views() IS 'Refreshs the materialised views that are used in the system. Should be called periodically via a crontab or manually on-demand';



