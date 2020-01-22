package com.routesme.taxi_screen.View.NewHomeScreen.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.App;
import com.routesme.taxi_screen.Class.Helper;
import com.routesme.taxi_screen.DetectInternetConnectionStatus.ConnectivityReceiver;
import com.routesme.taxi_screen.Tracking.Class.LocationFinder;
import com.routesme.taxi_screen.Tracking.Class.TrackingHandler;
import com.routesme.taxi_screen.View.Login.LoginScreen;
import com.routesme.taxi_screen.View.NewHomeScreen.Fragments.ContentFragment;
import com.routesme.taxi_screen.View.NewHomeScreen.Fragments.SideMenuFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.routesme.taxiscreen.R;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener  {

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
    //Room Database...
    private TrackingHandler trackingHandler;
    //Location Finder to get device current location [ GeoPoint(latitude,longitude) ] ...
    private LocationFinder finder;
    //Thread
    boolean isHandlerTrackingRunning = false;
    private Handler handlerTracking;
    private Runnable runnableTracking;
    //webSocket
    private WebSocketClient trackingWebSocket;
    private URI trackingWebSocketUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        RequestPermissions();




    }


    @Override
    protected void onResume() {
        // Toast.makeText(this, "On Resume", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

        //Check authorization of tablet before fetch advertisement data from server to display it ..
        if (isAuthorized()) {

            createWebSocketNew();
            //Vehicle Tracking...
            trackingHandler = new TrackingHandler(this,trackingWebSocket);

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

        // register connection status listener
/*
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getPackageName() + "android.net.conn.CONNECTIVITY_CHANGE");

        ConnectivityReceiver myReceiver = new ConnectivityReceiver();
        registerReceiver(myReceiver, intentFilter);
*/


        super.onResume();



    }







    @Override
    protected void onPause() {
        // Toast.makeText(this, "Pause here!", Toast.LENGTH_SHORT).show();
        // stopTrackingTimer();

        try {
            if (trackingWebSocket != null){
                trackingWebSocket.onCloseReceived();
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }




        super.onPause();
    }


    @Override
    protected void onDestroy() {
        //Toast.makeText(this, "Destroy here!", Toast.LENGTH_SHORT).show();
        //  stopTrackingTimer();

        try {
             if (trackingWebSocket != null){
            trackingWebSocket.onCloseReceived();
              }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }


        super.onDestroy();
    }


    private void startTracking() {
        try {
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
                try {
                    trackingWebSocket.connect();
                 } catch (Exception e) {
                Crashlytics.logException(e);
            }

                //createWebSocketNew();
                //  if (trackingWebSocket.getReadyState() == WebSocket.READYSTATE.CLOSED){


                // }


                //TrackingTimer();
            } else {
                finder.showSettingsAlert();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }


    }


    private void createWebSocketNew() {

        try {
            trackingWebSocketUri = new URI(Helper.getConfigValue(this, "trackingWebSocketUri"));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }



        trackingWebSocket = new WebSocketClient(trackingWebSocketUri) {
            @Override
            public void onOpen() {
                try {
                Log.i("trackingWebSocket:  ", "Opened");


                //Send deviceId to server (Device Identifiering)
                sendMessageViaSocket("deviceId:" + savedTabletSerialNo);

                //Send offline locations to server if it exists....
                trackingHandler.sendOfflineTrackingToServer();

                //Start Tracking Timer after 500 melli seconds...
               // handlerTracking.postDelayed(runnableTracking, 500);
                    handlerTracking.post(runnableTracking);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            }

            @Override
            public void onTextReceived(String message) {
                Log.i("trackingWebSocket:  ", "Received message:  " + message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.i("trackingWebSocket:  ", "onBinaryReceived:   " + data);
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.i("trackingWebSocket:  ", "onPingReceived:   " + data);
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.i("trackingWebSocket:  ", "onPongReceived:   " + data);
            }

            @Override
            public void onException(Exception e) {
                try {
                Log.i("trackingWebSocket:  ", "Exception Error:   " + e.getMessage());
                stopTrackingTimer();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }
            }

            @Override
            public void onCloseReceived() {
                try {
                Log.i("trackingWebSocket:  ", "Closed !");
                stopTrackingTimer();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }

            }
        };

        trackingWebSocket.setConnectTimeout(10000);
        trackingWebSocket.setReadTimeout(60000);
      //  trackingWebSocket.addHeader("Origin", "http://developer.example.com");
        trackingWebSocket.enableAutomaticReconnection(5000);
       // trackingWebSocket.connect();
    }




    private void sendMessageViaSocket(String message) {
        try {
        trackingWebSocket.send(message);
        Log.i("trackingWebSocket:  ", "Send message:  " + message);
    } catch (Exception e) {
        Crashlytics.logException(e);
    }
    }


    private void TrackingTimer() {
        try {

            runnableTracking = new Runnable() {
                @Override
                public void run() {
                    isHandlerTrackingRunning = true;
                    Log.i("trackingWebSocket:  ", "Tracking Timer running ...");
                    trackingHandler.locationChecker();

                    handlerTracking.postDelayed(runnableTracking, 5000);

                }
            };

            handlerTracking = new Handler();

        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void stopTrackingTimer() {
        if (isHandlerTrackingRunning) {
            try {
                handlerTracking.removeCallbacks(runnableTracking);
                isHandlerTrackingRunning = false;
                Log.i("trackingWebSocket:  ", "Tracking Timer stop ...");
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

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
    public void RequestPermissions() {
        int Permission_All = 1;

        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE};
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
