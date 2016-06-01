-- Materialized View: caseframe.helpline_mi

-- DROP MATERIALIZED VIEW caseframe.helpline_mi;

CREATE MATERIALIZED VIEW caseframe.helpline_mi AS 
 SELECT caseevent.caseeventid,
    caseevent.caseid,
    caseevent.description,
    caseevent.createdby,
    caseevent.createddatetime,
    caseevent.category,
    caseevent.subcategory,
    category.role
   FROM caseframe.caseevent,
    caseframe.category
  WHERE caseevent.createdby::text <> 'SYSTEM'::text AND caseevent.category::text = category.name::text
WITH NO DATA;

ALTER TABLE caseframe.helpline_mi
  OWNER TO postgres;
GRANT ALL ON TABLE caseframe.helpline_mi TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE caseframe.helpline_mi TO caseframesvc;
