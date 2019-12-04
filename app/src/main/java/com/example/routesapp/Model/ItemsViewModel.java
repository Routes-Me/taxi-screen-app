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

public class ItemsViewModel  extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<ItemsModel>> itemsList;

    //we will call this method to get the data
    public LiveData<List<ItemsModel>> getItems(int ch_ID, Context context, String savedToken) {
        //if the list is null

        if (itemsList == null) {
            itemsList = new MutableLiveData<List<ItemsModel>>();
            //we will load it asynchronously from server in this method
            loadItemsList(ch_ID,context, savedToken);
        }


       // itemsList = new MutableLiveData<List<ItemsModel>>();
        //we will load it asynchronously from server in this method
       // loadItemsList(ch_ID,context, savedToken);

        //finally we will return the list
        return itemsList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadItemsList(int ch_ID, final Context context, String savedToken) {

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
        Call<List<ItemsModel>> call = api.getItems(ch_ID,savedToken);


        call.enqueue(new Callback<List<ItemsModel>>() {
            @Override
            public void onResponse(Call<List<ItemsModel>> call, Response<List<ItemsModel>> response) {

                try {
                    //finally we are setting the list to our MutableLiveData
                    itemsList.setValue(response.body());
                }catch (Exception e){
                    //   Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<ItemsModel>> call, Throwable t) {
                 //  Toast.makeText(context, "Items....  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

