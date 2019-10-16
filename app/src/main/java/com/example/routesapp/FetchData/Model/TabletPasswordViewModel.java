package com.example.routesapp.FetchData.Model;

import android.app.Activity;

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

public class TabletPasswordViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<TabletPasswordModel>> TabletPassword;

    //we will call this method to get the data
    public LiveData<List<TabletPasswordModel>> getTabPassword(String tablet_sNo, Activity activity) {
        //if the list is null
        if (TabletPassword == null) {
            TabletPassword = new MutableLiveData<List<TabletPasswordModel>>();
            //we will load it asynchronously from server in this method
            getTabletPassword(tablet_sNo,activity);
        }

        //finally we will return the list
        return TabletPassword;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void getTabletPassword(String tablet_sNo, final Activity activity) {

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
        Call<List<TabletPasswordModel>> call = api.getTabletPassword(tablet_sNo);


        call.enqueue(new Callback<List<TabletPasswordModel>>() {
            @Override
            public void onResponse(Call<List<TabletPasswordModel>> call, Response<List<TabletPasswordModel>> response) {

                //finally we are setting the list to our MutableLiveData
                try {
                TabletPassword.setValue(response.body());
            }catch (Exception e){
               // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onFailure(Call<List<TabletPasswordModel>> call, Throwable t) {
               // Toast.makeText(activity, "Tablet Data....  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                activity.recreate();

            }
        });
    }
}
