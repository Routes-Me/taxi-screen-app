package com.example.routesapp.Model;

public class BannerModel {

    private int Adv_ID;
    private String Adv_URL;



//Constructor ...

    public BannerModel() {
    }

    public BannerModel(int adv_ID, String adv_URL) {
        Adv_ID = adv_ID;
        Adv_URL = adv_URL;
    }






    //Getter ...

    public int getAdv_ID() {
        return Adv_ID;
    }

    public String getAdv_URL() {
        return Adv_URL;
    }






    //Setter ...

    public void setAdv_ID(int adv_ID) {
        Adv_ID = adv_ID;
    }

    public void setAdv_URL(String adv_URL) {
        Adv_URL = adv_URL;
    }


}
