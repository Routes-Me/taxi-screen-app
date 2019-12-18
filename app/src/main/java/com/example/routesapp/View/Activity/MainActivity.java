package com.example.routesapp.View.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.App;
import com.example.routesapp.Class.CounterOperations;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.Model.Advertisement;
import com.example.routesapp.Model.BannerModel;
import com.example.routesapp.Model.BannersViewModel;
import com.example.routesapp.Model.ItemAnalytics;
import com.example.routesapp.Model.TabletPasswordModel;
import com.example.routesapp.Model.TabletPasswordViewModel;
import com.example.routesapp.R;
import com.example.routesapp.Model.TabletChannelModel;
import com.example.routesapp.Model.TabletChannelsViewModel;
import com.example.routesapp.View.Fragment.RecyclerViewFragment;
import com.example.routesapp.View.Login.LoginScreen;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private App app;


    private CounterOperations counterOperations;



    //To disable launcher app...
    private LinearLayout userView, adminPatternView;
    private String final_pattern = "";
    private PatternLockView pattern_lock_view_admin;
    private ImageView app_logo;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String savedLanguage = null, savedToken = null;
    private String tabletSerialNo = "" , tabletPassword = "";



    private Operations operations;

    //For ADS Videos
    private VideoView ADS_VideoView;

    //For ADS Images
    private ImageView ADS_ImageView;


    //for scrolling textView [Money & News]......
    private TextView scrollingtextMoney;


    ////////////////////for Time Clock....
    private Time mTime;
    private Handler handlerTime;
    private Runnable runnableTime;

    private TextView timeClock, DateClock, DayClock;

    ///////////////////////////////////////////


    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Delete Technical Support Authentication Credentials ....
        app = (App) getApplicationContext();

        app.setTechnicalSupportUserName(null);
        app.setTechnicalSupportPassword(null);
        app.setNewLogin(false);
        app.setTaxiOfficeId(0);
        app.setTaxiOfficeName(null);
        app.setTaxiPlateNumber(null);

        //Using Firebase Analytics ...
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        counterOperations = new CounterOperations(MainActivity.this);

        adminPatternView = findViewById(R.id.adminPatternView);
        userView = findViewById(R.id.userView);
        Admin_User_layoutVisibility("userView");

        pattern_lock_view_admin = findViewById(R.id.pattern_lock_view_admin);
        app_logo = findViewById(R.id.app_logo);
        app_logo.setOnLongClickListener(this);


        ADS_VideoView = findViewById(R.id.ADS_VideoView);
        ADS_VideoView.setOnClickListener(this);



        ADS_ImageView = findViewById(R.id.ADS_ImageView);
        ADS_ImageView.setOnClickListener(this);



        scrollingtextMoney = (TextView) findViewById(R.id.scrollingtextMoney);
        scrollingtextMoney.setSelected(true);

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        savedLanguage = sharedPreferences.getString("Language", "English");

        //For testing only we add token manually ...
       //  editor.putString("tabToken", "dy_W6xCrb0Nmi2GrIbp0AI_QCgDjxvkf8ec-RKAZ8TNCLQw9gU2xiLPvP2gZurwIl4NK2y66j84h9nYbN8K6mQyC2pz5nEQrujM0KAR92PteinZcsAOgXI9RM20ayE9EYLggKIMuDTsdwcgQvgfcR4ur_JBTSBMacFmtKaie0cT3HtsP_1YgRDbQ34S_5MiWm2v9-cyigfXCblcrUaADiDBM0vUSbnieaGMc8K7BbyET5jApCaMhsQQDclR0FnI_AxGFbNutaP2sEf297bo5AQPcx1IDEWm2YV-no32cXZ88P-abOdAf3E1pwMs9LmIyyXRGurlC1_eM48esTsJNr3MFry7bJunnOG_kHK2taZ3NUkIhSZjVItHb9XAg-Ha0cRRsVKEk_wQcSmpgL493Motj6rIw5On0m21gbo9MlsxEY85DrU-Bn9nnuTKyUhndclEB9ZvNIuzODY1r3o0fF4out9iFn_vxkiKN8gu06rmO_0f_bkmjUrfGU0eWtvyF");
        // editor.apply();


        if (sharedPreferences.getString("tabToken", null) != null){
            savedToken = "Bearer " +sharedPreferences.getString("tabToken", null);



            initialize();

            mainFragmentToShow();
        }else {
            startActivity(new Intent(MainActivity.this, LoginScreen.class));
            finish();
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void initialize() {

        operations = new Operations(this, ADS_VideoView, ADS_ImageView, scrollingtextMoney);



        //For Tablet Serial Number...
        tabletSerialNumber();


        ////////////////////for Time Clock....
        setUpClockTime();


    }

    public void Admin_User_layoutVisibility(String layoutName) {

        switch (layoutName) {
            case "userView":
                userView.setVisibility(View.VISIBLE);
                adminPatternView.setVisibility(View.GONE);
                break;

            case "adminPatternView":
                userView.setVisibility(View.GONE);
                adminPatternView.setVisibility(View.VISIBLE);
                break;
        }

        Operations.hideKeyboard(this);
    }

    //////////////////////////////////////////////////////////////////////////
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






    private void setUpClockTime() {

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


    private void tabletSerialNumber() {
        tabletSerialNo = sharedPreferences.getString("tabletSerialNo", "").trim();

        Crashlytics.setUserIdentifier(tabletSerialNo);
        firebaseAnalytics.setUserId(tabletSerialNo);


        if (!tabletSerialNo.equals("") && !tabletSerialNo.isEmpty() && !tabletSerialNo.equals(null)) {

           // getTabletData_And_Channels();
            operations.get_dataId_of_selectedLang();

        } else {
        }

    }



    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void mainFragmentToShow() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecyclerViewFragment()).commit();
        //  operations.showHideFragment(getSupportFragmentManager(), new RecyclerViewFragment(), new ViewItemFragment(), true, true);
    }


    public void resetPreferredLauncherAndOpenChooser() {


        pattern_lock_view_admin.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

                //   Toast.makeText(MainActivity.this, "onStarted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                // Toast.makeText(MainActivity.this, "onProgress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(pattern_lock_view_admin, pattern);
                tabletPassword = sharedPreferences.getString("tabletPassword", "0124678");

                if (final_pattern.equals(tabletPassword)) {
                    openSettings();
                } else {
                    pattern_lock_view_admin.clearPattern();
                    Admin_User_layoutVisibility("userView");
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





    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.app_logo:
                Admin_User_layoutVisibility("adminPatternView");

                // Operations.hideKeyboard(MainActivity.this);

                resetPreferredLauncherAndOpenChooser();
                break;
        }

        return true;
    }



//////////////////////////////////////////////////////



    private void getTabletPassword(){

        try {


            ViewModelProviders.of(this).get(TabletPasswordViewModel.class).getTabPassword(tabletSerialNo, MainActivity.this).observe(this, new Observer<List<TabletPasswordModel>>() {
                @Override
                public void onChanged(@Nullable List<TabletPasswordModel> tabletPasswordModel) {

                    //Get Tablet Password...
                    if (tabletPasswordModel != null) {
                        tabletPassword = tabletPasswordModel.get(0).getTabletData_Password();
                        editor.putString("tabletPassword", tabletPassword);
                        editor.apply();
                        //  Toast.makeText(MainActivity.this, "Password: " + tabletPassword, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }
}