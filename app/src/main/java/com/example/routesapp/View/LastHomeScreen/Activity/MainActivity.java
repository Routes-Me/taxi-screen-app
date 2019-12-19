package com.example.routesapp.View.LastHomeScreen.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.App;
import com.example.routesapp.Class.CounterOperations;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.Model.ItemAnalytics;
import com.example.routesapp.R;
import com.example.routesapp.View.LastHomeScreen.Fragment.RecyclerViewFragment;
import com.example.routesapp.View.Login.LoginScreen;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private App app;

    private Operations operations;

    private CounterOperations counterOperations;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
   // private SharedPreferences.Editor editor;
    private String  savedTabletToken = null,savedTabletSerialNo = "" , savedTabletPassword = "";
    private int savedTabletChannelId = 0;

    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;

    //To disable launcher app...
    private LinearLayout userView, adminPatternView;
    private String final_pattern = "";
    private PatternLockView pattern_lock_view_admin;
    private ImageView app_logo;


    //Define Advertisement Items
    private VideoView ADS_VideoView;
    private ImageView ADS_ImageView;
    private TextView scrollingCurrencies;

    //for Time Counter....
    private Time mTime;
    private Handler handlerTime;
    private Runnable runnableTime;
    private TextView timeClock, DateClock, DayClock;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initialize();

    }

    @SuppressLint("SimpleDateFormat")
    private void initialize() {

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

        //Delete Technical Support Authentication Credentials ....
        deleteAuthenticationCredentialsFromAppClass();

        counterOperations = new CounterOperations(MainActivity.this);

        //Using Firebase Analytics ...
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Initialize Advertisement Items
        adminPatternView = findViewById(R.id.adminPatternView);
        userView = findViewById(R.id.userView);
        showTechnicalExsitingPattern(false);

        pattern_lock_view_admin = findViewById(R.id.pattern_lock_view_admin);
        app_logo = findViewById(R.id.app_logo);
        app_logo.setOnLongClickListener(this);

        ADS_VideoView = findViewById(R.id.ADS_VideoView);
        ADS_VideoView.setOnClickListener(this);

        ADS_ImageView = findViewById(R.id.ADS_ImageView);
        ADS_ImageView.setOnClickListener(this);

        scrollingCurrencies = (TextView) findViewById(R.id.scrollingtextMoney);
        scrollingCurrencies.setSelected(true);




        //For testing only .. we add expired token manually ...
       // editor = sharedPreferences.edit();
       // editor.putString("tabToken", "dy_W6xCrb0Nmi2GrIbp0AI_QCgDjxvkf8ec-RKAZ8TNCLQw9gU2xiLPvP2gZurwIl4NK2y66j84h9nYbN8K6mQyC2pz5nEQrujM0KAR92PteinZcsAOgXI9RM20ayE9EYLggKIMuDTsdwcgQvgfcR4ur_JBTSBMacFmtKaie0cT3HtsP_1YgRDbQ34S_5MiWm2v9-cyigfXCblcrUaADiDBM0vUSbnieaGMc8K7BbyET5jApCaMhsQQDclR0FnI_AxGFbNutaP2sEf297bo5AQPcx1IDEWm2YV-no32cXZ88P-abOdAf3E1pwMs9LmIyyXRGurlC1_eM48esTsJNr3MFry7bJunnOG_kHK2taZ3NUkIhSZjVItHb9XAg-Ha0cRRsVKEk_wQcSmpgL493Motj6rIw5On0m21gbo9MlsxEY85DrU-Bn9nnuTKyUhndclEB9ZvNIuzODY1r3o0fF4out9iFn_vxkiKN8gu06rmO_0f_bkmjUrfGU0eWtvyF");
       // editor.apply();


        //Check authorization of tablet before fetch advertisement data from server to display it ..
        if (isAuthorized()){
            setUpTimeCounter();
            IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics();

            operations = new Operations(this, ADS_VideoView, ADS_ImageView, scrollingCurrencies);
            operations.fetchAdvertisementData();

            mainFragmentToShow();
        }else {
            startActivity(new Intent(MainActivity.this, LoginScreen.class));
            finish();
        }


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

        savedTabletToken = sharedPreferences.getString("tabToken", null);
        savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
        savedTabletPassword = sharedPreferences.getString("tabletPassword", null);
        savedTabletChannelId = sharedPreferences.getInt("tabletChannelId", 0);
    }

    private void setUpTimeCounter() {

        timeClock = findViewById(R.id.timeClock);
        DateClock = findViewById(R.id.DateClock);
        DayClock = findViewById(R.id.DayClock);

        mTime = new Time();

        runnableTime = new Runnable() {
            @Override
            public void run() {

                mTime.setToNow();

                counterOperations.getCurrentTime(mTime, timeClock, DateClock, DayClock);


                handlerTime.postDelayed(runnableTime, 1000);

            }
        };

        handlerTime = new Handler();
        handlerTime.postDelayed(runnableTime, 1000);

    }


    private void IdentifierTabletByItSerialNumber_For_FirebaseAnalyticsAndCrashlytics() {

        Crashlytics.setUserIdentifier(savedTabletSerialNo);
        firebaseAnalytics.setUserId(savedTabletSerialNo);

    }

    public void showTechnicalExsitingPattern(boolean show) {

        if (show){
            userView.setVisibility(View.GONE);
            adminPatternView.setVisibility(View.VISIBLE);
        }else {
            userView.setVisibility(View.VISIBLE);
            adminPatternView.setVisibility(View.GONE);
        }

        Operations.hideKeyboard(this);
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ADS_VideoView:

                updateFirebaseAnalystics(new ItemAnalytics(1,"click_video"));

                break;

            case R.id.ADS_ImageView:
                updateFirebaseAnalystics(new ItemAnalytics(3,"click_banner"));

                break;

        }

    }

    private void updateFirebaseAnalystics(ItemAnalytics itemAnalytics) {
        /*
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, itemAnalytics.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemAnalytics.getName());

        //Logs an app event.
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
     //   firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
       // firebaseAnalytics.setMinimumSessionDuration(20000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
      //  firebaseAnalytics.setSessionTimeoutDuration(500);

*/
        /*
        //save into ( SELECT_CONTENT Event )
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(itemAnalytics.getId()));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemAnalytics.getName());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, itemAnalytics.getName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
*/

        //save into ( Custom Item Event )
        Bundle params = new Bundle();
        //params.putString("image_name", name);
        // params.putString("full_text", text);

        firebaseAnalytics.logEvent(itemAnalytics.getName(), params);


    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.app_logo:
                showTechnicalExsitingPattern(true);
                readPatternFromTechnicalSupport();
                break;
        }

        return true;
    }


    public void readPatternFromTechnicalSupport() {

        pattern_lock_view_admin.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(pattern_lock_view_admin, pattern);
                savedTabletPassword = sharedPreferences.getString("tabletPassword", "0124678");

                if (final_pattern.equals(savedTabletPassword)) {
                    openSettings();
                } else {
                    pattern_lock_view_admin.clearPattern();
                    showTechnicalExsitingPattern(false);
                }

            }

            @Override
            public void onCleared() {

            }
        });

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



    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void mainFragmentToShow() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecyclerViewFragment()).commit();
        //  operations.showHideFragment(getSupportFragmentManager(), new RecyclerViewFragment(), new ViewItemFragment(), true, true);
    }

}