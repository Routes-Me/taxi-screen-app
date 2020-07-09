package com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.gson.Gson
import com.routesme.taxi_screen.java.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.DisplayManager
import com.routesme.taxi_screen.kotlin.Class.HomeScreenFunctions
import com.routesme.taxi_screen.kotlin.Class.Operations
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.kotlin.Model.IModeChanging
import com.routesme.taxi_screen.kotlin.Model.PaymentMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentProgressMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentStatus
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.SideMenuFragment
import com.routesme.taxi_screen.kotlin.View.PaymentScreen.Activity.PaymentScreen
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.home_screen.*
import org.json.JSONTokener

class HomeScreen : PermissionsActivity() ,IModeChanging{

    private val homeScreenFunctions = HomeScreenFunctions(this)
    private var isHotspotOn = false
    private var locationTrackingService: LocationTrackingService? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val displayManager = DisplayManager.instance
    private val PAYMENT_STATUS_REQUEST_CODE = 0x1234
    private lateinit var paymentMessage: PaymentMessage
    private val operations = Operations.instance

    companion object{
        @get:Synchronized
        val instance = HomeScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.currentActivity = this
        displayManager.registerActivity(this)
        if (displayManager.isAnteMeridiem()){setTheme(R.style.FullScreen_Light_Mode)}else{setTheme(R.style.FullScreen_Dark_Mode)}
        setContentView(R.layout.home_screen)
        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE)
        homeScreenFunctions.firebaseAnalytics_Crashlytics(sharedPreferences.getString("tabletSerialNo", null))
        openPattern.setOnClickListener {openPatternClick()}
    }

    override fun onDestroy() {
        displayManager.unregisterActivity(this)
        super.onDestroy()
    }

    override fun onResume() {
        homeScreenFunctions.hideNavigationBar()
        homeScreenFunctions.requestRuntimePermissions()
        turnOnHotspot()
        //startLocationTrackingService()
        showFragments()

        Nearby.getMessagesClient(this).subscribe(paymentMessageListener)
        super.onResume()
    }

    private fun startLocationTrackingService() {
        locationTrackingService = LocationTrackingService()
    }

    override fun onPause() {
       // if (locationTrackingService != null) locationTrackingService!!.stopLocationTrackingService()
        Nearby.getMessagesClient(this).unsubscribe(paymentMessageListener)
        super.onPause()
    }

    private fun showFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, ContentFragment.instance).commit()
        supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, SideMenuFragment.instance).commit()
    }

    override fun onPermissionsOkay() {}

    private fun turnOnHotspot() {
        if (!isHotspotOn) {
            sendImplicitBroadcast(Intent(getString(R.string.intent_action_turnon)))
            isHotspotOn = true
        }
    }

    private fun sendImplicitBroadcast(i: Intent) {
        val matches = packageManager.queryBroadcastReceivers(i, 0)
        for (resolveInfo in matches) {
            Intent(i).component = ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name)
            sendBroadcast(Intent(i))
        }
    }
    private fun openPatternClick() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10){
            homeScreenFunctions.showAdminVerificationDialog(sharedPreferences.getString("tabletPassword", null).toString())
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    override fun onModeChange() {
        recreate()
    }

    private val paymentMessageListener = object : MessageListener() {
        override fun onFound(message: Message) {
            if (message.content != null && message.content.isNotEmpty()){


                val json = JSONTokener(String(message.content)).nextValue()
               // if (json is PaymentMessage) {
                   // Toast.makeText(this@HomeScreen,"${message.content}",Toast.LENGTH_LONG).show()
                    paymentMessage = Gson().fromJson(String(message.content), PaymentMessage::class.java)
                    if (paymentMessage.status == PaymentStatus.Initiate.text) {
                        val intent = Intent(this@HomeScreen,PaymentScreen::class.java).putExtra(PaymentScreen.instance.PAYMENT_MESSAGE,paymentMessage)
                        startActivityForResult(intent,PAYMENT_STATUS_REQUEST_CODE)
                    }
                //}
            }
        }
        override fun onLost(message: Message?) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAYMENT_STATUS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Cancel Status...
                Toast.makeText(this, "Status: Canceled", Toast.LENGTH_LONG).show()

                //Publish confirmation message for cancellation message
                val confirmationCancellationMessage = PaymentProgressMessage(paymentMessage.identifier, PaymentStatus.Cancel.text)
                val jsonConfirmationCancellationMessage = Gson().toJson(confirmationCancellationMessage)
                operations.publish(jsonConfirmationCancellationMessage)


            } else if(resultCode == Activity.RESULT_OK && data != null && data.hasExtra(PaymentScreen.instance.STATUS)) {
                val status= data.getStringExtra(PaymentScreen.instance.STATUS)
                Toast.makeText(this, "Status: $status", Toast.LENGTH_LONG).show()
                val progressMessage = PaymentProgressMessage(paymentMessage.identifier,status)
                val jsonProgressMessage = Gson().toJson(progressMessage)
                operations.publish(jsonProgressMessage)
            }
        }
    }
}