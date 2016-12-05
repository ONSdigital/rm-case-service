-- Materialized View: casesvc.helpline_mi_daily_calls

-- DROP MATERIALIZED VIEW casesvc.helpline_mi_daily_calls;

CREATE MATERIALIZED VIEW casesvc.helpline_mi_daily_calls AS 
 SELECT caseevent.caseeventid,
    caseevent.caseid,
    caseevent.createdby,
    caseevent.createddatetime,
    caseevent.category,
    caseevent.subcategory,
    category.role
   FROM casesvc.caseevent,
    casesvc.category
  WHERE caseevent.category::text = category.name::text AND category.manual = true AND date_trunc('day'::text, now()) = date_trunc('day'::text, caseevent.createddatetime)
WITH NO DATA;

