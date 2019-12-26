package com.routesme.taxi_screen.Model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.ServerRetrofit;
import com.routesme.taxi_screen.Interface.RoutesApi;
import com.routesme.taxi_screen.View.Login.LoginScreen;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<TaxiOfficeList> officesList;

    //we will call this method to get the data
    public LiveData<TaxiOfficeList> getTaxiOfficesList(Activity activity, String savedToken, String include) {
        //if the list is null

        if (officesList == null) {
            officesList = new MutableLiveData<TaxiOfficeList>();
            //we will load it asynchronously from server in this method
            loadOfficesList(activity, savedToken, include);
        }




        //finally we will return the list
        return officesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadOfficesList(final Activity activity, String savedToken, String include) {

        try {
            ServerRetrofit serverRetrofit = new ServerRetrofit(activity);
            RoutesApi api = null;
            if (serverRetrofit != null){
                api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            }else {
                return;
            }
            //RoutesApi api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            Call<TaxiOfficeList> call = api.getTaxiOfficeList( savedToken, include);

            call.enqueue(new Callback<TaxiOfficeList>() {
                @Override
                public void onResponse(Call<TaxiOfficeList> call, Response<TaxiOfficeList> response) {
                    if (response.isSuccessful()){
                        officesList.setValue(response.body());
                    }else {
                        Toast.makeText(activity, "Error:  " + response.code(), Toast.LENGTH_SHORT).show();
                        if (response.code() == 401) {
                            activity.startActivity(new Intent(activity, LoginScreen.class));
                        }
                        activity.finish();
                    }
                }

                @Override
                public void onFailure(Call<TaxiOfficeList> call, Throwable t) {
                    //Toast.makeText(activity, "Taxi Offices ViewModel Failure:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "onResponse: " + "failed ... No Internet Connection!, Error Code:  " + t);
                    Toast.makeText(activity, "Error occur!", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }


    }
}

