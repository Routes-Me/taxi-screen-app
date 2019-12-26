package com.routesme.taxi_screen.Model;

public class RequestHeaders {

    private String country_code, Authorization, app_version;

    public RequestHeaders() {
    }


    public RequestHeaders( String authorization, String country_code, String app_version) {
        this.country_code = country_code;
        Authorization = authorization;
        this.app_version = app_version;
    }


    public String getCountry_code() {
        return country_code;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public String getApp_version() {
        return app_version;
    }


    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }
}
