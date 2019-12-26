package com.routesme.taxi_screen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaxiOfficeList {

    @SerializedName("data")
    @Expose
    private List<Office> officesData;


    @SerializedName("included")
    public IncludedOffices officesIncluded;



    //Constructors...

    public TaxiOfficeList() {
    }

    public TaxiOfficeList(List<Office> officesData) {
        this.officesData = officesData;
    }



    //Getter...

    public List<Office> getOfficesData() {
        return officesData;
    }

    public IncludedOffices getOfficesIncluded() {
        return officesIncluded;
    }


    //Setter...

    public void setOfficesData(List<Office> officesData) {
        this.officesData = officesData;
    }

    public void setOfficesIncluded(IncludedOffices officesIncluded) {
        this.officesIncluded = officesIncluded;
    }
}
