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

class TrackingService : Service(), HubConnectionListener, HubEventListener {

    private lateinit var hubConnection: HubConnection
    private lateinit var locationReceiver: LocationReceiver
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
        locationReceiver.removeLocationUpdates()
        hubConnection.disconnect()
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

    private fun startTrackingService() {
        locationReceiver = LocationReceiver(hubConnection,this).apply {
            if (isProviderEnabled()) {
                hubConnection.connect()
                initializeLocationManager()

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

    override fun onConnected() {

        locationReceiver.getLastKnownLocationMessage()?.let {
            hubConnection.invoke("SendLocation", it)
        }

    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
    }

    override fun onEventMessage(message: HubMessage) {
        Log.d("SignalR", "onEventMessage: ${message.target}\n" + "${Gson().toJson(message.arguments)}")
    }

    override fun onDisconnected() {
        hubConnection.connect()
    }

    override fun onError(exception: Exception) {
        if (handlerThread == null) handlerThread = HandlerThread("reconnection")

        handlerThread?.apply {
            if (!this.isAlive) {
                start()
            }

            if (mHandler == null) mHandler = Handler(this.looper)
            mHandler?.postDelayed(reconnection, 1 * 60 * 1000)
        }
    }

    private val reconnection: Runnable = Runnable { reconnect() }

    private fun reconnect() {
        mHandler?.removeCallbacks(reconnection)
        handlerThread?.quit()
        hubConnection.connect()
    }

    fun sendMessage(message: String) {
       // Log.d("message-tracking",message)
       // if (hubConnection.isConnected) hubConnection.invoke("SendLocation", message)
    }
}