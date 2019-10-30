package petersonlabs.financetool.model;

import com.google.common.collect.ImmutableList;
import org.intellij.lang.annotations.Identifier;

import java.util.Objects;

public class Category implements Identifiable {
	private int id;
	private String name;
	private String description;

	public static final Category UNKNOWN = new Category(1, "UNKNOWN", "unknown category");
	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"categories",
		ImmutableList.of("name", "description"),
		true);

	public Category(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return id == category.id &&
			name.equals(category.name) &&
			description.equals(category.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description);
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
