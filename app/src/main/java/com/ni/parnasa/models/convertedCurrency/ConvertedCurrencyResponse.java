package com.ni.parnasa.models.convertedCurrency;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ConvertedCurrencyResponse{

	@SerializedName("amount")
	private double amount;

	@SerializedName("terms")
	private String terms;

	@SerializedName("privacy")
	private String privacy;

	@SerializedName("from")
	private String from;

	@SerializedName("to")
	private List<ToItem> to;

	@SerializedName("timestamp")
	private String timestamp;

	public double getAmount(){
		return amount;
	}

	public String getTerms(){
		return terms;
	}

	public String getPrivacy(){
		return privacy;
	}

	public String getFrom(){
		return from;
	}

	public List<ToItem> getTo(){
		return to;
	}

	public String getTimestamp(){
		return timestamp;
	}
}