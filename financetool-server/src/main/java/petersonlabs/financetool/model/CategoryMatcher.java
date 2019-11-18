package petersonlabs.financetool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

public class CategoryMatcher implements Identifiable{
	private int id;
	private String text;
	private Category category;

	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"category_matchers",
		ImmutableList.of("text", "category"),
		true);

	@JsonCreator
	public CategoryMatcher(
		@JsonProperty(value = "id", defaultValue = "-1") Integer id,
		@JsonProperty(value = "text", defaultValue = "") String text,
		@JsonProperty(value = "category", defaultValue = "") Category category) {
		this.id = Objects.isNull(id) ? -1 : id;
		this.text = Objects.isNull(text) ? "" : text;
		this.category = Objects.isNull(category) ? Category.UNKNOWN : category;
	}

	@Override
	public int getId(){return id;}
	public String getText(){return text;}
	public Category getCategory(){return category;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CategoryMatcher that = (CategoryMatcher) o;
		return id == that.id &&
			text.equals(that.text) &&
			category.equals(that.category);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text, category);
	}
}
