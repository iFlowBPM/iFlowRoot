INSERT INTO counter VALUES ('nodekey',0,GETDATE());

CREATE TABLE  dbo.active_node (
  nodekey varchar(50) NOT NULL,
  expiration DATETIME NOT NULL,
  PRIMARY KEY (nodekey)
);

CREATE PROCEDURE get_next_pid 
  @retnodekey INT OUT
AS
BEGIN
	DECLARE @tmp INT
    set @retnodekey = 1
    select value into @tmp from counter where name='nodekey'
    update counter set value=(@tmp +1) where  name='nodekey'
    select value into @retnodekey from counter where name='nodekey'    
END
GO

CREATE TABLE  dbo.sharedobjectrefresh (
  id int NOT NULL IDENTITY,
  flowid int NOT NULL,
  PRIMARY KEY (id)
);