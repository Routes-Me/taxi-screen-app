package com.routesme.taxi_screen.MVVM.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxi_screen.MVVM.Model.Authorization
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Activity.HomeActivity
import com.routesme.taxiscreen.R

class ModelPresenter : AppCompatActivity() {

    private val AUTHORIZATION_KAY = "authorization"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_presenter)

        launchScreen()
    }

    private fun launchScreen() {
        if (isRegistered()) {
            if (intent.hasExtra(AUTHORIZATION_KAY)) {
                if (isAuthorized()) {
                    deleteAuthenticationCredentialsFromAppClass()
                    openScreen(Screens.HomeScreen)
                } else {
                    openScreen(Screens.LoginScreen)
                }
            } else {
                openScreen(Screens.HomeScreen)
            }
        } else {
            openScreen(Screens.LoginScreen)
        }
    }

    private fun deleteAuthenticationCredentialsFromAppClass() {
        val app = applicationContext as App
        app.signInCredentials!!.Username = ""
        app.signInCredentials!!.Password = ""
        app.isNewLogin = false
        app.institutionId = null
        app.institutionName = null
        app.taxiPlateNumber = null
    }

    private fun isRegistered(): Boolean {
        val sharedPreferences = getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreference.token, null) != null && sharedPreferences.getInt(SharedPreference.device_id, -999) != -999
    }

    private fun isAuthorized(): Boolean {
        val authorization: Authorization = intent.getSerializableExtra(AUTHORIZATION_KAY) as Authorization
        return authorization.isAuthorized
    }

    private fun openScreen(screen: Screens) {
        when (screen) {
            Screens.LoginScreen -> startActivity(Intent(this, LoginActivity::class.java))
            Screens.HomeScreen -> startActivity(Intent(this, HomeActivity::class.java))
        }
        this.finish()
    }
}
enum class Screens {
    HomeScreen, LoginScreen
}