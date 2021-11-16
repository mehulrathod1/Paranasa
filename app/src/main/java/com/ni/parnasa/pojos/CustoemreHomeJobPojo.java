package com.ni.parnasa.pojos;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CustoemreHomeJobPojo{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("data")
	private List<CustoemreHomeJobPojoItemItem> custoemreHomeJobPojoItem;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setCustoemreHomeJobPojoItem(List<CustoemreHomeJobPojoItemItem> custoemreHomeJobPojoItem){
		this.custoemreHomeJobPojoItem = custoemreHomeJobPojoItem;
	}

	public List<CustoemreHomeJobPojoItemItem> getCustoemreHomeJobPojoItem(){
		return custoemreHomeJobPojoItem;
	}

	@Override
 	public String toString(){
		return 
			"CustoemreHomeJobPojo{" + 
			"message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			",custoemreHomeJobPojoItem = '" + custoemreHomeJobPojoItem + '\'' + 
			"}";
		}
}