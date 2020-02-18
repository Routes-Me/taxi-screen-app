package com.routesme.taxi_screen.java.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.routesme.taxi_screen.java.Server.Class.AesBase64Wrapper;
import com.routesme.taxi_screen.java.Server.Class.RetrofitClientInstance;
import com.routesme.taxi_screen.java.Server.Interface.RoutesApi;
import com.routesme.taxi_screen.java.View.Login.TaxiInformationScreen;
import com.routesme.taxi_screen.kotlin.Class.App;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentialsError;
import com.routesme.taxi_screen.kotlin.Model.Token;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthCredentialsViewModel extends ViewModel {

    private App app;

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
            getToken(authCredentials,activity); // TODO: pass the same object authCredentials why to create a new one
             return authCredentialsErrors;
        }

    }

    //This method is using Retrofit to get the JSON data from URL
    private void getToken(final AuthCredentials authCredentials, final Activity activity) {

            RetrofitClientInstance retrofitClientInstance = new RetrofitClientInstance(activity);
            RoutesApi api = null;
            if (retrofitClientInstance != null){
                api = retrofitClientInstance.getRetrofitInstance(false).create(RoutesApi.class);
            }else {
                return;
            }
            encryptAuthCredentials1 = new AuthCredentials(aesBase64Wrapper.encryptAndEncode(authCredentials.getUsername()), aesBase64Wrapper.encryptAndEncode(authCredentials.getPassword()));
            Call<JsonElement> call = api.loginAuth(encryptAuthCredentials1);
        dialog.show();
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    dialog.dismiss();

                    if (response.isSuccessful() && response.body() != null){
                        JsonElement questions = response.body();
                        if (questions.isJsonArray()){
                            List<AuthCredentialsError> authErrors = new Gson().fromJson(((JsonArray)questions), new TypeToken<List<AuthCredentialsError>>(){}.getType());
                            if (!authErrors.isEmpty()){
                                authCredentialsErrors.postValue(authErrors);
                            }
                        }else if (questions.isJsonObject()){
                            Token token = new Gson().fromJson(questions.getAsJsonObject(), Token.class);

                            if (token.getAccess_token() != null) {
                                    saveDataIntoSharedPreference(authCredentials, token.getAccess_token());
                                    OpenTaxiInformationScreen(activity);
                            }

                        }else {
                        }
                    }
                }
                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    dialog.dismiss();
                }
            });
    }

    private void saveDataIntoSharedPreference(AuthCredentials authCredentials, String access_token) {
        String username = authCredentials.getUsername().trim();
        String password = authCredentials.getPassword().trim();
        //app.setTechnicalSupportUserName(username);
       // app.setTechnicalSupportPassword(password);
        editor.putString("tabToken", access_token);
        editor.apply();
    }

    private void OpenTaxiInformationScreen(Activity activity) {
        app.setNewLogin(true);
        activity.startActivity(new Intent(activity, TaxiInformationScreen.class));
        activity.finish();
    }
}