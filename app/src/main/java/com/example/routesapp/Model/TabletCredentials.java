package com.example.routesapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabletCredentials {

    @SerializedName("tabletRegesterTaxiOfficeID")
    @Expose
    private int taxiOfficeId;

    @SerializedName("tabletRegesterCarPlateNo")
    @Expose
    private String taxiPlateNumber;

    @SerializedName("tabletRegesterSerialNo")
    @Expose
    private String tabletSerialNumber;




    //Constructor...

    public TabletCredentials() {
    }

    public TabletCredentials(int taxiOfficeId, String taxiPlateNumber, String tabletSerialNumber) {
        this.taxiOfficeId = taxiOfficeId;
        this.taxiPlateNumber = taxiPlateNumber;
        this.tabletSerialNumber = tabletSerialNumber;
    }


    //Getter...

    public int getTaxiOfficeId() {
        return taxiOfficeId;
    }

    public String getTaxiPlateNumber() {
        return taxiPlateNumber;
    }

    public String getTabletSerialNumber() {
        return tabletSerialNumber;
    }


    //Setter...


    public void setTaxiOfficeId(int taxiOfficeId) {
        this.taxiOfficeId = taxiOfficeId;
    }

    public void setTaxiPlateNumber(String taxiPlateNumber) {
        this.taxiPlateNumber = taxiPlateNumber;
    }

    public void setTabletSerialNumber(String tabletSerialNumber) {
        this.tabletSerialNumber = tabletSerialNumber;
    }
}
