package com.routesme.vehicles.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.decard.NDKMethod.BasicOper
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.R
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.ValidatorCodes
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class BusValidatorService : Service(){
    private var isPortOpened = false
    private var qrCodeReaderTimeoutInMilliseconds: Long  =0
    private val twoMinutesInMilliseconds = TimeUnit.MINUTES.toMillis(2)
    private val idleModeReadingInMilliseconds = TimeUnit.SECONDS.toMillis(3)
    private val defaultReadingInMilliseconds = TimeUnit.MILLISECONDS.toMillis(500)
    private var activatedBusInfo: ActivatedBusInfo? = null
    private val thisApiCorService by lazy { RestApiService.createNewCorService(this) }

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
        activatedBusInfo = ActivatedBusInfo()

        if (!isPortOpened){
            isPortOpened = openPort()
            if (isPortOpened){
                //Log.d("BusValidator", "Open port.. Successfully")
                runQRCodeReader()
            }else{
                //Log.d("BusValidator", "Open port.. Failed")
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
                //Log.d("BusValidator", "qrCodeReaderTimer .. after delay: $timeInMilliseconds MS")
               //Log.d("BusValidator", "qrCodeReaderTimer .. Timeout before add the delay: $qrCodeReaderTimeoutInMilliseconds MS")
                qrCodeReaderTimeoutInMilliseconds += timeInMilliseconds
                //Log.d("BusValidator", "qrCodeReaderTimer .. Timeout after add the delay: $qrCodeReaderTimeoutInMilliseconds MS")
                if (qrCodeReaderTimeoutInMilliseconds >= twoMinutesInMilliseconds) {
                    //Log.d("BusValidator", "qrCodeReaderTimer .. Timeout is over ,, $qrCodeReaderTimeoutInMilliseconds MS")
                    qrCodeReaderTimeoutInMilliseconds = 0
                    this@apply.apply {
                        cancel()
                        purge()
                    }
                    readQrCodeContent(idleModeReadingInMilliseconds)
                }
                val result = BasicOper.dc_Scan2DBarcodeGetData()



                //QRCODE Content  0000|Expired   or    Expired
                val resultArray = result.split("|").dropLastWhile { it.isEmpty() }.toTypedArray()
                if (resultArray.first() == ValidatorCodes.operationSuccess) {
                    val content = resultArray[1].let { if (it.isNotEmpty())hexStringToString(it) else null }
                   content?.let { userId ->
                       //Log.d("BusValidator", "dc_Scan2DBarcodeGetData.. Success ,, Content: $it ")



                      ////////////////////Call the payment process endpoint here...////////////////////////
                      /*
                       val readQrCode =
                               if (it.startsWith("Expired")) ReadQrCode(it, false, PaymentRejectCauses.Expired)
                               else ReadQrCode(it, true)
                       */

                       activatedBusInfo?.let {
                           val busPaymentProcessCredentials = BusPaymentProcessCredentials(SecondID = it.busSecondId, Value = it.busPrice, UserID = userId)
                           //processedBusPaymentProcess(busPaymentProcessCredentials)
                           val call = thisApiCorService.busPaymentProcess(busPaymentProcessCredentials)
                           call.enqueue(object : Callback<JsonElement> {
                               override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                                   if (response.isSuccessful && response.body() != null) {
                                       val busPaymentProcessDTO = Gson().fromJson<BusPaymentProcessDTO>(response.body(), BusPaymentProcessDTO::class.java)
                                       val message: String? = if (!busPaymentProcessDTO.status) Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java) else null
                                       val readQrCode = ReadQrCode(busPaymentProcessCredentials.UserID!!, busPaymentProcessDTO.status, message)
                                       EventBus.getDefault().post(readQrCode)

                                       qrCodeReaderTimeoutInMilliseconds = 0
                                       this@apply.apply {
                                           cancel()
                                           purge()
                                       }
                                       readQrCodeContent(defaultReadingInMilliseconds)

                                       /*
                                        busPaymentProcessResponse.value =
                                                if (activateBusResponseDTO.status) BusPaymentProcessResponse(isProcessedSuccessfully = activateBusResponseDTO.status, busPaymentProcessSuccessDTO = Gson().fromJson<BusPaymentProcessSuccessDTO>(response.body()!!.asJsonObject["description"], BusPaymentProcessSuccessDTO::class.java))
                                                else BusPaymentProcessResponse(isProcessedSuccessfully = activateBusResponseDTO.status, busPaymentProcessFailedDTO = Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java))
                                   */
                                   } else {
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
                                   // busPaymentProcessResponse.value = BusPaymentProcessResponse(mThrowable = throwable)
                               }
                           })
                       }
                   }
                }
            }
        }
    }

    /*
    private fun processedBusPaymentProcess(busPaymentProcessCredentials: BusPaymentProcessCredentials) {
        val call = thisApiCorService.busPaymentProcess(busPaymentProcessCredentials)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val busPaymentProcessDTO = Gson().fromJson<BusPaymentProcessDTO>(response.body(), BusPaymentProcessDTO::class.java)
                    val message: String? = if (!busPaymentProcessDTO.status) Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java) else null
                    val readQrCode = ReadQrCode(busPaymentProcessCredentials.UserID!!, busPaymentProcessDTO.status, message)
                    EventBus.getDefault().post(readQrCode)

                   /*
                    busPaymentProcessResponse.value =
                            if (activateBusResponseDTO.status) BusPaymentProcessResponse(isProcessedSuccessfully = activateBusResponseDTO.status, busPaymentProcessSuccessDTO = Gson().fromJson<BusPaymentProcessSuccessDTO>(response.body()!!.asJsonObject["description"], BusPaymentProcessSuccessDTO::class.java))
                            else BusPaymentProcessResponse(isProcessedSuccessfully = activateBusResponseDTO.status, busPaymentProcessFailedDTO = Gson().fromJson<String>(response.body()!!.asJsonObject["description"], String::class.java))
               */
                } else {
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
               // busPaymentProcessResponse.value = BusPaymentProcessResponse(mThrowable = throwable)
            }
        })
    }
    */

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