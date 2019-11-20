package com.example.routesapp.Model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.Interface.RoutesApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrenciesViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<CurrenciesModel>> currenciesList;

    //we will call this method to get the data
    public LiveData<List<CurrenciesModel>> getCurrencies(int ch_ID, Context context) {
        //if the list is null
      /*
        if (currenciesList == null) {
            currenciesList = new MutableLiveData<List<CurrenciesModel>>();
            //we will load it asynchronously from server in this method
             loadCurrenciesList(ch_ID,context);
        }
*/

        currenciesList = new MutableLiveData<List<CurrenciesModel>>();
        //we will load it asynchronously from server in this method
        loadCurrenciesList(ch_ID,context);

        //finally we will return the list
        return currenciesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadCurrenciesList(int ch_ID, final Context context) {

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

        RoutesApi api = retrofit.create(RoutesApi.class);
        Call<List<CurrenciesModel>> call = api.getCurrencies(ch_ID);


        call.enqueue(new Callback<List<CurrenciesModel>>() {
            @Override
            public void onResponse(Call<List<CurrenciesModel>> call, Response<List<CurrenciesModel>> response) {

                try {
                    //finally we are setting the list to our MutableLiveData
                    currenciesList.setValue(response.body());
                }catch (Exception e){
                    //   Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<CurrenciesModel>> call, Throwable t) {
                  // Toast.makeText(context, "Currencies....  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


