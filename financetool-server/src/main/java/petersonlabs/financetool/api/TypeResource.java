package petersonlabs.financetool.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import petersonlabs.financetool.Util;
import petersonlabs.financetool.dao.Dao;
import petersonlabs.financetool.model.Type;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TypeResource {
	private final Javalin javalin;
	private final Dao dao;
	private final SimpleDateFormat dateFormat;

	@Inject
	public TypeResource(Javalin javalin, Dao dao, SimpleDateFormat dateFormat){
		this.javalin = javalin;
		this.dao = dao;
		this.dateFormat = dateFormat;
	}

	public void setup() {
		javalin.get("/api/types", this::getTypes);
		javalin.post("/api/types", this::postTypes);
		javalin.delete("/api/types/:id", this::deleteCategory);
	}

	@OpenApi(
		tags = {"Type"},
		summary = "get types",
		description = "gets the types",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/types",
		method = HttpMethod.GET
	)
	private void getTypes(Context context) throws SQLException
	{
		context.json(dao.getTypes().values());
	}

	@OpenApi(
		tags = {"Type"},
		summary = "add types",
		description = "add types",
		requestBody = @OpenApiRequestBody(
			content = @OpenApiContent(from = Type.class, type = "text/csv", isArray = true),
			required = true,
			description = "csv list of new Types"
		),
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/types",
		method = HttpMethod.POST
	)
	private void postTypes(Context context) throws SQLException, IOException
	{
		dao.insertTypes(
			ImmutableSet.copyOf(
				Util.parseJson(context.body(),
					new TypeReference<List<Type>>(){})
			)
		);
	}

	@OpenApi(
		tags = {"Type"},
		summary = "delete a type",
		description = "delete a category",
		pathParams = { @OpenApiParam(name = "id", description = "type id", type = Integer.class)},
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "text/plain")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/types/:id",
		method = HttpMethod.DELETE
	)
	private void deleteCategory(Context context) throws SQLException
	{
		dao.deleteType(context.pathParam("id", Integer.class).get());
	}



}
