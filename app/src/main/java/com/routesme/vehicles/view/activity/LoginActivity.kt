package com.routesme.vehicles.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.data.encryption.AesBase64Wrapper
import com.routesme.vehicles.data.model.Error
import com.routesme.vehicles.data.model.LoginResponse
import com.routesme.vehicles.data.model.SignInCredentials
import com.routesme.vehicles.helper.Helper
import com.routesme.vehicles.helper.Operations
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.viewmodel.LoginViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.exit_pattern_dialog.*
import kotlinx.android.synthetic.main.login_screen.*
import kotlinx.android.synthetic.main.technical_login_layout.*
import kotlinx.android.synthetic.main.technical_login_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private lateinit var telephonyManager: TelephonyManager
    private val READ_PHONE_STATE_REQUEST_CODE = 101
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val app = App.instance
    private lateinit var signInCredentials: SignInCredentials
    private lateinit var userName: String
    private lateinit var password: String
    private var dialog: AlertDialog? = null
    private val operations = Operations.instance
    var mWifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            checkWifiConnect()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        registerNetworkListener();
        initialize()
    }

    @SuppressLint("SetTextI18n")
    private fun initialize() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        dialogSetUp()
        openLoginLayout(app.isNewLogin)
        btnOpenLoginScreen.setOnClickListener { showLoginView() }
        openPatternBtn.setOnClickListener { openPatternDialog() }
        technical_login_screen.btn_learnMore.setOnClickListener { openLearnMoreScreen() }
        technical_login_screen.btn_next.setOnClickListener { buttonNextClick() }
        operations.enableNextButton(btn_next, true)
        editTextListener()
        appVersion_tv.text = "V${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        checkSimAvailability()
    }


    private fun editTextListener() {
        userName_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showErrorMessage(Error(Field.UserName.code, ""), false)
                operations.enableNextButton(btn_next, true)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        password_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showErrorMessage(Error(Field.Password.code, ""), false)
                operations.enableNextButton(btn_next, true)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun checkWifiConnect() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if(netInfo != null && netInfo.isConnected) {
            view.background = getDrawable(R.drawable.circular_active_bg)
            when (netInfo.type) {
                TYPE_WIFI -> textViewNetworkState.text = "WIFI"
                TYPE_MOBILE -> textViewNetworkState.text = "SIM"
            }
        }else{
            view.background = getDrawable(R.drawable.circular_deactive_bg)
            textViewNetworkState.text = "Not connected"
        }
    }

    private fun dialogSetUp() {
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
    }

    private fun buttonNextClick() {
        operations.enableNextButton(btn_next, false)
        saveAuthCredentials()
        if (userNameValid() && passwordValid()){
            signIn()
        }
    }

    private fun signIn() {
        dialog?.show()
        val signInCredentials = SignInCredentials(userName, password)
        val loginViewModel: LoginViewModel by viewModels()
        loginViewModel.signIn(signInCredentials, this).observe(this, Observer<LoginResponse> {
            dialog?.dismiss()
            operations.enableNextButton(btn_next, true)
            if (it != null) {
                if (it.isSuccess) {
                    val token = it.token ?: run {
                        operations.displayAlertDialog(this, getString(R.string.login_error_title), getString(R.string.token_is_null_value))
                        return@Observer
                    }
                    Account().apply { accessToken = token }
                    openNextActivity()

                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.login_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.login_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.login_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun encrypt(str: String) = AesBase64Wrapper().getEncryptedString(str)

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            if (error.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                operations.displayAlertDialog(this, getString(R.string.login_error_title), "Username or password incorrect !")
            } else {
                operations.displayAlertDialog(this, getString(R.string.login_error_title), "Error message: ${error.detail}")
            }
        }
    }

    private fun openNextActivity() {
        val isRegistered: Boolean = !App.instance.account.vehicle.deviceId.isNullOrEmpty()
        val intent = if (isRegistered) Intent(this, ModelPresenter::class.java) else Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun userNameValid(): Boolean {
        return if (userName.isNotEmpty()) true
        else {
            showErrorMessage(Error(Field.UserName.code, "User Name Required"), true)
            false
        }
    }

    private fun passwordValid(): Boolean {
        return when {
            password.isEmpty() -> {
                showErrorMessage(Error(Field.Password.code, "Password Required"), true); false
            }
            else -> true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showErrorMessage(error: Error, show: Boolean) {
        var editText: EditText? = null
        var textView: TextView? = null
        when (error.code) {
            1 -> {
                editText = userName_et
                textView = userName_error_tv
            }
            2 -> {
                editText = password_et
                textView = password_error_tv
            }
        }
        if (show) {
            editText?.setBackgroundResource(R.drawable.red_border)
            textView?.apply { text = "* ${error.detail}"; visibility = View.VISIBLE }
            return
        } else {
            editText?.setBackgroundResource(R.drawable.grey_border_edit_text)
            textView?.visibility = View.INVISIBLE
        }
    }

    private fun openPatternDialog() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            readPatternFromTechnicalSupport()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun readPatternFromTechnicalSupport() {
        val patternPassword = Helper.getConfigValue("exitPassword", R.raw.config)
        val exitPatternDialog = Dialog(this)
        exitPatternDialog.setContentView(R.layout.exit_pattern_dialog)
        exitPatternDialog.show()
        exitPatternDialog.setCancelable(false)
        val patternExitApp = exitPatternDialog.admin_verification_pattern
        patternExitApp.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {}
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                val finalPattern = PatternLockUtils.patternToString(patternExitApp, pattern)
                if (finalPattern == patternPassword) {
                    openSettings()
                } else {
                    patternExitApp.clearPattern()
                    exitPatternDialog.dismiss()
                }
            }

            override fun onCleared() {}
        })
    }

    private fun openSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        } else {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
        finish()
        exitProcess(0)
    }

    private fun showLoginView() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            openLoginLayout(true)
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun openLoginLayout(show: Boolean) {
        if (show) {
            btnOpenLoginScreen.visibility = View.GONE
            loginLayout.visibility = View.VISIBLE
            showSavedCredentials()
        } else {
            btnOpenLoginScreen.visibility = View.VISIBLE
            loginLayout.visibility = View.GONE
        }
    }

    private fun showSavedCredentials() {
        if (app.signInCredentials != null) {
            signInCredentials = app.signInCredentials!!
            userName = signInCredentials.userName
            password = signInCredentials.password
            if (userName.isNotEmpty()) {
                userName_et.setText(userName)
            }
            if (password.isNotEmpty()) {
                password_et.setText(password)
            }
        }
    }

    private fun openLearnMoreScreen() {
        saveAuthCredentials()
        startActivity(Intent(this, LearnMoreActivity::class.java))
    }

    private fun saveAuthCredentials() {
        userName = userName_et.text.toString().trim()
        password = password_et.text.toString().trim()
        signInCredentials = SignInCredentials(userName, password)
        app.signInCredentials = signInCredentials
        app.isNewLogin = true
    }

    private fun checkSimAvailability() {
        when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_READY -> {simStatus_tv.text = SimStates.READY.value; getSimSerialNumber() }
            TelephonyManager.SIM_STATE_ABSENT -> {simStatus_tv.text = SimStates.ABSENT.value; retryGetSimAvailability()}
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> simStatus_tv.text = SimStates.NETWORK_LOCKED.value
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> simStatus_tv.text = SimStates.PIN_REQUIRED.value
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> simStatus_tv.text = SimStates.PUK_REQUIRED.value
            // TelephonyManager.SIM_STATE_UNKNOWN -> SimStates.UNKNOWN.value
            else -> simStatus_tv.text = SimStates.UNKNOWN.value
        }
    }

    @SuppressLint("HardwareIds")
    private fun getSimSerialNumber() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
            return
        } else {
            simSerialNumber_tv.text = telephonyManager.simSerialNumber
        }
    }

    @SuppressLint("HardwareIds")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_PHONE_STATE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSimSerialNumber()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun retryGetSimAvailability() {
        Timer("RetryGetSimState", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(10)) {
                Log.d("SimState", "RetryGetSimState")
                GlobalScope.launch(Dispatchers.Main) {
                    checkSimAvailability()
                }
            }
        }
    }

    private fun registerNetworkListener() {
        registerReceiver(mWifiReceiver , IntentFilter(CONNECTIVITY_ACTION))
    }

    protected fun unRegisterNetworkListener() {
        try {
            unregisterReceiver(mWifiReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterNetworkListener();
    }

}

enum class Field(val code: Int) { UserName(1), Password(2) }
enum class SimStates(val value: String) {READY("SIM READY"), ABSENT("SIM ABSENT"), NETWORK_LOCKED("SIM NETWORK LOCKED"), PIN_REQUIRED("SIM PIN REQUIRED"), PUK_REQUIRED("SIM PUK REQUIRED"), UNKNOWN("SIM UNKNOWN")}