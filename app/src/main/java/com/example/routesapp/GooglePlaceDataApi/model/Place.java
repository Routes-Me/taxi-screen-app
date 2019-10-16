package com.example.routesapp.GooglePlaceDataApi.model;

import android.util.Log;

import com.example.routesapp.api.ApiClient;
import com.example.routesapp.api.ApiServices;
import com.example.routesapp.GooglePlaceDataApi.model.placePhotos.Photo;
import com.example.routesapp.GooglePlaceDataApi.model.placePhotos.PhotoResponse;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Place {


   // private String placeId;
    private String urlDataApi;



    private String name;
    private Double rating;

    //for photos data
    private String height,width,photo_reference;


    public Place(String placeId) {
        //this.placeId = placeId;
        urlDataApi = "json?placeid="+placeId+"&key=AIzaSyCMh38qM0lEiRtXPHiA7OZsdLZW-l2Nuv4";
        try {
            ApiClient apiClient = new ApiClient();
            final ApiServices apiService = apiClient.getClient().create(ApiServices.class);

            //get jsonObject ... Rating & Name..
            try {
                // parameters.put("login", SearchText);
                Call<JsonObject> call = apiService.getPlaceData(urlDataApi);
                //  Call<ItemResponse> call = apiService.getNews();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        //Toast.makeText(getActivity(), ""+response.body().getNews().get(0).getName(), Toast.LENGTH_SHORT).show();
                        if (response.body() != null) {
                            //Toast.makeText(getActivity(), ""+response.body().getAsJsonObject("result").get("name"), Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getActivity(), ""+response.body().getAsJsonObject("result").get("rating"), Toast.LENGTH_SHORT).show();

                          name = String.valueOf(response.body().getAsJsonObject("result").get("name"));
                          rating = response.body().getAsJsonObject("result").get("rating").getAsDouble();
                           // photo_reference = response.body().getAsJsonObject("result").getAsJsonArray("photos").get(0);


                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }


///////////get PhotoResponse////////////////////////
            try {
                // parameters.put("login", SearchText);
                Call<PhotoResponse> call = apiService.getPlacePhoto(urlDataApi);
                call.enqueue(new Callback<PhotoResponse>() {
                    @Override
                    public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                        List<Photo> photos = response.body().getPhotos();


                        if (photos != null) {

                            height = photos.get(0).getHeight();
                            width = photos.get(0).getWidth();
                            photo_reference = photos.get(0).getPhoto_reference();

                        }

                    }

                    @Override
                    public void onFailure(Call<PhotoResponse> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
////////////////////////////////////////////////////


        }catch (Exception e){}

    }





    public String getName() {
        return name;
    }

    public Double getRating() {
        return rating;
    }



    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }




    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }



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
