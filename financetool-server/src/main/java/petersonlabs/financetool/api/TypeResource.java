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
import java.util.stream.Collectors;

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
	}

	@OpenApi(
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
}
