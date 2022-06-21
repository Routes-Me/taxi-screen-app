package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.sdkdemo.LibBarCode
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.R
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class BusValidatorServiceE60Q : Service(){
    private val charset = Charsets.UTF_8
    private val hexArray = "0123456789ABCDEF".toCharArray()
    private var activatedBusInfo: ActivatedBusInfo? = null
    private val thisApiCorService by lazy { RestApiService.createNewCorService(this) }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(ServiceInfo.BusValidator.serviceId, getNotification())

        Log.d("BusValidator", "onStartCommand... BusValidatorService: BusValidatorServiceE60Q")

        activatedBusInfo = ActivatedBusInfo()
        initializeQrCodeReaderListener()

        return START_STICKY
    }

    private fun initializeQrCodeReaderListener() {
        LibBarCode.getInstance().barCodeRead { barcode, len ->
            // val contentString = hexStringToString(bytesToHex(barcode))
             val contentString = barcode.toString(charset)
            val userPaymentQrCodeData: UserPaymentQrcodeData = Gson().fromJson(contentString, UserPaymentQrcodeData::class.java)
            Log.d("BusValidator", "userPaymentQrCodeData: $userPaymentQrCodeData")

            activatedBusInfo?.let {
                val busPaymentProcessCredentials = BusPaymentProcessCredentials(SecondID = it.busSecondId, PaymentCode = userPaymentQrCodeData.paymentCode, Value = it.busPrice!!.trim().toDouble(), UserID = userPaymentQrCodeData.userId)
                Log.d("BusValidator", "busPaymentProcessCredentials: $busPaymentProcessCredentials")

                val call = thisApiCorService.busPaymentProcess(busPaymentProcessCredentials)
                call.enqueue(object : Callback<JsonElement> {
                    override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.d("BusValidator", "busPaymentProcess.. response.isSuccessful ,, Response.body: ${response.body()} ")
                            val busPaymentProcessDTO = Gson().fromJson<BusPaymentProcessDTO>(response.body(), BusPaymentProcessDTO::class.java)
                            val message: String? = if (!busPaymentProcessDTO.status) Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java) else null
                            val paymentResult = PaymentResult(userID = busPaymentProcessCredentials.UserID!!, userName = userPaymentQrCodeData.userName, isApproved = busPaymentProcessDTO.status,  rejectedReason =  message)
                            EventBus.getDefault().post(paymentResult)
                        } else {
                            Log.d("BusValidator", "busPaymentProcess.. !response.isSuccessful ,, Code : ${response.code()} ")
                            if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                                val objError = JSONObject(response.errorBody()!!.string())
                                val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                                // busPaymentProcessResponse.value = BusPaymentProcessResponse(mResponseErrors = errors)
                            } else {
                                val error = Error(detail = response.message(), statusCode = response.code())
                                val errors = mutableListOf<Error>().apply { add(error) }.toList()
                                val responseErrors = ResponseErrors(errors)
                                // busPaymentProcessResponse.value = BusPaymentProcessResponse(mResponseErrors = responseErrors)
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                        Log.d("BusValidator", "busPaymentProcess.. onFailure")
                        // busPaymentProcessResponse.value = BusPaymentProcessResponse(mThrowable = throwable)
                    }
                })
            }
            0
        }
    }

    private fun hexStringToString(hexString: String): String {
        var result = hexString
        result = result.replace(" ", "")
        val baKeyword = ByteArray(result.length / 2)
        for (i in baKeyword.indices) baKeyword[i] = (0xff and result.substring(i * 2, i * 2 + 2).toInt(16)).toByte()
        return String(baKeyword, Charsets.UTF_8)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
        // return String(baKeyword, Charsets.UTF_8)
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel(ServiceInfo.BusValidator.channelId, ServiceInfo.BusValidator.channelName, NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, ServiceInfo.BusValidator.channelId).setSmallIcon(R.mipmap.routes_icon_light).setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}