package com.example.routesapp.Model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.ServerRetrofit;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.View.Login.LoginScreen;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfficePlatesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<OfficePlatesList> officePlatesList;

    //we will call this method to get the data
    public LiveData<OfficePlatesList> getOfficePlatesList(Activity activity, String savedToken, int officeId, String include) {
        //if the list is null

        if (officePlatesList == null) {
            officePlatesList = new MutableLiveData<OfficePlatesList>();
            //we will load it asynchronously from server in this method
            loadOfficePlatesList(activity, savedToken, officeId, include);
        }




        //finally we will return the list
        return officePlatesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadOfficePlatesList(final Activity activity, String savedToken,  int officeId, String include) {

        try {
            ServerRetrofit serverRetrofit = new ServerRetrofit(activity);
            RoutesApi api = null;
            if (serverRetrofit != null){
                api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            }else {
                return;
            }

            //  RoutesApi api = serverRetrofit.getRetrofit().create(RoutesApi.class);
            Call<OfficePlatesList> call = api.getOfficePlatesList( savedToken, officeId, include);

            call.enqueue(new Callback<OfficePlatesList>() {
                @Override
                public void onResponse(Call<OfficePlatesList> call, Response<OfficePlatesList> response) {
                    if (response.isSuccessful()){
                        officePlatesList.setValue(response.body());
                    }else {
                        Toast.makeText(activity, "Error:  " + response.code(), Toast.LENGTH_SHORT).show();
                        if (response.code() == 401) {
                            activity.startActivity(new Intent(activity, LoginScreen.class));
                            activity.finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OfficePlatesList> call, Throwable t) {
                    Toast.makeText(activity, "Office Plates ViewModel Failure:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }





    }
}

