package com.routesme.taxi.MVVM.View.activity

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.routesme.taxi.MVVM.Model.Authorization
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.R

class ModelPresenter : AppCompatActivity() {
    private var bundle:Bundle?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)
        startActivity()
    }

    private fun startActivity() {

        val isRegistered: Boolean = !App.instance.account.vehicle.deviceId.isNullOrEmpty()
        Log.d("IsRegistered","${isRegistered}")
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