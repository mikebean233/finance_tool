package petersonlabs.financetool.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import petersonlabs.financetool.Util;
import petersonlabs.financetool.model.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static petersonlabs.financetool.Util.doWithLock;
import static petersonlabs.financetool.dao.SqlStrings.*;

@Singleton
public class Dao<T> {
	private DataSource dataSource;

	private final ReentrantReadWriteLock categoriesLock       = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock sourcesLock          = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock typesLock            = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock categoryMatchersLock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock transactionsLock     = new ReentrantReadWriteLock();

	private final Map<Integer, Category>         categories = new HashMap<>();
	private final Map<Integer, Source>              sources = new HashMap<>();
	private final Map<Integer, Type>                  types = new HashMap<>();
	private final Map<Integer, CategoryMatcher> catMatchers = new HashMap<>();
	private final HashSet<Transaction>         transactions = new HashSet<>();

	@Inject
	public Dao(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	/*********************** CACHE **************************/

	private <T extends Identifiable> void rebuildCache (
		Lock lock,
		String sql,
		Map<Integer, T> cache,
		Util.ThrowingProducer<T, ResultSet, SQLException> builder
	) throws SQLException {

		doWithLock(lock, () -> {
			ResultSet resultSet = this.dataSource.getConnection()
				.createStatement()
				.executeQuery(sql);

			cache.clear();
			while (resultSet.next()) {
				T item = builder.produce(resultSet);
				cache.put(item.getId(), item);
			}
		});
	}

	private void rebuildCategoriesCache() throws SQLException {
		rebuildCache(categoriesLock.writeLock(), SELECT_CATEGORIES, categories, this::getCategoryFromResultSet);
	}

	private void rebuildSourcesCache() throws SQLException {
		rebuildCache(sourcesLock.writeLock(), SELECT_SOURCES, sources, this::getSourceFromResultSet);
	}

	private void rebuildTypesCache() throws SQLException {
		rebuildCache(typesLock.writeLock(), SELECT_TYPES, types, this::getTypeFromResultSet);
	}

	private void rebuildCategoryMatchersCache() throws SQLException {
		rebuildCache(categoryMatchersLock.writeLock(), SELECT_CAT_MATCHERS, catMatchers, this::getCatMatcherFromResultSet);
	}

	private void rebuildTransactionsCache() throws SQLException {
		doWithLock(transactionsLock.writeLock(), () -> {
			ResultSet resultSet = this.dataSource.getConnection()
				.createStatement()
				.executeQuery(SELECT_TRANSACTIONS);

			transactions.clear();
			while(resultSet.next()){
				Transaction thisTransaction = getTransactionFromResultSet(resultSet);
				transactions.add(thisTransaction);
			}
		});
	}

	/********************* Getters ********************************/

	public Map<Integer, Category> getCategories() throws SQLException{
		if(categories.isEmpty())
			rebuildCategoriesCache();

		return doWithLock(categoriesLock.readLock(), () -> ImmutableMap.copyOf(categories));
	}

	public Map<Integer, Source> getSources() throws SQLException{
		if(sources.isEmpty())
			rebuildSourcesCache();

		return doWithLock(sourcesLock.readLock(), () -> ImmutableMap.copyOf(sources));
	}

	public Map<Integer, Type> getTypes() throws SQLException{
		if(types.isEmpty())
			rebuildTypesCache();

		return doWithLock(typesLock.readLock(), () -> ImmutableMap.copyOf(types));
	}

	public Map<Integer, CategoryMatcher> getCategoryMatchers() throws SQLException{
		if(catMatchers.isEmpty())
			rebuildCategoryMatchersCache();

		return doWithLock(categoryMatchersLock.readLock(), () -> ImmutableMap.copyOf(catMatchers));
	}

	public Set<Transaction> getTransactions() throws SQLException{
		if(transactions.isEmpty())
			rebuildTransactionsCache();

		return doWithLock(transactionsLock.readLock(), () -> ImmutableSet.copyOf(transactions));
	}

	public Category getCategoryFromId(int id) throws SQLException {
		if(categories.isEmpty())
			rebuildCategoriesCache();

		return doWithLock(categoriesLock.readLock(), () -> categories.get(id));
	}

	public Source getSourceFromId(int id) throws SQLException {
		if(sources.isEmpty())
			rebuildSourcesCache();

		return doWithLock(sourcesLock.readLock(), () -> sources.get(id));
	}

	public Type getTypeFromId(int id) throws SQLException {
		if(types.isEmpty())
			rebuildTypesCache();

		return doWithLock(typesLock.readLock(), () -> types.get(id));
	}

	public CategoryMatcher getCategoryMatcherFromId(int id) throws SQLException {
		if(catMatchers.isEmpty())
			rebuildCategoriesCache();

		return doWithLock(categoryMatchersLock.readLock(), () -> catMatchers.get(id));
	}

	/************************ Inserts *******************************************/

	public void insertCategories(Set<Category> categories) throws SQLException {
		for(Category category : categories)
			insertCategory(category);
	}

	public void insertCategory(Category category) throws SQLException {
		doWithLock(categoriesLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(INSERT_CATEGORY);
			preparedStatement.setString(1, category.getName());
			preparedStatement.setString(2, category.getDescription());
			preparedStatement.setString(3, category.getName());
			preparedStatement.setString(4, category.getDescription());
			preparedStatement.execute();
			preparedStatement.close();
			rebuildCategoriesCache();
		});
	}

	public void insertTypes(Set<Type> types) throws SQLException {
		for(Type type : types)
			insertType(type);
	}

	public void insertType(Type type) throws SQLException {
		doWithLock(typesLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(INSERT_TYPE);
			preparedStatement.setString(1, type.getName());
			preparedStatement.setString(2, type.getName());
			preparedStatement.execute();
			preparedStatement.close();
			rebuildCategoriesCache();
		});
	}

	public void insertSources(Set<String> sources) throws SQLException {
		for(String source : sources)
			insertSource(source);
	}

	public void insertSource(String sourceName) throws SQLException {
		doWithLock(sourcesLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(INSERT_SOURCE);
			preparedStatement.setString(1, sourceName);
			preparedStatement.setString(2, sourceName);
			preparedStatement.execute();
			preparedStatement.close();
			rebuildSourcesCache();
		});
	}

	public void insertCategoryMatchers(Set<CategoryMatcher> categoryMatchers) throws SQLException {
		for(CategoryMatcher categoryMatcher : categoryMatchers)
			insertCategoryMatcher(categoryMatcher);
	}

	public void insertCategoryMatcher(CategoryMatcher categoryMatcher) throws SQLException {
		doWithLock(categoryMatchersLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(INSERT_CAT_MATCHERS);
			preparedStatement.setString(1, categoryMatcher.getText());
			preparedStatement.setInt(2, categoryMatcher.getCategory().getId());
			preparedStatement.setString(3, categoryMatcher.getText());
			preparedStatement.setInt(4, categoryMatcher.getCategory().getId());
			preparedStatement.execute();
			preparedStatement.close();
			rebuildCategoryMatchersCache();
		});
	}

	public void insertTransactions(Set<Transaction> transactions) throws SQLException {
		for(Transaction transaction : transactions)
			insertTransaction(transaction);
	}

	public void insertTransaction(Transaction transaction) throws SQLException {
		doWithLock(transactionsLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(INSERT_TRANSACTION);
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
			preparedStatement.close();
			transactions.add(transaction);
		});
	}

	private java.sql.Date convertToSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	/*************************** Deletes ********************************/
	private <T extends Identifiable> void delete(Lock lock, String sql, Map<Integer, T> cache, Integer id) throws SQLException{
		doWithLock(lock, () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.execute();
			cache.remove(id);
			preparedStatement.closeOnCompletion();
		});
	}

	public void deleteCategory(Integer id) throws SQLException {
		delete(categoriesLock.writeLock(), DELETE_CATEGORY, categories, id);
	}

	public void deleteSource(Integer id) throws SQLException {
		delete(sourcesLock.writeLock(), DELETE_SOURCE, sources, id);
	}

	public void deleteType(Integer id) throws SQLException {
		delete(typesLock.writeLock(), DELETE_TYPE, types, id);
	}

	public void deleteCategoryMatcher(Integer id) throws SQLException {
		delete(categoryMatchersLock.writeLock(), DELETE_CAT_MATCHERS, catMatchers, id);
	}

	/*************************** update *********************/

	public void updateTransactions(Set<Transaction> transactions) throws SQLException {
		doWithLock(transactionsLock.writeLock(), () -> {
			for(Transaction transaction : transactions)
				updateTransaction(transaction);

			rebuildTransactionsCache();
		});
	}

	public void matchCategories() throws SQLException {
		doWithLock(transactionsLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(MATCH_CATEGORIES);
			preparedStatement.execute();
			preparedStatement.close();
		});
	}

	public void updateTransaction(Transaction transaction) throws SQLException {
		doWithLock(transactionsLock.writeLock(), () -> {
			PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(UPDATE_TRANSACTION);
			preparedStatement.setInt(1, transaction.getCategory().getId());
			preparedStatement.setBoolean(2, transaction.getManualCategory());

			preparedStatement.setDate(3, convertToSqlDate(transaction.getDate()));
			preparedStatement.setString(4, transaction.getVendor());
			preparedStatement.setFloat(5, transaction.getAmount());
			preparedStatement.setInt(6, transaction.getSource().getId());
			preparedStatement.setInt(7, transaction.getType().getId());
			preparedStatement.execute();
			preparedStatement.close();
		});
	}

	/*************************** ResultSet readers *********************/

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

	private CategoryMatcher getCatMatcherFromResultSet(ResultSet resultSet) throws SQLException {
		return new CategoryMatcher(
			resultSet.getInt("id"),
			resultSet.getString("text"),
			getCategoryFromId(resultSet.getInt("category"))
		);
	}

	private Transaction getTransactionFromResultSet(ResultSet resultSet) throws SQLException {
		return new Transaction(
			resultSet.getDate("date"),
			resultSet.getString("vendor"),
			resultSet.getFloat("amount"),
			getSourceFromId(resultSet.getInt("source")),
			getCategoryFromId(resultSet.getInt("category")),
			getTypeFromId(resultSet.getInt("type")),
			resultSet.getBoolean("manual_category")
		);
	}
}
