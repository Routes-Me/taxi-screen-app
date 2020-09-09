package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.kotlin.MVVM.Model.Vehicles;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficePlatesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<com.routesme.taxi_screen.kotlin.MVVM.Model.Vehicles> Vehicles;

    //we will call this method to get the data
    public LiveData<Vehicles> getVehicles(Activity activity, int offset, int limit, int institutionId) {
        //if the list is null

        if (Vehicles == null) {
            Vehicles = new MutableLiveData<Vehicles>();
            //we will load it asynchronously from server in this method
            loadVehicles(activity, offset, limit, institutionId);
        }
        //finally we will return the list
        return Vehicles;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadVehicles(final Activity activity, int offset, int limit, int institutionId) {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }
            Call<Vehicles> call = api.getVehicles( offset, limit, institutionId);
            call.enqueue(new Callback<Vehicles>() {
                @Override
                public void onResponse(Call<Vehicles> call, Response<Vehicles> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Vehicles.setValue(response.body());
                    }
                }
                @Override
                public void onFailure(Call<Vehicles> call, Throwable t) {
                    activity.finish();
                }
            });
    }
}

