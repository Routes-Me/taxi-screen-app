package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class Vehicle {
    var id: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.vehicle_id, null)?.let {
                return it
            }
            return null
        }
        set(value) {}


    var institutionId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.institution_id, null)?.let {
                return it
            }
            return null
        }
        set(value) {}


    var deviceId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.device_id, null)?.let {
                return it
            }
            return null
        }
        set(value) {}

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)

}