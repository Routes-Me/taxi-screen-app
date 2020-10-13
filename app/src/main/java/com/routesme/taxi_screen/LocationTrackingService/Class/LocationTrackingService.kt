package com.routesme.taxi_screen.LocationTrackingService.Class

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxiscreen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService() : Service(), HubConnectionListener, HubEventListener {

    private lateinit var hubConnection: HubConnection
    private lateinit var trackingDataLayer: TrackingDataLayer
    private lateinit var locationReceiver: LocationReceiver
    private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
    private var handlerCheckPermissions: Handler? = null
    private var runnableCheckPermissions: Runnable? = null
    private var permissionsHandlerRunning = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authorityUrl: URI
    private var vehicleId: String? = null
    private var institutionId: String? = null
    private var deviceId: String? = null
    private var token: String? = null
    private val NOTIFICATION_ID = 12345678
    private val reconnectionDelay: Long = 1*60*1000
    private var handlerThread: HandlerThread? = null
    private var mHandler: Handler? = null

    companion object {
        @get:Synchronized
        var instance: LocationTrackingService = LocationTrackingService()
        //  val token = instance.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE).getString(SharedPreference.token, null)

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
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

    fun checkPermissionsGranted() {
        if (hasPermissions(*permissions)) {
            startTracking()
        }
    }

    private fun startTracking() {
        sharedPreferences = App.instance.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
        vehicleId = getVehicleId()
        institutionId = getInstitutionId()
        deviceId = getDeviceId()
        token = getToken()
        if (!vehicleId.isNullOrEmpty() && !institutionId.isNullOrEmpty() && !deviceId.isNullOrEmpty() && !token.isNullOrEmpty()) {
            hubConnection = getSignalRHub()
            hubConnection.addListener(this)
            hubConnection.subscribeToEvent("SendLocation", this)

            trackingDataLayer = TrackingDataLayer(hubConnection)
            locationReceiver = LocationReceiver(trackingDataLayer)
            if (locationReceiver.setUpLocationListener()) {
                connect()
            }
        } else return
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (ContextCompat.checkSelfPermission(App.instance, p) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("trackingWebSocket:", "onBind")
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("trackingWebSocket:", "onDestroy")
        mHandler?.removeCallbacks(reconnection)
        if (locationReceiver != null) locationReceiver.unregisterLocationUpdates()
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }

    private fun getVehicleId() = sharedPreferences.getString(SharedPreference.vehicle_id, null)
    private fun getInstitutionId() = sharedPreferences.getString(SharedPreference.institution_id, null)
    private fun getDeviceId() = sharedPreferences.getString(SharedPreference.device_id, null)
    private fun getToken() = sharedPreferences.getString(SharedPreference.token, null)

    private fun connect() {
        try {
            mHandler?.removeCallbacks(reconnection)
            hubConnection.connect()
        } catch (ex: Exception) {
            Log.d("SignalR", "${ex.message} ,  ${ex}")
        }
    }

    override fun onConnected() {
        val lastKnownLocation = locationReceiver.getLastKnownMessage()
        if (!lastKnownLocation.isNullOrEmpty()) hubConnection.invoke("SendLocation", lastKnownLocation)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
    }

    override fun onEventMessage(message: HubMessage) {
        Log.d("SignalR", "onEventMessage: ${message.target}\n" + "${Gson().toJson(message.arguments)}")
    }

    override fun onDisconnected() {
        Log.d("SignalR", "onDisconnected")
            connect()
    }

    override fun onError(exception: Exception) {
        Log.d("SignalR", "onError: ${exception.message}")
        handlerThread = HandlerThread("reconnection")
        handlerThread?.start()
        mHandler = Handler(handlerThread?.looper);
        mHandler?.postDelayed(reconnection, reconnectionDelay)
    }

    val reconnection: Runnable = Runnable { connect() }
}