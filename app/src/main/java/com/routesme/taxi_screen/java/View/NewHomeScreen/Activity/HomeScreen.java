package com.routesme.taxi_screen.java.View.NewHomeScreen.Activity;


import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.java.Hotspot_Configuration.PermissionsActivity;
import com.routesme.taxi_screen.java.Tracking.Class.LocationTrackingService;
import com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments.ContentFragment;
import com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments.SideMenuFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.routesme.taxiscreen.R;

import java.util.List;

public class HomeScreen extends PermissionsActivity implements View.OnClickListener {

    private FirebaseAnalytics firebaseAnalytics;
    private LinearLayout homeScreenLayout;
    private boolean isLightTheme = true;
    private String savedTabletSerialNo = null, savedTabletPassword = null;
    private long PressedTime;
    private int clickTimes = 0;
    private String final_pattern = "";
    private Dialog exitPatternDialog;
    private PatternLockView pattern_exitApp;
    private LocationTrackingService locationTrackingService;
    private boolean isHotspotOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        RequestPermissions();
       // TurnOnHotspot();
    }

    @Override
    public void onPermissionsOkay() {
    }


    @Override
    protected void onResume() {
        startLocationTrackingService();


        initialize();
        hideNavigationBar();
        IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics();
        showFragments();
        super.onResume();
    }


    private void startLocationTrackingService() {
        locationTrackingService = new LocationTrackingService(this);
    }


    private void TurnOnHotspot() {
        if (!isHotspotOn) {
            Intent intent = new Intent(getString(R.string.intent_action_turnon));
            sendImplicitBroadcast(this, intent);
            isHotspotOn = true;
        }
    }

    private static void sendImplicitBroadcast(Context ctxt, Intent i) {
        PackageManager pm = ctxt.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(i, 0);
        for (ResolveInfo resolveInfo : matches) {
            Intent explicit = new Intent(i);
            ComponentName cn =
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }


    @Override
    protected void onPause() {
        if (locationTrackingService != null) {
            locationTrackingService.stopLocationTrackingService();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (locationTrackingService != null) {
            locationTrackingService.stopLocationTrackingService();
        }
        super.onDestroy();
    }


    private void initialize() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
        savedTabletPassword = sharedPreferences.getString("tabletPassword", null);

        //Using Firebase Analytics ...
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        homeScreenLayout = findViewById(R.id.homeScreenLayout);
        homeScreenLayout.setOnClickListener(this);
        ImageView openPattern = findViewById(R.id.openPattern);
        openPattern.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeScreenLayout:
                //  changeTheme();
                break;

            case R.id.openPattern:
                openPatternDialog();
                break;
        }
    }

    private void openPatternDialog() {
        clickTimes++;
        //if user Click on Back button Two Times
        if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            readPatternFromTechnicalSupport();
            clickTimes = 0;
        }
        PressedTime = System.currentTimeMillis();
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
        exitPatternDialog = new Dialog(this);
        exitPatternDialog.setContentView(R.layout.exit_pattern_dialog);
        pattern_exitApp = exitPatternDialog.findViewById(R.id.pattern_exitApp);
        exitPatternDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitPatternDialog.show();
        exitPatternDialog.setCancelable(false);
    }

    private void openSettings() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(intent);
        } else {
            final Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
        finish();
        System.exit(0);
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);
    }

    @SuppressLint("ResourceAsColor")
    private void changeTheme() {
        if (isLightTheme) {
            homeScreenLayout.setBackgroundColor(Color.parseColor("#000000"));
            isLightTheme = false;
        } else {
            homeScreenLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            isLightTheme = true;
        }
    }


    private void showFragments() {
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container, new ContentFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.sideMenuFragment_container, new SideMenuFragment()).commit();
    }


    private void IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics() {
        Crashlytics.setUserIdentifier(savedTabletSerialNo);
        firebaseAnalytics.setUserId(savedTabletSerialNo);
    }

    //for Request Permissions
    public void RequestPermissions() {
        int Permission_All = 1;
        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE};
        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
