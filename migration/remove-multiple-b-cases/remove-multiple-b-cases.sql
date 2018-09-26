delete from casesvc.response
where casefk in (
  select id from (
    select c.casepk as id, row_number() over (partition by c.casegroupfk order by c.createddatetime desc) as rnum
    from casesvc."case" c
  where sampleunittype='B') as innercase
where rnum > 1);

delete from casesvc.caseevent
where casefk in (
  select id from (
    select c.casepk as id, row_number() over (partition by c.casegroupfk order by c.createddatetime desc) as rnum
    from casesvc."case" c
  where sampleunittype='B') as innercase
where rnum > 1);

delete from casesvc.case
where id in (
select id from (
    select c.id as id, row_number() over (partition by c.casegroupfk order by c.createddatetime desc) as rnum
    from casesvc."case" c
where sampleunittype='B') as innercase
where rnum > 1);