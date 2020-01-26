package com.routesme.taxi_screen.Model;

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

    @SerializedName("simCardNumber")
    @Expose
    private String simCardNumber;



    //Constructor...

    public TabletCredentials() {
    }

    public TabletCredentials(int taxiOfficeId, String taxiPlateNumber, String tabletSerialNumber, String simCardNumber) {
        this.taxiOfficeId = taxiOfficeId;
        this.taxiPlateNumber = taxiPlateNumber;
        this.tabletSerialNumber = tabletSerialNumber;
        this.simCardNumber = simCardNumber;
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

    public String getSimCardNumber() {
        return simCardNumber;
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

    public void setSimCardNumber(String simCardNumber) {
        this.simCardNumber = simCardNumber;
    }


}
