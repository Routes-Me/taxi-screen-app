package com.example.routesapp.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.Class.AesBase64Wrapper;
import com.example.routesapp.Interface.RoutesApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthCredentialsViewModel extends ViewModel {

    private RoutesApi api;

    private AesBase64Wrapper aesBase64Wrapper;

    //this is the data that we will fetch asynchronously
    private List<AuthCredentialsError> authCredentialsErrorsList;
    private MutableLiveData<List<AuthCredentialsError>> authCredentialsErrors;



    //we will call this method to get the data
    public LiveData<List<AuthCredentialsError>> getToken(AuthCredentials authCredentials, Context context) {

        authCredentialsErrorsList = new ArrayList<AuthCredentialsError>();
        authCredentialsErrors = new MutableLiveData<List<AuthCredentialsError>>();

        String userName = authCredentials.getUsername();
        String password = authCredentials.getPassword();

        if (userName.isEmpty() || password.isEmpty() || password.length() < 8) {

            if (userName.isEmpty()) {

                authCredentialsErrorsList.add(new AuthCredentialsError(1, "* userName Required"));
                // showErrorMessage(userName_et, userName_error_tv,"* userName Required",true);
                //  return;
            }else if (password.isEmpty()) {
                authCredentialsErrorsList.add(new AuthCredentialsError(2, "* Password Required"));
                // showErrorMessage(password_et, password_error_tv,"* Password Required",true);
                //  return;
            } else if (password.length() < 8) {
                authCredentialsErrorsList.add(new AuthCredentialsError(2, "* Minimum Password is 8 digit"));
                // showErrorMessage(password_et, password_error_tv,"* Minimum Password is 8 digit",true);
                // return;
            }
            authCredentialsErrors.postValue(authCredentialsErrorsList);
            return authCredentialsErrors;
        } else {

            aesBase64Wrapper = new AesBase64Wrapper((Activity) context);



            loadAuthCredentialsErrors(new AuthCredentials(aesBase64Wrapper.encryptAndEncode(userName), aesBase64Wrapper.encryptAndEncode(password)),context);


        //  authCredentialsErrors.postValue(authCredentialsErrorsList);
          //  Toast.makeText(context, "userName:  " + userName, Toast.LENGTH_SHORT).show();
             return authCredentialsErrors;

        }


    }



    //This method is using Retrofit to get the JSON data from URL
    private void loadAuthCredentialsErrors(final AuthCredentials authCredentials, final Context context) {

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

         api = retrofit.create(RoutesApi.class);


        Call<Token> call_success = api.loginUserSuccess(authCredentials);


        call_success.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call_success, Response<Token> response_success) {

                //  String token = null;


                //  Token token = new Gson().fromJson(response.toString(),Token.class);

                if (response_success.isSuccessful()){
                    if (response_success.body().getAccess_token() != null) {
                        try {
                            Toast.makeText(context, "token:   " +  response_success.body().getAccess_token(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    //Toast.makeText(context, "Error is shown", Toast.LENGTH_SHORT).show();

                   Call<List<AuthCredentialsError>> call_failed = api.loginUserFailed(authCredentials);
                   call_failed.enqueue(new Callback<List<AuthCredentialsError>>() {
                       @SuppressLint("NewApi")
                       @Override
                       public void onResponse(Call<List<AuthCredentialsError>> call, Response<List<AuthCredentialsError>> response) {
                          // Toast.makeText(context, "m:  " + response.body().get(0).getErrorMasseg(), Toast.LENGTH_SHORT).show();

                           
                           authCredentialsErrors.setValue(response.body());
                       }

                       @Override
                       public void onFailure(Call<List<AuthCredentialsError>> call, Throwable t) {

                       }
                   });
                   /*
                   call_failed.enqueue(new Callback<List<AuthCredentialsError>>() {
                       @Override
                       public void onResponse(Call<List<AuthCredentialsError>> call_failed, Response<List<AuthCredentialsError>> response_failed) {

                           try {
                             //  Toast.makeText(context, "no error  " + response_failed.body(), Toast.LENGTH_SHORT).show();
                               authCredentialsErrors.setValue(response_failed.body());

                               //finally we are setting the list to our MutableLiveData
                              // authCredentialsErrorsList.addAll(response_failed.body());
                               //authCredentialsErrors.postValue(authCredentialsErrorsList);
                              // Toast.makeText(context, "his:   "  + response_failed.body().get(0).getErrorMasseg(), Toast.LENGTH_SHORT).show();
                             //  authCredentialsErrors.setValue(response_failed.body());

                           }catch (Exception e){
                                  Toast.makeText(context,"has error with get errors:   " +  e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }

                       @Override
                       public void onFailure(Call<List<AuthCredentialsError>> call, Throwable t) {
                           Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
*/
                }

            }

            @Override
            public void onFailure(Call<Token> call_success, Throwable t) {
                Toast.makeText(context, "error:   " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
/*
        RoutesApi api = retrofit.create(RoutesApi.class);
        Call<List<CurrenciesModel>> call = api.getCurrencies(ch_ID);


        call.enqueue(new Callback<List<CurrenciesModel>>() {
            @Override
            public void onResponse(Call<List<CurrenciesModel>> call, Response<List<CurrenciesModel>> response) {

                try {
                    //finally we are setting the list to our MutableLiveData
                    currenciesList.setValue(response.body());
                }catch (Exception e){
                    //   Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<CurrenciesModel>> call, Throwable t) {
                // Toast.makeText(context, "Currencies....  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }


}

