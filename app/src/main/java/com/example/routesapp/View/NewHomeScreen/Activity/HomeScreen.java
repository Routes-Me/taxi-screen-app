package com.example.routesapp.View.NewHomeScreen.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.App;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.R;
import com.example.routesapp.View.Login.LoginScreen;
import com.example.routesapp.View.NewHomeScreen.Fragments.ContentFragment;
import com.example.routesapp.View.NewHomeScreen.Fragments.SideMenuFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {


    private App app;

    private Operations operations;


    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;

    //To Change App. Mode ( Light / Dark ) ...
    private LinearLayout homeScreenLayout;
    private boolean isLightTheme = true;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    // private SharedPreferences.Editor editor;
    private String  savedTabletToken = null,savedTabletSerialNo = null , savedTabletPassword = null;
    private int savedTabletChannelId = 0;

    //To open Settings [ change launcher app ] ...
    private ImageView openPattern;
    private long PressedTime;
    private int clickTimes = 0;
    //Exit Pattern
    private String final_pattern = "";
    private Dialog exitPatternDialog;
    private PatternLockView pattern_exitApp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);


    }


    @Override
    protected void onResume() {
        // Toast.makeText(this, "On Resume", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

        //Check authorization of tablet before fetch advertisement data from server to display it ..
        if (isAuthorized()){


            initialize();
            hideNavigationBar();
            IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics();
            showFragments();

        }else {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }



        super.onResume();
    }


    private void initialize() {

        //Using Firebase Analytics ...
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Delete Technical Support Authentication Credentials ....
        deleteAuthenticationCredentialsFromAppClass();

        homeScreenLayout = findViewById(R.id.homeScreenLayout);
        homeScreenLayout.setOnClickListener(this);

        openPattern = findViewById(R.id.openPattern);
        openPattern.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.homeScreenLayout:
                //  changeTheme();
                break;

            case R.id.openPattern:
                openPatternDialog();
                break;

        }
    }

    private void openPatternDialog() {

            try {
                clickTimes++;
                //if user Click on Back button Two Times
                if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 15) {


                    readPatternFromTechnicalSupport();

                    //Toast.makeText(this, "Pattern!", Toast.LENGTH_SHORT).show();

                    clickTimes = 0;

                }
                else {

                }

                PressedTime = System.currentTimeMillis();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

    }


    public void readPatternFromTechnicalSupport() {

        showExitPatternDialog();

        pattern_exitApp.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(pattern_exitApp, pattern);

                if (final_pattern.equals(savedTabletPassword)) {
                    openSettings();
                } else {
                    pattern_exitApp.clearPattern();
                    exitPatternDialog.dismiss();
                }
            }
            @Override
            public void onCleared() {
            }
        });
    }

    private void showExitPatternDialog() {
        try{
            exitPatternDialog = new Dialog(this);
            exitPatternDialog.setContentView(R.layout.exit_pattern_dialog);


            pattern_exitApp = exitPatternDialog.findViewById(R.id.pattern_exitApp);


            exitPatternDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exitPatternDialog.show();
            exitPatternDialog.setCancelable(false);
        }catch (Exception e){
            Crashlytics.logException(e);
        }



    }

    private void openSettings() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                final Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
            } else {
                final Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }

            finish();
            System.exit(0);
        }catch (Exception e){
            Crashlytics.logException(e);
        }



    }

    private void hideNavigationBar() {
        try {
            View decorView = getWindow().getDecorView();
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flags);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    @SuppressLint("ResourceAsColor")
    private void changeTheme() {
        if (isLightTheme){
            homeScreenLayout.setBackgroundColor(Color.parseColor("#000000"));
            isLightTheme = false;
        }else {
            homeScreenLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            isLightTheme = true;
        }
    }


    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void showFragments() {
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container, new ContentFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.sideMenuFragment_container, new SideMenuFragment()).commit();
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    private void deleteAuthenticationCredentialsFromAppClass() {
        try {
            app = (App) getApplicationContext();

            app.setTechnicalSupportUserName(null);
            app.setTechnicalSupportPassword(null);
            app.setNewLogin(false);
            app.setTaxiOfficeId(0);
            app.setTaxiOfficeName(null);
            app.setTaxiPlateNumber(null);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    private boolean isAuthorized() {
        //Initialize sharedPreference Storage , and fetch data that saved into it ...
        fetchSharedPreferenceData();

        if (savedTabletToken != null && savedTabletSerialNo != null && savedTabletPassword != null && savedTabletChannelId > 0){
            // Bearer_TabletToken = "Bearer " +savedTabletToken;

            return true;
        }else {
            return false;
        }
    }

    private void fetchSharedPreferenceData() {
        try {
            savedTabletToken = sharedPreferences.getString("tabToken", null);
            savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
            savedTabletPassword = sharedPreferences.getString("tabletPassword", null);
            savedTabletChannelId = sharedPreferences.getInt("tabletChannelId", 0);
        }catch (Exception e){
            Crashlytics.logException(e);
        }


    }

    private void IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics() {

        try {
            Crashlytics.setUserIdentifier(savedTabletSerialNo);
            firebaseAnalytics.setUserId(savedTabletSerialNo);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }


}
