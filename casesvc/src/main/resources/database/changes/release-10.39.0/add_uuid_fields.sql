ALTER TABLE casesvc."case"
    ADD COLUMN id uuid NOT NULL;

ALTER TABLE casesvc."case" ALTER COLUMN partyid TYPE uuid USING partyid::uuid;

ALTER TABLE casesvc.casegroup
    ADD COLUMN id uuid NOT NULL;

ALTER TABLE casesvc."case"
    ADD CONSTRAINT "case_id_key" UNIQUE (id);

ALTER TABLE casesvc.casegroup
    ADD CONSTRAINT casegroup_id_key UNIQUE (id);
