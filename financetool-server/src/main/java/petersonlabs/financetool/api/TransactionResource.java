package petersonlabs.financetool.api;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.openapi.annotations.*;
import petersonlabs.financetool.dao.Dao;
import petersonlabs.financetool.model.Category;
import petersonlabs.financetool.model.Source;
import petersonlabs.financetool.model.Transaction;
import petersonlabs.financetool.model.Type;

import java.io.*;
import java.nio.Buffer;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static petersonlabs.financetool.api.Resource.ok;

@Singleton
public class TransactionResource {
	private final Javalin javalin;
	private final Dao dao;
	private final SimpleDateFormat dateFormat;

	@Inject
	public TransactionResource(Javalin javalin, Dao dao, SimpleDateFormat dateFormat){
		this.javalin = javalin;
		this.dao = dao;
		this.dateFormat = dateFormat;
	}

	public void setup()
	{
		javalin.post("/api/transactions", this::upsertTransactions);
		javalin.get("/api/transactions", this::getTransactions);
	}

	@OpenApi(
		summary = "get transactions",
		description = "gets the transactions",
		responses = {
			@OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
			@OpenApiResponse(status = "500")
		},
		path = "/api/transactions",
		method = HttpMethod.GET
	)
	public void getTransactions(Context context) throws SQLException
	{
		context.json(dao.getTransactions());
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
	private void upsertTransactions(Context context) throws SQLException, IOException, ParseException
	{
		UploadedFile file = context.uploadedFile("transactions");
		if(Objects.nonNull(file)){
			InputStream inputStream = file.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


			// Read in and map the table header
			String[] columnHeaders = reader.readLine().replace("\"", "").split(",");
			HashMap<Integer, String> columnMap = new HashMap<>();
			for(int i = 0; i < columnHeaders.length; ++i)
				columnMap.put(i, columnHeaders[i]);


			ImmutableSet.Builder<Transaction> transactionBuilder = ImmutableSet.builder();

			Set<Source> sources = ImmutableSet.copyOf(dao.getSources().values());

			Map<String, Type> typeNameMap = ((Collection<Type>)dao.getTypes().values()).stream()
				.collect(Collectors.toMap(Type::getName, Function.identity()));

			while(reader.ready()){
				String thisLine = reader.readLine().replace("\"", "");
				transactionBuilder.add(
					buildTransactionFromLine(
						columnMap,
						sources,
						typeNameMap,
						thisLine)
				);
			}
			dao.insertTransactions(transactionBuilder.build());

			ok(context);
		}
		else{
			context.status(500);
			context.result("no file uploaded");
		}
	}

	private Transaction buildTransactionFromLine(
		Map<Integer, String> columnMap,
		Set<Source> sources,
		Map<String, Type> typeNameMap,
		String thisLine
	) throws SQLException, ParseException {

		Date date = null;
		String vendor = null;
		float amount = Float.POSITIVE_INFINITY;
		Source source = null;
		Category category = Category.UNKNOWN;
		Type type = null;


		String[] values = thisLine.split(",");
		for (int column = 0; column < columnMap.values().size(); ++column) {
			String thisValue = values[column];
			String columnHeader = columnMap.get(column);

			if("Date".equals(columnHeader)) {
				date = dateFormat.parse(thisValue);
			} else if("Transaction".equals(columnHeader)) {
				type = typeNameMap.getOrDefault(thisValue, Type.UNKNOWN);
			} else if("Name".equals(columnHeader)) {

				// TODO: do this with a tree to make things faster
				for(Source thisSource : sources) {
					if(thisValue.contains(thisSource.getName())) {
						source = thisSource;

						String[] vendorSourceParts = thisValue.split(thisSource.getName());
						if(vendorSourceParts.length > 0)
							vendor = vendorSourceParts[1];
						else
							vendor = "";

						break;
					}
				}

				if(Objects.isNull(source)) {
					vendor = thisValue;
					source = Source.UNKNOWN;
				}
			} else if("Amount".equals(columnHeader)) {
				amount = Float.parseFloat(thisValue);
			}

		}
		return new Transaction(date, vendor, amount, source, category, type);
	}
}
