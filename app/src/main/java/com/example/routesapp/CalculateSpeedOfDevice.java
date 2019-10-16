package com.example.routesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.Toast;

public class CalculateSpeedOfDevice extends AppCompatActivity implements SensorEventListener {


    private SensorManager mSensorManager ;
    private Sensor mSensor ;

    EditText txt ;
    float last_x ;
    float last_y ;
    float last_z ;
    long lastUpdate ;
    int SHAKE_THRESHOLD = 1000 ;

    public float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_speed_of_device);


        txt = (EditText)findViewById(R.id.editText);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100 ){
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime ;

             speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000 ;

            if(speed > SHAKE_THRESHOLD){
                Vibrator v = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);

                // vibrate for 500 milliseconds
                v.vibrate(200);
                txt.setText(txt.getText()+" News shake : "+speed+"\n");

            //   Toast.makeText(this, "Speed : "+speed, Toast.LENGTH_SHORT).show();
            }

            last_x = x ;
            last_y = y ;
            last_z = z ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this , mSensor ,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
