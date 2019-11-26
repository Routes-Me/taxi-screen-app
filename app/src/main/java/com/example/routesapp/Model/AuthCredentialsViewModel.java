package com.example.routesapp.Model;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.Interface.RoutesApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthCredentialsViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<String> token;

    //we will call this method to get the data
    public LiveData<String>  getToken(AuthCredentials authCredentials, Context context) {
        //if the list is null
      /*
        if (bannersList == null) {
            bannersList = new MutableLiveData<List<BannerModel>>();
            //we will load it asynchronously from server in this method
            loadBannersList(ch_ID,context);
        }
*/
       // bannersList = new MutableLiveData<List<BannerModel>>();
        //we will load it asynchronously from server in this method

        token = new MutableLiveData<String>();

        fetchTabletToken(authCredentials,context);

        //finally we will return the list
        return token;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void fetchTabletToken(AuthCredentials authCredentials, final Context context) {

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
        Call<ResponseBody> call = api.loginUser(authCredentials);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                token.setValue(String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               // Toast.makeText(context, "Error:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
                token.setValue(String.valueOf(t.getMessage()));
            }
        });
    }
}

