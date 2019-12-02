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

public class VideosViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<VideoModel>> videosList;

    //we will call this method to get the data
    public LiveData<List<VideoModel>> getVideos(int ch_ID, Context context, String savedToken) {
        //if the list is null

        if (videosList == null) {
            videosList = new MutableLiveData<List<VideoModel>>();
            //we will load it asynchronously from server in this method
            loadVideosList(ch_ID, context, savedToken);
        }


        //videosList = new MutableLiveData<List<VideoModel>>();
        //we will load it asynchronously from server in this method
       // loadVideosList(ch_ID,context, savedToken);

        //finally we will return the list
        return videosList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadVideosList(int ch_ID ,final Context context, String savedToken) {

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
        Call<List<VideoModel>> call = api.getVideos(ch_ID, savedToken);

        call.enqueue(new Callback<List<VideoModel>>() {
            @Override
            public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {

                try {
                    //finally we are setting the list to our MutableLiveData
                    videosList.setValue(response.body());
            }catch (Exception e){
              //  Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            }

            @Override
            public void onFailure(Call<List<VideoModel>> call, Throwable t) {
               // Toast.makeText(context,"Videos....  " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
