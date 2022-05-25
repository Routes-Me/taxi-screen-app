package com.routesme.vehicles.view.activity

//import com.routesme.vehicles.service.BusPaymentService

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.IModeChanging
import com.routesme.vehicles.data.model.PaymentResult
import com.routesme.vehicles.helper.*
import com.routesme.vehicles.service.BusValidatorService
import com.routesme.vehicles.view.fragment.ApprovedPaymentFragment
import com.routesme.vehicles.view.fragment.MainFragment
import com.routesme.vehicles.view.fragment.MultiTicketsScanFirstFragment
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
    private val approvedScreenShowingTime = TimeUnit.SECONDS.toMillis(3)
    private val rejectedScreenShowingTime = TimeUnit.SECONDS.toMillis(6)
    private val transactionTone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 1000000)
    private lateinit var helper: HomeScreenHelper
    private lateinit var mainFragment: MainFragment
    private lateinit var approvedPaymentFragment: ApprovedPaymentFragment
    private lateinit var rejectedPaymentFragment: RejectedPaymentFragment
    private lateinit var multiTicketsScanFirstFragment: MultiTicketsScanFirstFragment
    private var isDismissFragmentTimerAlive = false
    private var dismissFragmentTimer: Timer? = null

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

        helper = HomeScreenHelper(this)
        openPatternBtn.setOnClickListener { openPattern() }
        EventBus.getDefault().register(this)

        mainFragment = MainFragment()
        approvedPaymentFragment = ApprovedPaymentFragment()
        rejectedPaymentFragment = RejectedPaymentFragment()
        multiTicketsScanFirstFragment = MultiTicketsScanFirstFragment()

      // showFragment(multiTicketsScanFirstFragment)
        showFragment(mainFragment)
       // startBusValidatorService()
        //startBusPaymentService()


        testing()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFragments()
        EventBus.getDefault().unregister(this)
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
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
        ContextCompat.startForegroundService(this, Intent(this, BusValidatorService::class.java))
    }

/*
    private fun startBusPaymentService() {
        ContextCompat.startForegroundService(this,Intent(this, BusPaymentService::class.java))
    }
    */

    private fun testing(){
        ApprovedScreen_btn.setOnClickListener {
            val paymentResult = PaymentResult("7453597dj", "Ahmed", true, null)
            val bundle: Bundle  = Bundle().apply { putSerializable("PaymentResult", paymentResult) }
            executeApprovedProcess(bundle)
        }

        RejectedScreen_btn.setOnClickListener {
            val paymentResult = PaymentResult("28246njjsh", "Ali", false, "No balance")
            val bundle: Bundle  = Bundle().apply { putSerializable("PaymentResult", paymentResult) }
            executeRejectedProcess(bundle)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(paymentResult: PaymentResult){
        //Log.d("BusValidator","Read new qr code: $readQrCode")
         if (isDismissFragmentTimerAlive) {
             //Log.d("BusValidator","There's dismiss timer already running")
             dismissFragmentTimer?.apply {
                 cancel()
                 purge()
             }
             hideFragments()
             isDismissFragmentTimerAlive = false
         }

        val bundle: Bundle  = Bundle().apply { putSerializable("PaymentResult", paymentResult) }
        if (paymentResult.isApproved) { executeApprovedProcess(bundle) } else { executeRejectedProcess(bundle) }
    }

    private fun executeApprovedProcess(bundle: Bundle) {
        showFragment(approvedPaymentFragment.apply { arguments = bundle })
        dismissFragment(approvedScreenShowingTime)
        transactionTone.startTone(ToneGenerator.TONE_PROP_BEEP, approvedScreenShowingTime.toInt())
    }

    private fun executeRejectedProcess(bundle: Bundle) {
        showFragment(rejectedPaymentFragment.apply { arguments = bundle })
        dismissFragment(rejectedScreenShowingTime)
        transactionTone.startTone(ToneGenerator.TONE_CDMA_ANSWER, rejectedScreenShowingTime.toInt())
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.fragment_container, fragment)
        }.commitAllowingStateLoss()
    }

    private fun dismissFragment(screenShowingTime: Long) {
        dismissFragmentTimer = Timer("dismissFragmentTimer", true).apply {
            //Log.d("BusValidator","Dismiss Fragment Timer, Calling, Timer: $this")
            isDismissFragmentTimerAlive = true
            schedule(screenShowingTime) {
                //Log.d("BusValidator","Dismiss Fragment Timer, Executing")
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
        //Log.d("BusValidator","Hide Fragment")
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { hide(it) }
        }.commitAllowingStateLoss()
    }
    private fun removeFragments() {
        supportFragmentManager.beginTransaction().apply {
            mainFragment.let { if (it.isAdded) remove(it) }
            approvedPaymentFragment.let { if (it.isAdded) remove(it) }
            rejectedPaymentFragment.let { if (it.isAdded) remove(it) }
            multiTicketsScanFirstFragment.let { if (it.isAdded) remove(it) }
        }.commitAllowingStateLoss()
    }

    override fun onModeChange() {
        recreate()
    }
}