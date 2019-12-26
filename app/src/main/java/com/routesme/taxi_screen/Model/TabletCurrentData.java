package com.routesme.taxi_screen.Model;

public class TabletCurrentData {

    private String TabletSerialNo;
    private double Lat, Lng;
    private boolean isActive;


    //Constructor....
    public TabletCurrentData() {
    }

    public TabletCurrentData(String tabletSerialNo, double lat, double lng, boolean isActive) {
        this.TabletSerialNo = tabletSerialNo;
        this.Lat = lat;
        this.Lng = lng;
        this.isActive = isActive;
    }



    //Getter....
    public String getTabletSerialNo() {
        return TabletSerialNo;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public boolean isActive() {
        return isActive;
    }




    //Setter....
    public void setTabletSerialNo(String tabletSerialNo) {
        TabletSerialNo = tabletSerialNo;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
