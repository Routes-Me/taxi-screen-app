package com.routesme.taxi_screen.java.Server.Interface;

import com.google.gson.JsonElement;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentialsError;
import com.routesme.taxi_screen.kotlin.Model.BannerModel;
import com.routesme.taxi_screen.kotlin.Model.OfficePlatesList;
import com.routesme.taxi_screen.kotlin.Model.TabletCredentials;
import com.routesme.taxi_screen.kotlin.Model.TabletInfo;
import com.routesme.taxi_screen.kotlin.Model.TaxiOfficeList;
import com.routesme.taxi_screen.kotlin.Model.Token;
import com.routesme.taxi_screen.kotlin.Model.VideoModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RoutesApi {

    @POST("auth")
    @Headers({"Content-Type: application/json", "DataServiceVersion: 2.0"})
    Call<JsonElement> loginAuth(@Body AuthCredentials authCredentials);
    //Taxi Office List
    @GET("TaxiOffices")
    Call<TaxiOfficeList> getTaxiOfficeList(@Query("include") String include);
    //Taxi Offic plates list
    @GET("Tablets")
    Call<OfficePlatesList> getOfficePlatesList(@Query("taxiOfficeId") int taxiOfficeId);
    //Tablet Register
    @POST("Tablets")
    @Headers({"Content-Type: application/json", "DataServiceVersion: 2.0"})
    Call<TabletInfo> tabletRegister(@Body TabletCredentials tabletCredentials);
    //Videos List
    @GET("Channels")
    Call<List<VideoModel>> getVideos(@Query("channelidvideolist") int ch_ID_Videos);
    //Banners List
    @GET("Channels")
    Call<List<BannerModel>> getBanners(@Query("channelidadvlist") int ch_ID_Banners);

}
