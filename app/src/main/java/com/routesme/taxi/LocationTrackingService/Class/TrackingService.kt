package com.routesme.taxi.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.*
import android.util.Log
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.Account
import com.routesme.taxi.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TrackingService() : Service(), HubConnectionListener, HubEventListener {

    private var hubConnection: HubConnection? = null
    private var locationReceiver: LocationReceiver? = null
    private val helper = TrackingServiceHelper.instance
    private var SendSavedLocationFeedsTimer: Timer? = null
    private val db = TrackingDatabase(App.instance)
    private val locationFeedsDao = db.locationFeedsDao()

    override fun onCreate() {
        super.onCreate()
        hubConnection = prepareHubConnection()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationReceiver?.destroyLocationReceiver()
        SendSavedLocationFeedsTimer?.cancel()
       // hubConnection.apply { if (isConnected) disconnect() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("Test-location-service", "onStartCommand")
        Log.d("Service-Thread", "onConnected... ${Thread.currentThread().name}")
        //startForeground(1, getNotification())
        locationReceiver = LocationReceiver().apply {
            if (isProviderEnabled()) {
                initializeLocationManager()
                connectSignalRHub()
            }
            sendSavedLocationFeedsTimer()
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

    private fun sendSavedLocationFeedsTimer() {
        SendSavedLocationFeedsTimer = Timer("SendSavedLocationFeeds", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(0), TimeUnit.SECONDS.toMillis(5)) {
                hubConnection?.let {
                        Log.d("Test-location-service", "sendSavedLocationFeedsTimer")
                        if (it.isConnected) {
                            Log.d("Test-location-service", "sendSavedLocationFeedsTimer-CheckHubConnection")
                            sendSavedLocationFeeds()
                        }
                }
            }
        }
    }

    private fun sendSavedLocationFeeds() {
        GlobalScope.launch {
            locationFeedsDao.getFeeds().let { feeds ->
                if (!feeds.isNullOrEmpty()) {
                    helper.getMessage(helper.getFeedsJsonArray(feeds).toString())?.let { message ->
                            Log.d("Test-location-service", "All feeds count before sending: ${locationFeedsDao.getAllFeeds().size}")
                            hubConnection?.invoke("SendLocation", message)
                            Log.d("Test-location-service", "Sent message: $message")
                            locationFeedsDao.deleteFeeds(feeds.first().id, feeds.last().id)
                            Log.d("Test-location-service", "All feeds count after sent: ${locationFeedsDao.getAllFeeds().size}")
                    }
                }
            }
        }
    }


    private fun prepareHubConnection() = createHubConnection().apply {
        addListener(this@TrackingService)
        subscribeToEvent("SendLocation", this@TrackingService)
    }

    private fun createHubConnection(): HubConnection = WebSocketHubConnectionP2(getTrackingUrl().toString(), Account().accessToken)

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

    override fun onConnected() {
        Log.d("SignalR-Thread","onConnected... ${Thread.currentThread().name}")
        Log.d("Test-location-service","Hub connected")
        locationReceiver?.getLastKnownLocationMessage()?.let {
            try {
            hubConnection?.invoke("SendLocation", it)
                Log.d("Test-location-service","Sent last known message: $it")
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }
       }
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR-Thread","onMessage... ${Thread.currentThread().name}")
    }

    override fun onEventMessage(message: HubMessage) {
        Log.d("SignalR-Thread","onEventMessage... ${Thread.currentThread().name}")
    }

    override fun onDisconnected() {
        Log.d("SignalR-Thread","onDisconnected... ${Thread.currentThread().name}")
        Log.d("Test-location-service","Hub disconnected")
        connectSignalRHub()
    }

    override fun onError(exception: Exception) {
        Log.d("SignalR-Thread","onError... ${Thread.currentThread().name}")
        Log.d("Test-location-service","Hub error")
        Timer("signalRReconnection", true).apply {
            schedule(TimeUnit.MINUTES.toMillis(1)) {
                Log.d("Test-location-service","Hub error")
                connectSignalRHub()
            }
        }
    }

    private fun connectSignalRHub(){
        try{
            Log.d("Test-location-service","Try to connect the hub")
            hubConnection?.connect()
        }catch (e: Exception){
            Log.d("Exception",e.message.toString())
        }
    }
}