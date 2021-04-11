package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.routesme.vehicles.R
import kotlinx.android.synthetic.main.technical_login_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BusPaymentService: Service(){

    private val paymentOperations = mutableSetOf<PaymentOperation>()

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(4, getNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_4", "Bus Payment Service Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_4").setSmallIcon(R.mipmap.routes_icon_light).setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(paymentOperation: PaymentOperation){
        paymentOperations.add(paymentOperation)
        Log.d("BusValidator", "From BusPaymentService, New Payment Operation , with QR Code Content: ${paymentOperation.qrCodeContent}")
        Log.d("BusValidator", "From BusPaymentService, Payment Operations: $paymentOperations")
    }
}

data class PaymentOperation(val qrCodeContent: String)