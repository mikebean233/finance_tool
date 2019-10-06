IF NOT EXISTS(
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES T
    WHERE T.TABLE_NAME = 'types'
          AND T.TABLE_SCHEMA = 'dbo')
  BEGIN
    CREATE TABLE dbo.types (
      id   TINYINT PRIMARY KEY IDENTITY (1, 1),
      name VARCHAR(100)
    )
  END

IF NOT EXISTS(SELECT 1
              FROM dbo.types
              WHERE name = 'DEBIT')
  BEGIN
    INSERT INTO dbo.types (name) VALUES ('DEBIT')
  END

IF NOT EXISTS(SELECT 1
              FROM dbo.types
              WHERE name = 'CREDIT')
  BEGIN
    INSERT INTO dbo.types (name) VALUES ('CREDIT')
  END

