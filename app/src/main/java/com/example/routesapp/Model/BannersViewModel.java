package com.example.routesapp.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.ServerRetrofit;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.View.Login.LoginScreen;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BannersViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<BannerModel>> bannersList;

    //we will call this method to get the data
    public LiveData<List<BannerModel>> getBanners(int ch_ID, Activity activity, String token) {
        //if the list is null

        if (bannersList == null) {
            bannersList = new MutableLiveData<List<BannerModel>>();
            //we will load it asynchronously from server in this method
            loadBannersList(ch_ID,activity,token);
        }


       // bannersList = new MutableLiveData<List<BannerModel>>();
        //we will load it asynchronously from server in this method
        //loadBannersList(ch_ID,context,token);

        //finally we will return the list
        return bannersList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadBannersList(int ch_ID, final Activity activity, String token) {
        try {

            ServerRetrofit serverRetrofit = new ServerRetrofit(activity);
            RoutesApi api = null;
            if (serverRetrofit != null){
                api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            }else {
                return;
            }

            //  RoutesApi api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            Call<List<BannerModel>> call = api.getBanners(ch_ID);


            call.enqueue(new Callback<List<BannerModel>>() {
                @Override
                public void onResponse(Call<List<BannerModel>> call, Response<List<BannerModel>> response) {

                    if (response.isSuccessful()){
                        //finally we are setting the list to our MutableLiveData
                        try {
                            bannersList.setValue(response.body());
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
                public void onFailure(Call<List<BannerModel>> call, Throwable t) {
                    // Toast.makeText(context, "Banners....  "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    activity.recreate();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }


    }
}

