package com.routesme.taxi_screen.kotlin.View.LoginScreens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.routesme.taxi_screen.java.View.Login.TaxiInformationScreen
import com.routesme.taxi_screen.kotlin.Class.AesBase64Wrapper
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.Operations
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials
import com.routesme.taxi_screen.kotlin.Model.AuthCredentialsError
import com.routesme.taxi_screen.kotlin.Model.Token
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.exit_pattern_dialog.*
import kotlinx.android.synthetic.main.login_screen.*
import kotlinx.android.synthetic.main.technical_login_layout.*
import kotlinx.android.synthetic.main.technical_login_layout.view.*
import kotlin.system.exitProcess

class LoginScreen : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val app = App.instance
    private lateinit var authCredentials: AuthCredentials
    private lateinit var userName: String
    private lateinit var password: String
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        initialize()
    }

    private fun initialize() {
        dialogSetUp()
        openLoginLayout(app!!.isNewLogin)
        btnOpenLoginScreen.setOnClickListener { showLoginView() }
        openPattern.setOnClickListener { openPatternDialog() }
        technical_login_screen.btn_learnMore.setOnClickListener { openLearnMoreScreen() }
        technical_login_screen.btn_next.setOnClickListener { buttonNextClick() }
        editTextListener()
    }

    private fun editTextListener() {
        userName_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showErrorMessage(1, "", false)
                Operations().enableNextButton(btn_next, true)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        password_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showErrorMessage(2, "", false)
                Operations().enableNextButton(btn_next, true)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun dialogSetUp() {
        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.SpotsDialogStyle)
                .setCancelable(false).build()


    }

    private fun buttonNextClick() {
        Operations().enableNextButton(btn_next, false)
        saveAuthCredentials()
        login()
    }

    private fun login() {
        if (userNameValid() && passwordValid()) {
            dialog.show()
            val aesBase64Wrapper = AesBase64Wrapper(this)
            val authCredentials = AuthCredentials(aesBase64Wrapper.encryptAndEncode(userName), aesBase64Wrapper.encryptAndEncode(password))
            val model: RoutesViewModel by viewModels()
            model.getToken(authCredentials, dialog, this).observe(this, Observer<JsonElement> {
                if (it.isJsonObject) {
                    val accessToken = Gson().fromJson<Token>(it, Token::class.java).access_token
                    if (!accessToken.isNullOrEmpty()) {
                        saveDataIntoSharedPreference(accessToken)
                        openTaxiInformationScreen()
                    }
                } else if (it.isJsonArray) {
                    val authErrors = Gson().fromJson<List<AuthCredentialsError>>(it as JsonArray?, object : TypeToken<List<AuthCredentialsError?>?>() {}.type)
                    if (authErrors != null) {
                        for (e in authErrors.indices) {
                            if (authErrors[e].ErrorNumber == 1 || authErrors[e].ErrorNumber == 2) {
                                showErrorMessage(authErrors[e].ErrorNumber, authErrors[e].ErrorMessage.toString(), true)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun saveDataIntoSharedPreference(access_token: String) {
        val editor = getSharedPreferences("userData", Activity.MODE_PRIVATE).edit()
        editor.putString("tabToken", access_token).apply()
    }

    private fun openTaxiInformationScreen() {
        startActivity(Intent(this, TaxiInformationScreen::class.java))
        finish()
    }

    private fun userNameValid(): Boolean {
        return if (userName.isNotEmpty()) true
        else {
            showErrorMessage(1, "User Name Required", true)
            false
        }
    }

    private fun passwordValid(): Boolean {
        return when {
            password.isEmpty() -> {
                showErrorMessage(2, "Password Required", true); false
            }
            password.length < 6 -> {
                showErrorMessage(2, "Minimum Password is 6 digit", true); false
            }
            else -> true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showErrorMessage(errorId: Int, errorStr: String, show: Boolean) {
        var editText: EditText? = null
        var textView: TextView? = null
        when (errorId) {
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
            editText!!.setBackgroundResource(R.drawable.red_border)
            textView!!.text = "* $errorStr"
            textView.visibility = View.VISIBLE
            return
        } else {
            editText!!.setBackgroundResource(R.drawable.grey_border_edit_text)
            textView!!.visibility = View.INVISIBLE
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
        val patternPassword = "2103678"
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
        if (app?.authCredentials != null) {
            authCredentials = app.authCredentials!!
            userName = authCredentials.Username
            password = authCredentials.Password
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
        startActivity(Intent(this, LearnMoreScreen::class.java))
    }

    private fun saveAuthCredentials() {
        userName = userName_et.text.toString().trim()
        password = password_et.text.toString().trim()
        authCredentials = AuthCredentials(userName, password)
        app!!.authCredentials = authCredentials
        app.isNewLogin = true
    }
}