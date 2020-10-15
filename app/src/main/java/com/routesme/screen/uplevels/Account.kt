package com.routesme.screen.uplevels

import android.content.Context
import com.routesme.screen.helper.SharedPreferencesHelper

class Account {
    var accessToken: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.token, null)?.let{
                return "Bearer $it"
            }
            return null
        }

        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.token, value).apply()
        }

    var vehicle = Vehicle()

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}