package com.routesme.taxi_screen.Interface;

import com.routesme.taxi_screen.Model.AuthCredentials;
import com.routesme.taxi_screen.Model.AuthCredentialsError;
import com.routesme.taxi_screen.Model.BannerModel;
import com.routesme.taxi_screen.Model.CurrenciesModel;
import com.routesme.taxi_screen.Model.ItemsModel;
import com.routesme.taxi_screen.Model.OfficePlatesList;
import com.routesme.taxi_screen.Model.TabletChannelModel;
import com.routesme.taxi_screen.Model.TabletCredentials;
import com.routesme.taxi_screen.Model.TabletInfo;
import com.routesme.taxi_screen.Model.TabletPasswordModel;
import com.routesme.taxi_screen.Model.TaxiOfficeList;
import com.routesme.taxi_screen.Model.Token;
import com.routesme.taxi_screen.Model.VideoModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RoutesApi {




    @POST("auth")
    @Headers({"Content-Type: application/json", "DataServiceVersion: 2.0"})
    Call<Token> loginUserSuccess(@Body AuthCredentials authCredentials);

    //Currencies List [Money Strip]
    @POST("auth")
    @Headers({"Content-Type: application/json", "DataServiceVersion: 2.0"})
    Call<List<AuthCredentialsError>> loginUserFailed(@Body AuthCredentials authCredentials);



    //Taxi Office List
    @GET("TaxiOffices")
    Call<TaxiOfficeList> getTaxiOfficeList(@Header("Authorization") String token, @Query("include") String include);

    //Taxi Offic plates list
    @GET("Tablets")
    Call<OfficePlatesList> getOfficePlatesList(@Header("Authorization") String token, @Query("taxiOfficeId") int taxiOfficeId, @Query("include") String include);


    //Tablet Register
    @POST("Tablets")
    @Headers({"Content-Type: application/json", "DataServiceVersion: 2.0"})
    Call<TabletInfo> tabletRegister(@Header("Authorization") String token, @Body TabletCredentials tabletCredentials);



    //Tablet Data
    @GET("Channels")
    Call<List<TabletChannelModel>> getTabletData(@Query("tabletserialno") String tablet_sNo, @Header("Authorization") String token);


    //Videos List
    @GET("Channels")
    Call<List<VideoModel>> getVideos(@Query("channelidvideolist") int ch_ID_Videos);


    //Increase Video View Times ...
    @PUT("Videos")
    Call <ResponseBody> IncreaseVideoViewTimes(@Query("VideoViewsId") int VideoViewsId);





    //Banners List
    @GET("Channels")
    Call<List<BannerModel>> getBanners(@Query("channelidadvlist") int ch_ID_Banners);


    //Increase Banner View Times ...
    @PUT("Advertisings")
    Call <ResponseBody> IncreaseBannerViewTimes(@Query("AdvertisingViewsId") int BannerViewsId);




    //Banners List
    @GET("List_Items")
    Call<List<ItemsModel>> getItems(@Query("getlistitems") int ItemsListId);


    //Currencies List [Money Strip]
    @GET("Currencies")
    Call<List<CurrenciesModel>> getCurrencies(@Query("getMyCurrencies") int CurrenciesListId);




    //PUT Tablet Current[ Lat & Long & Active ] values
    /*
    @PUT("Tablets")
    Call PutCurrentTabletData(@Path("tabletserialno") String CurrenciesListId,
                              @Path("lot") double lot,
                              @Path("lng") double lng,
                              @Path("act") boolean act);

    @GET("data/2.5/weather?appid={apikey}&lat={lat}&lon={lon}&units={units}")
    Call<Weather> getWeatherReport1(@Path("apikey") String apikey,
                                    @Path("lat") String lat,
                                    @Path("lon") String lng,
                                    @Path("units") String units);

    */

   // @PUT("Tablets?tabletserialno={tabletserialno}&lot={lot}&lng={lng}&act={act}")


    @PUT("Tablets")
    Call <ResponseBody> PutCurrentTabletData(@Query("tabletserialno") String tabletserialno,
                                             @Query("lot") double lat,
                                             @Query("lng") double lng,
                                             @Query("act") boolean isActive);

    //Get Tablet Password
    @GET("Tablets")
    Call<List<TabletPasswordModel>> getTabletPassword(@Query("tabletserialno") String tablet_sNo);



}