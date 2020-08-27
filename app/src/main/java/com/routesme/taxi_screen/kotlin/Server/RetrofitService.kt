package com.routesme.taxi_screen.kotlin.Server

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.taxi_screen.kotlin.Class.AesBase64Wrapper
import com.routesme.taxi_screen.kotlin.Class.Helper
import com.routesme.taxi_screen.kotlin.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitService() {

    val contentResponse: MutableLiveData<ContentResponse> = MutableLiveData()
    val videoList: MutableLiveData<List<VideoModel>> = MutableLiveData()
    val bannerList: MutableLiveData<List<BannerModel>> = MutableLiveData()
    val signInResponse:MutableLiveData<JsonElement> = MutableLiveData()

    companion object Factory {
        fun create(context: Context): RoutesApi {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Helper.getConfigValue("baseUrl"))
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

    fun signInResponse(signInCredentials: SignInCredentials, dialog: AlertDialog, context: Context): MutableLiveData<JsonElement> {
        val encryptedCredentials = SignInCredentials(signInCredentials.Username, encrypt(signInCredentials.Password))
        val call = create(context).signIn(encryptedCredentials)
        call.enqueue(object : Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {dialog.dismiss()}
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                dialog.dismiss()
                if (response.isSuccessful && response.body() != null) signInResponse.value = response.body() as JsonElement
            }
        })
        return signInResponse
    }

    private fun encrypt(str: String) = AesBase64Wrapper().getEncryptedString(str)
}