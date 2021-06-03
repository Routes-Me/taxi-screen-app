package com.routesme.vehicles.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.routesme.vehicles.R
import com.routesme.vehicles.view.fragment.PaymentFragment

class PaymentActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_activity)
        showFragment()
    }

    private fun showFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.payment_container, PaymentFragment()).commit()
        /*
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.payment_container, fragment)
            supportFragmentManager.fragments.forEach { if (it != fragment && it.isAdded) hide(it) }
        }.commit()
        */
    }
}