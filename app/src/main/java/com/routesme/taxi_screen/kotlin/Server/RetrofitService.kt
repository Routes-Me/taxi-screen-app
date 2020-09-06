package com.routesme.taxi_screen.kotlin.Server

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.taxi_screen.kotlin.Class.AesBase64Wrapper
import com.routesme.taxi_screen.kotlin.Class.Helper
import com.routesme.taxi_screen.kotlin.Model.*
import com.routesme.taxiscreen.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class RetrofitService() {

    val contentResponse: MutableLiveData<ContentResponse> = MutableLiveData()
    val videoList: MutableLiveData<List<VideoModel>> = MutableLiveData()
    val bannerList: MutableLiveData<List<BannerModel>> = MutableLiveData()
    val signInResponse:MutableLiveData<JsonElement> = MutableLiveData()
    val apiResponse = MutableLiveData<ApiResponse>()

    companion object Factory {
        fun create(context: Context): RoutesApi {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Helper.getConfigValue("baseUrl", R.raw.config))
                    .client(ApiWorker(context).client)
                    .addConverterFactory(ApiWorker(context).gsonConverter)
                    .build()
                    .create(RoutesApi::class.java)
        }
    }

    fun content(context: Context): MutableLiveData<ContentResponse> {
        val retrofitCall = create(context).getContent()
        retrofitCall.enqueue(object : Callback<ContentResponse> {
            override fun onFailure(call: Call<ContentResponse>, t: Throwable) {}
            override fun onResponse(call: Call<ContentResponse>, response: Response<ContentResponse>) {
                if (response.isSuccessful && response.body() != null) contentResponse.value = response.body()
            }
        })
        return contentResponse
    }

    fun loadVideoList(chId: Int, context: Context): MutableLiveData<List<VideoModel>>? {
        val retrofitCall = create(context).getVideos(chId)
        retrofitCall.enqueue(object : Callback<List<VideoModel>> {
            override fun onFailure(call: Call<List<VideoModel>>, t: Throwable) {}
            override fun onResponse(call: Call<List<VideoModel>>, response: Response<List<VideoModel>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) videoList.value = response.body()
            }
        }
        )
        return videoList
    }

    fun loadBannerList(chId: Int, context: Context): MutableLiveData<List<BannerModel>>? {
        val retrofitCall = create(context).getBanners(chId)
        retrofitCall.enqueue(object : Callback<List<BannerModel>> {
            override fun onFailure(call: Call<List<BannerModel>>, t: Throwable) {}
            override fun onResponse(call: Call<List<BannerModel>>, response: Response<List<BannerModel>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) bannerList.value = response.body()
            }
        }
        )
        return bannerList
    }

    fun signInResponse(signInCredentials: SignInCredentials, context: Context): MutableLiveData<ApiResponse> {
        val encryptedPassword = encrypt(signInCredentials.Password)
        Log.d("Encryption", encryptedPassword)

        val encryptedCredentials = SignInCredentials(signInCredentials.Username, encryptedPassword)
        val call = create(context).signIn(encryptedCredentials)
        call.enqueue(object : Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val signInSuccessResponse = Gson().fromJson<SignInSuccessResponse>(response.body(), SignInSuccessResponse::class.java)
                    apiResponse.value = ApiResponse(signInSuccessResponse)
                }else if (response.code() == 400 && response.body() != null){
                    val badRequestResponse = Gson().fromJson<BadRequestResponse>(response.body(), BadRequestResponse::class.java)
                    apiResponse.value = ApiResponse(badRequestResponse)
                } else{
                    val errorResponse = ErrorResponse(response.code(), response.message())
                    apiResponse.value = ApiResponse(errorResponse)
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                apiResponse.value = ApiResponse(throwable)
            }
        })
        return apiResponse
    }

    private fun encrypt(str: String) = AesBase64Wrapper().getEncryptedString(str)
}