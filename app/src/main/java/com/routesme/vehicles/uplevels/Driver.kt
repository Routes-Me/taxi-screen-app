package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class Driver {
    var driverName: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.driver_name, null)?.let {
                return it
            }
            return null
        }
        set(value) {}

    var driverPhoneNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.driver_phoneNumber, null)?.let {
                return it
            }
            return null
        }
        set(value) {}


    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}