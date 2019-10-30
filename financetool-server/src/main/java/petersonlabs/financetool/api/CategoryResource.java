package petersonlabs.financetool.api;

import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import petersonlabs.financetool.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

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
	}

	@OpenApi(
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

}
