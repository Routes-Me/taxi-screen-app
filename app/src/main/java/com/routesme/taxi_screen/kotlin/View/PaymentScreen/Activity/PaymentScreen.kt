package com.routesme.taxi_screen.kotlin.View.PaymentScreen.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.gson.Gson
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.Operations
import com.routesme.taxi_screen.kotlin.Model.PaymentMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentProgressMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentStatus
import com.routesme.taxi_screen.kotlin.View.PaymentScreen.Fragment.PaymentFragment
import com.routesme.taxiscreen.R
import org.json.JSONTokener

class PaymentScreen : AppCompatActivity() {

    private lateinit var paymentMessage:PaymentMessage
    private val operations = Operations.instance
    val STATUS = "STATUS"
    val PAYMENT_MESSAGE = "PAYMENT_MESSAGE"
    private lateinit var timeoutHandler: Handler
    private lateinit var timeoutRunnable: Runnable

    companion object{
        @get:Synchronized
        val instance = PaymentScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_screen)

        if (intent.hasExtra(PAYMENT_MESSAGE)){
            paymentMessage = intent.getSerializableExtra(PAYMENT_MESSAGE) as PaymentMessage
            if (paymentMessage.amount != null){

                val confirmationPaymentMessage = PaymentProgressMessage(paymentMessage.identifier, PaymentStatus.Initiate.text)
                val jsonConfirmationPaymentMessage = Gson().toJson(confirmationPaymentMessage)
                operations.publish(jsonConfirmationPaymentMessage)

                val bundle = Bundle().apply { putDouble(PaymentFragment.instance.PAYMENT_AMOUNT, paymentMessage.amount!!) }
                val paymentFragment = PaymentFragment.instance.apply { arguments = bundle }
                showFragment(paymentFragment)
                setupTimeoutHandler()
                timeoutHandler.postDelayed(timeoutRunnable, 1 * 60 * 1000)

            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.payment_container, fragment).commit()
        /*
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.payment_container, fragment)
            supportFragmentManager.fragments.forEach { if (it != fragment && it.isAdded) hide(it) }
        }.commit()
        */
    }

    private fun setupTimeoutHandler() {
        timeoutRunnable = Runnable {
            setResult(Activity.RESULT_OK,Intent().putExtra(STATUS, PaymentStatus.Timeout.text))
            finish()
        }
        timeoutHandler = Handler()
    }

    private fun stopTimeoutTimer() {
            timeoutHandler.removeCallbacks(timeoutRunnable)
    }


    private val paymentCancellationMessageListener = object : MessageListener() {
        override fun onFound(message: Message) {
            if (message.content != null && message.content.isNotEmpty()){
                val json = JSONTokener(String(message.content)).nextValue()

                //if (json is PaymentProgressMessage) {
                    val paymentCancellationMessage = Gson().fromJson(String(message.content), PaymentProgressMessage::class.java)
                    if (paymentCancellationMessage.identifier.equals(paymentMessage.identifier) && paymentCancellationMessage.status.equals(PaymentStatus.Cancel.text)) {

                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
               // }
            }
        }
        override fun onLost(message: Message?) {}
    }

    override fun onStart() {
        Nearby.getMessagesClient(this).subscribe(paymentCancellationMessageListener, App.nearbySubscribeOptions)
        super.onStart()
    }

    override fun onStop() {
        Nearby.getMessagesClient(this).unsubscribe(paymentCancellationMessageListener)
        stopTimeoutTimer()
        super.onStop()
    }

    override fun onBackPressed() {}
}