package com.routesme.taxi_screen.Tracking.model;

public class TrackingLocation {
    private Double latitude = 0.0;
    private Double longitude = 0.0;




    //Constructor...

    public TrackingLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }



    //Getter...
    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }



    //Setter...
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
