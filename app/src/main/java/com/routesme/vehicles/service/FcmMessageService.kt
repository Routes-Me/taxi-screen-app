package com.routesme.vehicles.service

import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.TerminalCredentials
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.uplevels.DeviceInformation

class FcmMessageService: FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    companion object {
        private const val TAG = "FcmMessageService"
    }

    override fun onNewToken(token: String){
        Log.d("FCM-Token",token)
        updateTokenInServer(token)
    }

    private fun updateTokenInServer(token: String?) {
        token?.let { newToken ->
            val deviceInformation = DeviceInformation()
            val deviceId = deviceInformation.deviceId
            val terminalId = deviceInformation.terminalId
            if (!deviceId.isNullOrEmpty() && !terminalId.isNullOrBlank()) {
                val apiCorService by lazy { RestApiService.createCorService(this) }
                val terminalCredentials = TerminalCredentials(newToken, deviceId)
                val call = apiCorService.updateTerminal(terminalId, terminalCredentials)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {

                        } else {
                            if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                                val objError = JSONObject(response.errorBody()!!.string())
                                val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                            } else {
                                val error = Error(detail = response.message(), statusCode = response.code())
                                val errors = mutableListOf<Error>().apply { add(error) }.toList()
                                val responseErrors = ResponseErrors(errors)
                            }
                        }
                    }
                    override fun onFailure(call: Call<Void>, throwable: Throwable) {}
                })
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        /*
        getSessionId()?.let {savedSessionId ->
            Log.d(TAG, "SessionId: $savedSessionId, RemoteMessage: ${remoteMessage.data}")
            val messageData = remoteMessage.data
            if (!messageData.isNullOrEmpty()){
                val notificationData = Gson().fromJson<NotificationData>(messageData.toString(), NotificationData::class.java)
                if (savedSessionId == notificationData.session?.id){
                    sendNotification(notificationData)
                }
            }
        }
        */
    }

    //private fun getSessionId() = getSharedPreferences(SharedPreferencesHelper.device_data, MODE_PRIVATE).getString(SharedPreferencesHelper.payment_session_id,null)
/*
    @SuppressLint("DefaultLocale")
    private fun sendNotification(messageBody: NotificationData) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = getActivityIntent(messageBody.session?.status?.toLowerCase()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (isAppOnForeground()){
            val previousActivity = App.instance.currentActivity
            startActivity(intent)
            previousActivity?.let {
                Log.d("previousActivity",previousActivity.toString())
                it.finish()
            }
        }else{
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val notificationBuilder = getNotificationBuilder()
            createNotification(notificationBuilder, pendingIntent, messageBody)
            notificationManager?.notify(0, notificationBuilder?.build())
        }
    }

    private fun createNotification(notificationBuilder: NotificationCompat.Builder?, pendingIntent: PendingIntent, messageBody: NotificationData) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder?.let {
            it.apply {
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle(messageBody.session?.id)
                setContentText(messageBody.session?.status)
                setAutoCancel(true)
                setSound(defaultSoundUri)
                setContentIntent(pendingIntent)
            }
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        var notificationBuilder: NotificationCompat.Builder? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = packageName
            notificationManager?.createNotificationChannel(channel)
            if (notificationBuilder == null) {
                notificationBuilder = NotificationCompat.Builder(application, packageName)
            }
        } else {
            if (notificationBuilder == null) {
                notificationBuilder = NotificationCompat.Builder(application,packageName)
            }
        }
        return notificationBuilder
    }

    private fun getActivityIntent(sessionStatus: String?):Intent{
        return when(sessionStatus){
            PaymentSessionStatus.SUCCESS.s -> Intent(App.instance, PaymentSuccessfulActivity::class.java)
            else -> Intent(App.instance, MainActivity::class.java)
        }
    }

    private fun isAppOnForeground() = (App.instance.isActivityVisible() == Lifecycle.State.RESUMED)
    */
}