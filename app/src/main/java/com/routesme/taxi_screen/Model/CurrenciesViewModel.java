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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrenciesViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<CurrenciesModel>> currenciesList;

    //we will call this method to get the data
    public LiveData<List<CurrenciesModel>> getCurrencies(int ch_ID, Activity activity, String savedToken) {
        //if the list is null

        if (currenciesList == null) {
            currenciesList = new MutableLiveData<List<CurrenciesModel>>();
            //we will load it asynchronously from server in this method
             loadCurrenciesList(ch_ID,activity, savedToken);
        }


       // currenciesList = new MutableLiveData<List<CurrenciesModel>>();
        //we will load it asynchronously from server in this method
      //  loadCurrenciesList(ch_ID,context, savedToken);

        //finally we will return the list
        return currenciesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadCurrenciesList(int ch_ID, final Activity activity, String savedToken) {
try {
    ServerRetrofit serverRetrofit = new ServerRetrofit(activity);
    RoutesApi api = null;
    if (serverRetrofit != null){
        api = serverRetrofit.getRetrofit().create(RoutesApi.class);
    }else {
        return;
    }

    // RoutesApi api = serverRetrofit.getRetrofit().create(RoutesApi.class);
    Call<List<CurrenciesModel>> call = api.getCurrencies(ch_ID);


    call.enqueue(new Callback<List<CurrenciesModel>>() {
        @Override
        public void onResponse(Call<List<CurrenciesModel>> call, Response<List<CurrenciesModel>> response) {

            if (response.isSuccessful()){
                //finally we are setting the list to our MutableLiveData
                try {
                    currenciesList.setValue(response.body());
                }catch (Exception e){
                    Crashlytics.logException(e);
                    // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, "Error Code:   " + response.code(), Toast.LENGTH_SHORT).show();

                if (response.code() == 401) {
                    activity.startActivity(new Intent(activity, LoginScreen.class));
                    activity.finish();
                }

            }


        }

        @Override
        public void onFailure(Call<List<CurrenciesModel>> call, Throwable t) {
            // Toast.makeText(context, "Currencies....  "+t.getMessage(), Toast.LENGTH_SHORT).show();

            Toast.makeText(activity, "Error occur!", Toast.LENGTH_SHORT).show();
        }
    });
}catch (Exception e){
    Crashlytics.logException(e);
}


    }
}


