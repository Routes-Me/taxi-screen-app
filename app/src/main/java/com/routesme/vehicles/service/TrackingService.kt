package com.routesme.vehicles.service

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.routesme.vehicles.helper.Helper
import com.routesme.vehicles.service.receiver.LocationReceiver
import com.routesme.vehicles.room.doa.LocationFeedsDao
import com.routesme.vehicles.room.TrackingDatabase
import com.routesme.vehicles.room.entity.LocationFeed
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.App
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TrackingService : Service() {

    private lateinit var hubConnection: HubConnection
    private lateinit var locationReceiver: LocationReceiver
    private lateinit var db : TrackingDatabase
    private lateinit var locationFeedsDao: LocationFeedsDao
    private var sendFeedsTimer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        hubConnection = prepareHubConnection()
        locationReceiver = LocationReceiver()
        db = TrackingDatabase(App.instance)
        locationFeedsDao = db.locationFeedsDao()
         //insertTestFeeds()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationReceiver.stopLocationUpdatesListener()
        sendFeedsTimer?.cancel()
        db.apply { if (isOpen) close() }
        hubConnection.apply { if (this.connectionState == HubConnectionState.CONNECTED) stop() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
      //  Log.d("Test-location-service", "onStartCommand")
       // Log.d("Service-Thread", "onConnected... ${Thread.currentThread().name}")
        startForeground(1, getNotification())
        locationReceiver.apply {
            if (isProviderEnabled()) {
                startLocationUpdatesListener()
                startHubConnection()
            }
            scheduleLocationFeeds()
        }
        return START_STICKY
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_1", "Live Tracking Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_1").setAutoCancel(true).build()
    }

    private fun insertTestFeeds() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 1..100000) {
                val locationFeed = LocationFeed(latitude = 28.313749, longitude = 48.0342295, timestamp = System.currentTimeMillis() / 1000)
                locationFeedsDao.insertLocation(locationFeed)
            }
        }
    }

    private fun scheduleLocationFeeds() {
        sendFeedsTimer = Timer("SendFeedsTimer", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(0), TimeUnit.SECONDS.toMillis(5)) {
                hubConnection.let {
                        if (it.connectionState == HubConnectionState.CONNECTED) {
                            sendFeeds()
                        }
                }
            }
        }
    }

    private fun sendFeeds() {
        GlobalScope.launch(Dispatchers.IO) {
            locationFeedsDao.getFeeds().let { feeds ->
                if (!feeds.isNullOrEmpty()) {
                    val feedCoordinates = feeds.map { it.coordinate }
                    hubConnection.let { if (it.connectionState == HubConnectionState.CONNECTED) it.send("SendLocations", feedCoordinates) }
                    locationFeedsDao.deleteFeeds(feeds.first().id, feeds.last().id)
                }
            }
        }
    }

    private fun prepareHubConnection(): HubConnection {
        val trackingUrl = getTrackingUrl().toString()
        Log.d("URL","${trackingUrl}")
        return HubConnectionBuilder
                .create(trackingUrl)
                .withHeader("Authorization", Account().accessToken)
                .build().apply {
                    serverTimeout = TimeUnit.MINUTES.toMillis(6)
                    onClosed {
                        Log.d("SocketSrv", "onClosed, Exception: $it")
                        startHubConnection()
                    }
                    on("CommonMessage", { message: String? ->
                        message?.let {
                            Log.d("SocketSrv", "CommonMessage .. Message: $it")
                        }
                    }, String::class.java)
                }
    }

    private fun getTrackingUrl(): Uri {
        val trackingAuthorityUrl = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl", R.raw.config)).toString()
        val sharedPref = applicationContext.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        val vehicleId = sharedPref.getString(SharedPreferencesHelper.vehicle_id, null)
        val institutionId = sharedPref.getString(SharedPreferencesHelper.institution_id, null)
        val deviceId = sharedPref.getString(SharedPreferencesHelper.device_id, null)
        return Uri.Builder().apply {
            scheme("http")
            encodedAuthority(trackingAuthorityUrl)
            appendPath("trackServiceHub")
            appendQueryParameter("vehicleId", vehicleId)
            appendQueryParameter("institutionId", institutionId)
            appendQueryParameter("deviceId", deviceId)
        }.build()
    }

    private fun startHubConnection() {
        hubConnection.start()
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(@NonNull d: Disposable) {}
                    override fun onError(@NonNull e: Throwable) {
                        Log.d("SocketSrv", "onError, Throwable: $e")
                        Timer("signalRReconnection", true).apply {
                            schedule(TimeUnit.MINUTES.toMillis(1)) {
                                startHubConnection()
                            }
                        }
                    }
                    override fun onComplete() {
                        Log.d("SocketSrv", "onComplete")
                        locationReceiver.getLastKnownLocationMessage()?.let {
                            val feedCoordinates = mutableListOf<LocationFeed>().apply { add(it) }.map { it.coordinate }
                            hubConnection.let { if (it.connectionState == HubConnectionState.CONNECTED) it.send("SendLocations", feedCoordinates) }
                        }
                    }
                })
    }

}