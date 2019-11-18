IF NOT EXISTS(
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES T
    WHERE T.TABLE_NAME = 'transactions'
          AND T.TABLE_SCHEMA = 'dbo')
  BEGIN
    CREATE TABLE dbo.transactions (
      [date] DATETIME2 NOT NULL,
      vendor VARCHAR(100) NOT NULL,
      amount FLOAT NOT NULL
    )
  END

---------------- manual_category ---------------
IF NOT EXISTS(
        SELECT 1
        FROM sys.columns
        WHERE Name = 'manual_category'
          AND Object_ID = Object_ID('dbo.transactions'))
    BEGIN
        ALTER TABLE dbo.transactions
            ADD manual_category BIT NOT NULL
            CONSTRAINT D_transactions_manual_category
                DEFAULT (0)
    END


---------------- category -------------------
IF NOT EXISTS(
    SELECT 1
    FROM sys.columns
    WHERE Name = 'category'
          AND Object_ID = Object_ID('dbo.transactions'))
  BEGIN
    ALTER TABLE dbo.transactions
      ADD category INT NOT NULL
          CONSTRAINT D_transactions_category
              DEFAULT (1)
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('FK_transactions_categories')
          AND parent_object_id = OBJECT_ID('dbo.categories')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_transactions_categories FOREIGN KEY (category)
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
      ADD source INT NOT NULL
          CONSTRAINT D_transactions_source
              DEFAULT (1)
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('FK_transactions_sources')
          AND parent_object_id = OBJECT_ID('dbo.sources')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_transactions_sources FOREIGN KEY (source)
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
      ADD type TINYINT NOT NULL
          CONSTRAINT D_transactions_type
            DEFAULT (1)
  END

IF NOT EXISTS(
    SELECT 1
    FROM sys.foreign_keys
    WHERE object_id = OBJECT_ID('FK_transactions_types')
          AND parent_object_id = OBJECT_ID('dbo.types')
)
  BEGIN
    ALTER TABLE dbo.transactions
      ADD CONSTRAINT FK_transactions_types FOREIGN KEY (source)
    REFERENCES dbo.sources (id)
  END

----------------- primary key -----------------------
  IF NOT EXISTS(
      SELECT 1
      FROM sys.key_constraints
      WHERE type = 'PK'
        AND OBJECT_NAME(parent_object_id) = 'transactions'
        AND name = 'PK_transactions_date_vendor_amount_source_type'
      )
  BEGIN
        IF EXISTS(
                SELECT 1
                FROM sys.key_constraints
                WHERE type = 'PK'
                  AND OBJECT_NAME(parent_object_id) = 'transactions'
            )
        BEGIN
            DECLARE @PKName VARCHAR(200) = (SELECT name FROM sys.key_constraints WHERE type = 'PK' AND OBJECT_NAME(parent_object_id) = 'transactions')
            DECLARE @sql VARCHAR(200) = (SELECT 'ALTER TABLE dbo.transactions DROP CONSTRAINT ' + @PKName + ' GO')
            --EXEC(@sql)

        END

        --ALTER TABLE dbo.transactions
        --ADD CONSTRAINT PK_transactions_date_vendor_amount_source_type PRIMARY KEY CLUSTERED(date, vendor, amount, source, type);
    END
