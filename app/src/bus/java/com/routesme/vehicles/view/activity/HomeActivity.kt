package com.routesme.vehicles.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.HomeScreenHelper
import com.routesme.vehicles.service.BusValidatorService
import kotlinx.android.synthetic.bus.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val helper = HomeScreenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val intent = Intent(this, BusValidatorService::class.java)
        ContextCompat.startForegroundService(this,intent)

        openPatternBtn.setOnClickListener { openPattern() }

        startBusValidatorService()
    }

    private fun openPattern() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            helper.showAdminVerificationDialog()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun startBusValidatorService() {

    }
}
