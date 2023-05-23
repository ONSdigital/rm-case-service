ALTER TABLE ONLY casesvc.case
DROP CONSTRAINT casegroup_fkey,
    ADD CONSTRAINT casegroup_fkey FOREIGN KEY (case_group_fk) REFERENCES casesvc.casegroup(case_group_pk)
        ON DELETE CASCADE;

ALTER TABLE ONLY casesvc.caseevent
DROP CONSTRAINT case_fkey,
    ADD CONSTRAINT case_fkey FOREIGN KEY (case_fk) REFERENCES casesvc.case(case_pk)
        ON DELETE CASCADE;
