package com.example.routesapp.GooglePlaceDataApi.model.placePhotos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("width")
    @Expose
    private String width;

    @SerializedName("photo_reference")
    @Expose
    private String photo_reference;



    //Constructor
    public Photo(String height, String width, String photo_reference) {
        this.height = height;
        this.width = width;
        this.photo_reference = photo_reference;
    }



    //Getter
    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }



    //Setter
    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }
}
