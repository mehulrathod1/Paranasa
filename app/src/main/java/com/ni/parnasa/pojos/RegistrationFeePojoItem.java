package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class RegistrationFeePojoItem{

	@SerializedName("register_fee")
	private String registerFee;

	@SerializedName("subfee_id")
	private String subfeeId;

	public void setRegisterFee(String registerFee){
		this.registerFee = registerFee;
	}

	public String getRegisterFee(){
		return registerFee;
	}

	public void setSubfeeId(String subfeeId){
		this.subfeeId = subfeeId;
	}

	public String getSubfeeId(){
		return subfeeId;
	}

	@Override
 	public String toString(){
		return 
			"RegistrationFeePojoItem{" + 
			"register_fee = '" + registerFee + '\'' + 
			",subfee_id = '" + subfeeId + '\'' + 
			"}";
		}
}