package com.example.routesapp.Class;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.format.Time;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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










}
