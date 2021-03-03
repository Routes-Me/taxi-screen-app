package com.routesme.taxi.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.routesme.taxi.App
import com.routesme.taxi.R

class RefreshTokenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refresh_token)
      //  App.instance.isRefreshActivityAlive = true
        Log.d("RefreshToken", "isRefreshActivityAlive ${App.instance.isRefreshActivityAlive}")
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.isRefreshActivityAlive = false
        Log.d("RefreshToken", "isRefreshActivityAlive ${App.instance.isRefreshActivityAlive}")
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }
}
