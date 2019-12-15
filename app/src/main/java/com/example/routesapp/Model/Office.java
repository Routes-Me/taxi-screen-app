package com.example.routesapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Office {


    private Integer taxiOfficeID, taxiOfficeCarsCount;

    private String taxiOfficeName, taxiOfficePhoneNumber;


    //Constructor...

    public Office() {
    }

    public Office(Integer taxiOfficeID, Integer taxiOfficeCarsCount, String taxiOfficeName, String taxiOfficePhoneNumber) {
        this.taxiOfficeID = taxiOfficeID;
        this.taxiOfficeCarsCount = taxiOfficeCarsCount;
        this.taxiOfficeName = taxiOfficeName;
        this.taxiOfficePhoneNumber = taxiOfficePhoneNumber;
    }


    //Getter...

    public Integer getTaxiOfficeID() {
        return taxiOfficeID;
    }

    public Integer getTaxiOfficeCarsCount() {
        return taxiOfficeCarsCount;
    }

    public String getTaxiOfficeName() {
        return taxiOfficeName;
    }

    public String getTaxiOfficePhoneNumber() {
        return taxiOfficePhoneNumber;
    }


    //Setter...

    public void setTaxiOfficeID(Integer taxiOfficeID) {
        this.taxiOfficeID = taxiOfficeID;
    }

    public void setTaxiOfficeCarsCount(Integer taxiOfficeCarsCount) {
        this.taxiOfficeCarsCount = taxiOfficeCarsCount;
    }

    public void setTaxiOfficeName(String taxiOfficeName) {
        this.taxiOfficeName = taxiOfficeName;
    }

    public void setTaxiOfficePhoneNumber(String taxiOfficePhoneNumber) {
        this.taxiOfficePhoneNumber = taxiOfficePhoneNumber;
    }
}
