package com.routesme.taxi.MVVM.View.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.routesme.taxi.MVVM.View.fragment.PaymentFragment
import com.routesme.taxi.R

class PaymentActivity :AppCompatActivity(){

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