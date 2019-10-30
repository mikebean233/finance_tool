package petersonlabs.financetool.model;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

public class Type implements Identifiable{
	private int id;
	private String name;

	public static final Type UNKNOWN = new Type(1, "UNKNOWN");
	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"types",
		ImmutableList.of("name"),
		true);


	public Type(int id, String name) {
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
		Type type = (Type) o;
		return id == type.id &&
			name.equals(type.name);
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
