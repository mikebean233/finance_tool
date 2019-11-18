package petersonlabs.financetool.model;

import org.junit.jupiter.api.Test;
import petersonlabs.financetool.Util;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
	@Test
	void Deserialize() throws IOException
	{
		String json = """
			  {
					"date": 1563948000000,
					"vendor": "VENDOR",
					"amount": -8,
					"source": {
					  "id": 18
					},
					"category": {
					  "id": 1
					},
					"type": {
					  "id": 1
					},
					"manualCategory": false
				  }
		""";

		Transaction transaction = Util.parseJson(json, Transaction.class);

		Transaction targetTransaction = new Transaction(
			Date.from(Instant.ofEpochMilli(1563948000000L)),
			"VENDOR",
			-8,
			new Source(18, ""),
			new Category(1, "", ""),
			new Type(1, ""),
			false);


		assertEquals(targetTransaction, transaction);
	}

}
