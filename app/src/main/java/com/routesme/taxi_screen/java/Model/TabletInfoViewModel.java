package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Button;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.kotlin.Class.Operations;
import com.routesme.taxi_screen.kotlin.Model.RegistrationCredentials;
import com.routesme.taxi_screen.kotlin.Model.RegistrationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabletInfoViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<RegistrationResponse> response;

    private Operations operations;
    private AlertDialog dialog;
    private Button register_btn;

    //we will call this method to get the data
    public LiveData<RegistrationResponse> getTabletInfo(Activity activity, String token, RegistrationCredentials registrationCredentials, AlertDialog dialog, Button register_btn) {
        operations = new Operations();
        this.dialog = dialog;
        this.register_btn = register_btn;

        response = new MutableLiveData<RegistrationResponse>();
        //we will load it asynchronously from server in this method
        loadTabletInfo(activity, registrationCredentials);
        return response;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTabletInfo(final Activity activity, RegistrationCredentials registrationCredentials) {
        RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
        RoutesApi api = null;
        if (retrofitClientInstance != null) {
            api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
        } else {
            return;
        }
        Call<RegistrationResponse> call = api.postDevice(registrationCredentials);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    TabletInfoViewModel.this.response.setValue(response.body());
                } else {
                    operations.enableNextButton(register_btn, true);
                }
            }
            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}
