package com.example.routesapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaxiOfficeList {

    @SerializedName("offices")
    @Expose
    private List<Office> offices;


    @SerializedName("recentOffices")
    @Expose
    private List<Office> recentOffices;


    //Constructors...


    public TaxiOfficeList() {
    }

    public TaxiOfficeList(List<Office> offices, List<Office> recentOffices) {
        this.offices = offices;
        this.recentOffices = recentOffices;
    }



    //Getter...
    public List<Office> getOffices() {
        return offices;
    }

    public List<Office> getRecentOffices() {
        return recentOffices;
    }


    //Setter...

    public void setOffices(List<Office> offices) {
        this.offices = offices;
    }

    public void setRecentOffices(List<Office> recentOffices) {
        this.recentOffices = recentOffices;
    }
}
