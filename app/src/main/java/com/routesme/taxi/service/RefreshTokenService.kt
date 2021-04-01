package com.routesme.taxi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.App
import com.routesme.taxi.api.APIHelper
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
        Log.d("RefreshTokenTesting", "RefreshTokenService onStartCommand()")
        startForeground(2, getNotification())
        refreshToken()

        return START_STICKY
    }

    private fun refreshToken() {
        Log.d("RefreshToken", "Hit Refresh Token")
        val refreshTokenCredentials = RefreshTokenCredentials(Account().refreshToken.toString())
        val call = thisApiCoreService.refreshToken(refreshTokenCredentials)
        APIHelper.enqueueWithRetry(call ,5,object :Callback<JsonElement> {
            @Override
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null){
                    val successResponse = Gson().fromJson<RefreshTokenSuccessResponse>(response.body(), RefreshTokenSuccessResponse::class.java)
                    successResponse?.let {
                        Log.d("RefreshTokenTesting", "Renewals response: $response , Code: ${response.code()}")
                        saveTokens(it)
                        if (App.instance.isRefreshActivityAlive) openHomeActivity()
                        stopRefreshTokenService()
                    }
                } else{
                    if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                        Log.d("RefreshTokenTesting", "Renewals response: $response , HTTP_NOT_ACCEPTABLE , Code: ${response.code()}")
                        /*
                         val objError = JSONObject(response.errorBody()!!.string())
                         val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                         Log.d("RefreshToken","errors: $errors")
*/
                        if (App.instance.isRefreshActivityAlive) openLoginActivity()
                        stopRefreshTokenService()
                    }else{
                        val error = Error(detail = response.message(), statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        Log.d("RefreshToken","responseErrors: $responseErrors")
                    }
                }
            }
            @Override
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d("RefreshToken","throwable: $t")
            }
        })
        /*
            call.enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    if (response.isSuccessful && response.body() != null) {
                        val successResponse = Gson().fromJson<RefreshTokenSuccessResponse>(response.body(), RefreshTokenSuccessResponse::class.java)
                        Log.d("RefreshToken","successResponse: $successResponse")
                        successResponse?.let {
                            saveTokens(it)
                            openHomeActivity()
                            stopRefreshTokenService()
                        }
                    } else{
                        if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                           /*
                            val objError = JSONObject(response.errorBody()!!.string())
                            val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                            Log.d("RefreshToken","errors: $errors")
*/
                            openLoginActivity()
                            stopRefreshTokenService()
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
                    */
    }

    private fun saveTokens(refreshTokenSuccessResponse: RefreshTokenSuccessResponse) {
        Account().apply {
            accessToken = refreshTokenSuccessResponse.accessToken
            refreshToken = refreshTokenSuccessResponse.refreshToken
        }
    }

    private fun openHomeActivity() {
        startActivity(Intent(applicationContext, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        RefreshTokenActivity.instance.finish()
    }

    private fun openLoginActivity() {
        startActivity(Intent(applicationContext, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        RefreshTokenActivity.instance.finish()

    }

    private fun stopRefreshTokenService() {
        stopForeground(true)
        stopSelf()
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_2", "Refresh Token Channel", NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, "channel_2").setAutoCancel(true).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}