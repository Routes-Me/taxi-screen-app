package com.routesme.taxi_screen.Class

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.gms.nearby.messages.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.MVVM.Model.PaymentData
import com.routesme.taxi_screen.MVVM.Model.SignInCredentials
import com.routesme.taxi_screen.MVVM.View.PaymentScreen.Activity.PaymentActivity
import com.routesme.taxiscreen.R
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    private val displayManager = DisplayManager.instance
    private var proxy: HttpProxyCacheServer? = null
    var signInCredentials: SignInCredentials? = null
    var isNewLogin = false
    var institutionId: String? = null
    var taxiPlateNumber: String? = null
    var vehicleId: String? = null
    var institutionName: String? = null
    private var trackingService: LocationTrackingService? = null
    private lateinit var telephonyManager: TelephonyManager
    private  var paymentData = PaymentData()
    private val operations = Operations.instance
    private var receivedSuccessfullyMessage: String = ""
    private var minute = 60 * 1000



    companion object {

        @get:Synchronized
        var instance = App()
        val imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: ExoDatabaseProvider? = null
        var exoPlayerCacheSize: Long = 90 * 1024 * 1024
        lateinit var currentActivity:Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)
        operations.context = this

        //video player...
        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = ExoDatabaseProvider(this)
        }

        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }

        val intent = Intent(instance, LocationTrackingService::class.java)
        ContextCompat.startForegroundService(instance,intent)
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("LocationTrackingService")) {
                trackingService = (service as LocationTrackingService.Companion.LocationServiceBinder).service
                LocationTrackingService.instance.checkPermissionsGranted()
                Log.i("Tracking-Service", "onServiceConnected")
            }
        }
        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "LocationTrackingService") {
                trackingService = null
                Log.i("Tracking-Service", "onServiceDisconnected")
            }
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
}