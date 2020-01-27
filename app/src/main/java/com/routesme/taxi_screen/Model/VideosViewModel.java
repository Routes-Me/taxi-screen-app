package com.routesme.taxi_screen.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.Server.Interface.RoutesApi;
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

        if (videosList == null) {
            videosList = new MutableLiveData<List<VideoModel>>();
            //we will load it asynchronously from server in this method
            loadVideosList(ch_ID, activity);
        }
        //finally we will return the list
        return videosList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadVideosList(int ch_ID ,final Activity activity) {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }
            Call<List<VideoModel>> call = api.getVideos(ch_ID);
            call.enqueue(new Callback<List<VideoModel>>() {
                @Override
                public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {
                    if (response.isSuccessful() && response.body() != null){
                        //finally we are setting the list to our MutableLiveData
                            videosList.setValue(response.body());
                    }
                }
                @Override
                public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                }
            });
    }
}

