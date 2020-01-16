package com.routesme.taxi_screen.Hotspot_Configuration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.routesme.taxiscreen.R;

public class HotspotScreen extends AppCompatActivity implements View.OnClickListener {


    private TextView textView;
    private Button enable_btn, disable_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotspot_screen);


        initialize();


    }

    private void initialize() {
        textView = findViewById(R.id.textView);
        enable_btn = findViewById(R.id.enable_btn);
        enable_btn.setOnClickListener(this);
        disable_btn = findViewById(R.id.disable_btn);
        disable_btn.setOnClickListener(this);



    }


    private static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiApControl apControl = WifiApControl.getApControl(wifiManager);
       // if (apControl != null) {

            // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
            turnOnOffWifi(context);




           // try {
                apControl.setWifiApEnabled(apControl.getWifiApConfiguration(), isTurnToOn);



               // Log.d("Change-Hotspot-State", "hotspot Success with:  " + isTurnToOn  + " ,SSID :" );
           // }catch (Exception e){
             //   Log.d("Change-Hotspot-State", "hotspot has error with:  " + isTurnToOn + "  ,error:  " + e.getMessage());
           // }

       // } else {
       //     Toast.makeText(context, "This device not supported for this features!", Toast.LENGTH_SHORT).show();
       // }
    }



    private static void turnOnOffWifi(Context context) {
        boolean isWifiEnabled;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        isWifiEnabled = wifiManager.isWifiEnabled();
        if (isWifiEnabled){
          //  Toast.makeText(context, "isWifiEnabled: " + isWifiEnabled, Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enable_btn:
                try{
                    turnOnOffHotspot(this,true);
                }catch (Exception e){

                }
                break;

            case R.id.disable_btn:
                try{
                    turnOnOffHotspot(this,false);
                }catch (Exception e){

                }
                break;
        }
    }
}
