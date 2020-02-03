package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficePlatesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<OfficePlatesList> officePlatesList;

    //we will call this method to get the data
    public LiveData<OfficePlatesList> getOfficePlatesList(Activity activity, int officeId) {
        //if the list is null

        if (officePlatesList == null) {
            officePlatesList = new MutableLiveData<OfficePlatesList>();
            //we will load it asynchronously from server in this method
            loadOfficePlatesList(activity, officeId);
        }
        //finally we will return the list
        return officePlatesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadOfficePlatesList(final Activity activity,  int officeId) {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }
            Call<OfficePlatesList> call = api.getOfficePlatesList( officeId);
            call.enqueue(new Callback<OfficePlatesList>() {
                @Override
                public void onResponse(Call<OfficePlatesList> call, Response<OfficePlatesList> response) {
                    if (response.isSuccessful() && response.body() != null){
                        officePlatesList.setValue(response.body());
                    }
                }
                @Override
                public void onFailure(Call<OfficePlatesList> call, Throwable t) {
                    activity.finish();
                }
            });
    }
}

