package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class ActivatedBusInfo {

    var busId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busId, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busId, value).apply() }

    var busActive: Boolean
        get() {
            sharedPrefs().getBoolean(SharedPreferencesHelper.busActive, false).let{ return it }
        }
        set(value) { sharedPrefs().edit().putBoolean(SharedPreferencesHelper.busActive, value).apply() }

    var busKind: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busKind, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busKind, value).apply() }

    var busPlateNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busPlateNumber, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busPlateNumber, value).apply() }

    var busRouteId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busRouteId, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busRouteId, value).apply() }

    var busRouteName: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busRouteName, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busRouteName, value).apply() }

    var busDestination: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busDestination, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busDestination, value).apply() }

    var busPrice: String?
    get() {
        sharedPrefs().getString(SharedPreferencesHelper.busPrice, null)?.let{ return it }
        return null
    }
    set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busPrice, value).apply() }

    var busPriceByFils: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busPrice, null)?.let{ return String.format("%.3f", it.toDouble()).split(".").last() }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busPrice, value).apply() }

    var busDriverId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busDriverId, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busDriverId, value).apply() }

    var busUserName: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busUserName, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busUserName, value).apply() }

    var busPhoneNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busPhoneNumber, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busPhoneNumber, value).apply() }

    var busCompanyId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busCompanyId, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busCompanyId, value).apply() }

    var busCompany: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busCompany, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busCompany, value).apply() }

    var busSecondId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.busSecondId, null)?.let{ return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.busSecondId, value).apply() }

    //var vehicle = Vehicle()

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}