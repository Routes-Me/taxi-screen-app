package com.example.routesapp.api;

import com.example.routesapp.GooglePlaceDataApi.model.placePhotos.PhotoResponse;
import com.example.routesapp.MoneyAndNewsPart.MoneyResponse;
import com.example.routesapp.MoneyAndNewsPart.NewsResponse;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiServices {

    /*
    @GET("/users/{username}")
    Call<User> getPlaceData(@Path("username") String username);
*/

/*
    @GET("json?placeid={Id}&fields=name,rating,formatted_phone_number&key=AIzaSyCMh38qM0lEiRtXPHiA7OZsdLZW-l2Nuv4")
    Call<JsonObject> getPlaceData(@Path("Id") String placeId);
   // Call<User> getPlaceData(@Path("id") String id);
*/


//To get Place [Rating & Name]
       @GET
    Call<JsonObject> getPlaceData(@Url String url);


//To get Place [Photo]
    @GET
    Call<PhotoResponse> getPlacePhoto(@Url String url);



    //ChIJG3ZFvI6Ezz8ResxnEC303Ek












    //for Money
    @GET("/search/users?q=language:java+location:lagos")
    Call<MoneyResponse> getItems_Money();



    //for News
    @GET("/search/users?q=language:java+location:lagos")
    Call<NewsResponse> getItems_News();



}
