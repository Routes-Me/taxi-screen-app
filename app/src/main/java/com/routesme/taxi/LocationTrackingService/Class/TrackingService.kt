package com.routesme.taxi.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.annotation.NonNull
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.Account
import com.routesme.taxi.uplevels.App
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TrackingService() : Service() {

    private lateinit var hubConnection: HubConnection
    private lateinit var locationReceiver: LocationReceiver
    private val helper = TrackingServiceHelper.instance
    private var sendFeedsTimer: Timer? = null
    private val db = TrackingDatabase(App.instance)
    private val locationFeedsDao = db.locationFeedsDao()

    override fun onCreate() {
        super.onCreate()
        hubConnection = prepareHubConnection()
        locationReceiver = LocationReceiver()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationReceiver.stopLocationUpdatesListener()
        sendFeedsTimer?.cancel()
        hubConnection.apply { if (this.connectionState == HubConnectionState.CONNECTED) stop() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("Test-location-service", "onStartCommand")
        Log.d("Service-Thread", "onConnected... ${Thread.currentThread().name}")
        //startForeground(1, getNotification())
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
        for (i in 1..100000) {
            // val locationFeed = LocationFeed(i,28.313749,48.0342295,1611477557)
            val location = Location("test-feed").apply {
                latitude = 28.313749
                longitude = 48.0342295
            }
            //   dataLayer.insertFeed(location)
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
        GlobalScope.launch {
            locationFeedsDao.getFeeds().let { feeds ->
                if (!feeds.isNullOrEmpty()) {
                    helper.getMessage(helper.getFeedsJsonArray(feeds).toString())?.let { message ->
                            Log.d("Test-location-service", "All feeds count before sending: ${locationFeedsDao.getAllFeeds().size}")
                            hubConnection.send("SendLocation", message)
                            Log.d("Test-location-service", "Sent message: $message")
                            locationFeedsDao.deleteFeeds(feeds.first().id, feeds.last().id)
                            Log.d("Test-location-service", "All feeds count after sent: ${locationFeedsDao.getAllFeeds().size}")
                    }
                }
            }
        }
    }

    private fun prepareHubConnection(): HubConnection {
        return HubConnectionBuilder
                .create(getTrackingUrl().toString())
                .withHeader("Authorization", Account().accessToken)
                .build().apply {
                    serverTimeout = TimeUnit.MINUTES.toMillis(6)
                    onClosed {
                        Log.d("SocketSrv", "onClosed, Exception: $it")
                        startHubConnection()
                    }
                    on("CommonMessage", { message: String ->
                        Log.d("SocketSrv", "onSendLocation .. Message: $message")
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
                            hubConnection.send("SendLocation", it)
                        }
                    }
                })
    }
}