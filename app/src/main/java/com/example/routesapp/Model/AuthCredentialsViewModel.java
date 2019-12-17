package com.example.routesapp.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.AesBase64Wrapper;
import com.example.routesapp.Class.App;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.View.Login.TaxiInformationScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthCredentialsViewModel extends ViewModel {

    private App app;

    private RoutesApi api;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    private AesBase64Wrapper aesBase64Wrapper;

    AuthCredentials encryptAuthCredentials1;

    //this is the data that we will fetch asynchronously
    private List<AuthCredentialsError> authCredentialsErrorsList;
    private MutableLiveData<List<AuthCredentialsError>> authCredentialsErrors;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ProgressDialog dialog;

    //we will call this method to get the data
    public LiveData<List<AuthCredentialsError>> getToken(final AuthCredentials authCredentials, final Activity activity, ProgressDialog dialog) {

        app = (App) activity.getApplicationContext();


        this.dialog = dialog;

        sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        authCredentialsErrorsList = new ArrayList<AuthCredentialsError>();
        authCredentialsErrors = new MutableLiveData<List<AuthCredentialsError>>();

        String userName = authCredentials.getUsername();
        String password = authCredentials.getPassword();

        if (userName.isEmpty() || password.isEmpty() || password.length() < 6) {

            if (userName.isEmpty()) {

                authCredentialsErrorsList.add(new AuthCredentialsError(1, "User Name Required"));

            }else if (password.isEmpty()) {
                authCredentialsErrorsList.add(new AuthCredentialsError(2, "Password Required"));

            } else if (password.length() < 6) {
                authCredentialsErrorsList.add(new AuthCredentialsError(2, "Minimum Password is 6 digit"));

            }
            authCredentialsErrors.postValue(authCredentialsErrorsList);
            return authCredentialsErrors;
        } else {

            aesBase64Wrapper = new AesBase64Wrapper(activity);

             okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();



             retrofit = new Retrofit.Builder()
                    .baseUrl(RoutesApi.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(RoutesApi.class);


           loadAuthCredentialsErrors(new AuthCredentials(userName, password),activity);

             return authCredentialsErrors;

        }


    }



    //This method is using Retrofit to get the JSON data from URL
    private void loadAuthCredentialsErrors(final AuthCredentials authCredentials, final Activity activity) {


         encryptAuthCredentials1 = new AuthCredentials(aesBase64Wrapper.encryptAndEncode(authCredentials.getUsername()), aesBase64Wrapper.encryptAndEncode(authCredentials.getPassword()));


        Call<Token> call_success = api.loginUserSuccess(encryptAuthCredentials1);


        call_success.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call_success, Response<Token> response_success) {

                if (response_success.isSuccessful()){
                    if (response_success.body().getAccess_token() != null) {
                        try {
                            String username = authCredentials.getUsername().trim();
                            String password = authCredentials.getPassword().trim();
                            app.setTechnicalSupportUserName(username);
                            app.setTechnicalSupportPassword(password);

                           // Toast.makeText(activity, "token:   " +  response_success.body().getAccess_token(), Toast.LENGTH_SHORT).show();

                            //Save Tablet token into sharedPref. ...
                            editor.putString("tabToken", response_success.body().getAccess_token());
                            editor.apply();

                            dialog.dismiss();
                           // ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.login_fragment_container, new TabletDataFragment()).commit();

                         //   activity.startActivity(new Intent(activity, MainActivity.class));


                          //  Toast.makeText(activity, "MVVM ... userName:  " + app.getTechnicalSupportName() + "  ,Token:  " + response_success.body().getAccess_token(), Toast.LENGTH_SHORT).show();

                            app.setNewLogin(true);
                            activity.startActivity(new Intent(activity, TaxiInformationScreen.class));
                            activity.finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(e);
                        }
                    }
                }else {


                }

            }

            @Override
            public void onFailure(Call<Token> call_success, Throwable t) {
                loadError(encryptAuthCredentials1);
            }
        });

    }

    private void loadError(AuthCredentials authCredentials) {

        Call<List<AuthCredentialsError>> call_failed = api.loginUserFailed(authCredentials);
        call_failed.enqueue(new Callback<List<AuthCredentialsError>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<List<AuthCredentialsError>> call, Response<List<AuthCredentialsError>> response) {

                if (response.isSuccessful()){
                    try {
                        authCredentialsErrorsList.addAll(response.body());
                        authCredentialsErrors.postValue(authCredentialsErrorsList);
                    }catch (Exception e){
                        Crashlytics.logException(e);
                    }
                }


            }

            @Override
            public void onFailure(Call<List<AuthCredentialsError>> call, Throwable t) {

            }
        });
    }


}

