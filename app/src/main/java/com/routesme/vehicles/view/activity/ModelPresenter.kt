package com.routesme.vehicles.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.routesme.vehicles.App
import com.routesme.vehicles.R

class ModelPresenter : AppCompatActivity() {
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)
        startActivity()
    }

    private fun startActivity() {
        val isRegistered: Boolean = !App.instance.account.vehicle.deviceId.isNullOrEmpty()
        if (isRegistered) {
            openActivity(HomeActivity())
        } else {
            openActivity(LoginActivity())
        }
    }

    private fun openActivity(activity: Activity) {
        startActivity(Intent(this, activity::class.java))
        finish()
    }

}