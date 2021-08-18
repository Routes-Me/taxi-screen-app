package com.routesme.vehicles.uplevels

import android.content.Context
import com.routesme.vehicles.App
import com.routesme.vehicles.helper.SharedPreferencesHelper

class DeviceInformation {
    var username: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.username, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.username, value).apply() }

    var registrationDate: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.registration_date, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.registration_date, value).apply() }

    var institutionId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.institution_id, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.institution_id, value).apply() }

    var institutionName: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.institution_name, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.institution_name, value).apply() }

    var vehicleId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.vehicle_id, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.vehicle_id, value).apply() }

    var vehiclePlateNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.vehicle_plate_number, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.vehicle_plate_number, value).apply() }

    var deviceId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.device_id, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.device_id, value).apply() }

    var deviceSerialNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.device_serial_number, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.device_serial_number, value).apply() }

    var simSerialNumber: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.sim_serial_number, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.sim_serial_number, value).apply() }

    var fromDate: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.from_date, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.from_date, value).apply() }

    var terminalId: String?
        get() {
            sharedPrefs().getString(SharedPreferencesHelper.terminal_id, null)?.let { return it }
            return null
        }
        set(value) { sharedPrefs().edit().putString(SharedPreferencesHelper.terminal_id, value).apply() }

    private fun sharedPrefs() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Context.MODE_PRIVATE)
}