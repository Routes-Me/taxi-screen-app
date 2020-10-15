package com.routesme.screen.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.*
import android.util.Log
import com.google.gson.Gson
import com.routesme.screen.Class.Helper
import com.routesme.screen.helper.SharedPreferencesHelper
import com.routesme.screen.uplevels.App
import com.routesme.screen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI
import java.net.URISyntaxException

class TrackingService() : Service(), HubConnectionListener, HubEventListener {

    private lateinit var hubConnection: HubConnection
    private var locationReceiver = LocationReceiver()


    private var dataLayer: TrackingDataLayer? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authorityUrl: URI
    private var vehicleId: String? = null
    private var institutionId: String? = null
    private var deviceId: String? = null
    private val notificationId = 1
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
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        vehicleId = getVehicleId()
        institutionId = getInstitutionId()
        deviceId = getDeviceId()
        startForeground(notificationId, getNotification())
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

        hubConnection = createHubConnection()
        hubConnection.addListener(this@TrackingService)
        hubConnection.subscribeToEvent("SendLocation", this@TrackingService)
      /*
                .apply {
            addListener(this@TrackingService)
            subscribeToEvent("SendLocation", this@TrackingService)
        }
*/
        dataLayer = TrackingDataLayer(hubConnection)

        locationReceiver.apply {
            dataLayer?.let { this.setDataLayer(it) }



                if (isProviderEnabled()) {
                    initializeLocationManager()
                    hubConnect()
                }
            }
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }

    private fun createHubConnection(): HubConnection {
        val url = getTrackingUrl().toString()
        val authHeader = App.instance.account.accessToken ?: ""
        return WebSocketHubConnectionP2(url, authHeader)
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

    private fun getVehicleId() = sharedPreferences.getString(SharedPreferencesHelper.vehicle_id, null)
    private fun getInstitutionId() = sharedPreferences.getString(SharedPreferencesHelper.institution_id, null)
    private fun getDeviceId() = sharedPreferences.getString(SharedPreferencesHelper.device_id, null)

    private fun hubConnect() {
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
        if (!lastKnownLocationMessage.isNullOrEmpty()) hubConnection?.invoke("SendLocation", lastKnownLocationMessage)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
    }

    override fun onEventMessage(message: HubMessage) {
        Log.d("SignalR", "onEventMessage: ${message.target}\n" + "${Gson().toJson(message.arguments)}")
    }

    override fun onDisconnected() {
        hubConnect()
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

    private val reconnection: Runnable = Runnable { hubConnect() }
}