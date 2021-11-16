package com.ni.parnasa.models.currencies;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CurrencySymbolResponse{

	@SerializedName("currencies")
	private List<CurrenciesItem> currencies;

	public List<CurrenciesItem> getCurrencies(){
		return currencies;
	}

	public class CurrenciesItem{

		@SerializedName("symbol")
		private String symbol;

		@SerializedName("currency")
		private String currency;

		@SerializedName("abbreviation")
		private String abbreviation;

		public String getSymbol(){
			return symbol;
		}

		public String getCurrency(){
			return currency;
		}

		public String getAbbreviation(){
			return abbreviation;
		}
	}
}