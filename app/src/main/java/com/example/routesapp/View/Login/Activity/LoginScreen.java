package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.R;
import com.example.routesapp.View.Login.LoginFragments.TabletDataFragment;
import com.example.routesapp.View.Login.LoginFragments.TechnicalLoginFragment;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {



    private ImageView btnOpenLoginScreen;
    private CardView loginCardView;

    private long PressedTime;
    private Toast Toast;
    private int clickTimes = 0;

    private Intent choose_Main_Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        initialize();


    }


    private void initialize() {

        btnOpenLoginScreen = findViewById(R.id.btnOpenLoginScreen);
        btnOpenLoginScreen.setOnClickListener(this);
        loginCardView = findViewById(R.id.loginCardView);


        //Main Fragment
        mainFragmentOfUserViewActivity();

    }


    private void mainFragmentOfUserViewActivity() {
        choose_Main_Fragment = getIntent();
        if (choose_Main_Fragment.hasExtra("Open_TabletDataFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new TabletDataFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new TechnicalLoginFragment()).commit();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenLoginScreen:
                showLoginView();
                break;
        }
    }

    private void showLoginView() {


        try {
            clickTimes++;
            //if user Click on Back button Two Times
            if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 5) {
                Toast.cancel();

                btnOpenLoginScreen.setVisibility(View.GONE);
                loginCardView.setVisibility(View.VISIBLE);

            }
            //if user Click on Back button One Times
            else {
                Toast = Toast.makeText(getBaseContext(), "Clicked Times:  " + clickTimes, Toast.LENGTH_SHORT);
                Toast.show();
            }

            PressedTime = System.currentTimeMillis();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }


    }
}
