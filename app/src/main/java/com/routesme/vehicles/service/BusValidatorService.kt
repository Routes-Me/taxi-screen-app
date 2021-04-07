package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.decard.NDKMethod.BasicOper
import com.routesme.vehicles.helper.ValidatorCodes

class BusValidatorService : Service(){
    private var isPortOpened = false
    private var qrCodeReadingFlag = false

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPortOpened) closePort()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(3, getNotification())
        Log.d("BusValidator", "ValidatorService onStartCommand()")

        if (!isPortOpened){
            isPortOpened = openPort()
            if (isPortOpened){
                Log.d("BusValidator", "Open port.. Successfully")
                openQRCodeReader()
            }else{
                Log.d("BusValidator", "Open port.. Failed")
            }
        }
        return START_STICKY
    }

    private fun openQRCodeReader() {
        Thread(Runnable {
            qrCodeReadingFlag = true
            var resultArray: Array<String>? = null
            var result: String? = null
            result = BasicOper.dc_Scan2DBarcodeStart(0)
            resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
            Log.d("BusValidator", "dc_Scan2DBarcodeStart.. result: $result")
            if (resultArray.first() != ValidatorCodes.operationSuccess) {
                Log.d("BusValidator", "dc_Scan2DBarcodeStart.. Failed, resultArray: ${resultArray.first()}")
                return@Runnable
            }
            Log.d("BusValidator", "dc_Scan2DBarcodeStart.. Success")
            Log.d("BusValidator", "Please move your QR Code")
            while (qrCodeReadingFlag) {
                SystemClock.sleep(500)
                result = BasicOper.dc_Scan2DBarcodeGetData()
                resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
                if (resultArray.first() == ValidatorCodes.operationSuccess) {
                    val content = resultArray[1].let { if (it.isNotEmpty())hexStringToString(it) else null }
                    Log.d("BusValidator", "dc_Scan2DBarcodeGetData.. Success ,, Content: $content ")
                    qrCodeReadingFlag = false
                    break
                }
            }
            result = BasicOper.dc_Scan2DBarcodeExit()
            resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
            if (resultArray.first() != ValidatorCodes.operationSuccess) {
                Log.d("BusValidator", "dc_Scan2DBarcodeExit.. Failed")
                return@Runnable
            }
            Log.d("BusValidator", "dc_Scan2DBarcodeExit.. Success")
            Log.d("BusValidator", "Qr Code operation .. Done !")
            openQRCodeReader()
        }).start()
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
        val channel = NotificationChannel("channel_3", "Reader Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_3").setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun hexStringToString(hexString: String): String {
        var result = hexString
        result = result.replace(" ", "")
        val baKeyword = ByteArray(result.length / 2)
        for (i in baKeyword.indices) baKeyword[i] = (0xff and result.substring(i * 2, i * 2 + 2).toInt(16)).toByte()
        return String(baKeyword, Charsets.UTF_8)
    }
}