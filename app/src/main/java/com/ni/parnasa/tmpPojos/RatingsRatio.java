package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class RatingsRatio{

	@SerializedName("4_star")
	private int jsonMember4Star;

	@SerializedName("3_star")
	private int jsonMember3Star;

	@SerializedName("2_star")
	private int jsonMember2Star;

	@SerializedName("5_star")
	private int jsonMember5Star;

	@SerializedName("1_star")
	private int jsonMember1Star;

	public void setJsonMember4Star(int jsonMember4Star){
		this.jsonMember4Star = jsonMember4Star;
	}

	public int getJsonMember4Star(){
		return jsonMember4Star;
	}

	public void setJsonMember3Star(int jsonMember3Star){
		this.jsonMember3Star = jsonMember3Star;
	}

	public int getJsonMember3Star(){
		return jsonMember3Star;
	}

	public void setJsonMember2Star(int jsonMember2Star){
		this.jsonMember2Star = jsonMember2Star;
	}

	public int getJsonMember2Star(){
		return jsonMember2Star;
	}

	public void setJsonMember5Star(int jsonMember5Star){
		this.jsonMember5Star = jsonMember5Star;
	}

	public int getJsonMember5Star(){
		return jsonMember5Star;
	}

	public void setJsonMember1Star(int jsonMember1Star){
		this.jsonMember1Star = jsonMember1Star;
	}

	public int getJsonMember1Star(){
		return jsonMember1Star;
	}

	@Override
 	public String toString(){
		return 
			"RatingsRatio{" + 
			"4_star = '" + jsonMember4Star + '\'' + 
			",3_star = '" + jsonMember3Star + '\'' + 
			",2_star = '" + jsonMember2Star + '\'' + 
			",5_star = '" + jsonMember5Star + '\'' + 
			",1_star = '" + jsonMember1Star + '\'' + 
			"}";
		}
}