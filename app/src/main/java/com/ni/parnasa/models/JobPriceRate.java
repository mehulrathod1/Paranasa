package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobPriceRate{

	@SerializedName("basic_rate")
	private String basicRate;

	@SerializedName("hour_rate")
	private String hourRate;

	@SerializedName("tax_rate")
	private String taxRate;

	public void setBasicRate(String basicRate){
		this.basicRate = basicRate;
	}

	public String getBasicRate(){
		return basicRate;
	}

	public void setHourRate(String hourRate){
		this.hourRate = hourRate;
	}

	public String getHourRate(){
		return hourRate;
	}

	public void setTaxRate(String taxRate){
		this.taxRate = taxRate;
	}

	public String getTaxRate(){
		return taxRate;
	}

	@Override
 	public String toString(){
		return 
			"JobPriceRate{" + 
			"basic_rate = '" + basicRate + '\'' + 
			",hour_rate = '" + hourRate + '\'' + 
			",tax_rate = '" + taxRate + '\'' + 
			"}";
		}
}