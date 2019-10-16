package com.example.routesapp.Class;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.text.format.Time;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.routesapp.View.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.LOCATION_SERVICE;

public class CounterOperations {

    private Activity activity;

    private ArrayList<String> monthName;


    public CounterOperations(Activity activity) {
        this.activity = activity;

        monthName = new ArrayList<String>(Arrays.asList("Jan.", "Feb.", "Mar.", "Apr.","May.","June.","July.","Aug.","Sept.","Oct.","Nov.","Dec."));
    }




    public void getCurrentTime(Time mTime, TextView timeClock, TextView DateClock, TextView DayClock) {


        //Time..
        timeClock.setText(getTime(mTime));

        //Name of [ Day of week ] ...
        DayClock.setText(getDayOfWeek(mTime));


        //Date...
        try {
            DateClock.setText(getDate(mTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Time ...
    private String getTime(Time mTime) {

        int cur_hour = mTime.hour;
        String cur_ampm = "AM";
        if (cur_hour == 0) {
            cur_hour = 12;
        }
        if (cur_hour > 12) {
            cur_hour = cur_hour - 12;
            cur_ampm = "PM";
        }

        String Time = String.format("%02d:%02d:%02d", cur_hour, mTime.minute, mTime.second);

       return Time + " " + cur_ampm;

    }

    //Day Of Week ...
    private String getDayOfWeek(Time mTime) {

        String day_of_week = "";
        if (mTime.weekDay == 1) {
            day_of_week = "MON";
        } else if (mTime.weekDay == 2) {
            day_of_week = "TUE";
        } else if (mTime.weekDay == 3) {
            day_of_week = "WED";
        } else if (mTime.weekDay == 4) {
            day_of_week = "THU";
        } else if (mTime.weekDay == 5) {
            day_of_week = "FRI";
        } else if (mTime.weekDay == 6) {
            day_of_week = "SAT";
        } else if (mTime.weekDay == 0) {
            day_of_week = "SUN";
        }

        return day_of_week;

    }

    //Date Of Day ...
    private String getDate(Time mTime) {

        String todayDate = mTime.monthDay + " " +monthName.get(mTime.month) + " " +  mTime.year;

        return todayDate;
    }




/*
    //Speed Of Taxi ...
    private void getSpeedOfTaxiNew() {
        try {
            // Get the location manager
            double lat;
            double lon;
            double speed = 0;
            LocationManager locationManager = (LocationManager)
                    activity.getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            try {
                lat = location.getLatitude();
                lon = location.getLongitude();
                speed = location.getSpeed();
            } catch (NullPointerException e) {
                lat = -1.0;
                lon = -1.0;
            }

            // mTxt_lat.setText("" + lat);
            //mTxt_speed.setText("" + speed);

            // Toast.makeText(this, "Speed: "+speed, Toast.LENGTH_SHORT).show();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/


    //Battery Level ...
    public float getBatteryLevel() {
        Intent batteryIntent = activity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float) level / (float) scale) * 100.0f;
    }


}
