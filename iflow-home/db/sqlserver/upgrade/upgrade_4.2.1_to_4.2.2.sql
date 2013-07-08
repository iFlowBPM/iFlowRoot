CREATE INDEX IDX_REPORTING ON dbo.reporting(flowid , pid ,subpid );

ALTER TABLE dbo.users ADD 
  orgadm_users NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_flows NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_processes NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_resources NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_org NUMERIC(1)  NOT NULL DEFAULT 1;