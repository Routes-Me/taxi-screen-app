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
    private String DeviceId;

    @SerializedName("SimSerialNumber")
    @Expose
    private String SimSerialNumber;



    //Constructor...

    public TabletCredentials() {
    }

    public TabletCredentials(int taxiOfficeId, String taxiPlateNumber, String DeviceId, String simCardNumber) {
        this.taxiOfficeId = taxiOfficeId;
        this.taxiPlateNumber = taxiPlateNumber;
        this.DeviceId = DeviceId;
        this.SimSerialNumber = simCardNumber;
    }



    //Getter...

    public int getTaxiOfficeId() {
        return taxiOfficeId;
    }

    public String getTaxiPlateNumber() {
        return taxiPlateNumber;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getSimSerialNumber() {
        return SimSerialNumber;
    }


    //Setter...

    public void setTaxiOfficeId(int taxiOfficeId) {
        this.taxiOfficeId = taxiOfficeId;
    }

    public void setTaxiPlateNumber(String taxiPlateNumber) {
        this.taxiPlateNumber = taxiPlateNumber;
    }

    public void setDeviceId(String DeviceId) {
        this.DeviceId = DeviceId;
    }

    public void setSimSerialNumber(String SimSerialNumber) {
        this.SimSerialNumber = SimSerialNumber;
    }


}
