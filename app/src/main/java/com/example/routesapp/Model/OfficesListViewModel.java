package com.example.routesapp.Model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.Class.ServerRetrofit;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.View.Login.LoginScreen;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
/*
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RoutesApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

 */

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
                        activity.finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<TaxiOfficeList> call, Throwable t) {
                Toast.makeText(activity, "Taxi Offices ViewModel Failure:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

