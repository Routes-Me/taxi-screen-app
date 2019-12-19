package com.example.routesapp.View.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
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

import com.example.routesapp.Class.App;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.Model.TabletCredentials;
import com.example.routesapp.Model.TabletInfo;
import com.example.routesapp.Model.TabletInfoViewModel;
import com.example.routesapp.R;
import com.example.routesapp.View.LastHomeScreen.Activity.MainActivity;

public class TaxiInformationScreen extends AppCompatActivity implements View.OnClickListener {

    private Operations operations;

    private static final String   List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";

    private App app;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String savedToken = null;


    private TabletInfoViewModel tabletInfoViewModel;

    private Toolbar myToolbar;

    private TelephonyManager telephonyManager;

    private TextView deviceSerialNumber_tv, taxiOffice_tv, taxiOffice_error_tv, taxiPlateNumber_tv, taxiPlateNumber_error_tv;
    private Button register_btn;

    private int taxiOfficeId = 0;
    private String tabletSerialNumber = null, taxiOfficeName = null, taxiPlateNumber = null;


    private boolean showRationale = true, getTabletSerialNumber = false;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        operations = new Operations(this);

        app = (App) getApplicationContext();
        //app.setTechnicalSupportName("Abdullah Soubeih");

        ToolbarSetUp();

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        savedToken = "Bearer " + sharedPreferences.getString("tabToken", null);

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


        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);



        //Get tablet serial number ...
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        getTabletSerialNumber();

    }


    @Override
    protected void onRestart() {

        operations.enableNextButton(register_btn,true);

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
                tabletSerialNumber = telephonyManager.getDeviceId();
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
                openTaxiOfficesList(Offices_STR);
                showInputError(false,0);
                break;

            case R.id.taxiPlateNumber_tv:
                if (taxiOfficeId > 0) {
                    openTaxiOfficesList(Office_Plates_STR);
                }else {
                    showInputError(true,1);
                    return;
                }
                showInputError(false,0);


                break;

            case R.id.register_btn:
                 openHomeScreen();
                break;

        }

    }

    private void openTaxiOfficesList(String listType) {
        try {
            startActivity(new Intent(this, TaxiInformationListScreen.class).putExtra(List_Type_STR,listType));
        }catch (Exception e){}

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

        if (tabletSerialNumber == null || tabletSerialNumber.isEmpty()){
            showTabletSerialNumberError(true);
            return;
        }


        operations.enableNextButton(register_btn,false);
        dialog.show();

        TabletCredentials tabletCredentials = new TabletCredentials(taxiOfficeId, taxiPlateNumber, tabletSerialNumber);



        tabletInfoViewModel = ViewModelProviders.of((FragmentActivity) this).get(TabletInfoViewModel.class);
        tabletInfoViewModel.getTabletInfo(this,savedToken,tabletCredentials, dialog,register_btn).observe((LifecycleOwner) this, new Observer<TabletInfo>() {
            @Override
            public void onChanged(TabletInfo tabletInfo) {
               // Toast.makeText(TaxiInformationScreen.this, "Password:  " + tabletInfo.getTabletPassword() + "  ,Channel ID: " + tabletInfo.getTabletChannelId() , Toast.LENGTH_SHORT).show();




                editor.putString("tabletPassword", tabletInfo.getTabletPassword());
                editor.putInt("tabletChannelId", tabletInfo.getTabletChannelId());
                editor.putString("tabletSerialNo", tabletSerialNumber);
                editor.apply();

                startActivity(new Intent(TaxiInformationScreen.this, MainActivity.class));
                finish();



               // operations.enableNextButton(register_btn,true);
             //   dialog.dismiss();
            }
        });





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
           // enableNextButton(false);
        }else {
            taxiOffice_error_tv.setVisibility(View.INVISIBLE);
            taxiPlateNumber_error_tv.setVisibility(View.INVISIBLE);
          //  enableNextButton(true);
        }




    }





}
