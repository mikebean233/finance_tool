package petersonlabs.financetool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

public class Source implements Identifiable {
	private int id;
	private String name;

	public static final Source UNKNOWN = new Source(1, "UNKNOWN");
	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"sources",
		ImmutableList.of("name"),
		true);
	@JsonCreator
	public Source(
		@JsonProperty(value = "id", defaultValue = "-1") Integer id,
		@JsonProperty(value = "name", defaultValue = "") String name){
		this.id = Objects.isNull(id) ? -1 : id;
		this.name = Objects.isNull(name) ? "" : name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Source source = (Source) o;
		return id == source.id &&
			name.equals(source.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
