package petersonlabs.financetool.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.HashMap;

public class Transaction {
	private Date date;
	private String vendor;
	private float amount;
	private Source source;
	private Category category;
	private Type type;

	public Transaction(
		Date date,
		String vendor,
		float amount,
		Source source,
		Category category,
		Type type){
		this.date = date;
		this.vendor = vendor;
		this.amount = amount;
		this.source = source;
		this.category = category;
		this.type = type;
	}

	@Override
	public String toString()
	{
		HashMap<String, String> outMap = new HashMap<>();
		outMap.put("date", date.toString());
		outMap.put("vendor", vendor);
		outMap.put("amount", Float.toString(amount));
		outMap.put("source", source.toString());
		outMap.put("category", source.toString());
		outMap.put("type", type.toString());
		try {
			return new ObjectMapper().writeValueAsString(outMap);
		} catch(JsonProcessingException ex)
		{
			return "{ error: \"ERROR BUILDING TRANSACTION JSON\"}";
		}
	}
}
