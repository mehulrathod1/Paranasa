package com.ni.parnasa.models;

public class Service_data {

    public String service_id;
    public String service_name;
    public String service_icon;
    public String keyword;

    public Service_data(String service_id, String service_name, String service_icon, String keyword) {
        this.service_id = service_id;
        this.service_name = service_name;
        this.service_icon = service_icon;
        this.keyword = keyword;
    }

    public String getService_id() {
        return service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public String getService_icon() {
        return service_icon;
    }

    public String getKeyword() {
        return keyword;
    }
}
