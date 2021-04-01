package com.routesme.taxi.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.routesme.taxi.App
import com.routesme.taxi.R
import com.routesme.taxi.service.RefreshTokenService

class RefreshTokenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refresh_token)
        instance = this
        Log.d("RefreshTokenTesting", "RefreshTokenActivity OnCreate()")
        Log.d("RefreshToken", "isRefreshActivityAlive ${App.instance.isRefreshActivityAlive}")

        startRefreshTokenService()
    }

    companion object{
        @get:Synchronized
        var instance = RefreshTokenActivity()
    }

    private fun startRefreshTokenService() {
        val intent = Intent(this, RefreshTokenService::class.java)
        ContextCompat.startForegroundService(this,intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.isRefreshActivityAlive = false
        Log.d("RefreshToken", "isRefreshActivityAlive ${App.instance.isRefreshActivityAlive}, onDestroy")
    }
}