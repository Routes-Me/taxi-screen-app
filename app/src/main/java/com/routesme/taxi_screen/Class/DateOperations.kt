package com.routesme.taxi_screen.Class

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateOperations {

    companion object {
        val instance = DateOperations()
    }

    fun registrationDate(date: Date) = "${dayOfMonth(date)}, ${monthOfYear(date)} ${timeClockWithAMPM(date)}"
    fun timeClock(date: Date) = SimpleDateFormat("h:mm").format(date) as String
    fun dayOfWeek(date: Date) = DateFormat.format("EEEE", date) as String
    fun date(date: Date) = "${monthOfYear(date)} ${dayOfMonth(date)}"
    private fun dayOfMonth(date: Date) = DateFormat.format("d", date) as String
    private fun monthOfYear(date: Date) = DateFormat.format("MMM", date) as String
    private fun timeClockWithAMPM(date: Date) = SimpleDateFormat("hh:mm aa").format(date) as String
}