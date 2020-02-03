package com.routesme.taxi_screen.java.Class;

import android.text.format.Time;
import java.util.ArrayList;
import java.util.Arrays;

public class CounterOperations {


    private ArrayList<String> monthName;


    public CounterOperations() {
        monthName = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar", "Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"));
    }

    //Time ...
    public String getTimeClock(Time mTime) {
        int cur_hour = mTime.hour;
        if (cur_hour == 0) {
            cur_hour = 12;
        }
        if (cur_hour > 12) {
            cur_hour = cur_hour - 12;
        }
       String Time = String.format("%02d:%02d", cur_hour, mTime.minute);
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
         String todayDate = monthName.get(mTime.month) + " " + mTime.monthDay;
        return todayDate;
    }



}
