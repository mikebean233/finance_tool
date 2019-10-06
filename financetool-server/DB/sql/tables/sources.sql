IF NOT EXISTS(
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES T
    WHERE T.TABLE_NAME = 'sources'
          AND T.TABLE_SCHEMA = 'dbo')
  BEGIN
    CREATE TABLE dbo.sources (
      id   INT PRIMARY KEY IDENTITY (1, 1),
      name VARCHAR(100)
    )
  END

IF NOT EXISTS(
    SELECT 1
    FROM dbo.sources
    WHERE name = 'UNKNOWN')
  BEGIN
    INSERT INTO dbo.sources (name) VALUES ('UNKNOWN')
  END
