package com.routesme.vehicles.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.Helper
import kotlinx.android.synthetic.main.learn_more_screen.*

class LearnMoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_more_screen)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        initialize()
    }

    private fun initialize() {
        toolbarSetUp()
        showRoutesWebsite()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showRoutesWebsite() {
        webView_routesWebsite.webViewClient = WebViewClient()
        webView_routesWebsite.loadUrl(BuildConfig.ROUTES_WEBSITE_URL)
        val webSettings = webView_routesWebsite.settings
        webSettings.javaScriptEnabled = true
    }

    private fun toolbarSetUp() {
        setSupportActionBar(MyToolBar)
        supportActionBar!!.title = getString(R.string.learn_more)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backToLoginScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backToLoginScreen() {
        App.instance.isNewLogin = true
        startActivity(Intent(this@LearnMoreActivity, LoginActivity::class.java))
        finish()
    }
}