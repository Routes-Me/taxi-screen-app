package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateOperations {
    private val date: Date? = Date()

    @SuppressLint("SimpleDateFormat")
    fun timeClock() = SimpleDateFormat("hh:mm").format(date) as String

    fun dayOfWeek() = DateFormat.format("EEEE", date) as String

    fun date() = "${monthOfYear()} ${dayOfMonth()}"

    fun registrationDate() = "${dayOfMonth()}, ${monthOfYear()} ${timeClockWithAMPM()}"

    private fun dayOfMonth() = DateFormat.format("dd", date) as String
    private fun monthOfYear() = DateFormat.format("MMM", date) as String

    @SuppressLint("SimpleDateFormat")
    private fun timeClockWithAMPM() = SimpleDateFormat("hh:mm aa").format(date) as String
}