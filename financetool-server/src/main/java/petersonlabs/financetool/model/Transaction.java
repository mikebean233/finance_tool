package petersonlabs.financetool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Transaction {
	private Date date;
	private String vendor;
	private float amount;
	private Source source;
	private Category category;
	private Type type;

	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"transactions",
		ImmutableList.of("date", "vendor", "amount", "source", "category", "type"),
		false);

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

	public Date getDate() {
		return date;
	}

	public String getVendor() {
		return vendor;
	}

	public float getAmount() {
		return amount;
	}

	public Source getSource() {
		return source;
	}

	public Category getCategory() {
		return category;
	}

	public Type getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transaction that = (Transaction) o;
		return Float.compare(that.amount, amount) == 0 &&
			date.equals(that.date) &&
			vendor.equals(that.vendor) &&
			source.equals(that.source) &&
			category.equals(that.category) &&
			type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, vendor, amount, source, category, type);
	}
}
