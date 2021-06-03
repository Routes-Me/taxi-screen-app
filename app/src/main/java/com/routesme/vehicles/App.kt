package com.routesme.vehicles

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.google.android.gms.nearby.messages.PublishCallback
import com.google.android.gms.nearby.messages.PublishOptions
import com.google.android.gms.nearby.messages.Strategy
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.vehicles.helper.DisplayManager
import com.routesme.vehicles.service.TrackingService
import com.routesme.vehicles.data.model.SignInCredentials
import com.routesme.vehicles.worker.TaskManager
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.view.events.PublishNearBy
import org.greenrobot.eventbus.EventBus
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
    var isRefreshActivityAlive: Boolean = false

    companion object {
        @get:Synchronized
        var instance = App()
        var nearbyPublishOptions = PublishOptions.Builder()
                .setStrategy(nearbyStrategy())
                .setCallback(object : PublishCallback() {
                    override fun onExpired() {
                        super.onExpired()
                        EventBus.getDefault().post(PublishNearBy(true))
                        Log.d("Publish","Expire")
                    }
                }).build()


        private fun nearbyStrategy(): Strategy {
            return Strategy.Builder()
                    .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
                    .setDistanceType(Strategy.DISTANCE_TYPE_DEFAULT)
                    .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
                    .build()
        }
        val constraint: Constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(TaskManager::class.java, 4, TimeUnit.HOURS).setConstraints(constraint).setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS).build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)
        startTrackingService()
    }

    fun startTrackingService(){
        val isRegistered = !getDeviceId().isNullOrEmpty()
        if (isLocationPermissionsGranted() && isRegistered){
            val intent = Intent(instance, TrackingService::class.java)
            ContextCompat.startForegroundService(instance,intent)
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