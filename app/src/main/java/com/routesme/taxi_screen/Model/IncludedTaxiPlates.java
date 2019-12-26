package com.routesme.taxi_screen.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IncludedTaxiPlates {

    @SerializedName("recentTabletsCarPlateNumbers")
    public List<TaxiPlate> recentPlateNumbers;


    //Constructor...
    public IncludedTaxiPlates() {
    }

    public IncludedTaxiPlates(List<TaxiPlate> recentPlateNumbers) {
        this.recentPlateNumbers = recentPlateNumbers;
    }



    //Getter...
    public List<TaxiPlate> getRecentPlateNumbers() {
        return recentPlateNumbers;
    }



    //Setter...

    public void setRecentPlateNumbers(List<TaxiPlate> recentPlateNumbers) {
        this.recentPlateNumbers = recentPlateNumbers;
    }
}
