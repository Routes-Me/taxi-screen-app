package com.routesme.vehicles.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FcmMessageService: FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    companion object {
        private const val TAG = "FcmMessageService"
    }

    override fun onNewToken(token: String){
        Log.d("FCM-Token",token)
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String?) {

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