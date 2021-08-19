package com.routesme.vehicles.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.routesme.vehicles.App
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.TerminalCredentials
import com.routesme.vehicles.data.model.TerminalResponse
import com.routesme.vehicles.helper.Operations
import com.routesme.vehicles.viewmodel.TerminalViewModel
import dmax.dialog.SpotsDialog
import java.io.IOException

class ModelPresenter : AppCompatActivity() {
    private var dialog: AlertDialog? = null
    private val operations = Operations.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)

        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
        startNextActivity()
    }

    private fun startNextActivity() {
        val isDeviceRegistered: Boolean = !App.instance.deviceInformation.deviceId.isNullOrEmpty()
        val isTerminalRegistered: Boolean = !App.instance.deviceInformation.terminalId.isNullOrBlank()
        if (isDeviceRegistered) {
            if (isTerminalRegistered) openActivity(HomeActivity()) else registerDeviceAsTerminal()
        } else {
            openActivity(LoginActivity())
        }
    }

    private fun registerDeviceAsTerminal() {
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("FCM-Token", "Fetching FCM registration token failed, Exception: ${task.exception}")
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    Log.d("FCM-Token","Token: $token")
                    registerTerminal(token)
                })
    }

    private fun registerTerminal(token: String) {
        dialog?.show()
        val deviceId = App.instance.deviceInformation.deviceId
        val terminalCredentials = TerminalCredentials(token,deviceId)
        val terminalViewModel: TerminalViewModel by viewModels()
        terminalViewModel.register(terminalCredentials, this).observe(this, Observer<TerminalResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {
                    val terminalId = it.terminalId ?: run {
                        operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.terminal_id_is_null_value))
                        return@Observer
                    }
                    App.instance.deviceInformation.terminalId = terminalId
                    Log.d("TestTerminal","Terminal Credentials: $terminalCredentials, Terminal Id: ${it.terminalId}")
                    openActivity(HomeActivity())
                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors ->  operations.displayAlertDialog(this, getString(R.string.registration_error_title), "Error message: ${errors.first().detail}") }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun openActivity(activity: Activity) {
        startActivity(Intent(this, activity::class.java))
        finish()
    }
}