package com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.routesme.taxi_screen.kotlin.Class.SharedPreference
import com.routesme.taxi_screen.kotlin.Model.DetailActionStatus
import com.routesme.taxi_screen.kotlin.MVVM.View.LoginActivity
import com.routesme.taxiscreen.BuildConfig

class AdminConsoleHelper(val activity: Activity) {
    private val sharedPreferences = activity.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
    private val appPackageName = activity.packageName
    private val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private var locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        private const val defaultValue = "- -"
    }

    fun plateNumber() = sharedPreferences.getString(SharedPreference.vehicle_plate_number, defaultValue)
    fun institutionName() = sharedPreferences.getString(SharedPreference.institution_name, defaultValue)
    fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    fun simSerialNumber() = sharedPreferences.getString(SharedPreference.sim_serial_number, defaultValue)
    fun deviceSerialNumber() = sharedPreferences.getString(SharedPreference.device_serial_number, defaultValue)
    fun technicalUserName() = sharedPreferences.getString(SharedPreference.technician_username, defaultValue)
    fun registrationDate() = sharedPreferences.getString(SharedPreference.registration_date, defaultValue)
    fun isMyAppDefaultLauncher(): DetailActionStatus {
        val filters: MutableList<IntentFilter> = ArrayList()
        val activities: List<ComponentName> = ArrayList()
        val packageManager = activity.packageManager as PackageManager
        val filter = IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        filters.add(filter)
        packageManager.getPreferredActivities(filters, activities, null)
        for (activity in activities) {
            if (appPackageName == activity.packageName) return DetailActionStatus.DONE
        }
        return DetailActionStatus.PENDING
    }
    fun openDefaultLauncherSetting() {
        activity.apply { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
    }
    fun isLocationPermissionsAllowed(): DetailActionStatus {
            for (p in locationPermissions) {
                if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) return DetailActionStatus.PENDING
            }
        return DetailActionStatus.DONE
    }
    fun openAppGeneralSettings(){
        activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", appPackageName, null)))
        // activity.startActivity(Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", appPackageName, null)))
    }

    fun logOff() {
        sharedPreferences.edit().clear().apply()
        activity.apply { startActivity(Intent(this, LoginActivity::class.java)); finish() }
    }
    fun isLocationProviderEnabled() = isGPSEnabled() || isNetworkEnabled()
    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}