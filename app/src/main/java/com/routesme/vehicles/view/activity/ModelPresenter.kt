package com.routesme.vehicles.view.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.routesme.vehicles.App
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.AdvertisementsHelper
import com.routesme.vehicles.helper.SharedPreferencesHelper

class ModelPresenter : AppCompatActivity() {
    private var bundle: Bundle? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isCacheCleared:Boolean?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        /*isCacheCleared  = sharedPreferences?.getBoolean(SharedPreferencesHelper.isCacheClear,false)
        if(!isCacheCleared!!){
            editor.putBoolean(SharedPreferencesHelper.isCacheClear, AdvertisementsHelper.instance.deleteCache()).apply()
        }*/
        startActivity()
    }

    private fun startActivity() {
        val isRegistered: Boolean = !App.instance.account.vehicle.deviceId.isNullOrEmpty()
        if (isRegistered) {
            openActivity(HomeActivity())
        } else {
            openActivity(LoginActivity())
        }
    }

    private fun openActivity(activity: Activity) {
        startActivity(Intent(this, activity::class.java))
        finish()
    }

}