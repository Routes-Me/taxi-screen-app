package com.routesme.vehicles.helper

import com.routesme.vehicles.view.utils.Period
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    private val simpleDateFormat = SimpleDateFormat("yyMMdd", Locale.ENGLISH)

    companion object {
        val instance = DateHelper()
    }

    private fun currentHour(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentPeriod(): Period {
        return when (currentHour()) {
            in 0..5 -> Period.NIGHT
            in 6..11 -> Period.MORNING
            in 12..17 -> Period.NOON
            else -> Period.EVENING
        }
    }

    fun getCurrentDate() = Calendar.getInstance().timeInMillis

    fun getDateString(time: Long): String = simpleDateFormat.format(time)
}