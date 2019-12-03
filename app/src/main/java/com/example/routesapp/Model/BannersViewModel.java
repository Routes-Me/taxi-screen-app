package com.example.routesapp.Model;

import android.content.Context;
import android.widget.Toast;

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

public class BannersViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<BannerModel>> bannersList;

    //we will call this method to get the data
    public LiveData<List<BannerModel>> getBanners(int ch_ID, Context context, String token) {
        //if the list is null

        if (bannersList == null) {
            bannersList = new MutableLiveData<List<BannerModel>>();
            //we will load it asynchronously from server in this method
            loadBannersList(ch_ID,context,token);
        }


       // bannersList = new MutableLiveData<List<BannerModel>>();
        //we will load it asynchronously from server in this method
        //loadBannersList(ch_ID,context,token);

        //finally we will return the list
        return bannersList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadBannersList(int ch_ID, final Context context, String token) {

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
        Call<List<BannerModel>> call = api.getBanners(ch_ID, token);


        call.enqueue(new Callback<List<BannerModel>>() {
            @Override
            public void onResponse(Call<List<BannerModel>> call, Response<List<BannerModel>> response) {

                 try {
                //finally we are setting the list to our MutableLiveData
                bannersList.setValue(response.body());
                    }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<BannerModel>> call, Throwable t) {
               // Toast.makeText(context, "Banners....  "+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}

