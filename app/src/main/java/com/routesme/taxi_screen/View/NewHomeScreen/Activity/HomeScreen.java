package com.routesme.taxi_screen.View.NewHomeScreen.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.App;
import com.routesme.taxi_screen.Tracking.Class.LocationFinder;
import com.routesme.taxi_screen.Tracking.Class.TrackingHandler;
import com.routesme.taxi_screen.View.Login.LoginScreen;
import com.routesme.taxi_screen.View.NewHomeScreen.Fragments.ContentFragment;
import com.routesme.taxi_screen.View.NewHomeScreen.Fragments.SideMenuFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.routesme.taxiscreen.R;

import java.util.List;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeScreen";

    private App app;

    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;

    //To Change App. Mode ( Light / Dark ) ...
    private LinearLayout homeScreenLayout;
    private boolean isLightTheme = true;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    // private SharedPreferences.Editor editor;
    private String savedTabletToken = null, savedTabletSerialNo = null, savedTabletPassword = null;
    private int savedTabletChannelId = 0;

    //To open Settings [ change launcher app ] ...
    private ImageView openPattern;
    private long PressedTime;
    private int clickTimes = 0;
    //Exit Pattern
    private String final_pattern = "";
    private Dialog exitPatternDialog;
    private PatternLockView pattern_exitApp;


    ////////Tracking System...

    //Location requested permissions...
    public static final int LOCATION_REQUEST_CODE = 102;
    private String[] Location_Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean showRationale = true;


    //Room Database...
    private TrackingHandler trackingHandler;
    //Location Finder to get device current location [ GeoPoint(latitude,longitude) ] ...
    private LocationFinder finder;
    //Thread
    boolean isHandlerTrackingRunning = false;
    private Handler handlerTracking;
    private Runnable runnableTracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        RequestLocationPermission();

    }


    @Override
    protected void onResume() {
        // Toast.makeText(this, "On Resume", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

        //Check authorization of tablet before fetch advertisement data from server to display it ..
        if (isAuthorized()) {


            //Vehicle Tracking...
            trackingHandler = new TrackingHandler(this);

            //Get Vehicle Current Location & running TrackingTimer...
            startTracking();
            // vehicleTracking();

            initialize();
            hideNavigationBar();
            IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics();
            showFragments();




        } else {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        // Toast.makeText(this, "Pause here!", Toast.LENGTH_SHORT).show();
        if (isHandlerTrackingRunning) {
            handlerTracking.removeCallbacks(runnableTracking);
            isHandlerTrackingRunning = false;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Toast.makeText(this, "Destroy here!", Toast.LENGTH_SHORT).show();
        if (isHandlerTrackingRunning) {
            handlerTracking.removeCallbacks(runnableTracking);
            isHandlerTrackingRunning = false;
        }

        super.onDestroy();
    }
/*
    private void vehicleTracking() {

       // RequestLocationPermission();
        //Start vehicle tracking ....
       // startTracking();
        vehicleTracking();

    }
*/

    private void startTracking() {
        // trackingHandler.insertLocation(new TrackingLocation(29.375990,47.986486));
        // TrackingTimer();

        finder = new LocationFinder(this, trackingHandler);
        if (finder.canGetLocation()) {
            /*
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();
            Toast.makeText(this, "Room DB Insert .... First Location ... lat-lng :  " + latitude + "  —  " + longitude, Toast.LENGTH_LONG).show();
             trackingHandler.insertLocation(new TrackingLocation(latitude,longitude));
             */
            TrackingTimer();
        } else {
            finder.showSettingsAlert();
        }

    }


    private void TrackingTimer() {
        try {

            runnableTracking = new Runnable() {
                @Override
                public void run() {
                    isHandlerTrackingRunning = true;
                    trackingHandler.locationChecker();

                    handlerTracking.postDelayed(runnableTracking, 5000);

                }
            };

            handlerTracking = new Handler();
            handlerTracking.postDelayed(runnableTracking, 5000);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

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

        try {
            clickTimes++;
            //if user Click on Back button Two Times
            if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {


                readPatternFromTechnicalSupport();

                //Toast.makeText(this, "Pattern!", Toast.LENGTH_SHORT).show();

                clickTimes = 0;

            } else {

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
        try {
            exitPatternDialog = new Dialog(this);
            exitPatternDialog.setContentView(R.layout.exit_pattern_dialog);


            pattern_exitApp = exitPatternDialog.findViewById(R.id.pattern_exitApp);


            exitPatternDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exitPatternDialog.show();
            exitPatternDialog.setCancelable(false);
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

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
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container, new ContentFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.sideMenuFragment_container, new SideMenuFragment()).commit();
        } catch (Exception e) {
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
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }


    private boolean isAuthorized() {
        boolean isAuthorized = false;
        try {
            try {
                savedTabletToken = sharedPreferences.getString("tabToken", null);
                savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
                savedTabletPassword = sharedPreferences.getString("tabletPassword", null);
                savedTabletChannelId = sharedPreferences.getInt("tabletChannelId", 0);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }


            if (savedTabletToken != null && savedTabletSerialNo != null && savedTabletPassword != null && savedTabletChannelId > 0) {
                // Bearer_TabletToken = "Bearer " +savedTabletToken;
                Log.d(TAG, "isAuthorized: true , TabletChannelId:  " + savedTabletChannelId);
                isAuthorized = true;
            } else {
                Log.d(TAG, "isAuthorized: false  , TabletChannelId:  " + savedTabletChannelId);

                isAuthorized = false;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return isAuthorized;
    }

    private void IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics() {

        try {
            Crashlytics.setUserIdentifier(savedTabletSerialNo);
            firebaseAnalytics.setUserId(savedTabletSerialNo);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }


    //for Request Permissions
    public void RequestLocationPermission() {
        int Permission_All = 1;

        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
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



/*
    private void vehicleTracking() {
        try {

            if (!hasPermissions(this, Location_Permissions)) {
                ActivityCompat.requestPermissions(this, Location_Permissions, LOCATION_REQUEST_CODE);
                return;
            } else {
                //start tracking...
                startTracking();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!hasPermissions(this, Location_Permissions)) {
                        ActivityCompat.requestPermissions(this, Location_Permissions, LOCATION_REQUEST_CODE);
                        return;
                    }

                    //start tracking...
                    vehicleTracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
                        openLocationSettingsDialog();

                    }
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void openLocationSettingsDialog() {
        if (!showRationale) {

            new AlertDialog.Builder(this)
                    .setTitle("Location Permission required")
                    .setMessage("Enable location permission from app settings is required to using tracking system")

                    .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            try {

                                //showTabletSerialNumberError(false);
                                startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));

                            } catch (Exception e) {
                            }

                        }
                    })

                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // showTabletSerialNumberError(true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();

        } else {
            //start tracking...
            vehicleTracking();
        }
    }
    */


}
