package petersonlabs.financetool.api;

import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import petersonlabs.financetool.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import static petersonlabs.financetool.api.Resource.*;

public class SourceResource {
	private final Javalin javalin;
	private final Dao dao;
	private final SimpleDateFormat dateFormat;

	@Inject
	public SourceResource(Javalin javalin, Dao dao, SimpleDateFormat dateFormat){
		this.javalin = javalin;
		this.dao = dao;
		this.dateFormat = dateFormat;
	}

	public void setup() {
		javalin.get("/api/sources", this::getSources);
		javalin.post("/api/sources", this::postSources);
	}

	@OpenApi(
		summary = "get sources",
		description = "gets the sources that transactions originate from",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/sources",
		method = HttpMethod.GET
	)
	private void getSources(Context context) throws SQLException
	{
		context.json(dao.getSources().values());
	}

	@OpenApi(
		summary = "add sources",
		description = "adds a list of sources in CSV format",
		requestBody = @OpenApiRequestBody(content = @OpenApiContent(type = "text/csv"), required = true, description = "csv list of new soruces"),
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/sources",
		method = HttpMethod.POST
	)
	private void postSources(Context context) throws SQLException
	{
		dao.insertSources(Arrays.stream(context.body().split(",")).collect(Collectors.toSet()));
		ok(context);
	}

}
