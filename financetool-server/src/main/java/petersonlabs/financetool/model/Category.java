package petersonlabs.financetool.model;

import java.util.Objects;

public class Category {
	private int id;
	private String name;
	private String description;

	public static final Category UNKNOWN = new Category(1, "UNKNOWN", "unknown category");

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
