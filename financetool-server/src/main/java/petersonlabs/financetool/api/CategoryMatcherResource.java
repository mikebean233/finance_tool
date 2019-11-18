package petersonlabs.financetool.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import petersonlabs.financetool.Util;
import petersonlabs.financetool.dao.Dao;
import petersonlabs.financetool.model.Category;
import petersonlabs.financetool.model.CategoryMatcher;
import petersonlabs.financetool.model.Transaction;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class CategoryMatcherResource {
	private final Javalin javalin;
	private final Dao dao;
	private final SimpleDateFormat dateFormat;

	@Inject
	public CategoryMatcherResource(Javalin javalin, Dao dao, SimpleDateFormat dateFormat){
		this.javalin = javalin;
		this.dao = dao;
		this.dateFormat = dateFormat;
	}

	public void setup() {
		javalin.get("/api/match_categories", this::matchCategories);
		javalin.get("/api/category_matchers", this::getCategoryMatchers);
		javalin.post("/api/category_matchers", this::postCategoryMatcher);
		javalin.delete("/api/category_matchers/:id", this::deleteCategoryMatcher);
	}

	@OpenApi(
		tags = {"CategoryMatcher"},
		summary = "match categories",
		description = """
		Use category matchers to assign categories to transactions.
		""",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "text/plain")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/match_categories",
		method = HttpMethod.GET
	)
	public void matchCategories(Context context) throws SQLException, IOException
	{
		dao.matchCategories();
		context.result("OK");
	}


	@OpenApi(
		tags = {"CategoryMatcher"},
		summary = "get category matchers",
		description = "gets the category matchers",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/category_matchers",
		method = HttpMethod.GET
	)
	private void getCategoryMatchers(Context context) throws SQLException
	{
		context.json(dao.getCategoryMatchers().values());
	}

	@OpenApi(
		tags = {"CategoryMatcher"},
		summary = "add category matcher",
		description = "add category matcher",
		requestBody = @OpenApiRequestBody(
			content = @OpenApiContent(from = CategoryMatcher.class, type = "text/csv", isArray = true),
			required = true,
			description = "csv list of new category matcher"
		),
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/category_matchers",
		method = HttpMethod.POST
	)
	private void postCategoryMatcher(Context context) throws SQLException, IOException
	{
		dao.insertCategoryMatchers(
			ImmutableSet.copyOf(
				Util.parseJson(context.body(),
					new TypeReference<List<CategoryMatcher>>(){})
			)
		);
	}

	@OpenApi(
		tags = {"CategoryMatcher"},
		summary = "delete a category matcher",
		description = "delete a category matcher",
		pathParams = { @OpenApiParam(name = "id", description = "category matcher id", type = Integer.class)},
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "text/plain")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/category_matchers/:id",
		method = HttpMethod.DELETE
	)
	private void deleteCategoryMatcher(Context context) throws SQLException
	{
		dao.deleteCategoryMatcher(context.pathParam("id", Integer.class).get());
	}

}
