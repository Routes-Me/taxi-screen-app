package com.routesme.taxi_screen.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.*
import android.util.Log
import com.google.gson.Gson
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxiscreen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService() : Service(), HubConnectionListener, HubEventListener {

    private lateinit var hubConnection: HubConnection
    private var trackingDataLayer: TrackingDataLayer? = null
    private var locationReceiver: LocationReceiver? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authorityUrl: URI
    private var vehicleId: String? = null
    private var institutionId: String? = null
    private var deviceId: String? = null
    private var token: String? = null
    private val notificationId = 1
    private val reconnectionDelay: Long = 1 * 60 * 1000
    private var handlerThread: HandlerThread? = null
    private var mHandler: Handler? = null

    companion object {
        @get:Synchronized
        var instance: LocationTrackingService = LocationTrackingService()

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService?
                get() = instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
        vehicleId = getVehicleId()
        institutionId = getInstitutionId()
        deviceId = getDeviceId()
        token = getToken()
        startForeground(notificationId, getNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeCallbacks(reconnection)
        locationReceiver?.removeLocationUpdates()
    }

    override fun onBind(intent: Intent): IBinder {
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTrackingService()
        return START_STICKY
    }

    private fun startTrackingService() {
        if (!vehicleId.isNullOrEmpty() && !institutionId.isNullOrEmpty() && !deviceId.isNullOrEmpty() && !token.isNullOrEmpty()) {
            hubConnection = getSignalRHub().apply {
                addListener(this@LocationTrackingService)
                subscribeToEvent("SendLocation", this@LocationTrackingService)
            }
            trackingDataLayer = TrackingDataLayer(hubConnection).apply {
                locationReceiver = LocationReceiver(this, hubConnection).apply {
                    if (isProviderEnabled()) {
                        initializeLocationManager()
                        connect()
                    }
                }
            }

        } else return
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }

    private fun getSignalRHub(): HubConnection {
        val url = getTrackingUrl().toString()
        val authHeader = "Bearer $token"
        val hubConnection = WebSocketHubConnectionP2(url, authHeader)

        return hubConnection
    }

    private fun getTrackingUrl(): URI {
        try {
            authorityUrl = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl", R.raw.config))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("http")
                .encodedAuthority(authorityUrl.toString())
                .appendPath("trackServiceHub")
                .appendQueryParameter("vehicleId", vehicleId.toString())
                .appendQueryParameter("institutionId", institutionId.toString())
                .appendQueryParameter("deviceId", deviceId.toString())
        return URI(builder.build().toString())
    }

    private fun getVehicleId() = sharedPreferences.getString(SharedPreference.vehicle_id, null)
    private fun getInstitutionId() = sharedPreferences.getString(SharedPreference.institution_id, null)
    private fun getDeviceId() = sharedPreferences.getString(SharedPreference.device_id, null)
    private fun getToken() = sharedPreferences.getString(SharedPreference.token, null)

    private fun connect() {
        try {
            if (mHandler != null){
                mHandler?.removeCallbacks(reconnection)
                mHandler = null
            }
            if (handlerThread != null) {
                handlerThread?.quit()
                handlerThread = null
            }
            hubConnection.connect()
        } catch (ex: Exception) {
            Log.d("SignalR", "${ex.message} ,  ${ex}")
        }
    }

    override fun onConnected() {
        val lastKnownLocationMessage = locationReceiver?.getLastKnownLocationMessage()
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
        if (handlerThread == null){
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