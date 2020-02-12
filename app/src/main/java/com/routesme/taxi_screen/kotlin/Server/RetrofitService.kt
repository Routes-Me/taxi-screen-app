package com.routesme.taxi_screen.kotlin.Server

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.routesme.taxi_screen.kotlin.Model.Authorization
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxi_screen.kotlin.SplashScreen.SplashScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitService() {

    val VideoList: MutableLiveData<List<VideoModel>> = MutableLiveData()

    companion object Factory {
        fun create(context: Context): RoutesApi {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Constants.API_BASE_PATH)
                    .client(ApiWorker(context).client)
                    .addConverterFactory(ApiWorker(context).gsonConverter)
                    .build()
            return retrofit.create(RoutesApi::class.java)

        }
    }

    fun loadVideoList(chId: Int,context: Context): MutableLiveData<List<VideoModel>>? {
        val retrofitCall = create(context).getVideos(chId)
        retrofitCall?.enqueue(object : Callback<List<VideoModel?>?> {
            override fun onFailure(call: Call<List<VideoModel?>?>, t: Throwable) {
            Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<List<VideoModel?>?>, response: Response<List<VideoModel?>?>) {
                Toast.makeText(context,"${response.code()}",Toast.LENGTH_LONG).show()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    VideoList.value = response.body() as List<VideoModel>?
                }
            }
        }
        )
        return VideoList
    }



}