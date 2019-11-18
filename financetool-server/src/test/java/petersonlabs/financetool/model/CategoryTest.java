package petersonlabs.financetool.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import petersonlabs.financetool.Util;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
	@Test
	void Deserialize() throws IOException
	{
		String json = """
			{"id":123,"name":"cat1","description":"desc1"}
		""";

		Category category = Util.parseJson(json, Category.class);
		assertEquals(new Category(123, "cat1", "desc1"), category);
	}

	@Test
	void Deserialize_default_id() throws IOException
	{
		String json = """
			{"name":"cat1","description":"desc1"}
		""";

		Category category = Util.parseJson(json, Category.class);
		assertEquals(new Category(-1, "cat1", "desc1"), category);
	}

	@Test
	void Deserialize_id_only() throws IOException
	{
		String json = """
			{"id":3}
		""";

		Category category = Util.parseJson(json, Category.class);
		assertEquals(new Category(3, "", ""), category);
	}


	@Test
	void DeserializeList() throws IOException
	{
		String json = """
			[
				{"id":123,"name":"cat1","description":"desc1"},
				{"id":456,"name":"cat2","description":"desc2"}
			]
		""";

		List<Category> categories = Util.parseJson(json, new TypeReference<List<Category>>(){});
		assertEquals(
			ImmutableList.copyOf(categories),
			ImmutableList.of(
				new Category(123, "cat1", "desc1"),
				new Category(456, "cat2", "desc2")
			)
		);
	}

	@Test
	void Serialize() throws IOException
	{
		String json = """
		{"id":123,"name":"cat1","description":"desc1"}""";

		Category category = new Category(123, "cat1", "desc1");
		assertEquals(json, Util.serialize(category));
	}
}