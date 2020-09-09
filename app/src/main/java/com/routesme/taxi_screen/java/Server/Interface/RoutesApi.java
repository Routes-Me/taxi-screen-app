package com.routesme.taxi_screen.java.Server.Interface;



import com.routesme.taxi_screen.kotlin.MVVM.Model.Institutions;
import com.routesme.taxi_screen.kotlin.MVVM.Model.RegistrationCredentials;
import com.routesme.taxi_screen.kotlin.MVVM.Model.RegistrationSuccessResponse;
import com.routesme.taxi_screen.kotlin.MVVM.Model.Vehicles;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RoutesApi {

    //Taxi Office List
    @GET("institutions")
    Call<Institutions> getInstitutions(@Query("offset") int offset, @Query("limit") int limit);
    //Taxi Offic plates list
    @GET("vehicles")
    Call<Vehicles> getVehicles(@Query("offset") int offset, @Query("limit") int limit, @Query("institutionId") int institutionId);
    //Tablet Register
    @POST("devices")
    Call<RegistrationSuccessResponse> postDevice(@Body RegistrationCredentials registrationCredentials);
}
