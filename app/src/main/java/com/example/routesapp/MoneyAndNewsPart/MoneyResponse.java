package com.example.routesapp.MoneyAndNewsPart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoneyResponse {

    @SerializedName("items")
    @Expose
    private List<Money> monies;

    public List<Money> getMonies(){
        return monies;
    }

    public void setMonies(List<Money> monies){
        this.monies = monies;
    }



}
