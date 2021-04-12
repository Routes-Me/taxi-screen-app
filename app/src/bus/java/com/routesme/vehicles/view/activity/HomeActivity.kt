package com.routesme.vehicles.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.HomeScreenHelper
import com.routesme.vehicles.service.BusPaymentService
import com.routesme.vehicles.service.BusValidatorService
import com.routesme.vehicles.service.PaymentOperation
import kotlinx.android.synthetic.bus.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val helper = HomeScreenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        openPatternBtn.setOnClickListener { openPattern() }

        EventBus.getDefault().register(this)

        startBusValidatorService()
        startBusPaymentService()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
        val intent = Intent(this, BusValidatorService::class.java)
        ContextCompat.startForegroundService(this,intent)
    }

    private fun startBusPaymentService() {
        val intent = Intent(this, BusPaymentService::class.java)
        ContextCompat.startForegroundService(this,intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(paymentOperation: PaymentOperation){
        readingStatus_tv.text = "Reading successfully \n Content: ${paymentOperation.qrCodeContent}"
    }
}
