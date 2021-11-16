package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ViewInvoicePojo{

	@SerializedName("data")
	private ViewInvoicePojoItem viewInvoicePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setViewInvoicePojoItem(ViewInvoicePojoItem viewInvoicePojoItem){
		this.viewInvoicePojoItem = viewInvoicePojoItem;
	}

	public ViewInvoicePojoItem getViewInvoicePojoItem(){
		return viewInvoicePojoItem;
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
			"ViewInvoicePojo{" + 
			"viewInvoicePojoItem = '" + viewInvoicePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}