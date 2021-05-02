package com.routesme.vehicles.uplevels

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.routesme.vehicles.App
import com.routesme.vehicles.data.model.CarrierInformationModel.*
import com.routesme.vehicles.helper.SharedPreferencesHelper
import java.lang.reflect.Type

class CarrierInformation {

        var routeNumber: String?
            get() {
                sharedPrefs().getString(SharedPreferencesHelper.busRouteNumber, null)?.let {
                    return it
                }
                return null
            }
            set(value) {
                sharedPrefs().edit().putString(SharedPreferencesHelper.busRouteNumber, value).apply()
            }

        var destination: String?
            get() {
                sharedPrefs().getString(SharedPreferencesHelper.busDestination, null)?.let{
                    return it
                }
                return null
            }
            set(value) {
                sharedPrefs().edit().putString(SharedPreferencesHelper.busDestination, value).apply()
            }

    var tickets: List<Ticket>?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busTickets, null)?.let{
                val type: Type = object : TypeToken<List<Ticket?>?>() {}.type
                return Gson().fromJson(it, type)
            }
            return null
        }
        set(value) {
            val json = Gson().toJson(value)
            sharedPrefs().edit().putString(SharedPreferencesHelper.busTickets, json).apply()
        }

    var currencies: List<Currency>?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.currencies, null)?.let{
                val type: Type = object : TypeToken<List<Currency?>?>() {}.type
                return Gson().fromJson(it, type)
            }
            return null
        }
        set(value) {
            val json = Gson().toJson(value)
            sharedPrefs().edit().putString(SharedPreferencesHelper.currencies, json).apply()
        }

        private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}