package com.routesme.vehicles.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.routesme.vehicles.R
import com.routesme.vehicles.view.activity.HomeActivity

class FirebaseService : FirebaseMessagingService(){
    private var CHANNEL_ID = "1"// The id of the channel.
    private val TAG = "FirebaseMessagingService"
    private val NOTIFICATION_ID = 0
    val context: Context?=null
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationCompat: NotificationCompat.Builder

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Notification","${remoteMessage.data}")
            /*var notificationHandler = NotificationHandler()
            try {

                notificationHandler.setMessage(remoteMessage.data["message"].toString())
                notificationHandler.setDoctorId(remoteMessage.data["sender_id"].toString())
                notificationHandler.setUnReadCount("1")

                Log.d("Notification", remoteMessage.data.toString())

            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }*/

            //EventBus.getDefault().post(HandlreNotification(notificationHandler, true))
            //showNotification(notificationHandler)

        }
        showNotification()
        Log.d("Notification Message",remoteMessage.notification.toString())

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }

    private fun showNotification() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //intent.putExtra(Common.NOTIFICATION_DATA, notificationHandler)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
        )
        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.taxi_screen_icon)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.taxi_screen_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Hello Whats up!!!")
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
        val note = notificationBuilder.build()
        note.defaults = note.defaults or Notification.DEFAULT_VIBRATE
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, note)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChanel()
        }
        notificationCompat = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChanel() {
        val channel =
                NotificationChannel(CHANNEL_ID, "taxi", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }
}