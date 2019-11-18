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
	}

	@OpenApi(
		tags = {"Source"},
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
}
