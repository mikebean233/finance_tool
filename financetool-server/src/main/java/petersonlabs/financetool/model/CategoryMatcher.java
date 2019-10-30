package petersonlabs.financetool.model;

import com.google.common.collect.ImmutableList;

public class CategoryMatcher implements Identifiable{
	private int id;
	private String text;
	private Category category;

	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"category_matchers",
		ImmutableList.of("text", "category"),
		true);

	public CategoryMatcher(int id, String text, Category category) {
		this.id = id;
		this.text = text;
		this.category = category;
	}

	@Override
	public int getId(){return id;}
	public String getText(){return text;}
	public Category getCategory(){return category;}
}
