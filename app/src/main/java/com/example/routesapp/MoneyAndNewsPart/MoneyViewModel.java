package com.example.routesapp.MoneyAndNewsPart;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.routesapp.R;
import com.example.routesapp.View.Activity.MainActivity;
import com.example.routesapp.api.ApiClient;
import com.example.routesapp.api.ApiServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoneyViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private List<Money> monies;

    //we will call this method to get the data
    public LiveData<List<MoneyResponse>> getHeroes() {
        //if the list is null
        if (monies == null) {
            monies = (List<Money>) new MutableLiveData<List<MoneyResponse>>();
            //we will load it asynchronously from server in this method
            scrollingTextView_Money();
        }

        //finally we will return the list
        return (LiveData<List<MoneyResponse>>) monies;
    }








    private void scrollingTextView_Money() {



        try {

            ApiClient Client = new ApiClient();
            final ApiServices apiService = Client.getClient_Money().create(ApiServices.class);
            try {
                // parameters.put("login", SearchText);
                Call<MoneyResponse> call = apiService.getItems_Money();
                //  Call<ItemResponse> call = apiService.getItems();
                call.enqueue(new Callback<MoneyResponse>() {
                    @Override
                    public void onResponse(Call<MoneyResponse> call, Response<MoneyResponse> response) {
                        try {
                            monies = response.body().getMonies();
                        } catch (Exception e) {
                        }




                    }

                    @Override
                    public void onFailure(Call<MoneyResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                       // Toast.makeText(MainActivity.this, "Error Fetching Data !", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        }

    }


}



