package com.routesme.taxi_screen.kotlin.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials
import com.routesme.taxi_screen.kotlin.Server.RetrofitService

class RoutesViewModel() : ViewModel() {

    private val mService = RetrofitService()

    fun getVideoList(ch_ID: Int, context: Context) = mService.loadVideoList(ch_ID, context)
    fun getBannerList(ch_ID: Int, context: Context) = mService.loadBannerList(ch_ID, context)
    fun getToken(authCredentials: AuthCredentials, context: Context) = mService.getToken(context, authCredentials)

}