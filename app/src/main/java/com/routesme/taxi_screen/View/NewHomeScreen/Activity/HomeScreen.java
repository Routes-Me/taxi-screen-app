package com.routesme.taxi_screen.View.NewHomeScreen.Activity;


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
import com.routesme.taxi_screen.Class.Helper;
import com.routesme.taxi_screen.New_Hotspot_Configuration.PermissionsActivity;
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

public class HomeScreen extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = "HomeScreen";
    private App app;
    private FirebaseAnalytics firebaseAnalytics;
    private LinearLayout homeScreenLayout;
    private boolean isLightTheme = true;
    private SharedPreferences sharedPreferences;
    private String savedTabletToken = null, savedTabletSerialNo = null, savedTabletPassword = null;
    private int savedTabletChannelId = 0;
    private ImageView openPattern;
    private long PressedTime;
    private int clickTimes = 0;
    private String final_pattern = "";
    private Dialog exitPatternDialog;
    private PatternLockView pattern_exitApp;
    private TrackingHandler trackingHandler;
    private LocationFinder finder;
    boolean isHandlerTrackingRunning = false;
    private Handler handlerTracking;
    private Runnable runnableTracking;
    private WebSocketClient trackingWebSocket;
    private URI trackingWebSocketUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        RequestPermissions();

        TurnOnHotspot();


    }

    @Override
    public void onPermissionsOkay() {

    }


    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

        //Check authorization of tablet before fetch advertisement data from server to display it ..
        if (isAuthorized()) {

            createWebSocketNew();

            trackingHandler = new TrackingHandler(this, trackingWebSocket);

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


    private void TurnOnHotspot() {
        try {
            Intent intent = new Intent(getString(R.string.intent_action_turnon));
            sendImplicitBroadcast(this, intent);
        } catch (Exception e) {
            Crashlytics.logException(e);
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
        try {
            if (trackingWebSocket != null) {
                trackingWebSocket.onCloseReceived();
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }


        super.onPause();
    }


    @Override
    protected void onDestroy() {
        try {
            if (trackingWebSocket != null) {
                trackingWebSocket.onCloseReceived();
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }


        super.onDestroy();
    }


    private void startTracking() {
        try {

            finder = new LocationFinder(this, trackingHandler);
            if (finder.canGetLocation()) {

                TrackingTimer();
                try {
                    trackingWebSocket.connect();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
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
        } catch (URISyntaxException e) {
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
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container, new ContentFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.sideMenuFragment_container, new SideMenuFragment()).commit();
    }

    private void deleteAuthenticationCredentialsFromAppClass() {
        app = (App) getApplicationContext();
        app.setTechnicalSupportUserName(null);
        app.setTechnicalSupportPassword(null);
        app.setNewLogin(false);
        app.setTaxiOfficeId(0);
        app.setTaxiOfficeName(null);
        app.setTaxiPlateNumber(null);
    }


    private boolean isAuthorized() {
        boolean isAuthorized = false;
        try {
            savedTabletToken = sharedPreferences.getString("tabToken", null);
            savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
            savedTabletPassword = sharedPreferences.getString("tabletPassword", null);
            savedTabletChannelId = sharedPreferences.getInt("tabletChannelId", 0);


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
