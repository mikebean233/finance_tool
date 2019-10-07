package petersonlabs.financetool.dao;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import petersonlabs.financetool.model.Category;
import petersonlabs.financetool.model.Source;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class Dao {
	private DataSource dataSource;

	private final String GET_CATEGORIES = "SELECT id, name, description FROM dbo.categories;";
	private final String GET_SOURCES    = "SELECT id, name FROM dbo.sources;";
	private final String INSERT_SOURCE  = "" +
		"IF NOT EXISTS (\n" +
		"    SELECT 1\n" +
		"    FROM dbo.sources\n" +
		"    WHERE name = ?\n" +
		")\n" +
		"    INSERT INTO dbo.sources(name) VALUES (?);";

	@Inject
	public Dao(DataSource dataSource)
	{
		this.dataSource = dataSource;
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

		ImmutableSet.Builder<Source> resultBuilder = ImmutableSet.builder();
		while(resultSet.next()){
			resultBuilder.add(getSourceFromResultSet(resultSet));
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

	private Source getSourceFromResultSet(ResultSet resultSet) throws SQLException
	{
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
