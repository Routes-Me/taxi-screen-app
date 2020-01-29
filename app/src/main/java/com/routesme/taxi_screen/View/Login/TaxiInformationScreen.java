package com.routesme.taxi_screen.View.Login;

import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.routesme.taxi_screen.Class.App;
import com.routesme.taxi_screen.Class.Operations;
import com.routesme.taxi_screen.Model.TabletCredentials;
import com.routesme.taxi_screen.Model.TabletInfo;
import com.routesme.taxi_screen.Model.TabletInfoViewModel;
import com.routesme.taxi_screen.View.NewHomeScreen.Activity.HomeScreen;
import com.routesme.taxiscreen.R;

public class TaxiInformationScreen extends AppCompatActivity implements View.OnClickListener {

    private Operations operations;
    public static final int READ_PHONE_STATE_REQUEST_CODE = 101;
    private static final String   List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";
    private App app;
    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
   // private String savedToken = null;
    private String bearerToken = null;
    private TabletInfoViewModel tabletInfoViewModel;
    private Toolbar myToolbar;
    private TelephonyManager telephonyManager;
    private TextView deviceSerialNumber_tv, SimCardNumber_tv, taxiOffice_tv, taxiOffice_error_tv, taxiPlateNumber_tv, taxiPlateNumber_error_tv;
    private Button register_btn;
    private int taxiOfficeId = 0;
    private String tabletSerialNumber = null, simCardNumber = null , taxiOfficeName = null, taxiPlateNumber = null;
    private boolean showRationale = true, getDeviceInfo = false;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        initialize();

    }

    private void initialize() {
        operations = new Operations(this);
        app = (App) getApplicationContext();

        initializeViews();
        sharedPreferencesStorage();
        getTabletInfo();
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

        deviceSerialNumber_tv = findViewById(R.id.deviceSerialNumber_tv);
        deviceSerialNumber_tv.setOnClickListener(this);
        SimCardNumber_tv = findViewById(R.id.SimCardNumber_tv);
        SimCardNumber_tv.setOnClickListener(this);

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
    }

    private void sharedPreferencesStorage() {
        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    private void toolbarSetUp() {
        myToolbar = findViewById(R.id.MyToolBar);
        setSupportActionBar(myToolbar);
        String username = app.getTechnicalSupportUserName();
        if (username != null && !username.isEmpty()){
            getSupportActionBar().setTitle("Welcome,  " + username.substring(0, 1).toUpperCase() + username.substring(1));
        }else {
            getSupportActionBar().setTitle("Welcome,");
        }
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
    }

    @Override
    protected void onRestart() {
        taxiOffice_tv.setEnabled(true);
        taxiPlateNumber_tv.setEnabled(true);
        operations.enableNextButton(register_btn,true);
        getTabletInfo();
        taxiOfficeId = app.getTaxiOfficeId();
        taxiOfficeName = app.getTaxiOfficeName();
        taxiPlateNumber = app.getTaxiPlateNumber();
        if (taxiOfficeName != null && !taxiOfficeName.isEmpty()){
            taxiOffice_tv.setText(taxiOfficeName);
        }else {
            taxiOffice_tv.setText(null);
        }
        if (taxiPlateNumber != null && !taxiPlateNumber.isEmpty()){
            taxiPlateNumber_tv.setText(taxiPlateNumber);
        }else {
            taxiPlateNumber_tv.setText(null);
        }
        super.onRestart();
    }





    @SuppressLint("HardwareIds")
    private void getTabletInfo() {

        if (telephonyManager == null){
            telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
                return;
            }else {
                tabletSerialNumber = telephonyManager.getDeviceId();
                simCardNumber = telephonyManager.getSimSerialNumber();
                deviceSerialNumber_tv.setText(tabletSerialNumber);
                SimCardNumber_tv.setText(simCardNumber);
                showTabletInfoError(false);
            }
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case READ_PHONE_STATE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
                        return;
                    }
                    tabletSerialNumber = telephonyManager.getDeviceId();
                    simCardNumber = telephonyManager.getSimSerialNumber();
                    deviceSerialNumber_tv.setText(tabletSerialNumber);
                    SimCardNumber_tv.setText(simCardNumber);
                    showTabletInfoError(false);
                }
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale( Manifest.permission.READ_PHONE_STATE );

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
        switch (v.getId()){
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
                 openHomeScreen();
                break;
        }
    }

    private void openTaxiOfficesList() {
            taxiOffice_tv.setEnabled(false);
            openDataList(Offices_STR);
            showInputError(false,0);
    }

    private void openTaxiOfficePlateNumbersList() {
            if (taxiOfficeId > 0) {
                taxiPlateNumber_tv.setEnabled(false);
                openDataList(Office_Plates_STR);
            }else {
                showInputError(true,1);
                return;
            }
            showInputError(false,0);
    }
    private void openDataList(String listType) {
            startActivity(new Intent(this, TaxiInformationListScreen.class).putExtra(List_Type_STR,listType));
    }

    private void openHomeScreen() {
        if (taxiOfficeId <= 0) {
            showInputError(true, 1);
            return;
        }
        if (taxiPlateNumber == null || taxiPlateNumber.isEmpty()){
            showInputError(true, 2);
            return;
        }
        if (tabletSerialNumber == null || tabletSerialNumber.isEmpty() || simCardNumber == null || simCardNumber.isEmpty()){
            showTabletInfoError(true);
            return;
        }
        operations.enableNextButton(register_btn,false);
        dialog.show();
        TabletCredentials tabletCredentials = new TabletCredentials(taxiOfficeId, taxiPlateNumber, tabletSerialNumber, simCardNumber);

        if (Token() != null){
            tabletInfoViewModel = ViewModelProviders.of((FragmentActivity) this).get(TabletInfoViewModel.class);
            tabletInfoViewModel.getTabletInfo(this, Token(),tabletCredentials, dialog,register_btn).observe((LifecycleOwner) this, new Observer<TabletInfo>() {
                @Override
                public void onChanged(TabletInfo tabletInfo) {
                    editor.putString("tabletPassword", tabletInfo.getTabletPassword());
                    editor.putInt("tabletChannelId", tabletInfo.getTabletChannelId());
                    editor.putInt("taxiOfficeId", taxiOfficeId);
                    editor.putString("taxiPlateNumber", taxiPlateNumber);
                    editor.putString("tabletSerialNo", tabletSerialNumber);
                    editor.putString("simCardNumber", simCardNumber);
                    editor.apply();
                    startActivity(new Intent(TaxiInformationScreen.this, HomeScreen.class));
                    finish();
                }
            });
        }
    }


    private String Token() {
        String savedToken =  sharedPreferences.getString("tabToken", null);
        return savedToken != null && !savedToken.isEmpty() ? "Bearer " + savedToken : null;
    }

    private void clickOnGetDeviceInfo() {
        if (! showRationale && !getDeviceInfo) {
            new AlertDialog.Builder(this)
                    .setTitle("Phone Permission required")
                    .setMessage("Enable phone permission from app settings is required to get device information")

                    .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                showTabletInfoError(false);
                                startActivity( new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));
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

        }else if (!getDeviceInfo){
            getTabletInfo();
        }
    }

    private void showTabletInfoError(boolean show){
        if (show){
            deviceSerialNumber_tv.setError("Click here to get serial number");
            deviceSerialNumber_tv.requestFocus();
            SimCardNumber_tv.setError("Click here to get SIM card number");
            SimCardNumber_tv.requestFocus();
            getDeviceInfo = false;
            return;
        }else {
            deviceSerialNumber_tv.setError(null);
            deviceSerialNumber_tv.clearFocus();
            SimCardNumber_tv.setError(null);
            SimCardNumber_tv.clearFocus();
            getDeviceInfo = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            app.setNewLogin(true);
            startActivity(new Intent(this,LoginScreen.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showInputError(boolean show,int requireField){
        if (show){
            switch (requireField){
                case 1:
                    taxiOffice_error_tv.setVisibility(View.VISIBLE);
                    taxiPlateNumber_error_tv.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    taxiOffice_error_tv.setVisibility(View.INVISIBLE);
                    taxiPlateNumber_error_tv.setVisibility(View.VISIBLE);
                    break;
            }
        }else {
            taxiOffice_error_tv.setVisibility(View.INVISIBLE);
            taxiPlateNumber_error_tv.setVisibility(View.INVISIBLE);
        }
    }
}
