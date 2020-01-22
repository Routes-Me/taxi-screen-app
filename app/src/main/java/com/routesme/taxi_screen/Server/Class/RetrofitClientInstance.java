package com.routesme.taxi_screen.Server.Class;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.routesme.taxi_screen.Class.Helper;
import com.routesme.taxi_screen.Model.RequestHeaders;
import com.routesme.taxi_screen.View.Login.LoginScreen;
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
    private String  savedTabletToken = null;

    private int versionCode;
    private String versionName ;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit = null;

    private RequestHeaders requestHeaders;




    public RetrofitClientInstance(final Activity activity) {


        this.activity = activity;

        //sharedPreference Storage
        sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);


    }

    private OkHttpClient getOkHttpClient(boolean hasToken){

        //requestHeaders = new RequestHeaders(getToken(),getCountryCode(), getVersionName());
        if (hasToken){

            savedTabletToken = sharedPreferences.getString("tabToken", null);
            if (savedTabletToken != null) {
                requestHeaders = new RequestHeaders(getToken(),getCountryCode(), getVersionName());
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
                                    activity.startActivity(new Intent(activity, LoginScreen.class));
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
            }else {
            activity.startActivity(new Intent(activity, LoginScreen.class));
            activity.finish();
        }
        }else {
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

    private String getToken(){
        return "Bearer " + savedTabletToken;
            //return "Bearer " + "BJikwjcJvaaUF8obe_XLDjZeKqtXY7fD1_HuNHlbWhxX8357uTfZKB-L5ADQk1683jzwulZoJH1OyY7Z0E3DvUg8Dg-mW8C6NVn7CSRE4qZ8SZVC_bQdM9aquF-HHFACJe-C0f8JG54Fh8FMRMAcMyyxWWYdid2NUDNffp7ALnv3NakqdhAqT6sKXah1CA3nQV4ASlPIZhFrHIqVDoPaxlNBVdx9yXtbSoU3Zp0O8JrHuKgnOHEJ0XKsjl4pUivjS7mRoOU5S3dLH5-_MxHtPeJ5QOK_VQJ_ioFCVjlzVfzHjGx_u1kRGuYbh9ow83fEAyDwwHnb_T_A-HTOb9rEuXgKuUxI6HUDRTaB1BzJCKD2Q-Suvwpgo_H-hS0IadQ_6fUp55jlf_5cNN3ZqJap7gTiZTWbNUyHEAzBtbrRU2JXUvmkfSab3zdKlpxSyg4KKYGWXZaEWn6FdNaSx-RcjboJ1g1nuZ1p0HDgoNk28Wccc";
    }

    private String getVersionName(){

        versionName = BuildConfig.VERSION_NAME;
        versionCode = BuildConfig.VERSION_CODE;
        return versionName+"."+versionCode;
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
