package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class AuthorizationTokens {
    var accessToken: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.token, null)?.let {
                return "Bearer $it"
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

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}