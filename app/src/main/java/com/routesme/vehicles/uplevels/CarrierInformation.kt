package com.routesme.vehicles.uplevels

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.routesme.vehicles.App
import com.routesme.vehicles.data.model.CarrierInformationModel.*
import com.routesme.vehicles.helper.SharedPreferencesHelper
import java.lang.reflect.Type

class CarrierInformation {

    var lastUpdateDate: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.carrierInformationLastUpdate, null)?.let {
                return it
            }
            return "12, Jan 1:00 PM"//null
        }
        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.carrierInformationLastUpdate, value).apply()
        }

    var routeNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.carrierRouteNumber, null)?.let {
                return it
            }
            return "999"//null
        }
        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.carrierRouteNumber, value).apply()
        }

    var destination: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.carrierDestination, null)?.let {
                return it
            }
            return null
        }
        set(value) {
            sharedPrefs().edit().putString(SharedPreferencesHelper.carrierDestination, value).apply()
        }

    var tickets: List<Ticket>?
        get() {
            return listOf(
                    Ticket(amount = 300.0, first_station = "Station 1"),
                    Ticket(amount = 250.0, first_station = "Station 2"),
                    Ticket(amount = 200.0),
                    Ticket(amount = 150.0, first_station = "Station 4"),
                    Ticket(amount = 100.0, first_station = "Station 5")
            )
            /*
            sharedPrefs().getString(SharedPreferencesHelper.carrierTickets, null)?.let {
                val type: Type = object : TypeToken<List<Ticket?>?>() {}.type
                return Gson().fromJson(it, type)
            }
            return null
            */
        }
        set(value) {
            val json = Gson().toJson(value)
            sharedPrefs().edit().putString(SharedPreferencesHelper.carrierTickets, json).apply()
        }

    var currencies: List<Currency>?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.currencies, null)?.let {
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