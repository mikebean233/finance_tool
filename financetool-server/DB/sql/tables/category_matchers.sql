IF NOT EXISTS(
        SELECT 1
        FROM INFORMATION_SCHEMA.TABLES T
        WHERE T.TABLE_NAME = 'category_matchers'
          AND T.TABLE_SCHEMA = 'dbo')
    BEGIN
        CREATE TABLE dbo.category_matchers (
            id          INT PRIMARY KEY IDENTITY (1, 1),
            text VARCHAR(100)
        )
    END


---------------- category -------------------
IF NOT EXISTS(
        SELECT 1
        FROM sys.columns
        WHERE Name = 'category'
          AND Object_ID = Object_ID('dbo.category_matchers'))
    BEGIN
        ALTER TABLE dbo.category_matchers
            ADD category INT
    END

IF NOT EXISTS(
        SELECT 1
        FROM sys.foreign_keys
        WHERE object_id = OBJECT_ID('dbo.FK_category_matchers_id')
          AND parent_object_id = OBJECT_ID('dbo.categories')
    )
    BEGIN
        ALTER TABLE dbo.category_matchers
            ADD CONSTRAINT FK_category_matchers_id FOREIGN KEY (category)
                REFERENCES dbo.categories (id)
    END
