package com.routesme.taxi.uplevels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi.Class.DisplayManager
import com.routesme.taxi.LocationTrackingService.Class.TrackingService
import com.routesme.taxi.MVVM.Model.SignInCredentials
import com.routesme.taxi.MVVM.task.TaskManager
import com.routesme.taxi.helper.SharedPreferencesHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
        val constraint: Constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorkRequest : PeriodicWorkRequest = PeriodicWorkRequest.Builder(TaskManager::class.java, 0, TimeUnit.MINUTES).setConstraints(constraint).build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)
        //Log.d("Process","${getProcessName()}")
        //startTrackingService()
    }

    fun startTrackingService(){
        val isRegistered = !getDeviceId().isNullOrEmpty()
        if (isLocationPermissionsGranted() && isRegistered){
            val intent = Intent(instance, TrackingService::class.java)
            startService(intent)
            //ContextCompat.startForegroundService(instance,intent)
           // this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
    private fun getDeviceId() =  getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE).getString(SharedPreferencesHelper.device_id,null)
}