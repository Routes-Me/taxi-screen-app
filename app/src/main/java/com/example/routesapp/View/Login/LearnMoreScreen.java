package com.example.routesapp.View.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.App;
import com.example.routesapp.R;

public class LearnMoreScreen extends AppCompatActivity {

    private App app;

    private Toolbar myToolbar;

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


        ToolbarSetUp();

        app = (App) getApplicationContext();

        try {
            webView_routesWebsite = findViewById(R.id.webView_routesWebsite);
            webView_routesWebsite.setWebViewClient(new WebViewClient());
            webView_routesWebsite.loadUrl(routesWebsiteLink);
            WebSettings webSettings = webView_routesWebsite.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }



    private void backToLoginScreen() {
        app.setNewLogin(true);
        startActivity(new Intent(LearnMoreScreen.this, LoginScreen.class));
    }


    private void ToolbarSetUp() {
        //Toolbar..
        myToolbar = findViewById(R.id.MyToolBar);

        setSupportActionBar(myToolbar);


            getSupportActionBar().setTitle("Learn more");




        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            backToLoginScreen();

        }

        return super.onOptionsItemSelected(item);
    }


}
