IF DB_ID('finance') IS NULL
	CREATE DATABASE finance
GO

USE finance

IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE type = 'S' AND name = 'liquibase')
BEGIN
	CREATE LOGIN liquibase WITH PASSWORD = '<LIQUIBASE_PASSWORD>';  
END
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE type = 'S' AND name = 'liquibase_user')
BEGIN
	CREATE USER liquibase_user FOR LOGIN liquibase   
    WITH DEFAULT_SCHEMA = dbo;  
END
GO

ALTER ROLE db_owner ADD MEMBER liquibase_user
