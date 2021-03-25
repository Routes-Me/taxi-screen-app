package com.routesme.taxi.uplevels

import android.content.Context
import com.routesme.taxi.App
import com.routesme.taxi.helper.SharedPreferencesHelper

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