package com.routesme.taxi_screen.Model;

public class Advertisement {

    private int advertisement_ID;
    private String advertisement_URL;


    //Constructor...

    public Advertisement() {
    }

    public Advertisement(int advertisement_ID, String advertisement_URL) {
        this.advertisement_ID = advertisement_ID;
        this.advertisement_URL = advertisement_URL;
    }


    //Getter...

    public int getAdvertisement_ID() {
        return advertisement_ID;
    }

    public String getAdvertisement_URL() {
        return advertisement_URL;
    }


    //Setter...

    public void setAdvertisement_ID(int advertisement_ID) {
        this.advertisement_ID = advertisement_ID;
    }

    public void setAdvertisement_URL(String advertisement_URL) {
        this.advertisement_URL = advertisement_URL;
    }

}
