package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class VehicleReferral {
    var referralCode: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.referral_code, null)?.let {
                return it
            }
            return null
        }
        set(value) {sharedPrefs().edit().putString(SharedPreferencesHelper.referral_code, value).apply()}

    var referralUrl: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.referral_url, null)?.let {
                return it
            }
            return null
        }
        set(value) {sharedPrefs().edit().putString(SharedPreferencesHelper.referral_url, value).apply()}


    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}