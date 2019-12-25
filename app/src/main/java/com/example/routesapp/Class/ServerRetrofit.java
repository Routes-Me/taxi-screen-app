package com.example.routesapp.Class;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import com.example.routesapp.BuildConfig;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.Model.RequestHeaders;
import com.example.routesapp.View.Login.LoginScreen;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerRetrofit {

    private Activity activity;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String  savedTabletToken = null;

    private int versionCode;
    private String versionName ;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit = null;

    private RequestHeaders requestHeaders;




    public ServerRetrofit(final Activity activity) {


        this.activity = activity;

        //sharedPreference Storage
        sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedTabletToken = sharedPreferences.getString("tabToken", null);

        if (savedTabletToken != null) {




          //  Toast.makeText(activity, "Authorization: " + requestHeaders.getAuthorization() + "  ,country_code: " + requestHeaders.getCountry_code() + " ,app_version: " + requestHeaders.getApp_version(), Toast.LENGTH_SHORT).show();

            requestHeaders = new RequestHeaders(getToken(),getCountryCode(), getVersionName());

                    okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request().newBuilder().addHeader("Authorization", requestHeaders.getAuthorization()).addHeader("country_code", requestHeaders.getCountry_code()).addHeader("app_version", requestHeaders.getApp_version()).build();
                            return chain.proceed(request);
                        }
                    })
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();



            retrofit = new Retrofit.Builder()
                    .baseUrl(Helper.getConfigValue(activity, "baseUrl"))
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

          //  Toast.makeText(activity, "Authorization: " + requestHeaders.getAuthorization() + "  ,country_code: " + requestHeaders.getCountry_code() + " ,app_version: " + requestHeaders.getApp_version(), Toast.LENGTH_SHORT).show();

        }else {
            activity.startActivity(new Intent(activity, LoginScreen.class));
            activity.finish();
        }


    }

    private String getCountryCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return activity.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            return activity.getResources().getConfiguration().locale.getCountry();
        }
    }

    private String getToken(){
            return "Bearer " + savedTabletToken;
    }

    private String getVersionName(){

        versionName = BuildConfig.VERSION_NAME;
        versionCode = BuildConfig.VERSION_CODE;
        return versionName+"."+versionCode;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
