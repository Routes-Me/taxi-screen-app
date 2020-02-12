package com.routesme.taxi_screen.kotlin.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxi_screen.kotlin.Server.RetrofitService

class ViewModel() : ViewModel() {

    private val mService  =  RetrofitService()

    fun getVideoList(ch_ID:Int,context: Context) : MutableLiveData<List<VideoModel>>? {
        return mService.loadVideoList(ch_ID,context)
    }

    fun getBannerList(ch_ID:Int,context: Context) : MutableLiveData<List<BannerModel>>? {
        return mService.loadBannerList(ch_ID,context)
    }
}