package com.routesme.taxi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.impl.Schedulers.schedule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.App
import com.routesme.taxi.api.RestApiService
import com.routesme.taxi.data.model.RefreshTokenCredentials
import com.routesme.taxi.data.model.RefreshTokenSuccessResponse
import com.routesme.taxi.data.model.ResponseErrors
import com.routesme.taxi.data.model.Error
import com.routesme.taxi.uplevels.Account
import com.routesme.taxi.view.activity.HomeActivity
import com.routesme.taxi.view.activity.LoginActivity
import com.routesme.taxi.view.activity.RefreshTokenActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class RefreshTokenService: Service() {

    private val thisApiCoreService by lazy { RestApiService.createCorService(this) }

    override fun onCreate() {
        super.onCreate()
        Log.d("RefreshToken", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RefreshToken", "onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         super.onStartCommand(intent, flags, startId)
         startForeground(2, getNotification())

       // refreshToken()

        Timer("SendFeedsTimer", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(10)) {
                refreshToken()
            }
        }

         return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("RefreshToken", "Application killed")
    }

    private fun refreshToken() {
            Log.d("RefreshToken", "Hit Refresh Token")
            val refreshTokenCredentials = RefreshTokenCredentials(Account().refreshToken.toString())
            val call = thisApiCoreService.refreshToken(refreshTokenCredentials)
            call.enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    if (response.isSuccessful && response.body() != null) {
                        val successResponse = Gson().fromJson<RefreshTokenSuccessResponse>(response.body(), RefreshTokenSuccessResponse::class.java)
                        Log.d("RefreshToken","successResponse: $successResponse")
                        successResponse?.let {
                            Account().apply {
                                accessToken = it.accessToken
                                refreshToken = it.refreshToken
                            }
                            if (App.instance.isRefreshActivityAlive) {
                                startActivity(Intent(applicationContext, HomeActivity::class.java))
                                //Should finish the Refresh Token Activity here
                                RefreshTokenActivity.instance.finish()
                            }

                            stopForeground(true)
                            stopSelf()
                        }
                    } else{
                        if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                            val objError = JSONObject(response.errorBody()!!.string())
                            val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                            Log.d("RefreshToken","errors: $errors")

                            if (App.instance.isRefreshActivityAlive) {
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                //Should finish the Refresh Token Activity here
                                RefreshTokenActivity.instance.finish()
                            }

                            stopForeground(true)
                            stopSelf()
                        }else{
                            val error = Error(detail = response.message(), statusCode = response.code())
                            val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                            val responseErrors = ResponseErrors(errors)
                            Log.d("RefreshToken","responseErrors: $responseErrors")
                        }
                    }
                }
                override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                    Log.d("RefreshToken","throwable: $throwable")
                }
            })
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_2", "Refresh Token Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_2").setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}