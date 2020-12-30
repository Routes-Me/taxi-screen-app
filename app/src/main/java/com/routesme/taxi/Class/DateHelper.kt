package com.routesme.taxi.Class

import android.annotation.SuppressLint
import com.routesme.taxi.utils.Period
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateHelper(){

    companion object{

        val instance = DateHelper()
    }

    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDate(time: String) = SimpleDateFormat("HH:mm").parse(time)

    fun checkDate(from_date:Long) : Boolean{
        val diff = TimeUnit.DAYS.convert((getCurrentDate() - from_date), TimeUnit.MILLISECONDS)
        return diff > 0
    }

    fun getCurrentPeriod(): Period {

        if(isMorning()){

            return Period.MORNING

        }else if(isNoon()){

            return Period.NOON

        }else if(isEvening()){

            return Period.EVENING
        }else{

            return Period.NIGHT
        }

    }

    fun getCurrentDate() = Calendar.getInstance().timeInMillis

    fun isMorning() = currentDate().after(parseDate("06:00")) && currentDate().before(parseDate("11:59"))
    fun isNoon() = currentDate().after(parseDate("12:00")) && currentDate().before(parseDate("16:59"))
    fun isEvening() = currentDate().after(parseDate("17:00")) && currentDate().before(parseDate("23:59"))
}