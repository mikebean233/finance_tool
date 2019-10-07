package petersonlabs.financetool.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.openapi.annotations.*;
import petersonlabs.financetool.dao.Dao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.Buffer;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class TransactionResource {
	private final Javalin javalin;
	private final Dao dao;

	@Inject
	public TransactionResource(Javalin javalin, Dao dao){
		this.javalin = javalin;
		this.dao = dao;
	}

	public void setup()
	{
		javalin.post("/api/transactions", this::upsertTransactions);
		javalin.get("/api/categories", this::getCategories);
		javalin.get("/api/sources", this::getSources);
		javalin.post("/api/sources", this::postSources);
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
		context.json(dao.getSources());
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
		context.json(dao.getCategories());
	}

	@OpenApi(
		summary = "upload csv",
		description = "upload a transaction csv file from the bank to add it to the database",
		fileUploads = { @OpenApiFileUpload(name = "transactions", description = "the csv file from the bank")},
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "text/html")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/transactions",
		method = HttpMethod.GET
	)
	private void upsertTransactions(Context context)
	{
		UploadedFile file = context.uploadedFile("transactions");
		if(Objects.nonNull(file)){
			InputStream inputStream = file.getContent();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			ok(context);
		}
		else{
			context.status(500);
			context.result("no file uploaded");
		}
	}

	private void ok(Context context)
	{
		context.status(200);
		context.result("OK");
	}
}
