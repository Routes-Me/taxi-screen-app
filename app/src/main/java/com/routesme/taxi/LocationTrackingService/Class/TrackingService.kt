package com.routesme.taxi.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI

class TrackingService() : Service(), HubConnectionListener, HubEventListener {

    private var hubConnection: HubConnection? = null
    private var locationReceiver: LocationReceiver? = null
    private var handlerThread: HandlerThread? = null
    private var mHandler: Handler? = null

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
        locationReceiver?.removeLocationUpdates()
        instance.hubConnection?.disconnect()
    }

    override fun onBind(intent: Intent): IBinder {
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
         Log.d("TS-L","onStartCommand")
        return START_STICKY
    }

     fun startTrackingService() {
         this.hubConnection = getHubConnection()
        locationReceiver = LocationReceiver(hubConnection).apply {
            if (isProviderEnabled()) {
                initializeLocationManager()

                instance.hubConnection?.connect()
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

    private fun sharedPref() = App.instance.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)

    override fun onConnected() {
        locationReceiver?.getLastKnownLocationMessage()?.let {
            instance.hubConnection?.invoke("SendLocation", it)
       }
        locationReceiver?.isHubConnected(true)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("send-location-testing","onMessage: ${message.arguments.toString()}")
    }

    override fun onEventMessage(message: HubMessage) {
    }

    override fun onDisconnected() {
        locationReceiver?.isHubConnected(false)
        instance.hubConnection?.connect()
    }

    override fun onError(exception: Exception) {
        locationReceiver?.isHubConnected(false)
        if (handlerThread == null){
            handlerThread = HandlerThread("reconnection").apply {
                start()
                if (mHandler == null) {
                    mHandler = Handler(this.looper).apply {
                        postDelayed(reconnection, 1 * 60 * 1000)
                    }
                }
            }
        }
    }

    private val reconnection: Runnable = Runnable { reconnect() }

    private fun reconnect() {
        if (mHandler != null){
            mHandler?.removeCallbacks(reconnection)
            mHandler = null
        }
        if (handlerThread != null) {
            handlerThread?.quit()
            handlerThread = null
        }
        instance.hubConnection?.connect()
    }
}