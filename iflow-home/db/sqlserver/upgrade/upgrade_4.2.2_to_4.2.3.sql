DECLARE @sql NVARCHAR(MAX);
SELECT @sql = 'ALTER TABLE dbo.process DROP CONSTRAINT ' + name + ';'
FROM sys.key_constraints
WHERE [type] = 'PK' AND [parent_object_id] = OBJECT_ID('dbo.process');
EXEC sp_executeSQL @sql;
