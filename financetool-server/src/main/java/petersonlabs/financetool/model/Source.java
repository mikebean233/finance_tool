package petersonlabs.financetool.model;

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

	public Source(int id, String name) {
		this.id = id;
		this.name = name;
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
