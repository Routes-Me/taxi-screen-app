package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.routesme.vehicles.R
import com.routesme.vehicles.data.ReadQrCode
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BusPaymentService: Service(){

    private val paymentOperations = mutableSetOf<ReadQrCode>()

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(ServiceInfo.BusPayment.serviceId, getNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel(ServiceInfo.BusPayment.channelId, ServiceInfo.BusPayment.channelName, NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, ServiceInfo.BusPayment.channelId).setSmallIcon(R.mipmap.routes_icon_light).setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(readQrCode: ReadQrCode){
        Log.d("BusValidator", "From BusPaymentService, New QR Code , with Content: ${readQrCode.content}, isApproved: ${readQrCode.isApproved}, RejectCauses: ${readQrCode.rejectCauses?.message}")
        if (readQrCode.isApproved){
            paymentOperations.add(readQrCode)
            Log.d("BusValidator", "From BusPaymentService, Payment Operations: $paymentOperations")
        }
    }
}