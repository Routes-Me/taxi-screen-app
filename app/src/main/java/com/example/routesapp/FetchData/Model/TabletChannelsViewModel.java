package com.example.routesapp.FetchData.Model;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.FetchData.Interface.RoutesApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TabletChannelsViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<TabletChannelModel>> TabletChannelList;

    //we will call this method to get the data
    public LiveData<List<TabletChannelModel>> getTabletChannel(String tablet_sNo, Activity activity) {
        //if the list is null
        if (TabletChannelList == null) {
            TabletChannelList = new MutableLiveData<List<TabletChannelModel>>();
            //we will load it asynchronously from server in this method
            loadTabletChannelList(tablet_sNo,activity);
        }

        //finally we will return the list
        return TabletChannelList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTabletChannelList(String tablet_sNo,final Activity activity) {

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
        Call<List<TabletChannelModel>> call = api.getTabletData(tablet_sNo);


        call.enqueue(new Callback<List<TabletChannelModel>>() {
            @Override
            public void onResponse(Call<List<TabletChannelModel>> call, Response<List<TabletChannelModel>> response) {

                //finally we are setting the list to our MutableLiveData
                try {
                TabletChannelList.setValue(response.body());
            }catch (Exception e){
               // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onFailure(Call<List<TabletChannelModel>> call, Throwable t) {
               // Toast.makeText(activity, "Tablet Data....  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                activity.recreate();

            }
        });
    }
}
