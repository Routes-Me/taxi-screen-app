package com.routesme.taxi_screen.Model;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.Server.Interface.RoutesApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficesListViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<TaxiOfficeList> officesList;

    //we will call this method to get the data
    public LiveData<TaxiOfficeList> getTaxiOfficesList(Activity activity, String include) {
        //if the list is null
        if (officesList == null) {
            officesList = new MutableLiveData<TaxiOfficeList>();
            //we will load it asynchronously from server in this method
            loadOfficesList(activity, include);
        }

        //finally we will return the list
        return officesList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadOfficesList(final Activity activity, String include) {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }
            Call<TaxiOfficeList> call = api.getTaxiOfficeList(include);

            call.enqueue(new Callback<TaxiOfficeList>() {
                @Override
                public void onResponse(Call<TaxiOfficeList> call, Response<TaxiOfficeList> response) {
                    if (response.isSuccessful() && response.body() != null){
                        officesList.setValue(response.body());
                    }
                }
                @Override
                public void onFailure(Call<TaxiOfficeList> call, Throwable t) {
                    activity.finish();
                }
            });

    }
}

