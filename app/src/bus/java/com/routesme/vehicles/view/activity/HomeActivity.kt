package com.routesme.vehicles.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sdkdemo.LibBarCode
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.*
import com.routesme.vehicles.service.BusValidatorServiceE60Q
import com.routesme.vehicles.service.BusValidatorServiceP18
import com.routesme.vehicles.view.fragment.ApprovedPaymentFragment
import com.routesme.vehicles.view.fragment.MainFragment
import com.routesme.vehicles.view.fragment.MultiTicketsScanFirstFragment
import com.routesme.vehicles.view.fragment.RejectedPaymentFragment
import kotlinx.android.synthetic.bus.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity(), IModeChanging {
    private val READ_PHONE_STATE_REQUEST_CODE = 303
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val approvedScreenShowingTime = TimeUnit.SECONDS.toMillis(3)
    private val rejectedScreenShowingTime = TimeUnit.SECONDS.toMillis(6)
    private val transactionTone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100000) //h volume
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

        startBusValidatorService()
    }

    private fun startBusValidatorService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
            return
        } else {
            val deviceModel = Build.MODEL
            if (deviceModel == BusValidatorModels.p18q_dual.toString()) startBusValidatorServiceP18() else startBusValidatorServiceE60Q()
        }
    }

    @SuppressLint("HardwareIds")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_PHONE_STATE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
                    return
                }
                startBusValidatorService()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startBusValidatorServiceP18() {
        ContextCompat.startForegroundService(this, Intent(this, BusValidatorServiceP18::class.java))
    }

    private fun startBusValidatorServiceE60Q() {
         val charset = Charsets.UTF_8

        //ContextCompat.startForegroundService(this, Intent(this, BusValidatorServiceE60Q::class.java))
        LibBarCode.getInstance().barCodeRead { barcode, len ->
            // val contentString = hexStringToString(bytesToHex(barcode))
            val contentString = barcode.toString(charset)
            val userPaymentQrCodeData: UserPaymentQrcodeData = Gson().fromJson(contentString, UserPaymentQrcodeData::class.java)
            Log.d("BusValidator", "userPaymentQrCodeData: $userPaymentQrCodeData")


            0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFragments()
        EventBus.getDefault().unregister(this)
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
    }

    private fun openPattern() {
        clickTimes++
        if (pressedTime + 500 > System.currentTimeMillis() && clickTimes >= 10) {
            helper.showAdminVerificationDialog()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

/*
    private fun startBusPaymentService() {
        ContextCompat.startForegroundService(this,Intent(this, BusPaymentService::class.java))
    }
    */

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
        transactionTone.startTone(ToneGenerator.TONE_PROP_BEEP, approvedScreenShowingTime.toInt())// sound for approved
    }

    private fun executeRejectedProcess(bundle: Bundle) {
        showFragment(rejectedPaymentFragment.apply { arguments = bundle })
        dismissFragment(rejectedScreenShowingTime)
        transactionTone.startTone(ToneGenerator.TONE_CDMA_ANSWER, rejectedScreenShowingTime.toInt()) // sound for rejected
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

enum class BusValidatorModels{p18q_dual, kt11_64}