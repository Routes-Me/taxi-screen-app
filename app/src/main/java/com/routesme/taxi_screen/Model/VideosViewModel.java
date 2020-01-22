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

public class VideosViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<VideoModel>> videosList;

    //we will call this method to get the data
    public LiveData<List<VideoModel>> getVideos(int ch_ID, Activity activity) {
        //if the list is null

      //  if (videosList == null) {
            videosList = new MutableLiveData<List<VideoModel>>();
            //we will load it asynchronously from server in this method
            loadVideosList(ch_ID, activity);
      //  }


        //videosList = new MutableLiveData<List<VideoModel>>();
        //we will load it asynchronously from server in this method
       // loadVideosList(ch_ID,context, savedToken);

        //finally we will return the list
        return videosList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadVideosList(int ch_ID ,final Activity activity) {

        try {

            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }

            // RoutesApi api = serverRetrofit.getRetrofitInstance().create(RoutesApi.class);
            Call<List<VideoModel>> call = api.getVideos(ch_ID);

            call.enqueue(new Callback<List<VideoModel>>() {
                @Override
                public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {

                    if (response.isSuccessful() && response.body() != null){
                        //finally we are setting the list to our MutableLiveData
                        try {
                            videosList.setValue(response.body());
                        }catch (Exception e){
                            Crashlytics.logException(e);
                            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(activity, "VideoViewModel . request is not Success! , with error code:   " + response.code(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                    // Toast.makeText(context,"Videos....  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (t instanceof IOException) {
                        Toast.makeText(activity, "VideoViewModel. request onFailure ... this is an actual network failure!", Toast.LENGTH_SHORT).show();
                        // logging probably not necessary
                    }
                    else {
                        Toast.makeText(activity, "VideoViewModel. request onFailure ... conversion issue!", Toast.LENGTH_SHORT).show();
                        // todo log to some central bug tracking service
                    }
                    //activity.recreate();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }
}

