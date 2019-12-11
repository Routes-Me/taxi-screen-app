package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routesapp.Class.App;
import com.example.routesapp.R;

public class TaxiInformationScreen extends AppCompatActivity implements View.OnClickListener {

    private App app;

    private Toolbar myToolbar;

    private TelephonyManager telephonyManager;

    private TextView deviceSerialNumber_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);


        app = (App) getApplicationContext();
        app.setTechnicalSupportName("Abdullah Soubeih");

        ToolbarSetUp();



     //   startActivity(new Intent(this, MainActivity.class));
       // finish();


        deviceSerialNumber_et = findViewById(R.id.deviceSerialNumber_et);
        deviceSerialNumber_et.setOnClickListener(this);

        getDeviceId();

    }


    private void ToolbarSetUp() {
        //Toolbar..
        myToolbar = findViewById(R.id.MyToolBar);


        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Welcome,  " + app.getTechnicalSupportName());


        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

    }


    private void getDeviceId() {
        try {
            telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                return;
            }else {
                //Toast.makeText(TaxiInformationScreen.this,telephonyManager.getDeviceId(),Toast.LENGTH_LONG).show();

                deviceSerialNumber_et.setText(telephonyManager.getDeviceId());
                deviceSerialNumber_et.setError(null);//removes error
                deviceSerialNumber_et.clearFocus();
            }
        }catch (Exception e){
         //   Toast.makeText(this, "Device ID Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(TaxiInformationScreen.this,telephonyManager.getDeviceId(),Toast.LENGTH_LONG).show();
                    deviceSerialNumber_et.setText(telephonyManager.getDeviceId());
                    deviceSerialNumber_et.setError(null);//removes error
                    deviceSerialNumber_et.clearFocus();
                } else {
                   // Toast.makeText(TaxiInformationScreen.this,"Without permission we check",Toast.LENGTH_LONG).show();

                  //  if (Link.isEmpty()){
                    deviceSerialNumber_et.setError("Click here to get serial number");
                    deviceSerialNumber_et.requestFocus();
                        return;
                  //  }

                   // deviceSerialNumber_et.setText("Get Tablet Serial Number");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.deviceSerialNumber_et:
                    getDeviceId();
                break;

        }

    }
}
