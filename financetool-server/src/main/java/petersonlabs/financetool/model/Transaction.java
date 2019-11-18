package petersonlabs.financetool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class Transaction {
	private Date date;
	private String vendor;
	private float amount;
	private Source source;
	private Category category;
	private Type type;
	private boolean manualCategory;

	public static final Transaction UNKNOWN = new Transaction(
		Date.from(Instant.EPOCH),
		"",
		Float.NEGATIVE_INFINITY,
		Source.UNKNOWN,
		Category.UNKNOWN,
		Type.UNKNOWN,
		false
	);

	public static final DatabaseSchema DB_SCHEMA = new DatabaseSchema(
		"transactions",
		ImmutableList.of("date", "vendor", "amount", "source", "category", "type", "manual_category"),
		false);

	public Transaction(
		@JsonProperty(value = "date") Date date,
		@JsonProperty(value = "vendor") String vendor,
		@JsonProperty(value = "amount") float amount,
		@JsonProperty(value = "source") Source source,
		@JsonProperty(value = "category") Category category,
		@JsonProperty(value = "type") Type type,
		@JsonProperty(value = "manualCategory") boolean manualCategory){
		this.date = date;
		this.vendor = vendor;
		this.amount = amount;
		this.source = source;
		this.category = category;
		this.type = type;
		this.manualCategory = manualCategory;
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

	public boolean getManualCategory() {return manualCategory;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transaction that = (Transaction) o;
		return Float.compare(that.amount, amount) == 0 &&
			date.equals(that.date) &&
			vendor.equals(that.vendor) &&
			source.equals(that.source) &&
			type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, vendor, amount, source, type);
	}
}
