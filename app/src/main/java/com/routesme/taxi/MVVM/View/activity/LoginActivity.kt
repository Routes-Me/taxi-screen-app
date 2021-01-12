package com.routesme.taxi.MVVM.View.activity

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
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.Class.Operations
import com.routesme.taxi.MVVM.Model.Error
import com.routesme.taxi.MVVM.Model.LoginResponse
import com.routesme.taxi.MVVM.Model.SignInCredentials
import com.routesme.taxi.MVVM.ViewModel.LoginViewModel
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.uplevels.App
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.exit_pattern_dialog.*
import kotlinx.android.synthetic.main.login_screen.*
import kotlinx.android.synthetic.main.technical_login_layout.*
import kotlinx.android.synthetic.main.technical_login_layout.view.*
import java.io.IOException
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val app = App.instance
    private lateinit var signInCredentials: SignInCredentials
    private lateinit var userName: String
    private lateinit var password: String
    private var dialog: AlertDialog? = null
    private val operations = Operations.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        initialize()
    }

    private fun initialize() {
        dialogSetUp()
        openLoginLayout(app.isNewLogin)
        btnOpenLoginScreen.setOnClickListener { showLoginView() }
        openPatternBtn.setOnClickListener { openPatternDialog() }
        technical_login_screen.btn_learnMore.setOnClickListener { openLearnMoreScreen() }
        technical_login_screen.btn_next.setOnClickListener { buttonNextClick() }
        editTextListener()
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

    private fun dialogSetUp() {
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
    }

    private fun buttonNextClick() {
        operations.enableNextButton(btn_next, false)
        saveAuthCredentials()
        if (userNameValid() && passwordValid()) signIn() //testEncryption()
    }

    /*private fun testEncryption() {
        val str = password
        val encrypted = AesBase64Wrapper().getEncryptedString(str)
        Log.d("Encryption", "Origin: $str \n Encrypted: $encrypted")
    }*/

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
                    saveDataIntoSharedPreference(token)
                    openRegistrationActivity()

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

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            if (error.code == 1 || error.code == 2) {
                showErrorMessage(error, true)
            } else {
                operations.displayAlertDialog(this, getString(R.string.login_error_title), "Error message: ${error.detail}")
            }
        }
    }

    private fun saveDataIntoSharedPreference(access_token: String) {
        val editor = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE).edit()
        editor.apply{
            putString(SharedPreferencesHelper.token, access_token)
        }.apply()

    }



    private fun openRegistrationActivity() {
        startActivity(Intent(this, RegistrationActivity::class.java))
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
}

enum class Field(val code: Int) { UserName(1), Password(2) }