package com.example.routesapp.View.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.routesapp.Class.CounterOperations;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.FetchData.Interface.RoutesApi;
import com.example.routesapp.FetchData.Model.TabletCurrentData;
import com.example.routesapp.FetchData.Model.TabletPasswordModel;
import com.example.routesapp.FetchData.Model.TabletPasswordViewModel;
import com.example.routesapp.R;
import com.example.routesapp.FetchData.Model.TabletChannelModel;
import com.example.routesapp.FetchData.Model.TabletChannelsViewModel;
import com.example.routesapp.View.Fragment.RecyclerViewFragment;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hbb20.CountryCodePicker;

import java.util.List;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, MenuItem.OnMenuItemClickListener {




    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;


    private TabletCurrentData tabletCurrentData;


    private boolean tablet_isActive = false;

    private CounterOperations counterOperations;

    //Get Tablet Data & Channels by it serial Number ...
    private TabletChannelsViewModel tabletChannelsViewModel;

    //To Select App. Language ....
    private ImageView btn_selectLang;
    private TextView btn_englishLang, btn_arabicLang, btn_tagalogLang, btn_urduLang;
    private LinearLayout languageLayout, counterLayout;
    private String selectedLayout = "counterLayout";

    //To disable launcher app...
    private LinearLayout userView, adminPatternView, adminSerialNoView;
    private String final_pattern = "";
    private PatternLockView pattern_lock_view_admin;
    private ImageView app_logo;


    private Location location;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String savedLanguage = null;
    private String tabletSerialNo = "" , tabletPassword = "";

    //For Tablet Serial Number...
    private EditText editText_addTabletSerialNo;
    private TextView button_addTabletSerialNo;

    private Operations operations;



    private MenuItem txtLanguage, English_Lang, Arabic_Lang, tagalog_Lang, Urdu_Lang;
    private TextView txtLanguageView;


    //For ADS Videos
    private VideoView ADS_VideoView;



    private SimpleExoPlayerView ADS_exoPlayer_VideoView;




    private ImageView ADS_VideoView_defaultImage;


    //For ADS Images
    private ImageView ADS_ImageView;


    //for scrolling textView [Money & News]......
    private TextView scrollingtextMoney;


    // mainLayoutID & AddItemLayoutID
    private FrameLayout fragment_container;
    private RelativeLayout AddItemLayoutID;
    // AddItemLayoutID Elements
    private Button btnClose_AddItemLayoutID, btnSave_AddItemLayoutID;
    private CountryCodePicker ccp;
    private EditText editText_phoneNumber_AddItemLayoutID;
    private TextView tvEnterPhoneNumber, tvScanQRCode;


    private Handler handler;
    private Runnable r;


    ////////////////////for Time Clock....
    private Time mTime;
    private Handler handlerTime, handlerTime_locationUpdate;
    private Runnable runnableTime, runnableTime_locationUpdate;

    private TextView timeClock, DateClock, DayClock;

    ///////////////////////////////////////////


    //Activity Life Cycle .......

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        initialize();


        // getTabletCurrentLocation();



    }


    @Override
    protected void onStart() {
        super.onStart();

       // tabletLocation(true);

        tablet_isActive = true;
        tabletCurrentData.setActive(true);
       // updateTablet_Location_Status();
     //   updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), true);

      //  ADS_VideoView.start();


        hideSoftKeyboard_closeAllDialog();

        // location.beginUpdates();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        tablet_isActive = true;
        tabletCurrentData.setActive(true);
     //   updateTablet_Location_Status();
       // updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), true);

        hideSoftKeyboard_closeAllDialog();

        //   location.beginUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        tablet_isActive = false;
        tabletCurrentData.setActive(false);
       // updateTablet_Location_Status();
       // updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), false);

       tabletLocation(false);

        //   scrollingTextView_Money();
        //scrollingTextView_News();


      //  ADS_VideoView.pause();
      //    operations.exoPlayer.setPlayWhenReady(false);

        pausePlayer();

        // stop location updates (saves battery)
        // location.endUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();

        tabletLocation(true);

        tablet_isActive = true;
        tabletCurrentData.setActive(true);
      //  updateTablet_Location_Status();
       // updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), true);

        hideSoftKeyboard_closeAllDialog();
        //for scrolling textView [Money & News].....
        //scrollingTextView_Money();
        // scrollingTextView_News();

     //   ADS_VideoView.resume();
        //operations.exoPlayer.setPlayWhenReady(true);

        startPlayer();

        // make the device update its location
        //     location.beginUpdates();

    }

    @Override
    protected void onStop() {
        super.onStop();

        tablet_isActive = false;
        tabletCurrentData.setActive(false);
      //  updateTablet_Location_Status();
       // updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), false);

        handler.removeCallbacks(r);


       // ADS_VideoView.pause();
       // operations.exoPlayer.setPlayWhenReady(false);

        pausePlayer();


        //  recreate();

        //   t.interrupt();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handlerTime_locationUpdate.removeCallbacks(runnableTime_locationUpdate);

        tablet_isActive = false;
        tabletCurrentData.setActive(false);
      //  updateTablet_Location_Status();
       // updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), false);

        tabletLocation(false);

       // ADS_VideoView.pause();

        // t.destroy();
    }


    private void pausePlayer(){
        //operations.exoPlayer.setPlayWhenReady(false);

        try {
            operations.exoPlayer.setPlayWhenReady(false);
            operations.exoPlayer.getPlaybackState();
        }catch (Exception e){
         //   Toast.makeText(this, "Pause Player..." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void startPlayer(){
        //operations.exoPlayer.setPlayWhenReady(true);
        try {
            operations.exoPlayer.setPlayWhenReady(true);
            operations.exoPlayer.getPlaybackState();
        }catch (Exception e){
            //Toast.makeText(this, "Start Player..." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void initialize() {


        tabletCurrentData = new TabletCurrentData();

        counterOperations = new CounterOperations(MainActivity.this);

        //  RequestPermission();

        initializeLayouts();

        adminPatternView = findViewById(R.id.adminPatternView);
        adminSerialNoView = findViewById(R.id.adminSerialNoView);
        userView = findViewById(R.id.userView);
        Admin_User_layoutVisibility("userView");

        pattern_lock_view_admin = findViewById(R.id.pattern_lock_view_admin);
        app_logo = findViewById(R.id.app_logo);
        app_logo.setOnLongClickListener(this);


        ADS_VideoView = findViewById(R.id.ADS_VideoView);
        ADS_VideoView.setOnClickListener(this);

        ADS_exoPlayer_VideoView = findViewById(R.id.ADS_exoPlayer_VideoView);
      //  ADS_exoPlayer_VideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
      //  ADS_exoPlayer_VideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
      //  ADS_exoPlayer_VideoView.setOnClickListener(this);




        ADS_VideoView_defaultImage = findViewById(R.id.ADS_VideoView_defaultImage);
        ADS_ImageView = findViewById(R.id.ADS_ImageView);
        // ADS_ImageView.setOnClickListener(this);

        scrollingtextMoney = (TextView) findViewById(R.id.scrollingtextMoney);
        scrollingtextMoney.setSelected(true);


        operations = new Operations(this, ADS_exoPlayer_VideoView, ADS_VideoView, ADS_VideoView_defaultImage, ADS_ImageView, scrollingtextMoney);

        Operations.hideKeyboard(this);

        //To Set Language ... Default Language is [English]
        //sharedPreference Storage
        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        savedLanguage = sharedPreferences.getString("Language", "English");

        //123321123321

        //For Tablet Serial Number...
        tabletSerialNumber();

        operations.setAppLocale(savedLanguage);


        // mainLayoutID & AddItemLayoutID
        fragment_container = findViewById(R.id.fragment_container);
        AddItemLayoutID = findViewById(R.id.AddItemLayoutID);
        // AddItemLayoutID Elements
        btnClose_AddItemLayoutID = findViewById(R.id.btnClose_AddItemLayoutID);
        btnClose_AddItemLayoutID.setOnClickListener(this);
        btnSave_AddItemLayoutID = findViewById(R.id.btnSave_AddItemLayoutID);
        btnSave_AddItemLayoutID.setOnClickListener(this);
        btnSave_AddItemLayoutID.setText(R.string.save);

        editText_phoneNumber_AddItemLayoutID = findViewById(R.id.editText_phoneNumber_AddItemLayoutID);
        editText_phoneNumber_AddItemLayoutID.setTransformationMethod(new NumberKeyBoardTransformationMethod());
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editText_phoneNumber_AddItemLayoutID);

        tvEnterPhoneNumber = findViewById(R.id.tvEnterPhoneNumber);
        tvEnterPhoneNumber.setText(R.string.please_enter_your_phone_number);
        tvScanQRCode = findViewById(R.id.tvScanQRCode);
        tvScanQRCode.setText(R.string.or_scan_qr_code);


        ////////////////////for Time Clock....
        setUpClockTime();
        //////////////// Update location every 20 Min ( 1200000 Milli Second )
        locationUpdateTimer();

    }

    public void Admin_User_layoutVisibility(String layoutName) {

        switch (layoutName) {

            case "userView":
                userView.setVisibility(View.VISIBLE);
                adminPatternView.setVisibility(View.GONE);
                adminSerialNoView.setVisibility(View.GONE);
                Operations.hideKeyboard(MainActivity.this);

                //define main fragment
                //mainFragmentToShow();
                break;

            case "adminPatternView":
                userView.setVisibility(View.GONE);
                adminPatternView.setVisibility(View.VISIBLE);
                adminSerialNoView.setVisibility(View.GONE);
                Operations.hideKeyboard(MainActivity.this);
                break;

            case "adminSerialNoView":
                userView.setVisibility(View.GONE);
                adminPatternView.setVisibility(View.GONE);
                adminSerialNoView.setVisibility(View.VISIBLE);
                // Operations.hideKeyboard(MainActivity.this);
                break;
        }


    }

    //////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.button_addTabletSerialNo:
                addTabletSerialNumber();
                break;


            case R.id.btn_selectLang:
                switch (selectedLayout) {

                    case "counterLayout":
                        showingLayout("languageLayout");
                        break;

                    case "languageLayout":
                        showingLayout("counterLayout");
                        break;

                }
                break;

            case R.id.btn_englishLang:
                editor.putString("Language", "English");
                editor.apply();
                recreate();

                // selectLang("English");
                break;

            case R.id.btn_arabicLang:
                editor.putString("Language", "Arabic");
                editor.apply();
                recreate();

                // selectLang("Arabic");
                break;

            case R.id.btn_tagalogLang:
                editor.putString("Language", "Tagalog");
                editor.apply();
                recreate();

                // selectLang("Telugu");
                break;

            case R.id.btn_urduLang:
                editor.putString("Language", "Urdu");
                editor.apply();
                recreate();

                // selectLang("Urdu");
                break;


            case R.id.ADS_VideoView:

                /*
                Uri mUri = null;
                try {
                    Field mUriField = VideoView.class.getDeclaredField("mUri");
                    mUriField.setAccessible(true);
                    mUri = (Uri)mUriField.get(ADS_VideoView);
                } catch(Exception e) {}

                Toast.makeText(this, "ADS VideoModel Clicked! +  " + VideoList.indexOf(mUri.toString()) , Toast.LENGTH_SHORT).show();
               */


                //get index of current VideoModel...
                //   Toast.makeText(this, "ADS VideoModel Clicked! +  " + operations.getCurrentVideoIndex() , Toast.LENGTH_SHORT).show();


                /*
                Toast.makeText(MainActivity.this, "ID: " + videosViewModel.getVideos(video_Ch_Id).getValue().get(operations.getCurrentVideoIndex()).getVideo_ID()
                        +" ,Title_En: " + videosViewModel.getVideos(video_Ch_Id).getValue().get(operations.getCurrentVideoIndex()).getVideo_Title_En()
                        +" ,Title_Ar: " + videosViewModel.getVideos(video_Ch_Id).getValue().get(operations.getCurrentVideoIndex()).getVideo_Title_Ar()
                        +" ,Status: " + videosViewModel.getVideos(video_Ch_Id).getValue().get(operations.getCurrentVideoIndex()).getVideo_Status(), Toast.LENGTH_LONG).show();
*/


                break;

            case R.id.ADS_ImageView:
/*
                //get index of current Image...
                Toast.makeText(this, "ADS Banner Clicked ! +  " + operations.getCurrentImageIndex(), Toast.LENGTH_SHORT).show();
*/
                break;

            case R.id.btnClose_AddItemLayoutID:
                VisibleLayout("fragment_container");
                //  hideKeyboard(MainActivity.this);
                break;


            case R.id.btnSave_AddItemLayoutID:
                String phoneNumber_withPlus = ccp.getFullNumberWithPlus();
                String phoneNumber_withoutPlus = editText_phoneNumber_AddItemLayoutID.getText().toString().trim();


                if (phoneNumber_withoutPlus.isEmpty()) {
                    editText_phoneNumber_AddItemLayoutID.setError("Phone Number Is Required");
                    editText_phoneNumber_AddItemLayoutID.requestFocus();
                    return;
                }


                if (!ccp.isValidFullNumber()) {
                    editText_phoneNumber_AddItemLayoutID.setError("Enter Valid Phone Number");
                    editText_phoneNumber_AddItemLayoutID.requestFocus();
                    return;
                }


                /*
                if (phoneNumber.length() != 8){
                    editText_phoneNumber_AddItemLayoutID.setError("Enter Valid Phone Number");
                    editText_phoneNumber_AddItemLayoutID.requestFocus();
                    return;
                }
                 */



                /*
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber_withPlus));
                intent.putExtra("sms_body", "Welcome Abdullah");
                startActivity(intent);
                */


               // Toast.makeText(MainActivity.this, "Your Phone Number Is : " + phoneNumber_withPlus, Toast.LENGTH_SHORT).show();
                VisibleLayout("fragment_container");
                //  dialog.dismiss();
                // Operations.hideKeyboard(MainActivity.this);

                //  onResume();
                break;


        }

    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    //To Stop Volume [Down & Up] Press....
/*
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(this, "Volume can't change!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
*/

/////////////////////////////////////////////////////////////////////////


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d("Focus debug", "Focus changed !");


        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !");
          //  Toast.makeText(this, "Window Focus Changed", Toast.LENGTH_SHORT).show();
            try {

                // hideSoftKeyboard_closeAllDialog();

                // getWindow().getDecorView().setSystemUiVisibility(flags);


                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);


            } catch (Exception e) {
             //   Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
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
        editText_addTabletSerialNo = findViewById(R.id.editText_addTabletSerialNo);
        editText_addTabletSerialNo.setTransformationMethod(new NumberKeyBoardTransformationMethod());
        button_addTabletSerialNo = findViewById(R.id.button_addTabletSerialNo);
        button_addTabletSerialNo.setOnClickListener(this);

        if (!tabletSerialNo.equals("") && !tabletSerialNo.isEmpty() && !tabletSerialNo.equals(null)) {
            getTabletData_And_Channels();
            // operations.hideKeyboard(this);
        } else {
            Admin_User_layoutVisibility("adminSerialNoView");
        }
    }

    private void initializeLayouts() {
        //To Select App. Language ....
        btn_selectLang = findViewById(R.id.btn_selectLang);
        btn_selectLang.setOnClickListener(this);
        btn_englishLang = findViewById(R.id.btn_englishLang);
        btn_englishLang.setOnClickListener(this);
        btn_arabicLang = findViewById(R.id.btn_arabicLang);
        btn_arabicLang.setOnClickListener(this);
        btn_tagalogLang = findViewById(R.id.btn_tagalogLang);
        btn_tagalogLang.setOnClickListener(this);
        btn_urduLang = findViewById(R.id.btn_urduLang);
        btn_urduLang.setOnClickListener(this);
        languageLayout = findViewById(R.id.languageLayout);
        counterLayout = findViewById(R.id.counterLayout);

        showingLayout("counterLayout");
    }

    public void showingLayout(String layoutName) {


        switch (layoutName) {

            case "counterLayout":
                counterLayout.setVisibility(View.VISIBLE);
                languageLayout.setVisibility(View.GONE);
                selectedLayout = "counterLayout";
                break;

            case "languageLayout":
                counterLayout.setVisibility(View.GONE);
                languageLayout.setVisibility(View.VISIBLE);
                selectedLayout = "languageLayout";
                break;
        }

    }


    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void mainFragmentToShow() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecyclerViewFragment()).commit();
    }


    //Visible of Layout   [fragment_container  or AddItemLayoutID] ..........
    public void VisibleLayout(String LayoutToVisible) {

        switch (LayoutToVisible) {
            case "fragment_container":
                fragment_container.setVisibility(View.VISIBLE);
                AddItemLayoutID.setVisibility(View.GONE);
                //hideKeyboard(this);
                Operations.hideKeyboard(MainActivity.this);
                break;

            case "AddItemLayout":
                fragment_container.setVisibility(View.GONE);
                AddItemLayoutID.setVisibility(View.VISIBLE);
                editText_phoneNumber_AddItemLayoutID.setText(null);
                break;
        }

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


    private void addTabletSerialNumber() {
        tabletSerialNo = editText_addTabletSerialNo.getText().toString().trim();

        if (tabletSerialNo.isEmpty()) {
            editText_addTabletSerialNo.setError("Serial Number Required");
            editText_addTabletSerialNo.requestFocus();
            return;
        }

        tabletSerialNoChecker(tabletSerialNo);

        // tabletSerialNoChecker(editText_addTabletSerialNo.getText().toString());
        /*
        if (!tabletSerialNoChecker( tabletSerialNo)){
            editText_addTabletSerialNo.setError("Enter Valid Serial Number");
            editText_addTabletSerialNo.requestFocus();
            return;
        }else {
            editor.putString("tabletSerialNo", tabletSerialNo);
            editor.apply();
            recreate();
        }
         */

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        //selectLanguage = menu.findItem(R.id.menu_language);

        English_Lang = menu.findItem(R.id.English_Lang);
        English_Lang.setOnMenuItemClickListener(this);
        Arabic_Lang = menu.findItem(R.id.Arabic_Lang);
        Arabic_Lang.setOnMenuItemClickListener(this);
        tagalog_Lang = menu.findItem(R.id.Tagalog_Lang);
        tagalog_Lang.setOnMenuItemClickListener(this);
        Urdu_Lang = menu.findItem(R.id.Urdu_Lang);
        Urdu_Lang.setOnMenuItemClickListener(this);


        txtLanguage = menu.findItem(R.id.txt_lang_menu_item);

        View actionView = txtLanguage.getActionView();

        if (actionView != null) {
            txtLanguageView = actionView.findViewById(R.id.txtLang);
        }

        switch (savedLanguage) {
            case "en":
                txtLanguageView.setText("English");
                break;
            case "ar":
                txtLanguageView.setText("العربية");
                break;
            case "phi":
                txtLanguageView.setText("తెలుగు");
                break;
            case "hi":
                txtLanguageView.setText("اردو");
                break;

        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.English_Lang:
                changeLanguage("en");
                break;
            case R.id.Arabic_Lang:
                changeLanguage("ar");
                break;
            case R.id.Tagalog_Lang:
                changeLanguage("phi");
                break;
            case R.id.Urdu_Lang:
                changeLanguage("hi");
                break;

        }

        return false;
    }


    private void changeLanguage(String language) {

      //  Toast.makeText(this, "Selected Language: " + language, Toast.LENGTH_SHORT).show();

        //To save Language into sharedPreference Storage
        editor.putString("Language", language);
        editor.apply();
        operations.setAppLocale(language);


        switch (language) {

            case "en":
                txtLanguageView.setText("English");
                break;

            case "ar":
                txtLanguageView.setText("العربية");
                break;

            case "phi":
                txtLanguageView.setText("తెలుగు");
                break;

            case "hi":
                txtLanguageView.setText("اردو");
                break;
        }


        // recreate();
        // getVideosList();
        // setADSImages();
        // scrollingTextView_Money();
        //scrollingTextView_News();




        /*
        scrollMoney.postDelayed(new Runnable() {
            public void run() {
                scrollMoney.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
*/
    }


    //TO Make Keyboard Of Enter Phone Number Is Numbers Only ......
    private class NumberKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }


//////////////////////////////////////////////////////

    private void hideSoftKeyboard_closeAllDialog() {


        try {


            handler = new Handler();

            r = new Runnable() {
                public void run() {

                    try {
                        // getWindow().getDecorView().setSystemUiVisibility(flags);


                        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(closeDialog);
                    } catch (Exception e) {
                    }


                    handler.postDelayed(this, 500);
                }
            };

            handler.postDelayed(r, 500);

        } catch (Exception e) {
        }
    }


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

        }

    }

    //Get Tablet Data & Channels from API ...
    private void getTabletData_And_Channels() {

        try {


            tabletChannelsViewModel = ViewModelProviders.of(this).get(TabletChannelsViewModel.class);

            tabletChannelsViewModel.getTabletChannel(tabletSerialNo, MainActivity.this).observe(this, new Observer<List<TabletChannelModel>>() {
                @Override
                public void onChanged(@Nullable List<TabletChannelModel> channelModelList) {


                    //Make all Channels ID equal to 0 (ID = 0)
                    editor.putInt("Video_En_Channel_ID", 0);
                    editor.putInt("Video_Ar_Channel_ID", 0);
                    editor.putInt("Video_Ta_Channel_ID", 0);
                    editor.putInt("Video_Ur_Channel_ID", 0);
                    editor.apply();

                    //  Toast.makeText(MainActivity.this, "Changed Now", Toast.LENGTH_SHORT).show();

                    for (int TabletChannelNo = 0; TabletChannelNo < channelModelList.size(); TabletChannelNo++) {

                        //   Toast.makeText(MainActivity.this, "No: " + VideosList.get(0).getVideo_URL(), Toast.LENGTH_LONG).show();

                        String Lang = channelModelList.get(TabletChannelNo).getChannel_Language();
                        //Toast.makeText(MainActivity.this, "Last Lang.: " + channelModelList.get(TabletChannelNo).getChannel_Language(), Toast.LENGTH_SHORT).show();

                        switch (Lang) {

                            case "English":
                                editor.putInt("Video_En_Channel_ID", channelModelList.get(TabletChannelNo).getChannel_ID());

                                //  Toast.makeText(MainActivity.this, "Video_En_Channel_ID: "+ channelModelList.get(TabletChannelNo).getChannel_ID(), Toast.LENGTH_SHORT).show();
                                break;

                            case "Arabic":
                                editor.putInt("Video_Ar_Channel_ID", channelModelList.get(TabletChannelNo).getChannel_ID());

                                //   Toast.makeText(MainActivity.this, "Video_Ar_Channel_ID: "+ channelModelList.get(TabletChannelNo).getChannel_ID(), Toast.LENGTH_SHORT).show();
                                break;

                            case "Tagalog":
                                editor.putInt("Video_Ta_Channel_ID", channelModelList.get(TabletChannelNo).getChannel_ID());

                                //     Toast.makeText(MainActivity.this, "Video_Ta_Channel_ID: "+ channelModelList.get(TabletChannelNo).getChannel_ID(), Toast.LENGTH_SHORT).show();
                                break;

                            case "Urdu":
                                editor.putInt("Video_Ur_Channel_ID", channelModelList.get(TabletChannelNo).getChannel_ID());

                                // Toast.makeText(MainActivity.this, "Video_Ur_Channel_ID: "+ channelModelList.get(TabletChannelNo).getChannel_ID(), Toast.LENGTH_SHORT).show();
                                break;
                        }

                        editor.apply();

                    }


                }
            });


            operations.selectLang(savedLanguage, btn_selectLang);

            getTabletPassword();

            mainFragmentToShow();

            getTabletCurrentLocation();


        } catch (Exception e) {

        }
    }


    //Get Tablet Data & Channels from API ...
    private void tabletSerialNoChecker(final String SerialNo) {


        ViewModelProviders.of(this).get(TabletChannelsViewModel.class).getTabletChannel(SerialNo, MainActivity.this).observe(this, new Observer<List<TabletChannelModel>>() {
            @Override
            public void onChanged(@Nullable List<TabletChannelModel> channelModelList) {

                if (channelModelList.isEmpty()) {
                   // Toast.makeText(MainActivity.this, "Tablet Not Valid " + SerialNo, Toast.LENGTH_SHORT).show();

                    editText_addTabletSerialNo.setError("Enter Valid Serial Number");
                    editText_addTabletSerialNo.requestFocus();
                    recreate();


                } else {
                 //   Toast.makeText(MainActivity.this, "Tablet Valid", Toast.LENGTH_SHORT).show();
                    editor.putString("tabletSerialNo", tabletSerialNo);
                    editor.apply();
                    operations.hideKeyboard(MainActivity.this);
                    recreate();

                }

            }
        });


    }


    private void updateTablet_Location_Status(final double Lat , final double Long , final boolean isActive) {


        tabletCurrentData.setTabletSerialNo(tabletSerialNo);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RoutesApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoutesApi api = retrofit.create(RoutesApi.class);

        Call<ResponseBody> call = api.PutCurrentTabletData(tabletCurrentData.getTabletSerialNo(), Lat, Long, isActive);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //    Toast.makeText(MainActivity.this, "Put Successfully!", Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(getApplicationContext(), "Post Updated Title: "+response.body().getTitle()+" Body: "+response.body().getBody()+" PostId: "+response.body().getId(), Toast.LENGTH_LONG).show();

                    Toast.makeText(MainActivity.this, "  Latitude: " + Lat + "  ,Longitude: " +  Long + " ,Active: " + isActive, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              //  Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                recreate();
            }
        });


    }


    private void getTabletCurrentLocation() {

        RequestPermission();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();


        // user defines the criteria
        criteria.setCostAllowed(false);


        //for release apk
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default



         provider = locationManager.getBestProvider(criteria, false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);




        mylistener = new MyLocationListener();




        if (location != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mylistener.onLocationChanged(location);
            }


        } else {
            // leads to the settings because there is no last known location
          //  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
           // startActivity(intent);
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);


        if (location != null) {
            tabletCurrentData.setLat(location.getLatitude());
            tabletCurrentData.setLng(location.getLongitude());
            updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), true);

          //  Toast.makeText(this, "First...  Latitude: " + tabletCurrentData.getLat() + "  ,Longitude: " +  tabletCurrentData.getLng(), Toast.LENGTH_LONG).show();
        }

    }



    private class MyLocationListener implements LocationListener {



        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                tabletCurrentData.setLat(location.getLatitude());
                tabletCurrentData.setLng(location.getLongitude());
              //  updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), true);

            //    Toast.makeText(MainActivity.this, "Changed...  Latitude: " + tabletCurrentData.getLat() + "  ,Longitude: " +  tabletCurrentData.getLng(), Toast.LENGTH_LONG).show();
            }

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //    Toast.makeText(MainActivity.this, provider + "'s status changed to " + status + "!", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!", Toast.LENGTH_SHORT).show();

        }



    }



    //for Request Permissions
    public void RequestPermission() {
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


    private void tabletLocation(boolean IsActive){
        updateTablet_Location_Status(tabletCurrentData.getLat(), tabletCurrentData.getLng(), IsActive);
       // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }


    private void locationUpdateTimer(){

        runnableTime_locationUpdate = new Runnable() {
            @Override
            public void run() {

                tabletLocation(true);



                 //20 Min = 20 Min * 60 Sec * 60 Milli Second = 1200000
                handlerTime_locationUpdate.postDelayed(runnableTime_locationUpdate, 1200000);

            }
        };

        handlerTime_locationUpdate = new Handler();
        handlerTime_locationUpdate.postDelayed(runnableTime_locationUpdate, 1200000);

    }

}