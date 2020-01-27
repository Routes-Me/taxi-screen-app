package com.routesme.taxi_screen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OfficePlatesList {

    @SerializedName("data")
    @Expose
    private List<TaxiPlate> officePlatesData;


    @SerializedName("included")
    public IncludedTaxiPlates officePlatesIncluded;





    //Constructor...

    public OfficePlatesList() {
    }

    public OfficePlatesList(List<TaxiPlate> officePlatesData) {
        this.officePlatesData = officePlatesData;
    }




    //Getter...

    public List<TaxiPlate> getOfficePlatesData() {
        return officePlatesData;
    }

    public IncludedTaxiPlates getOfficePlatesIncluded() {
        return officePlatesIncluded;
    }

    //Setter...

    public void setOfficePlatesData(List<TaxiPlate> officePlatesData) {
        this.officePlatesData = officePlatesData;
    }

    public void setOfficePlatesIncluded(IncludedTaxiPlates officePlatesIncluded) {
        this.officePlatesIncluded = officePlatesIncluded;
    }
}
