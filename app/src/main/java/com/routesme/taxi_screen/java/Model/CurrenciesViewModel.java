package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrenciesViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<CurrenciesModel>> currenciesList;

    //we will call this method to get the data
    public LiveData<List<CurrenciesModel>> getCurrencies(int ch_ID, Activity activity) {
        //if the list is null

        if (currenciesList == null) {
            currenciesList = new MutableLiveData<List<CurrenciesModel>>();
            //we will load it asynchronously from server in this method
             loadCurrenciesList(ch_ID,activity);
        }
        //finally we will return the list
        return currenciesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadCurrenciesList(int ch_ID, final Activity activity) {

    RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
    RoutesApi api = null;
    if (retrofitClientInstance != null){
        api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
    }else {
        return;
    }
    Call<List<CurrenciesModel>> call = api.getCurrencies(ch_ID);
    call.enqueue(new Callback<List<CurrenciesModel>>() {
        @Override
        public void onResponse(Call<List<CurrenciesModel>> call, Response<List<CurrenciesModel>> response) {

            if (response.isSuccessful() && response.body() != null  && !response.body().isEmpty()){
                //finally we are setting the list to our MutableLiveData
                    currenciesList.setValue(response.body());
            }
        }
        @Override
        public void onFailure(Call<List<CurrenciesModel>> call, Throwable t) {

        }
    });
    }
}


