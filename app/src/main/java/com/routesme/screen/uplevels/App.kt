package com.routesme.screen.uplevels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.screen.Class.DisplayManager
import com.routesme.screen.helper.SharedPreferencesHelper
import com.routesme.screen.LocationTrackingService.Class.TrackingService
import com.routesme.screen.MVVM.Model.SignInCredentials
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    val account = Account()
    private val displayManager = DisplayManager.instance
    var signInCredentials: SignInCredentials? = null
    var isNewLogin = false
    var institutionId: String? = null
    var taxiPlateNumber: String? = null
    var vehicleId: String? = null
    var institutionName: String? = null

    companion object {
        @get:Synchronized
        var instance = App()
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)

        val isRegistered: Boolean = !(getDeviceId()?.isNullOrEmpty() ?: true)
        if (isLocationPermissionsGranted() && isRegistered){
            bindTrackingService()
        }
    }

    private fun logApplicationStartingPeriod(timePeriod: TimePeriod) {
        val params = Bundle()
        params.putString("TimePeriod", timePeriod.toString())
        FirebaseAnalytics.getInstance(this).logEvent("application_starting_period", params)
    }
    private fun currentPeriod(): TimePeriod {
        val currentDate = currentDate()
        return if (currentDate.after(parseDate("04:00")) && currentDate.before(parseDate("12:00"))) TimePeriod.Morning
        else if (currentDate.after(parseDate("12:00")) && currentDate.before(parseDate("17:00"))) TimePeriod.Noon
        else if (currentDate.after(parseDate("17:00")) && currentDate.before(parseDate("24:00"))) TimePeriod.Evening
        else TimePeriod.Night
    }
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDate(time: String) = SimpleDateFormat("HH:mm").parse(time)
    enum class TimePeriod { Morning, Noon, Evening, Night }

    private fun bindTrackingService() {// TODO: check if provider avaliable
        Log.d("LC", "bindTrackingService - App")

        val intent = Intent(instance, TrackingService::class.java)
        ContextCompat.startForegroundService(instance, intent)
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("LC", "onServiceConnected - App ${className.className}")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d("LC", "onServiceDisconnected - App ${className.className}")
        }
    }

    private fun isLocationPermissionsGranted(): Boolean {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (ContextCompat.checkSelfPermission(instance, p) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
    private fun getDeviceId(): String? {
        val sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferencesHelper.device_id, null)
    }
}