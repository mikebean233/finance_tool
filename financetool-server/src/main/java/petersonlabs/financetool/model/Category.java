package petersonlabs.financetool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import petersonlabs.financetool.Util;

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

	@JsonCreator
	public Category(
		@JsonProperty(value = "id", defaultValue = "-1") Integer id,
		@JsonProperty(value = "name", defaultValue = "") String name,
		@JsonProperty(value = "description", defaultValue = "") String description) {
		this.id = Objects.isNull(id) ? -1 : id;
		this.name = Objects.isNull(name) ? "" : name;
		this.description = Objects.isNull(description) ? "" : description;
	}

	@Override
	public String toString() {
		return Util.serialize(this);
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
