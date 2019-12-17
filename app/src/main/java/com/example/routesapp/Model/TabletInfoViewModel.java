package com.example.routesapp.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.View.Login.LoginScreen;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if (tabletInfo == null) {
            tabletInfo = new MutableLiveData<TabletInfo>();
            //we will load it asynchronously from server in this method
            loadTabletInfo(activity, token, tabletCredentials);
        }

        //finally we will return the list

      //  operations.enableNextButton(register_btn,true);
      //  this.dialog.dismiss();

        return tabletInfo;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTabletInfo(final Activity activity, String token, TabletCredentials tabletCredentials) {

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
        Call<TabletInfo> call = api.tabletRegister(token,tabletCredentials);
        call.enqueue(new Callback<TabletInfo>() {
            @Override
            public void onResponse(Call<TabletInfo> call, Response<TabletInfo> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                   // operations.enableNextButton(register_btn,true);
                    if (response.body() != null) {

                        try {
                            tabletInfo.setValue(response.body());

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(e);
                        }
                    }

                }else {
                    operations.enableNextButton(register_btn,true);
                    dialog.dismiss();

                    Toast.makeText(activity, "Error Code:   " + response.code(), Toast.LENGTH_SHORT).show();

                    if (response.code() == 401) {
                        activity.startActivity(new Intent(activity, LoginScreen.class));
                        activity.finish();
                    }

                }
            }

            @Override
            public void onFailure(Call<TabletInfo> call, Throwable t) {
                Toast.makeText(activity, "Failure ... TabletInfo ViewModel:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
