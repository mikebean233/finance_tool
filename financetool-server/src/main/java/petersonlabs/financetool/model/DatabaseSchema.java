package petersonlabs.financetool.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class DatabaseSchema {
	String tableName;
	List<String> columns;
	List<String> allColumns;
	boolean hasId;

	public DatabaseSchema(String tableName, List<String> columns, boolean hasId) {
		this.tableName = tableName;
		this.columns = columns;
		this.hasId = hasId;

		ImmutableList.Builder<String> allColumnsBuilder = ImmutableList.builder();
		if(hasId)
			allColumnsBuilder.add("id");

		allColumnsBuilder.addAll(columns);
		this.allColumns = allColumnsBuilder.build();
	}

	public String getTableName(){return tableName;}
	public List<String> getNonIdColumns(){return columns;}
	public List<String> getAllColumns(){return allColumns;}
	public boolean hasId(){return hasId;}
}
