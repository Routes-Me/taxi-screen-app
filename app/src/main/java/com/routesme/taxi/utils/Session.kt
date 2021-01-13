package com.routesme.taxi.utils

import android.app.Activity
import android.content.Intent
import com.routesme.taxi.BuildConfig
import com.routesme.taxi.MVVM.View.activity.LoginActivity
import com.routesme.taxi.helper.SharedPreferencesHelper

class Session(val activity: Activity){
    private val sharedPreferences = activity.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)

    companion object {
        private const val defaultValue = "- -"
    }

    fun plateNumber() = sharedPreferences.getString(SharedPreferencesHelper.vehicle_plate_number, defaultValue)
    fun institutionName() = sharedPreferences.getString(SharedPreferencesHelper.institution_name, defaultValue)
    fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    fun simSerialNumber() = sharedPreferences.getString(SharedPreferencesHelper.sim_serial_number, defaultValue)
    fun deviceSerialNumber() = sharedPreferences.getString(SharedPreferencesHelper.device_serial_number, defaultValue)
    fun technicalUserName() = sharedPreferences.getString(SharedPreferencesHelper.username, defaultValue)
    fun registrationDate() = sharedPreferences.getString(SharedPreferencesHelper.registration_date, defaultValue)
    fun vehicleId() = sharedPreferences.getString(SharedPreferencesHelper.vehicle_id, defaultValue)
    fun deviceId() = sharedPreferences.getString(SharedPreferencesHelper.device_id, defaultValue)
    fun fromDate() = sharedPreferences.getString(SharedPreferencesHelper.from_date, defaultValue)
    fun getRefreshToken() = sharedPreferences.getString(SharedPreferencesHelper.refresh_token, defaultValue)
    fun getAccessToken() = sharedPreferences.getString(SharedPreferencesHelper.access_token_exp, defaultValue)
    fun getAccessTokenExpireDate() = sharedPreferences.getString(SharedPreferencesHelper.access_token_exp, defaultValue)
    fun getRefreshTokenExpireDate() = sharedPreferences.getString(SharedPreferencesHelper.refresh_token_exp, defaultValue)
    fun logOff(){

        sharedPreferences?.edit()?.clear()?.apply()
        activity.apply {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}