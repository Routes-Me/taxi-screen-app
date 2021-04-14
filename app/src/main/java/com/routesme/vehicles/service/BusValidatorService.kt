package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.decard.NDKMethod.BasicOper
import com.routesme.vehicles.R
import com.routesme.vehicles.data.PaymentRejectCauses
import com.routesme.vehicles.data.ReadQrCode
import com.routesme.vehicles.helper.ValidatorCodes
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class BusValidatorService : Service(){
    private var isPortOpened = false
    private var qrCodeReaderTimeoutInMilliseconds: Long  =0
    private val twoMinutesInMilliseconds = TimeUnit.MINUTES.toMillis(2)
    private val idleModeReadingInMilliseconds = TimeUnit.SECONDS.toMillis(3)
    private val defaultReadingInMilliseconds = TimeUnit.MILLISECONDS.toMillis(500)

    override fun onDestroy() {
        super.onDestroy()
        if (isPortOpened) {
            closePort()
            isPortOpened = false
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(ServiceInfo.BusValidator.serviceId, getNotification())

        if (!isPortOpened){
            isPortOpened = openPort()
            if (isPortOpened){
                Log.d("BusValidator", "Open port.. Successfully")
                runQRCodeReader()
            }else{
                Log.d("BusValidator", "Open port.. Failed")
            }
        }

        return START_STICKY
    }

    private fun runQRCodeReader() {
        if (!startQrCodeReader()) return
        readQrCodeContent(idleModeReadingInMilliseconds)
    }

    private fun startQrCodeReader(): Boolean {
        val result = BasicOper.dc_Scan2DBarcodeStart(0)
        val  resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
        return resultArray.first() == ValidatorCodes.operationSuccess
    }

    private fun stopQrCodeReader(): Boolean{
        val result = BasicOper.dc_Scan2DBarcodeExit()
        val resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
        return resultArray.first() == ValidatorCodes.operationSuccess
    }

    private fun readQrCodeContent(timeInMilliseconds: Long) {
        Timer("qrCodeReaderTimer", true).apply {
            schedule(TimeUnit.MILLISECONDS.toMillis(timeInMilliseconds), TimeUnit.MILLISECONDS.toMillis(timeInMilliseconds)) {
                Log.d("BusValidator", "qrCodeReaderTimer .. after delay: $timeInMilliseconds MS")
               Log.d("BusValidator", "qrCodeReaderTimer .. Timeout before add the delay: $qrCodeReaderTimeoutInMilliseconds MS")
                qrCodeReaderTimeoutInMilliseconds += timeInMilliseconds
                Log.d("BusValidator", "qrCodeReaderTimer .. Timeout after add the delay: $qrCodeReaderTimeoutInMilliseconds MS")
                if (qrCodeReaderTimeoutInMilliseconds >= twoMinutesInMilliseconds) {
                    Log.d("BusValidator", "qrCodeReaderTimer .. Timeout is over ,, $qrCodeReaderTimeoutInMilliseconds MS")
                    qrCodeReaderTimeoutInMilliseconds = 0
                    this@apply.apply {
                        cancel()
                        purge()
                    }
                    readQrCodeContent(idleModeReadingInMilliseconds)
                }
                val result = BasicOper.dc_Scan2DBarcodeGetData()
                val resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
                if (resultArray.first() == ValidatorCodes.operationSuccess) {
                    val content = resultArray[1].let { if (it.isNotEmpty())hexStringToString(it) else null }
                   content?.let {
                       Log.d("BusValidator", "dc_Scan2DBarcodeGetData.. Success ,, Content: $it ")
                       //For testing of Approved or Rejected
                       val readQrCode =
                               if (it.startsWith("Expired")) ReadQrCode(it, false , PaymentRejectCauses.Expired)
                               else ReadQrCode(it, true)
                       EventBus.getDefault().post(readQrCode)

                       qrCodeReaderTimeoutInMilliseconds = 0
                       this@apply.apply {
                           cancel()
                           purge()
                       }
                       readQrCodeContent(defaultReadingInMilliseconds)
                   }
                }
            }
        }
    }

    private fun hexStringToString(hexString: String): String {
        var result = hexString
        result = result.replace(" ", "")
        val baKeyword = ByteArray(result.length / 2)
        for (i in baKeyword.indices) baKeyword[i] = (0xff and result.substring(i * 2, i * 2 + 2).toInt(16)).toByte()
        return String(baKeyword, Charsets.UTF_8)
    }

    private fun openPort(): Boolean {
        BasicOper.dc_setLanguageEnv(1)
        val openPortResult = BasicOper.dc_open("COM", null, "/dev/dc_spi32765.0", 115200)
        return openPortResult > 0
    }

    private fun closePort(){
        BasicOper.dc_exit()
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel(ServiceInfo.BusValidator.channelId, ServiceInfo.BusValidator.channelName, NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, ServiceInfo.BusValidator.channelId).setSmallIcon(R.mipmap.routes_icon_light).setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}