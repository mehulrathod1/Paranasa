package com.ni.parnasa.models.country;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryResponse{

	@SerializedName("countries")
	private Countries countries;

	public Countries getCountries(){
		return countries;
	}

	public class Countries{

		@SerializedName("country")
		private List<CountryItem> country;

		public List<CountryItem> getCountry(){
			return country;
		}
	}

	public class CountryItem{

		@SerializedName("capital")
		private String capital;

		@SerializedName("countryCode")
		private String countryCode;

		@SerializedName("countryName")
		private String countryName;

		@SerializedName("continentName")
		private String continentName;

		@SerializedName("currencyCode")
		private String currencyCode;

		@SerializedName("population")
		private String population;

		public String getCapital(){
			return capital;
		}

		public String getCountryCode(){
			return countryCode;
		}

		public String getCountryName(){
			return countryName;
		}

		public String getContinentName(){
			return continentName;
		}

		public String getCurrencyCode(){
			return currencyCode;
		}

		public String getPopulation(){
			return population;
		}
	}
}