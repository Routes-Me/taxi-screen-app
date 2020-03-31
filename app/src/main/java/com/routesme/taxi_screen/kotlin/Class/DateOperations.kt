package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateOperations {

    fun registrationDate(date: Date) = "${dayOfMonth(date)}, ${monthOfYear(date)} ${timeClockWithAMPM(date)}"
    fun timeDate(date: Date)= "${timeClock(date)}\n${dayOfWeek(date)},\n${date(date)}"
    private fun timeClock(date: Date) = SimpleDateFormat("hh:mm").format(date) as String
    private fun dayOfWeek(date: Date) = DateFormat.format("EEEE", date) as String
    private fun date(date: Date) = "${monthOfYear(date)} ${dayOfMonth(date)}"
    private fun dayOfMonth(date: Date) = DateFormat.format("dd", date) as String
    private fun monthOfYear(date: Date) = DateFormat.format("MMM", date) as String
    private fun timeClockWithAMPM(date: Date) = SimpleDateFormat("hh:mm aa").format(date) as String
}