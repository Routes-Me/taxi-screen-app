package com.routesme.taxi.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.routesme.taxi.App
import com.routesme.taxi.R

class ModelPresenter : AppCompatActivity() {
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)
        Log.d("RefreshToken", "Model Presenter Activity")
        startActivity()
    }

    private fun startActivity() {
        Log.d("RefreshToken", "Model Presenter Activity..Check witch the next activity to open")
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

    override fun onDestroy() {
        super.onDestroy()
    }
}