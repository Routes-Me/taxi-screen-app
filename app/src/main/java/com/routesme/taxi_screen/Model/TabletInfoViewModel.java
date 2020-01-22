package com.routesme.taxi_screen.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.Operations;
import com.routesme.taxi_screen.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.View.Login.LoginScreen;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabletInfoViewModel extends ViewModel {




    //this is the data that we will fetch asynchronously
    private MutableLiveData<TabletInfo> tabletInfo;

    private Operations operations;
    private ProgressDialog dialog;
    private Button register_btn;

    //we will call this method to get the data
    public LiveData<TabletInfo> getTabletInfo(Activity activity, String token, TabletCredentials tabletCredentials, ProgressDialog dialog, Button register_btn) {
        operations = new Operations(activity);
        this.dialog = dialog;
        this.register_btn = register_btn;
        //if the list is null
       // if (tabletInfo == null) {
            tabletInfo = new MutableLiveData<TabletInfo>();
            //we will load it asynchronously from server in this method
            loadTabletInfo(activity, tabletCredentials);
       // }

        //finally we will return the list

      //  operations.enableNextButton(register_btn,true);
      //  this.dialog.dismiss();

        return tabletInfo;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTabletInfo(final Activity activity, TabletCredentials tabletCredentials) {

        try {
            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
            }else {
                return;
            }

            //  RoutesApi api = serverRetrofit.getRetrofitInstance().create(RoutesApi.class);
            Call<TabletInfo> call = api.tabletRegister(tabletCredentials);
            call.enqueue(new Callback<TabletInfo>() {
                @Override
                public void onResponse(Call<TabletInfo> call, Response<TabletInfo> response) {
                    dialog.dismiss();
                    if (response.isSuccessful() && response.body() != null){

                        // operations.enableNextButton(register_btn,true);


                            try {
                                tabletInfo.setValue(response.body());

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Crashlytics.logException(e);
                            }


                    }else {
                        operations.enableNextButton(register_btn,true);


                        Toast.makeText(activity, "TabletInfoViewModel . request is not Success! , with error code:   " + response.code(), Toast.LENGTH_SHORT).show();


                    }
                }

                @Override
                public void onFailure(Call<TabletInfo> call, Throwable t) {
                    dialog.dismiss();
                   // Toast.makeText(activity, "Failure ... TabletInfo ViewModel:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (t instanceof IOException) {
                        Toast.makeText(activity, "TabletInfoViewModel. request onFailure ... this is an actual network failure!", Toast.LENGTH_SHORT).show();
                        // logging probably not necessary
                    }
                    else {
                        Toast.makeText(activity, "TabletInfoViewModel. request onFailure ... conversion issue!", Toast.LENGTH_SHORT).show();
                        // todo log to some central bug tracking service
                    }

                    //activity.recreate();
                }
            });
        }catch (Exception e){
            Crashlytics.logException(e);
        }




    }
}
