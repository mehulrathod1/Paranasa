package com.ni.parnasa.pojos;

import java.util.List;

public class ManageServicePojo {

    private String name;
    private String price;
    private List<String> keyword;

    public ManageServicePojo(String name, String price, List<String> keyword) {
        this.name = name;
        this.price = price;
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public List<String> getKeyword() {
        return keyword;
    }
}
