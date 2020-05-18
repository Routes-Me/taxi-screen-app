package com.routesme.taxi_screen.java.View.Login;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.routesme.taxi_screen.java.Model.TabletInfoViewModel;
import com.routesme.taxi_screen.kotlin.Class.App;
import com.routesme.taxi_screen.kotlin.Class.DateOperations;
import com.routesme.taxi_screen.kotlin.Class.Operations;
import com.routesme.taxi_screen.kotlin.Model.Authorization;
import com.routesme.taxi_screen.kotlin.Model.TabletCredentials;
import com.routesme.taxi_screen.kotlin.Model.TabletInfo;
import com.routesme.taxi_screen.kotlin.View.LoginScreens.LoginScreen;
import com.routesme.taxi_screen.kotlin.View.ModelPresenter;
import com.routesme.taxiscreen.R;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import kotlin.jvm.internal.Intrinsics;

public class TaxiInformationScreen extends AppCompatActivity implements View.OnClickListener {

    private App app;
    private TabletCredentials tabletCredentials;
    private Operations operations;
    public static final int READ_PHONE_STATE_REQUEST_CODE = 101;
    private static final String List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Toolbar myToolbar;
    private TelephonyManager telephonyManager;
    private TextView tabletSerialNumber_tv, SimCardNumber_tv, taxiOffice_tv, taxiOffice_error_tv, taxiPlateNumber_tv, taxiPlateNumber_error_tv;
    private Button register_btn;
    private AlertDialog dialog;
    private int taxiOfficeId = 0;
    private boolean showRationale = true, getDeviceInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        initialize();
    }
    private void initialize() {
       // requestRuntimePermissions();
        operations = new Operations();
        app = (App) getApplicationContext();
        tabletCredentials = new TabletCredentials();
        initializeViews();
        sharedPreferencesStorage();

        getTabletInfo();
    }


    private void requestRuntimePermissions() {
        String[] permissionsList = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        if (!hasPermissions(permissionsList)) {
            ActivityCompat.requestPermissions(this, permissionsList, 1);
        }

    }

    private boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeViews() {
        toolbarSetUp();
        initLayoutViews();
        initProgressDialog();
    }
    private void initLayoutViews() {
        taxiOffice_tv = findViewById(R.id.taxiOffice_tv);
        taxiOffice_tv.setOnClickListener(this);
        taxiOffice_error_tv = findViewById(R.id.taxiOffice_error_tv);
        taxiPlateNumber_tv = findViewById(R.id.taxiPlateNumber_tv);
        taxiPlateNumber_tv.setOnClickListener(this);
        taxiPlateNumber_error_tv = findViewById(R.id.taxiPlateNumber_error_tv);
        tabletSerialNumber_tv = findViewById(R.id.deviceSerialNumber_tv);
        tabletSerialNumber_tv.setOnClickListener(this);
        SimCardNumber_tv = findViewById(R.id.SimCardNumber_tv);
        SimCardNumber_tv.setOnClickListener(this);
        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
    }
    private void initProgressDialog() {
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build();
    }
    private void sharedPreferencesStorage() {
        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);

    }
    private void toolbarSetUp() {
        myToolbar = findViewById(R.id.MyToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(welcomeMessage());
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
    }
    private String welcomeMessage() {
        String username = app.getAuthCredentials().getUsername();
        return  "Welcome,  " +  (username != null && !username.isEmpty() ?  username.substring(0, 1).toUpperCase() + username.substring(1) : "");
    }
    @Override
    protected void onRestart() {
        if (tabletCredentials == null){
            tabletCredentials = new TabletCredentials();
        }
        taxiOffice_tv.setEnabled(true);
        taxiPlateNumber_tv.setEnabled(true);
        operations.enableNextButton(register_btn, true);
        getTabletInfo();
        taxiOfficeId = app.getTaxiOfficeId();
        tabletCredentials.setTaxiOfficeId(app.getTaxiOfficeId());
        tabletCredentials.setTaxiPlateNumber(app.getTaxiPlateNumber());

        taxiOffice_tv.setText(showTaxiOfficeName(app.getTaxiOfficeName()));
        taxiPlateNumber_tv.setText(showTaxiPlateNumber(tabletCredentials.getTaxiPlateNumber()));

        super.onRestart();
    }
    private String showTaxiOfficeName(String taxiOfficeName){
        return taxiOfficeName != null && !taxiOfficeName.isEmpty() ? taxiOfficeName : null;
    }
    private String showTaxiPlateNumber(String taxiPlateNumber){
        return taxiPlateNumber != null && !taxiPlateNumber.isEmpty() ? taxiPlateNumber : null;
    }
    @SuppressLint("HardwareIds")
    private void getTabletInfo() {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
            return;
        } else {
            tabletCredentials.setDeviceId(telephonyManager.getDeviceId());
            tabletCredentials.setSimSerialNumber(telephonyManager.getSimSerialNumber());
            tabletSerialNumber_tv.setText(tabletCredentials.getDeviceId());
            SimCardNumber_tv.setText(tabletCredentials.getSimSerialNumber());
            showTabletInfoError(false);
        }
    }
    @SuppressLint("HardwareIds")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_PHONE_STATE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
                        return;
                    }
                    getTabletInfo();
                    //showTabletInfoError(false);
                }
                 else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
                        showTabletInfoError(true);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deviceSerialNumber_tv:
                clickOnGetDeviceInfo();
                break;
            case R.id.SimCardNumber_tv:
                clickOnGetDeviceInfo();
                break;
            case R.id.taxiOffice_tv:
                openTaxiOfficesList();
                break;
            case R.id.taxiPlateNumber_tv:
                openTaxiOfficePlateNumbersList();
                break;

            case R.id.register_btn:
                TabletRegister();
                break;
        }
    }
    private void openTaxiOfficesList() {
        taxiOffice_tv.setEnabled(false);
        openDataList(Offices_STR);
        showInputError(false, 0);
    }
    private void openTaxiOfficePlateNumbersList() {
        if (taxiOfficeId > 0) {
            taxiPlateNumber_tv.setEnabled(false);
            openDataList(Office_Plates_STR);
        } else {
            showInputError(true, 1);
            return;
        }
        showInputError(false, 0);
    }
    private void openDataList(String listType) {
        startActivity(new Intent(this, TaxiInformationListScreen.class).putExtra(List_Type_STR, listType));
    }
    private void TabletRegister() {
        if (Token() != null && AllDataExist()) {
            operations.enableNextButton(register_btn, false);
            dialog.show();
            TabletInfoViewModel tabletInfoViewModel = ViewModelProviders.of((FragmentActivity) this).get(TabletInfoViewModel.class);
            tabletInfoViewModel.getTabletInfo(this, Token(), tabletCredentials, dialog, register_btn).observe((LifecycleOwner) this, new Observer<TabletInfo>() {
                @Override
                public void onChanged(TabletInfo tabletInfo) {
                    saveTabletInfoIntoSharedPreferences(tabletInfo);
                    openModelPresenterScreen();
                }
            });
        }
    }
    private String Token() {
        String savedToken = sharedPreferences.getString("tabToken", null);
        return savedToken != null && !savedToken.isEmpty() ? "Bearer " + savedToken : null;
    }
    private boolean AllDataExist() {
        if (taxiOfficeIdExist() && taxiPlateNumberExist() && tabletInformationExist()){
            return true;
        }else {
            return false;
        }
    }
    private boolean taxiOfficeIdExist() {
        taxiOfficeId = tabletCredentials.getTaxiOfficeId();
        if (taxiOfficeId <= 0) {
            showInputError(true, 1);
            return false;
        }else {
            return true;
        }
    }
    private boolean taxiPlateNumberExist() {
       String taxiPlateNumber = tabletCredentials.getTaxiPlateNumber();
        if (taxiPlateNumber == null || taxiPlateNumber.isEmpty()) {
            showInputError(true, 2);
            return false;
        }else {
            return true;
        }
    }
    private boolean tabletInformationExist() {
       String tabletSerialNumber = tabletCredentials.getDeviceId();
       String simCardNumber = tabletCredentials.getSimSerialNumber();
        if (tabletSerialNumber == null || tabletSerialNumber.isEmpty() || simCardNumber == null || simCardNumber.isEmpty()) {
            showTabletInfoError(true);
            return false;
        }else {
            return true;
        }
    }
    private void saveTabletInfoIntoSharedPreferences(TabletInfo tabletInfo){
        if (editor == null){
            editor = sharedPreferences.edit();
        }
        editor.putString("technicalUserName", Objects.requireNonNull(app.getAuthCredentials()).getUsername());
        editor.putString("registrationDate", new DateOperations().registrationDate(new Date()));
        editor.putString("tabletPassword", tabletInfo.getTabletPassword());
        editor.putInt("tabletChannelId", tabletInfo.getTabletChannelId());
        editor.putInt("taxiOfficeId", tabletCredentials.getTaxiOfficeId());
        editor.putString("institutionName",app.getTaxiOfficeName());
        editor.putString("taxiPlateNumber", tabletCredentials.getTaxiPlateNumber());
        editor.putString("tabletSerialNo", tabletCredentials.getDeviceId());
        editor.putString("simCardNumber", tabletCredentials.getSimSerialNumber());
        editor.apply();
    }
    private void openModelPresenterScreen() {
        Authorization authorization = new Authorization(true,200);
        Intent ModelPresenterScreenIntent = new Intent(TaxiInformationScreen.this, ModelPresenter.class);
        ModelPresenterScreenIntent.putExtra("authorization",authorization);
        startActivity(ModelPresenterScreenIntent);
        finish();
    }
    private void clickOnGetDeviceInfo() {
        if (!showRationale && !getDeviceInfo) {
            new AlertDialog.Builder(this)
                    .setTitle("Phone Permission required")
                    .setMessage("Enable phone permission from app settings is required to get device information")

                    .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showTabletInfoError(false);
                            startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showTabletInfoError(true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();

        } else if (!getDeviceInfo) {
            getTabletInfo();
        }
    }
    private void showInputError(boolean show, int requireField) {
        if (show) {
            switch (requireField) {
                case 1:
                    taxiOffice_error_tv.setVisibility(View.VISIBLE);
                    taxiPlateNumber_error_tv.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    taxiOffice_error_tv.setVisibility(View.INVISIBLE);
                    taxiPlateNumber_error_tv.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            taxiOffice_error_tv.setVisibility(View.INVISIBLE);
            taxiPlateNumber_error_tv.setVisibility(View.INVISIBLE);
        }
    }
    private void showTabletInfoError(boolean show) {
        if (show) {
            tabletSerialNumber_tv.setError("Click here to get serial number");
            tabletSerialNumber_tv.requestFocus();
            SimCardNumber_tv.setError("Click here to get SIM card number");
            SimCardNumber_tv.requestFocus();
            getDeviceInfo = false;
            return;
        } else {
            tabletSerialNumber_tv.setError(null);
            tabletSerialNumber_tv.clearFocus();
            tabletSerialNumber_tv.setClickable(false);
            SimCardNumber_tv.setError(null);
            SimCardNumber_tv.clearFocus();
            SimCardNumber_tv.setClickable(false);
            getDeviceInfo = true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            app.setNewLogin(true);
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}