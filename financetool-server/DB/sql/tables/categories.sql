IF NOT EXISTS(
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES T
    WHERE T.TABLE_NAME = 'categories'
          AND T.TABLE_SCHEMA = 'dbo')
  BEGIN
    CREATE TABLE dbo.categories (
      id          INT IDENTITY (1, 1),
      name        VARCHAR(100),
      description VARCHAR(500)
    )
  END

IF NOT EXISTS(
    SELECT 1
    FROM dbo.categories
    WHERE name = 'UNKNOWN')
  BEGIN
    INSERT INTO dbo.categories (name, description) VALUES ('UNKNOWN', 'unknown category')
  END
