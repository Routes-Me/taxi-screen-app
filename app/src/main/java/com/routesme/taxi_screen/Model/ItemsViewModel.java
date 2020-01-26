package com.routesme.taxi_screen.Model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.View.Login.LoginScreen;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsViewModel  extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<ItemsModel>> itemsList;

    //we will call this method to get the data
    public LiveData<List<ItemsModel>> getItems(int ch_ID, Activity activity) {
        //if the list is null

        if (itemsList == null) {
            itemsList = new MutableLiveData<List<ItemsModel>>();
            //we will load it asynchronously from server in this method
            loadItemsList(ch_ID,activity);
        }



        //finally we will return the list
        return itemsList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadItemsList(int ch_ID, final Activity activity) {

    RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
    RoutesApi api = null;
    if (retrofitClientInstance != null){
        api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
    }else {
        return;
    }

    Call<List<ItemsModel>> call = api.getItems(ch_ID);


    call.enqueue(new Callback<List<ItemsModel>>() {
        @Override
        public void onResponse(Call<List<ItemsModel>> call, Response<List<ItemsModel>> response) {


            if (response.isSuccessful() && response.body() != null){
                //finally we are setting the list to our MutableLiveData
                    itemsList.setValue(response.body());

            }else {
                Toast.makeText(activity, "ItemsViewModel . request is not Success! , with error code:   " + response.code(), Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onFailure(Call<List<ItemsModel>> call, Throwable t) {
            if (t instanceof IOException) {
                Toast.makeText(activity, "ItemsViewModel. request onFailure ... this is an actual network failure!", Toast.LENGTH_SHORT).show();
                // logging probably not necessary
            }
            else {
                Toast.makeText(activity, "ItemsViewModel. request onFailure ... conversion issue!", Toast.LENGTH_SHORT).show();
                // todo log to some central bug tracking service
            }
        }
    });


    }
}

