package com.routesme.taxi_screen.java.Server.Class;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.routesme.taxi_screen.java.Class.Helper;
import com.routesme.taxi_screen.java.Model.RequestHeaders;
import com.routesme.taxi_screen.kotlin.Model.Authorization;
import com.routesme.taxi_screen.kotlin.View.SplashScreen;
import com.routesme.taxiscreen.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private Activity activity;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedTabletToken = null;

    private int versionCode;
    private String versionName;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit = null;

    private RequestHeaders requestHeaders;


    public RetrofitClientInstance(final Activity activity) {
        this.activity = activity;
        //sharedPreference Storage
        sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
    }

    private OkHttpClient getOkHttpClient(boolean hasToken) {

        if (hasToken) {

            savedTabletToken = sharedPreferences.getString("tabToken", null);
            if (savedTabletToken != null) {
                requestHeaders = new RequestHeaders(getToken(), getCountryCode(), getVersionName());
                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder().addHeader("Authorization", requestHeaders.getAuthorization()).addHeader("country_code", requestHeaders.getCountry_code()).addHeader("app_version", requestHeaders.getApp_version()).build();
                                return chain.proceed(request);
                            }
                        })
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                okhttp3.Response response = chain.proceed(request);

                                // todo deal with the issues the way you need to
                                if (response.code() == 401) {
                                    Authorization authorization = new Authorization(false, response.code());
                                    Intent SplashScreenIntent = new Intent(activity, SplashScreen.class);
                                    SplashScreenIntent.putExtra("authorization", authorization);
                                    activity.startActivity(SplashScreenIntent);
                                    activity.finish();

                                    return response;
                                }

                                return response;
                            }
                        })

                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build();
            } else {
                activity.startActivity(new Intent(activity, SplashScreen.class));
                activity.finish();
            }
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    private String getCountryCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return activity.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            return activity.getResources().getConfiguration().locale.getCountry();
        }
    }

    private String getToken() {
        return "Bearer " + savedTabletToken;
    }

    private String getVersionName() {
        versionName = BuildConfig.VERSION_NAME;
        versionCode = BuildConfig.VERSION_CODE;
        return versionName + "." + versionCode;
    }

    public Retrofit getRetrofitInstance(boolean hasToken) {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.getConfigValue(activity, "baseUrl"))
                .client(getOkHttpClient(hasToken))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
