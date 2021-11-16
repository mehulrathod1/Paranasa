package com.ni.parnasa.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonToPojoUtils {

    public Object getPojo(String jsonObject, Class<?> pojoClass) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonObject, (Class) pojoClass);
    }

}
