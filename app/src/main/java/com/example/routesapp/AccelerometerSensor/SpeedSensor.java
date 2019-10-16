package com.example.routesapp.AccelerometerSensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routesapp.R;

public class SpeedSensor extends AppCompatActivity implements SensorEventListener {

    private Activity context;
    private TextView tv;
    private boolean start = true;
    private float xLast, yLast;
    private SensorManager sensorManager;
    private Sensor sensor;

    private float xDelta;
    private float yDelta;

    private float xCurrent;
    private float yCurrent;


    private Handler handlerStopTime;
    private Runnable runnableStopTime;
    private static int stopTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_sensor);

        tv = (TextView) findViewById(R.id.txtview);
        context = this;


        calculateStopTime();

    }


    protected void onResume() {
        super.onResume();
        // Register sensor
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

       // handlerStopTime.postDelayed(runnableStopTime, 2000);

    }

    protected void onPause() {
        super.onPause();
        // Unregister sensor
        sensorManager.unregisterListener(this, sensor);

       // handlerStopTime.removeCallbacks(runnableStopTime);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        //Log.e("Main", "GX="+String.valueOf(event.values[0])+"\nGY="+String.valueOf(event.values[1])+"\nGZ="+String.valueOf(event.values[2]));
        xCurrent = event.values[0]; // Get current x
        yCurrent = event.values[1]; // Get current y
        if (start) {
            // Initialize last x and y
            xLast = xCurrent;
            yLast = yCurrent;
            start = false;
        } else {
            // Calculate variation between last x and current x, last y and current y
            xDelta = xLast - xCurrent;
            yDelta = yLast - yCurrent;
            if (Math.sqrt(xDelta * xDelta / 2) > 1) tv.setText("The device is moved horizontally.");
            if (Math.sqrt(yDelta * yDelta / 2) > 1) tv.setText("The device is moved vertically.");

            // Update last x and y
            xLast = xCurrent;
            yLast = yCurrent;
        }


    }

    private void calculateStopTime() {

        // Get SensorManager instance
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        // Get ACCELEROMETER sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        handlerStopTime = new Handler();

        runnableStopTime = new Runnable() {
            public void run() {


                if (!(Math.sqrt(xDelta * xDelta / 2) > 1) && !(Math.sqrt(yDelta * yDelta / 2) > 1)) {

                    stopTime++;

                    Toast.makeText(SpeedSensor.this, stopTime + " sec", Toast.LENGTH_SHORT).show();

                    tv.setText("The device not moved ");

                }



                /*
                if (mAccel == 0.00f) {
                    stopTime++;
                    Toast.makeText(SpeedSensor.this, stopTime + " sec", Toast.LENGTH_SHORT).show();
                }
*/

                handlerStopTime.postDelayed(this, 1000);
            }
        };

        handlerStopTime.postDelayed(runnableStopTime, 1000);


    }


}

