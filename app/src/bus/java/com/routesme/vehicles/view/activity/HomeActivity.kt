package com.routesme.vehicles.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.routesme.vehicles.R
import com.routesme.vehicles.data.ReadQrCode
import com.routesme.vehicles.helper.HomeScreenHelper
import com.routesme.vehicles.service.BusPaymentService
import com.routesme.vehicles.service.BusValidatorService
import com.routesme.vehicles.view.fragment.ApprovedPaymentFragment
import com.routesme.vehicles.view.fragment.RejectedPaymentFragment
import kotlinx.android.synthetic.bus.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val helper = HomeScreenHelper(this)
    private lateinit var approvedPaymentFragment: ApprovedPaymentFragment
    private lateinit var rejectedPaymentFragment: RejectedPaymentFragment
    private var isDismissFragmentTimerAlive = false
    private lateinit var dismissFragmentTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        openPatternBtn.setOnClickListener { openPattern() }

        dismissFragmentTimer = Timer("dismissFragmentTimer", true)

        EventBus.getDefault().register(this)

        approvedPaymentFragment = ApprovedPaymentFragment.instance
        rejectedPaymentFragment = RejectedPaymentFragment.instance

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
    fun onEvent(readQrCode: ReadQrCode){
      if (readQrCode.isApproved) replaceFragment(approvedPaymentFragment)
      else {
          replaceFragment(rejectedPaymentFragment)
          readQrCode.rejectCauses?.let { rejectedPaymentFragment.displayRejectCause(it) }
      }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.fragment_container, fragment)
            if (isDismissFragmentTimerAlive) dismissFragmentTimer.cancel()
            dismissFragment()
            supportFragmentManager.fragments.forEach { if (it != fragment && it.isAdded) hide(it) }
        }.commitAllowingStateLoss()
    }

    private fun dismissFragment() {
        dismissFragmentTimer.apply {
            isDismissFragmentTimerAlive = true
            schedule(TimeUnit.MILLISECONDS.toMillis(1500)) {
                Log.d("BusValidator","Dismiss Fragment")
                supportFragmentManager.beginTransaction().apply {
                    supportFragmentManager.fragments.forEach { if (it.isAdded) hide(it) }
                }.commitAllowingStateLoss()
                isDismissFragmentTimerAlive = false
            }
        }
    }
}