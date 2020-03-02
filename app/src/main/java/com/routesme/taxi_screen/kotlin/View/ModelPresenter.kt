package com.routesme.taxi_screen.kotlin.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Model.Authorization
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity.HomeScreen
import com.routesme.taxi_screen.kotlin.View.LoginScreens.LoginScreen
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
        app.authCredentials!!.Username = ""
        app.authCredentials!!.Password = ""
        app.isNewLogin = false
        app.taxiOfficeId = 0
        app.taxiOfficeName = null
        app.taxiPlateNumber = null
    }

    private fun isRegistered(): Boolean {
        val sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("tabToken", null) != null && sharedPreferences.getInt("tabletChannelId", -999) != -999
    }

    private fun isAuthorized(): Boolean {
        val authorization: Authorization = intent.getSerializableExtra(AUTHORIZATION_KAY) as Authorization
        return authorization.isAuthorized
    }

    private fun openScreen(screen: Screens) {
        when (screen) {
            Screens.LoginScreen -> startActivity(Intent(this, LoginScreen::class.java))
            Screens.HomeScreen -> startActivity(Intent(this, HomeScreen::class.java))
        }
        this.finish()
    }
}
enum class Screens {
    HomeScreen, LoginScreen
}