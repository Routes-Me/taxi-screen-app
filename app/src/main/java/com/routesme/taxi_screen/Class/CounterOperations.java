package com.routesme.taxi_screen.Class;

import android.app.Activity;
import android.text.format.Time;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class CounterOperations {

    private Activity activity;

    private ArrayList<String> monthName;


    public CounterOperations(Activity activity) {
        this.activity = activity;

        monthName = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar", "Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"));
    }




    public void getCurrentTime_oldView(Time mTime, TextView timeClock, TextView DateClock, TextView DayClock) {


        //Time..
        timeClock.setText(getTimeClock(mTime));

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
    public String getTimeClock(Time mTime) {

        int cur_hour = mTime.hour;

        String cur_ampm = "AM";
        if (cur_hour == 0) {
            cur_hour = 12;
        }
        if (cur_hour > 12) {
            cur_hour = cur_hour - 12;
            cur_ampm = "PM";
        }

      //  String Time = String.format("%02d:%02d:%02d", cur_hour, mTime.minute, mTime.second);
       String Time = String.format("%02d:%02d", cur_hour, mTime.minute);

      // return Time + " " + cur_ampm;
        return Time;

    }

    //Day Of Week ...
    public String getDayOfWeek(Time mTime) {

        String day_of_week = "";
        if (mTime.weekDay == 1) {
            day_of_week = "Monday";
        } else if (mTime.weekDay == 2) {
            day_of_week = "Tuesday";
        } else if (mTime.weekDay == 3) {
            day_of_week = "Wednesday";
        } else if (mTime.weekDay == 4) {
            day_of_week = "Thursday";
        } else if (mTime.weekDay == 5) {
            day_of_week = "Friday";
        } else if (mTime.weekDay == 6) {
            day_of_week = "Saturday";
        } else if (mTime.weekDay == 0) {
            day_of_week = "Sunday";
        }

        return day_of_week;

    }

    //Date Of Day ...
    public String getDate(Time mTime) {

       // String todayDate = mTime.monthDay + " " +monthName.get(mTime.month) + " " +  mTime.year;
         String todayDate = monthName.get(mTime.month) + " " + mTime.monthDay;

        return todayDate;
    }










}
