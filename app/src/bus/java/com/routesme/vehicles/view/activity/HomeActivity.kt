package com.routesme.vehicles.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.routesme.vehicles.R
import com.routesme.vehicles.data.PaymentRejectCauses
import com.routesme.vehicles.data.ReadQrCode
import com.routesme.vehicles.data.model.IModeChanging
import com.routesme.vehicles.helper.*
import com.routesme.vehicles.service.BusPaymentService
import com.routesme.vehicles.service.BusValidatorService
import com.routesme.vehicles.view.fragment.ApprovedPaymentFragment
import com.routesme.vehicles.view.fragment.MainFragment
import com.routesme.vehicles.view.fragment.RejectedPaymentFragment
import kotlinx.android.synthetic.bus.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity(), IModeChanging {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val helper = HomeScreenHelper(this)
    private lateinit var mainFragment: MainFragment
    private lateinit var approvedPaymentFragment: ApprovedPaymentFragment
    private lateinit var rejectedPaymentFragment: RejectedPaymentFragment
    private var isDismissFragmentTimerAlive = false
    private var dismissFragmentTimer: Timer? = null
    private val approvedScreenShowingTime = TimeUnit.MILLISECONDS.toMillis(1500)
    private val rejectedScreenShowingTime = TimeUnit.SECONDS.toMillis(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DisplayManager.instance.registerActivity(this)
        if (DisplayManager.instance.isAnteMeridiem()) {
           DisplayManager.instance.currentMode = Mode.Light
            setTheme(R.style.FullScreen_Light_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 80)
        } else {
            DisplayManager.instance.currentMode = Mode.Dark
            setTheme(R.style.FullScreen_Dark_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 20)
        }
        setContentView(R.layout.activity_home)

        openPatternBtn.setOnClickListener { openPattern() }
        EventBus.getDefault().register(this)

        mainFragment = MainFragment()
        approvedPaymentFragment = ApprovedPaymentFragment()
        rejectedPaymentFragment = RejectedPaymentFragment()

        showFragment(mainFragment)
       // addAllFragments()
       // hideFragments()
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
        Log.d("BusValidator","Read new qr code: $readQrCode")
         if (isDismissFragmentTimerAlive) {
             Log.d("BusValidator","There's dismiss timer already running")
             dismissFragmentTimer?.apply {
                 cancel()
                 purge()
             }
             hideFragments()
             isDismissFragmentTimerAlive = false
         }


      if (readQrCode.isApproved) {
          showFragment(approvedPaymentFragment)
          dismissFragment(approvedScreenShowingTime)
      }
      else {
          showFragment(rejectedPaymentFragment)
          dismissFragment(rejectedScreenShowingTime)
      }
    }
    
    private fun showFragment(fragment: Fragment) {
        //Log.d("BusValidator","Show Fragment: $fragment")
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.fragment_container, fragment)
            //Log.d("BusValidator","No of current fragments: ${supportFragmentManager.fragments.size}")
        }.commitAllowingStateLoss()
    }

    private fun dismissFragment(screenShowingTime: Long) {
        dismissFragmentTimer = Timer("dismissFragmentTimer", true).apply {
            Log.d("BusValidator","Dismiss Fragment Timer, Calling, Timer: $this")
            isDismissFragmentTimerAlive = true
            schedule(screenShowingTime) {
                Log.d("BusValidator","Dismiss Fragment Timer, Executing")
                hideFragments()
                showFragment(mainFragment)
                isDismissFragmentTimerAlive = false
                this@apply.apply{
                    cancel()
                    purge()
                }
            }
        }
    }
    private fun hideFragments(){
        Log.d("BusValidator","Hide Fragment")
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { hide(it) }
        }.commitAllowingStateLoss()
    }

    override fun onModeChange() {
        removeFragments()
        recreate()
    }

    private fun removeFragments() {
        supportFragmentManager.beginTransaction().apply {
            remove(approvedPaymentFragment)
            remove(rejectedPaymentFragment)
        }.commitAllowingStateLoss()
    }
}