package com.routesme.taxi_screen.kotlin.View.PaymentScreen.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.routesme.taxi_screen.kotlin.Model.PaymentMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentProgressMessage
import com.routesme.taxi_screen.kotlin.Model.PaymentStatus
import com.routesme.taxi_screen.kotlin.View.PaymentScreen.Fragment.PaymentFragment
import com.routesme.taxiscreen.R

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentMessage:PaymentMessage
    val PAYMENT_MESSAGE = "PAYMENT_MESSAGE"

    companion object{
        @get:Synchronized
        val instance = PaymentActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_screen)

        if (intent.hasExtra(PAYMENT_MESSAGE)){
            paymentMessage = intent.getSerializableExtra(PAYMENT_MESSAGE) as PaymentMessage
            if (paymentMessage.amount != null){

                val confirmationPaymentMessage = PaymentProgressMessage(paymentMessage.identifier, PaymentStatus.Initiate.text)
                val jsonConfirmationPaymentMessage = Gson().toJson(confirmationPaymentMessage)
              //  operations.publish(jsonConfirmationPaymentMessage)

                val bundle = Bundle().apply { putDouble(PaymentFragment.instance.PAYMENT_AMOUNT, paymentMessage.amount!!) }
                val paymentFragment = PaymentFragment.instance.apply { arguments = bundle }
                showFragment(paymentFragment)
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

    override fun onBackPressed() {}
}