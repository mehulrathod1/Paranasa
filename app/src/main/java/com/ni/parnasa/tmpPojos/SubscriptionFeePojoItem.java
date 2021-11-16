package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionFeePojoItem {

	@SerializedName("subfee_id")
	private String subfeeId;

	@SerializedName("monthly_subscription_fee_silver")
	private String monthlySubscriptionFeeSilver;

	@SerializedName("yearly_subscription_fee_gold")
	private String yearlySubscriptionFeeGold;

	@SerializedName("monthly_subscription_fee_gold")
	private String monthlySubscriptionFeeGold;

	@SerializedName("yearly_subscription_fee_silver")
	private String yearlySubscriptionFeeSilver;

	@SerializedName("free_trial")
	private String freeTrial;

	public void setSubfeeId(String subfeeId){
		this.subfeeId = subfeeId;
	}

	public String getSubfeeId(){
		return subfeeId;
	}

	public void setMonthlySubscriptionFeeSilver(String monthlySubscriptionFeeSilver){
		this.monthlySubscriptionFeeSilver = monthlySubscriptionFeeSilver;
	}

	public String getMonthlySubscriptionFeeSilver(){
		return monthlySubscriptionFeeSilver;
	}

	public void setYearlySubscriptionFeeGold(String yearlySubscriptionFeeGold){
		this.yearlySubscriptionFeeGold = yearlySubscriptionFeeGold;
	}

	public String getYearlySubscriptionFeeGold(){
		return yearlySubscriptionFeeGold;
	}

	public void setMonthlySubscriptionFeeGold(String monthlySubscriptionFeeGold){
		this.monthlySubscriptionFeeGold = monthlySubscriptionFeeGold;
	}

	public String getMonthlySubscriptionFeeGold(){
		return monthlySubscriptionFeeGold;
	}

	public void setYearlySubscriptionFeeSilver(String yearlySubscriptionFeeSilver){
		this.yearlySubscriptionFeeSilver = yearlySubscriptionFeeSilver;
	}

	public String getYearlySubscriptionFeeSilver(){
		return yearlySubscriptionFeeSilver;
	}

	public void setFreeTrial(String freeTrial){
		this.freeTrial = freeTrial;
	}

	public String getFreeTrial(){
		return freeTrial;
	}

	@Override
 	public String toString(){
		return 
			"SubscriptionFeePojoItem{" + 
			"subfee_id = '" + subfeeId + '\'' + 
			",monthly_subscription_fee_silver = '" + monthlySubscriptionFeeSilver + '\'' + 
			",yearly_subscription_fee_gold = '" + yearlySubscriptionFeeGold + '\'' + 
			",monthly_subscription_fee_gold = '" + monthlySubscriptionFeeGold + '\'' + 
			",yearly_subscription_fee_silver = '" + yearlySubscriptionFeeSilver + '\'' + 
			",free_trial = '" + freeTrial + '\'' + 
			"}";
		}
}