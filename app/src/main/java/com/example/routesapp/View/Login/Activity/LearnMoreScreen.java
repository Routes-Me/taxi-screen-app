package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.routesapp.Class.App;
import com.example.routesapp.R;

public class LearnMoreScreen extends AppCompatActivity implements View.OnClickListener {

    private App app;

    private ImageView btn_back;
    private WebView webView_routesWebsite;
    private static String routesWebsiteLink = "http://www.routesme.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_more_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        initialize();

    }

    private void initialize() {

        app = (App) getApplicationContext();

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        webView_routesWebsite = findViewById(R.id.webView_routesWebsite);
        webView_routesWebsite.setWebViewClient(new WebViewClient());
        webView_routesWebsite.loadUrl(routesWebsiteLink);
        WebSettings webSettings = webView_routesWebsite.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                backToLoginScreen();
                break;
        }
    }

    private void backToLoginScreen() {
        app.setNewLogin(true);
        startActivity(new Intent(LearnMoreScreen.this, LoginScreen.class));
    }
}
