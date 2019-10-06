IF NOT EXISTS (
	SELECT * 
	FROM INFORMATION_SCHEMA.TABLES T 
		WHERE T.TABLE_NAME = 'transactions'
		AND T.TABLE_SCHEMA = 'dbo')
BEGIN
	CREATE TABLE dbo.transactions (
		vendor VARCHAR(100),
		[date] DATETIME2,
        amount FLOAT
	)
END

IF NOT EXISTS(SELECT 1 FROM sys.columns 
          WHERE Name = 'category'
          AND Object_ID = Object_ID('dbo.transactions'))
BEGIN
   ALTER TABLE dbo.transaction ADD category INT NULL
END

IF NOT EXISTS (SELECT * FROM sys.foreign_keys 
   WHERE object_id = OBJECT_ID('dbo.FK_categories_id')
   AND parent_object_id = OBJECT_ID('dbo.category')
)
BEGIN
    ALTER TABLE dbo.transaction
    ADD CONSTRAINT FK_categories_id FOREIGN KEY (categories)
        REFERENCES dbo.transaction_category (id)
END
