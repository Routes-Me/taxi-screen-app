package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.kotlin.Model.Institutions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<Institutions> Institutions;

    //we will call this method to get the data
    public LiveData<Institutions> getInstitutions(Activity activity, int offset, int limit) {
        //if the list is null
        if (Institutions == null) {
            Institutions = new MutableLiveData<Institutions>();
            //we will load it asynchronously from server in this method
            loadInstitutions(activity, offset, limit);
        }

        //finally we will return the list
        return Institutions;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadInstitutions(final Activity activity, int offset, int limit) {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }
            Call<Institutions> call = api.getInstitutions(offset,limit);

            call.enqueue(new Callback<Institutions>() {
                @Override
                public void onResponse(Call<Institutions> call, Response<Institutions> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Institutions.setValue(response.body());
                    }
                }
                @Override
                public void onFailure(Call<Institutions> call, Throwable t) {
                    activity.finish();
                }
            });

    }
}

