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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficePlatesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<OfficePlatesList> officePlatesList;

    //we will call this method to get the data
    public LiveData<OfficePlatesList> getOfficePlatesList(Activity activity, int officeId, String include) {
        //if the list is null

        if (officePlatesList == null) {
            officePlatesList = new MutableLiveData<OfficePlatesList>();
            //we will load it asynchronously from server in this method
            loadOfficePlatesList(activity, officeId, include);
        }




        //finally we will return the list
        return officePlatesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadOfficePlatesList(final Activity activity,  int officeId, String include) {

        try {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }

            //  RoutesApi api = serverRetrofit.getRetrofitInstance().create(RoutesApi.class);
            Call<OfficePlatesList> call = api.getOfficePlatesList( officeId, include);

            call.enqueue(new Callback<OfficePlatesList>() {
                @Override
                public void onResponse(Call<OfficePlatesList> call, Response<OfficePlatesList> response) {
                    if (response.isSuccessful() && response.body() != null){
                        officePlatesList.setValue(response.body());
                    }else {
                        Toast.makeText(activity, "OfficePlatesListViewModel . request is not Success! , with error code:   " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OfficePlatesList> call, Throwable t) {
                   // Toast.makeText(activity, "Office Plates ViewModel Failure:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (t instanceof IOException) {
                        Toast.makeText(activity, "OfficePlatesListViewModel. request onFailure ... this is an actual network failure!", Toast.LENGTH_SHORT).show();
                        // logging probably not necessary
                    }
                    else {
                        Toast.makeText(activity, "OfficePlatesListViewModel. request onFailure ... conversion issue!", Toast.LENGTH_SHORT).show();
                        // todo log to some central bug tracking service
                    }
                    activity.finish();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }





    }
}

