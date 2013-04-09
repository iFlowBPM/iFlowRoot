CREATE INDEX IDX_REPORTING ON dbo.reporting(flowid , pid ,subpid );

ALTER TABLE dbo.users ADD 
  orgadm_users NUMBER(1)  NOT NULL DEFAULT 1,
  orgadm_flows NUMBER(1)  NOT NULL DEFAULT 1,
  orgadm_processes NUMBER(1)  NOT NULL DEFAULT 1,
  orgadm_resources NUMBER(1)  NOT NULL DEFAULT 1,
  orgadm_org NUMBER(1)  NOT NULL DEFAULT 1;