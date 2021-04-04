package com.routesme.vehicles.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.HomeScreenHelper
import kotlinx.android.synthetic.bus.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val helper = HomeScreenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        openPatternBtn.setOnClickListener { openPattern() }
    }

    private fun openPattern() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            helper.showAdminVerificationDialog()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }
}
