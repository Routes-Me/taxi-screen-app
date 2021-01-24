package com.routesme.taxi.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.*
import android.util.Log
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TrackingService() : Service(), HubConnectionListener, HubEventListener {

    private var hubConnection: HubConnection? = null
    private var locationReceiver: LocationReceiver? = null
    private val helper = TrackingServiceHelper.instance
    private var SendSavedLocationFeedsTimer: Timer? = null
    private var dataLayer = TrackingDataLayer()

    companion object {
        @get:Synchronized
        var instance: TrackingService = TrackingService()
        class LocationServiceBinder : Binder() {
            val service: TrackingService
                get() = instance
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationReceiver?.destroyLocationReceiver()
        SendSavedLocationFeedsTimer?.cancel()
        hubConnection?.disconnect()
    }

    override fun onBind(intent: Intent): IBinder {
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("Test-location-service","onStartCommand")
        startForeground(1, getNotification())
        startTracking()
        return START_STICKY
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_1", "Live Tracking Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_1").setAutoCancel(true).build()
    }

     private fun startTracking() {
     //    insertTestFeeds()
         hubConnection = getHubConnection()
         Log.d("Test-location-service","hubConnection: $hubConnection")
         sendSavedLocationFeedsTimer(hubConnection)
        locationReceiver = LocationReceiver().apply {
            if (isProviderEnabled()) {
                initializeLocationManager()
                connectSignalRHub()
            }
        }
    }

    private fun insertTestFeeds() {
       for (i in 1..10000){
          // val locationFeed = LocationFeed(i,28.313749,48.0342295,1611477557)
           val location = Location("test-feed").apply {
               latitude = 28.313749
               longitude = 48.0342295
           }
           dataLayer.insertLocation(location)
       }
    }

    private fun sendSavedLocationFeedsTimer(hubConnection: HubConnection?) {
        SendSavedLocationFeedsTimer = Timer("SendSavedLocationFeeds", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(5)) {
                hubConnection?.let { hub ->
                        try {
                            Log.d("Test-location-service","sendSavedLocationFeedsTimer")
                            if (hub.isConnected){
                                Log.d("Test-location-service","sendSavedLocationFeedsTimer-CheckHubConnection")
                                sendSavedLocationFeeds(hub)
                            }
                        }catch (e: Exception){
                        }
                }
            }
        }
    }

    private fun sendSavedLocationFeeds(hub: HubConnection) {
       dataLayer.getFeeds().let { feeds ->
            if (!feeds.isNullOrEmpty()){
                helper.getMessage(helper.getFeedsJsonArray(feeds).toString())?.let { message ->
                    try {
                        Log.d("Test-location-service","All feeds count before sending: ${dataLayer.getAllFeeds().size}")
                        hub.invoke("SendLocation", message)
                        Log.d("Test-location-service","Sent message: $message")
                        dataLayer.deleteFeeds(feeds.first().id, feeds.last().id)
                        Log.d("Test-location-service","All feeds count after sent: ${dataLayer.getAllFeeds().size}")
                    } catch (e: Exception) {
                        Log.d("Exception", e.message)
                    }
                }
            }
        }
    }

    private fun getHubConnection() = createHubConnection().apply {
        addListener(this@TrackingService)
        subscribeToEvent("SendLocation", this@TrackingService)
    }

    private fun createHubConnection(): HubConnection {
        val url = getTrackingUrl().toString()
        Log.d("Test-location-service","Hub url: $url")
        return WebSocketHubConnectionP2(url, getToken())
    }

    private fun getTrackingUrl(): Uri {
        val deviceData = getDeviceData()

        return Uri.Builder().apply {
            scheme("http")
            encodedAuthority(getAuthorityUrl())
            appendPath("trackServiceHub")
            appendQueryParameter("vehicleId", deviceData.vehicle_id)
            appendQueryParameter("institutionId", deviceData.institution_id)
            appendQueryParameter("deviceId", deviceData.device_id)
        }.build()
    }

    private fun getDeviceData(): DeviceData {
        val preferences = sharedPref()
        return DeviceData(preferences.getString(SharedPreferencesHelper.vehicle_id, null), preferences.getString(SharedPreferencesHelper.institution_id, null), preferences.getString(SharedPreferencesHelper.device_id, null))
    }

    data class DeviceData(val vehicle_id: String?, val institution_id: String?, val device_id: String?)

    private fun getAuthorityUrl() = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl", R.raw.config)).toString()

    private fun getToken(): String? {
        sharedPref().getString(SharedPreferencesHelper.token, null)?.let {
            return "Bearer $it"
        }
        return null
    }

    private fun sharedPref() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)

    override fun onConnected() {
        Log.d("Test-location-service","Hub connected")
        locationReceiver?.getLastKnownLocationMessage()?.let {
            try {
            hubConnection?.invoke("SendLocation", it)
                Log.d("Test-location-service","Sent last known message: $it")
        } catch (e: Exception) {
            Log.d("Exception", e.message)
        }
       }
    }

    override fun onMessage(message: HubMessage) {}

    override fun onEventMessage(message: HubMessage) {
    }

    override fun onDisconnected() {
        Log.d("Test-location-service","Hub disconnected")
        connectSignalRHub()
    }

    override fun onError(exception: Exception) {
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
            Log.d("Exception",e.message)
        }
    }
}