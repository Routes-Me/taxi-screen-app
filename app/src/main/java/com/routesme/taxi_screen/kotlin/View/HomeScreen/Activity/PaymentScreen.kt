package com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.routesme.taxi_screen.kotlin.Model.PaymentData
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.payment_screen.*

class PaymentScreen : AppCompatActivity() {

    private  var paymentData = PaymentData()

    companion object{
        @get:Synchronized
        val instance = PaymentScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_screen)

        if (intent.hasExtra("paymentData")){
            paymentData = intent.getSerializableExtra("paymentData") as PaymentData
            amountTv.text = paymentData.paymentAmount.toString()
            Toast.makeText(this,"Amount: ${paymentData.paymentAmount} KD", Toast.LENGTH_LONG).show()
        }
    }
}
