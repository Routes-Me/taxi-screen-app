package com.routesme.taxi.uplevels

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
import com.google.android.gms.nearby.messages.PublishOptions
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi.Class.DisplayManager
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.LocationTrackingService.Class.TrackingService
import com.routesme.taxi.MVVM.Model.SignInCredentials
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
    private var trackingService: TrackingService? = null

    companion object {
        @get:Synchronized
        var instance = App()
        val nearbySubscribeOptions: SubscribeOptions = SubscribeOptions.Builder()
                .setStrategy(nearbyStrategy())
                .build()


        val nearbyPublishOptions: PublishOptions = PublishOptions.Builder()
                .setStrategy(nearbyStrategy())
                .build()

        private fun nearbyStrategy(): Strategy {
            return Strategy.Builder()
                    .setTtlSeconds(Strategy.TTL_SECONDS_INFINITE)
                    .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                    .build()
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)
        val isRegistered = !getDeviceId().isNullOrEmpty()
        if (isLocationPermissionsGranted() && isRegistered){
            val intent = Intent(instance, TrackingService::class.java)
            ContextCompat.startForegroundService(instance,intent)
            this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("TrackingService")) {
                Log.i("trackingWebSocket:", "onServiceConnected")
                trackingService = (service as TrackingService.Companion.LocationServiceBinder).service
                trackingService?.startTrackingService()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "TrackingService") {
                trackingService = null
                Log.i("trackingWebSocket:", "onServiceDisconnected")
            }
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
    private fun getDeviceId() =  getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE).getString(SharedPreferencesHelper.device_id,null)
}