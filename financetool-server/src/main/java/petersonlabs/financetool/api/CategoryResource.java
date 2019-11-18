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

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class CategoryResource {
	private final Javalin javalin;
	private final Dao dao;
	private final SimpleDateFormat dateFormat;

	@Inject
	public CategoryResource(Javalin javalin, Dao dao, SimpleDateFormat dateFormat){
		this.javalin = javalin;
		this.dao = dao;
		this.dateFormat = dateFormat;
	}

	public void setup() {
		javalin.get("/api/categories", this::getCategories);
		javalin.delete("/api/categories/:id/", this::deleteCategory);
		javalin.post("/api/categories", this::postCategory);
	}

	@OpenApi(
		tags = {"Category"},
		summary = "get categories",
		description = "gets the categories that the transactions fall in to",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/categories",
		method = HttpMethod.GET
	)
	private void getCategories(Context context) throws SQLException
	{
		context.json(dao.getCategories().values());
	}

	@OpenApi(
		tags = {"Category"},
		summary = "add categories",
		description = "add categories",
		requestBody = @OpenApiRequestBody(
			content = @OpenApiContent(from = Category.class, type = "text/csv", isArray = true),
			required = true,
			description = "csv list of new categories"
		),
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/categories",
		method = HttpMethod.POST
	)
	private void postCategory(Context context) throws SQLException, IOException
	{
		dao.insertCategories(
			ImmutableSet.copyOf(
				Util.parseJson(context.body(),
					new TypeReference<List<Category>> (){})
			)
		);
	}

	@OpenApi(
		tags = {"Category"},
		summary = "delete a category",
		description = "delete a category",
		pathParams = { @OpenApiParam(name = "id", description = "category id", type = Integer.class)},
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "text/plain")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/categories/:id/",
		method = HttpMethod.DELETE
	)
	private void deleteCategory(Context context) throws SQLException
	{
		dao.deleteCategory(context.pathParam("id", Integer.class).get());
	}
}
