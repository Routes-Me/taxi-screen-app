package com.routesme.taxi_screen.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Button;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.routesme.taxi_screen.Class.Operations;
import com.routesme.taxi_screen.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.Server.Interface.RoutesApi;
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

        tabletInfo = new MutableLiveData<TabletInfo>();
        //we will load it asynchronously from server in this method
        loadTabletInfo(activity, tabletCredentials);
        return tabletInfo;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTabletInfo(final Activity activity, TabletCredentials tabletCredentials) {
        RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
        RoutesApi api = null;
        if (retrofitClientInstance != null) {
            api = retrofitClientInstance.getRetrofitInstance(true).create(RoutesApi.class);
        } else {
            return;
        }
        Call<TabletInfo> call = api.tabletRegister(tabletCredentials);
        call.enqueue(new Callback<TabletInfo>() {
            @Override
            public void onResponse(Call<TabletInfo> call, Response<TabletInfo> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    tabletInfo.setValue(response.body());
                } else {
                    operations.enableNextButton(register_btn, true);
                }
            }
            @Override
            public void onFailure(Call<TabletInfo> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}
