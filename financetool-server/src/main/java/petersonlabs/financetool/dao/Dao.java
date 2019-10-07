package petersonlabs.financetool.dao;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import petersonlabs.financetool.model.Category;
import petersonlabs.financetool.model.Source;
import petersonlabs.financetool.model.Transaction;
import petersonlabs.financetool.model.Type;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class Dao {
	private DataSource dataSource;

	private final String GET_CATEGORIES = "SELECT id, name, description FROM dbo.categories;";
	private final String GET_SOURCES    = "SELECT id, name FROM dbo.sources;";
	private final String GET_TYPES      = "SELECT id, name FROM dbo.types;";
	private final String INSERT_SOURCE  = "" +
		"IF NOT EXISTS (\n     " +
		"    SELECT 1\n        " +
		"    FROM dbo.sources\n" +
		"    WHERE name = ?\n  " +
		")\n                   " +
		"    INSERT INTO dbo.sources(name) VALUES (?);";

	private final String INSERT_TRANSACTION  = "" +
		"IF NOT EXISTS (\n          " +
		"    SELECT 1\n             " +
		"    FROM dbo.transactions\n" +
		"    WHERE date = ?\n       " +
		"		AND vendor = ?\n    " +
		"		AND amount = ?\n    " +
		"		AND source = ?\n    " +
		"		AND type = ?" +
		")\n" +
		"    INSERT INTO dbo.transactions(date, vendor, amount, category, source, type) " +
		"		VALUES ( ?, ?, ?, ?, ?, ? );";


	@Inject
	public Dao(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public void insertTransactions(Set<Transaction> transactions) throws SQLException
	{
		for(Transaction transaction : transactions)
			insertTransaction(transaction);
	}

	public void insertTransaction(Transaction transaction) throws SQLException {
		PreparedStatement preparedStatement = this.dataSource.getConnection()
			.prepareStatement(INSERT_TRANSACTION);

		preparedStatement.setDate(1, convertToSqlDate(transaction.getDate()));
		preparedStatement.setString(2, transaction.getVendor());
		preparedStatement.setFloat(3, transaction.getAmount());
		preparedStatement.setInt(4, transaction.getSource().getId());
		preparedStatement.setInt(5, transaction.getType().getId());

		preparedStatement.setDate(6, convertToSqlDate(transaction.getDate()));
		preparedStatement.setString(7, transaction.getVendor());
		preparedStatement.setFloat(8, transaction.getAmount());
		preparedStatement.setInt(9, transaction.getCategory().getId());
		preparedStatement.setInt(10, transaction.getSource().getId());
		preparedStatement.setInt(11, transaction.getType().getId());

		preparedStatement.execute();

	}

	private java.sql.Date convertToSqlDate(Date date)
	{
		return new java.sql.Date(date.getTime());
	}

	public void insertSources(Set<String> sources) throws SQLException {
		for(String source : sources)
			insertSource(source);
	}

	public void insertSource(String sourceName) throws SQLException {
		PreparedStatement preparedStatement = this.dataSource.getConnection()
			.prepareStatement(INSERT_SOURCE);

		preparedStatement.setString(1, sourceName);
		preparedStatement.setString(2, sourceName);
		preparedStatement.execute();
	}

	public Set<Source> getSources() throws SQLException
	{
		ResultSet resultSet = this.dataSource.getConnection()
			.createStatement()
			.executeQuery(GET_SOURCES);

		List<Source> unsortedResult = new ArrayList<>();
		while(resultSet.next()){
			unsortedResult.add(getSourceFromResultSet(resultSet));
		}

		return ImmutableSet.copyOf(
			unsortedResult.stream()
				.sorted((a,b) -> b.getName().compareTo(a.getName()))
				.collect(Collectors.toList())
			);
	}

	public Set<Type> getTypes() throws SQLException
	{
		ResultSet resultSet = this.dataSource.getConnection()
			.createStatement()
			.executeQuery(GET_TYPES);

		ImmutableSet.Builder<Type> resultBuilder = ImmutableSet.builder();
		while(resultSet.next()){
			resultBuilder.add(getTypeFromResultSet(resultSet));
		}
		return resultBuilder.build();
	}

	public Set<Category> getCategories() throws SQLException
	{
		ResultSet resultSet = this.dataSource.getConnection()
			.createStatement()
			.executeQuery(GET_CATEGORIES);

		ImmutableSet.Builder<Category> resultBuilder = ImmutableSet.builder();
		while(resultSet.next()){
			resultBuilder.add(getCategoryFromResultSet(resultSet));
		}
		return resultBuilder.build();
	}

	private Type getTypeFromResultSet(ResultSet resultSet) throws SQLException {
		return new Type(
			resultSet.getInt("id"),
			resultSet.getString("name")
		);
	}

	private Source getSourceFromResultSet(ResultSet resultSet) throws SQLException {
		return new Source(
			resultSet.getInt("id"),
			resultSet.getString("name")
		);
	}

	private Category getCategoryFromResultSet(ResultSet resultSet) throws SQLException
	{
		return new Category(
			resultSet.getInt("id"),
			resultSet.getString("name"),
			resultSet.getString("description")
		);
	}

}
