package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class SignupNewPojo{

	@SerializedName("data")
	private SignupNewPojoItem signupNewPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setSignupNewPojoItem(SignupNewPojoItem signupNewPojoItem){
		this.signupNewPojoItem = signupNewPojoItem;
	}

	public SignupNewPojoItem getSignupNewPojoItem(){
		return signupNewPojoItem;
	}

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

	@Override
 	public String toString(){
		return 
			"SignupNewPojo{" + 
			"signupNewPojoItem = '" + signupNewPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}