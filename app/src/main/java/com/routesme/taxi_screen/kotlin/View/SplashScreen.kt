package com.routesme.taxi_screen.kotlin.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.routesme.taxi_screen.java.Class.App
import com.routesme.taxi_screen.java.View.Login.LoginScreen
import com.routesme.taxi_screen.java.View.NewHomeScreen.Activity.HomeScreen
import com.routesme.taxi_screen.kotlin.Model.Authorization
import com.routesme.taxiscreen.R

class SplashScreen : AppCompatActivity() {

    // @BindView(R.id.showKotlinToast) lateinit var showKotlinToast: Button
    val AUTHORIZATION_KAY = "authorization"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //ButterKnife.bind(this)

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
        app.setTechnicalSupportUserName(null)
        app.setTechnicalSupportPassword(null)
        app.setNewLogin(false)
        app.setTaxiOfficeId(0)
        app.setTaxiOfficeName(null)
        app.setTaxiPlateNumber(null)
    }


    private fun isRegistered(): Boolean {
        val sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE)
        val Token = sharedPreferences.getString("tabToken", null)
        val channelId = sharedPreferences.getInt("tabletChannelId", -999)
        return Token != null && channelId != -999
    }


    private fun isAuthorized(): Boolean {
        val authorization: Authorization = intent.getSerializableExtra(AUTHORIZATION_KAY) as Authorization
        Toast.makeText(this, "isAuthorized: ${authorization.isAuthorized} .. responseCode: ${authorization.responseCode}", Toast.LENGTH_SHORT).show()
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

