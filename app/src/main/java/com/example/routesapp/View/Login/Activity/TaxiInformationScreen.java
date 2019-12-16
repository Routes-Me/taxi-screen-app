package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.routesapp.Class.App;
import com.example.routesapp.R;
import com.example.routesapp.View.Activity.MainActivity;
import com.example.routesapp.View.Login.TaxiInformationListScreen;

public class TaxiInformationScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String   List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";

    private App app;

    private Toolbar myToolbar;

    private TelephonyManager telephonyManager;

    private TextView deviceSerialNumber_tv, taxiOffice_tv, taxiOffice_error_tv, taxiPlateNumber_tv, taxiPlateNumber_error_tv;
    private Button register_btn;

    private int taxiOfficeId = 0;
    private String taxiOfficeName = null, taxiPlateNumber = null;


    private boolean showRationale = true, getTabletSerialNumber = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        app = (App) getApplicationContext();
        //app.setTechnicalSupportName("Abdullah Soubeih");

        ToolbarSetUp();



     //   startActivity(new Intent(this, MainActivity.class));
       // finish();


        deviceSerialNumber_tv = findViewById(R.id.deviceSerialNumber_tv);
        deviceSerialNumber_tv.setOnClickListener(this);
        taxiOffice_tv = findViewById(R.id.taxiOffice_tv);
        taxiOffice_tv.setOnClickListener(this);
        taxiOffice_error_tv = findViewById(R.id.taxiOffice_error_tv);
        taxiPlateNumber_tv = findViewById(R.id.taxiPlateNumber_tv);
        taxiPlateNumber_tv.setOnClickListener(this);
        taxiPlateNumber_error_tv = findViewById(R.id.taxiPlateNumber_error_tv);
        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);



        //Get tablet serial number ...
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        getTabletSerialNumber();

    }


    @Override
    protected void onRestart() {
        getTabletSerialNumber();

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

    private void ToolbarSetUp() {
        //Toolbar..
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


    private void getTabletSerialNumber() {
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                return;
            }else {
                deviceSerialNumber_tv.setText(telephonyManager.getDeviceId());
                showTabletSerialNumberError(false);
            }
        }catch (Exception e){
        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    deviceSerialNumber_tv.setText(telephonyManager.getDeviceId());
                    showTabletSerialNumberError(false);
                }
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale( Manifest.permission.READ_PHONE_STATE );

                        showTabletSerialNumberError(true);
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
                clickOnGetDeviceSerialNumber();
                break;

            case R.id.taxiOffice_tv:
                showInputError(false,0);
                 openTaxiOfficesList(Offices_STR);
                break;

            case R.id.taxiPlateNumber_tv:
                showInputError(false,0);
                if (taxiOfficeId > 0) {
                    openTaxiOfficesList(Office_Plates_STR);
                }else {
                    showInputError(true,1);
                    return;
                }


                break;

            case R.id.register_btn:
               //  openHomeScreen();
                break;

        }

    }

    private void openTaxiOfficesList(String listType) {
        try {
            startActivity(new Intent(this, TaxiInformationListScreen.class).putExtra(List_Type_STR,listType));
        }catch (Exception e){}

    }

    private void openHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void clickOnGetDeviceSerialNumber() {
        if (! showRationale && !getTabletSerialNumber) {

            new AlertDialog.Builder(this)
                    .setTitle("Phone Permission required")
                    .setMessage("Enable phone permission from app settings is required to get the serial number")

                    .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            try {

                                showTabletSerialNumberError(false);
                                startActivity( new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));

                            }catch (Exception e){}

                        }
                    })

                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showTabletSerialNumberError(true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();

        }else if (!getTabletSerialNumber){
            getTabletSerialNumber();
        }
    }


    private void showTabletSerialNumberError(boolean show){
        if (show){
            deviceSerialNumber_tv.setError("Click here to get serial number");
            deviceSerialNumber_tv.requestFocus();
            getTabletSerialNumber = false;
            return;
        }else {
            deviceSerialNumber_tv.setError(null);
            deviceSerialNumber_tv.clearFocus();
            getTabletSerialNumber = true;
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
