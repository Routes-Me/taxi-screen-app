package com.routesme.taxi_screen.kotlin.Class

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.routesme.taxi_screen.java.View.Login.TaxiInformationScreen
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials

class App : Application() {
    private val displayManager = DisplayManager.instance
    private var proxy: HttpProxyCacheServer? = null
    var authCredentials:AuthCredentials? = null
    var isNewLogin = false
    var taxiOfficeId = 0
    var taxiPlateNumber: String? = null
    var taxiOfficeName: String? = null
    private var gpsService:LocationTrackingService? = null
    private lateinit var telephonyManager: TelephonyManager


    companion object {
        @get:Synchronized
        var instance = App()
        //video player...
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: ExoDatabaseProvider? = null
        var exoPlayerCacheSize: Long = 90 * 1024 * 1024
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        displayManager.setAlarm(this)

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
        this.startService(intent)
        //this.getApplication().startForegroundService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("LocationTrackingService")) {
                Log.i("trackingWebSocket:","onServiceConnected")
                gpsService = (service as LocationTrackingService.Companion.LocationServiceBinder).service
                LocationTrackingService.instance.checkPermissionsGranted()

            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "LocationTrackingService") {
                gpsService = null
                Log.i("trackingWebSocket:","onServiceDisconnected")
            }
        }
    }


}