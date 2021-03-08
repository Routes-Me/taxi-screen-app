package com.routesme.taxi.uplevels

import android.content.Context
import com.routesme.taxi.App
import com.routesme.taxi.helper.SharedPreferencesHelper

class Account {
    var accessToken: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.token, null)?.let{
                return "Bearer $it dfd"
            }
            return null
        }
        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.token, value).apply()
        }

    var refreshToken: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.refresh_token, null)?.let{
                return it
            }
            return null
        }
        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.refresh_token, value).apply()
        }

    var vehicle = Vehicle()

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}