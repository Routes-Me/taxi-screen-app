package com.routesme.taxi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class RefreshTokenService: Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         super.onStartCommand(intent, flags, startId)
         startForeground(2, getNotification())
         return START_STICKY
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_2", "Refresh Token Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_2").setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}