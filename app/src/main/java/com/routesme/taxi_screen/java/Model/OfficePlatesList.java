package com.routesme.taxi_screen.java.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OfficePlatesList {

    @SerializedName("data")
    @Expose
    private List<TaxiPlate> officePlatesData;





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

    //Setter...

    public void setOfficePlatesData(List<TaxiPlate> officePlatesData) {
        this.officePlatesData = officePlatesData;
    }


}
