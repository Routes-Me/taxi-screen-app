package com.routesme.taxi_screen.java.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IncludedOffices {

    @SerializedName("recentOffices")
    public List<Office> recentOffices;


    //Constructor...
    public IncludedOffices() {
    }

    public IncludedOffices(List<Office> recentOffices) {
        this.recentOffices = recentOffices;
    }


    //Getter...
    public List<Office> getRecentOffices() {
        return recentOffices;
    }



    //Setter...
    public void setRecentOffices(List<Office> recentOffices) {
        this.recentOffices = recentOffices;
    }
}
