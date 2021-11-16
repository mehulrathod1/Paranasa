package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class Pagination{

	@SerializedName("per_page")
	private String perPage;

	@SerializedName("total_records")
	private int totalRecords;

	@SerializedName("total_pages")
	private int totalPages;

	@SerializedName("current_page")
	private String currentPage;

	public void setPerPage(String perPage){
		this.perPage = perPage;
	}

	public String getPerPage(){
		return perPage;
	}

	public void setTotalRecords(int totalRecords){
		this.totalRecords = totalRecords;
	}

	public int getTotalRecords(){
		return totalRecords;
	}

	public void setTotalPages(int totalPages){
		this.totalPages = totalPages;
	}

	public int getTotalPages(){
		return totalPages;
	}

	public void setCurrentPage(String currentPage){
		this.currentPage = currentPage;
	}

	public String getCurrentPage(){
		return currentPage;
	}

	@Override
 	public String toString(){
		return 
			"Pagination{" + 
			"per_page = '" + perPage + '\'' + 
			",total_records = '" + totalRecords + '\'' + 
			",total_pages = '" + totalPages + '\'' + 
			",current_page = '" + currentPage + '\'' + 
			"}";
		}
}