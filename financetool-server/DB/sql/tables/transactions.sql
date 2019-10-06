IF NOT EXISTS(
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES T
    WHERE T.TABLE_NAME = 'transactions'
          AND T.TABLE_SCHEMA = 'dbo')
  BEGIN
    CREATE TABLE dbo.transactions (
      [date] DATETIME2,
      vendor VARCHAR(100),
      amount FLOAT
    )
  END


---------------- category -------------------
IF NOT EXISTS(
    SELECT 1
    FROM sys.columns
    WHERE Name = 'category'
          AND Object_ID = Object_ID('dbo.transactions'))
  BEGIN
    ALTER TABLE dbo.transactions
      ADD category INT
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('dbo.FK_categories_id')
          AND parent_object_id = OBJECT_ID('dbo.categories')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_categories_id FOREIGN KEY (category)
    REFERENCES dbo.categories (id)
  END


---------------- source -------------------
IF NOT EXISTS(
    SELECT 1
    FROM sys.columns
    WHERE Name = 'source'
          AND Object_ID = Object_ID('dbo.transactions'))
  BEGIN
    ALTER TABLE dbo.transactions
      ADD source INT
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('dbo.FK_sources_id')
          AND parent_object_id = OBJECT_ID('dbo.sources')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_sources_id FOREIGN KEY (source)
    REFERENCES dbo.sources (id)
  END

---------------- type -------------------
IF NOT EXISTS(
    SELECT 1
    FROM sys.columns
    WHERE Name = 'type'
          AND Object_ID = Object_ID('dbo.transactions'))
  BEGIN
    ALTER TABLE dbo.transactions
      ADD type TINYINT
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('dbo.FK_types_id')
          AND parent_object_id = OBJECT_ID('dbo.types')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_types_id FOREIGN KEY (source)
    REFERENCES dbo.sources (id)
  END

