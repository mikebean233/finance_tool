package petersonlabs.financetool.dao;

import petersonlabs.financetool.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class SqlStrings {
	private static final DatabaseSchema CATEGORIES_SCHEMA   = Category.DB_SCHEMA;
	private static final DatabaseSchema SOURCES_SCHEMA      = Source.DB_SCHEMA;
	private static final DatabaseSchema TYPES_SCHEMA        = Type.DB_SCHEMA;
	private static final DatabaseSchema CAT_MATCHERS_SCHEMA = CategoryMatcher.DB_SCHEMA;
	private static final DatabaseSchema TRANSACTIONS_SCHEMA = Transaction.DB_SCHEMA;

	static final String SELECT_CATEGORIES   = buildSelectString(CATEGORIES_SCHEMA);
	static final String SELECT_SOURCES      = buildSelectString(SOURCES_SCHEMA);
	static final String SELECT_TYPES        = buildSelectString(TYPES_SCHEMA);
	static final String SELECT_TRANSACTIONS = buildSelectString(TRANSACTIONS_SCHEMA);
	static final String SELECT_CAT_MATCHERS = buildSelectString(CAT_MATCHERS_SCHEMA);

	static final String INSERT_CATEGORY     = buildInsertString(CATEGORIES_SCHEMA);
	static final String INSERT_SOURCE       = buildInsertString(SOURCES_SCHEMA);
	static final String INSERT_TYPE         = buildInsertString(TYPES_SCHEMA);
	static final String INSERT_CAT_MATCHERS = buildInsertString(CAT_MATCHERS_SCHEMA);
	static final String INSERT_TRANSACTION  = """
		IF NOT EXISTS (
			SELECT 1
			FROM dbo.transactions
			WHERE date = ?
				AND vendor = ?
				AND amount = ?
				AND source = ?
				AND type = ?)
		BEGIN
			INSERT INTO dbo.transactions (date, vendor, amount, category, source, type)
			VALUES (?,?,?,?,?,?)
		END
	""";


	static final String DELETE_CATEGORY     = buildDeleteString(CATEGORIES_SCHEMA);
	static final String DELETE_SOURCE       = buildDeleteString(SOURCES_SCHEMA);
	static final String DELETE_TYPE         = buildDeleteString(TYPES_SCHEMA);
	static final String DELETE_CAT_MATCHERS = buildDeleteString(CATEGORIES_SCHEMA);

	static final String UPDATE_TRANSACTION = """
		UPDATE dbo.transactions
			SET category = ?, manual_category = ?
			WHERE date = ?
				AND vendor = ?
				AND amount = ?
				AND source = ?
				AND type = ?
	""";

	static final String MATCH_CATEGORIES = """
		UPDATE dbo.transactions
		SET category = (
    	SELECT TOP 1 NC.category
			FROM (
				SELECT 0 [order], CT.category
				FROM dbo.category_matchers CT
				WHERE vendor LIKE '%' + text + '%'
				UNION
				SELECT 1 [order], 1 category
			) AS NC ORDER BY [order] ASC
    	)
		WHERE manual_category != 1
	""";

	/************************** Dynamic SQL Generators *****************************/
	private static String buildSelectString(DatabaseSchema tableSchema) {
		return String.format("SELECT %s FROM dbo.%s;",
			String.join(", ", tableSchema.getAllColumns()), String.join(", ", tableSchema.getTableName()));
	}

	private static String buildInsertString(DatabaseSchema tableSchema) {
		String tableName = tableSchema.getTableName();
		List<String> columns = tableSchema.getNonIdColumns();

		String whereConditionsString = columns.stream().map(column -> column + " =  ?").collect(Collectors.joining(" AND "));
		String columnsString = String.join(", ", columns);
		String questionsString = columns.stream().map(c -> "?").collect(Collectors.joining(","));

		return String.format("IF NOT EXISTS ( SELECT 1 FROM dbo.%s WHERE %s ) INSERT INTO dbo.%s ( %s ) VALUES ( %s );",
			tableName, whereConditionsString, tableName, columnsString, questionsString
		);
	}

	private static String buildDeleteString(DatabaseSchema tableSchema) {
		return String.format("DELETE from dbo.%s WHERE id = ?;", tableSchema.getTableName());
	}

}
