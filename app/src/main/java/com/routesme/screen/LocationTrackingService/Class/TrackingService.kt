package com.routesme.screen.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import com.google.gson.Gson
import com.routesme.screen.Class.Helper
import com.routesme.screen.helper.SharedPreferencesHelper
import com.routesme.screen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI

class TrackingService() : Service(), HubConnectionListener, HubEventListener {

    private lateinit var hubConnection: HubConnection
    private var locationReceiver = LocationReceiver()
    //private var dataLayer: TrackingDataLayer? = null
    private val reconnectionDelay: Long = 1 * 60 * 1000
    private var handlerThread: HandlerThread? = null
    private var mHandler: Handler? = null

    companion object {
        @get:Synchronized
        var instance: TrackingService = TrackingService()

        class LocationServiceBinder : Binder() {
            val service: TrackingService?
                get() = instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, getNotification())
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_1", "Live Tracking Channel", NotificationManager.IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(applicationContext, "channel_1").setAutoCancel(true).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeCallbacks(reconnection)
        locationReceiver.removeLocationUpdates()
    }

    override fun onBind(intent: Intent): IBinder {
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        hubConnection = getHubConnection()
        startTrackingService()
        return START_STICKY
    }

    private fun connectHub() {
        hubConnection = createHubConnection().apply {
            addListener(this@TrackingService)
            subscribeToEvent("SendLocation", this@TrackingService)
        }


    }

    private fun startTrackingService() {
        locationReceiver.apply {
            if (isProviderEnabled()) {
                initializeLocationManager()
                hubConnection.connect()
            }
        }
    }

    private fun getHubConnection() = createHubConnection().apply {
        addListener(this@TrackingService)
        subscribeToEvent("SendLocation", this@TrackingService)
    }

    private fun createHubConnection(): HubConnection {
        val url = getTrackingUrl().toString()
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

    private fun sharedPref() = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)

    private fun connect() {
        try {
            if (mHandler != null) {
                mHandler?.removeCallbacks(reconnection)
                mHandler = null
            }
            if (handlerThread != null) {
                handlerThread?.quit()
                handlerThread = null
            }

        } catch (ex: Exception) {
            Log.d("SignalR", "${ex.message} ,  ${ex}")
        }
    }

    override fun onConnected() {
        val lastKnownLocationMessage = locationReceiver.getLastKnownLocationMessage()
        if (!lastKnownLocationMessage.isNullOrEmpty()) hubConnection.invoke("SendLocation", lastKnownLocationMessage)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
    }

    override fun onEventMessage(message: HubMessage) {
        Log.d("SignalR", "onEventMessage: ${message.target}\n" + "${Gson().toJson(message.arguments)}")
    }

    override fun onDisconnected() {
        connect()
    }

    override fun onError(exception: Exception) {
        if (handlerThread == null) {
            handlerThread = HandlerThread("reconnection").apply {
                start()
                if (mHandler == null) {
                    mHandler = Handler(this.looper).apply {
                        postDelayed(reconnection, reconnectionDelay)
                    }
                }
            }
        }
    }

    private val reconnection: Runnable = Runnable { connect() }
}