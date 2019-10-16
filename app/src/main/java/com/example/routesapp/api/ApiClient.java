package com.example.routesapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/";
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/";

    public static Retrofit retrofit = null;

    public static Retrofit getClient(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }













//for Money ....
    public static final String BASE_URL_Money = "https://api.github.com";
    public static Retrofit retrofit_Money = null;

    public static Retrofit getClient_Money(){

        if (retrofit_Money == null){
            retrofit_Money = new Retrofit.Builder()
                    .baseUrl(BASE_URL_Money)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit_Money;

    }



//for News
    public static final String BASE_URL_News = "https://api.github.com";
    public static Retrofit retrofit_News = null;

    public static Retrofit getClient_News(){

        if (retrofit_News == null){
            retrofit_News = new Retrofit.Builder()
                    .baseUrl(BASE_URL_News)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit_News;

    }

}
