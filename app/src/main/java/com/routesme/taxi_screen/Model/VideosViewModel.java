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

public class VideosViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<VideoModel>> videosList;

    //we will call this method to get the data
    public LiveData<List<VideoModel>> getVideos(int ch_ID, Activity activity, String savedToken) {
        //if the list is null

      //  if (videosList == null) {
            videosList = new MutableLiveData<List<VideoModel>>();
            //we will load it asynchronously from server in this method
            loadVideosList(ch_ID, activity, savedToken);
      //  }


        //videosList = new MutableLiveData<List<VideoModel>>();
        //we will load it asynchronously from server in this method
       // loadVideosList(ch_ID,context, savedToken);

        //finally we will return the list
        return videosList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadVideosList(int ch_ID ,final Activity activity, String savedToken) {

        try {

            ServerRetrofit serverRetrofit = new ServerRetrofit(activity);
            RoutesApi api = null;
            if (serverRetrofit != null){
                api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            }else {
                return;
            }

            // RoutesApi api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            Call<List<VideoModel>> call = api.getVideos(ch_ID);

            call.enqueue(new Callback<List<VideoModel>>() {
                @Override
                public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {

                    if (response.isSuccessful()){
                        //finally we are setting the list to our MutableLiveData
                        try {
                            videosList.setValue(response.body());
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
                public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                    // Toast.makeText(context,"Videos....  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, "Error occur!", Toast.LENGTH_SHORT).show();
                    //activity.recreate();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }
}

