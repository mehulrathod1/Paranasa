package com.ni.parnasa.models.convertedCurrency;

import com.google.gson.annotations.SerializedName;

public class ToItem{

	@SerializedName("mid")
	private String mid;

	@SerializedName("quotecurrency")
	private String quotecurrency;

	public String getMid(){
		return mid;
	}

	public String getQuotecurrency(){
		return quotecurrency;
	}
}