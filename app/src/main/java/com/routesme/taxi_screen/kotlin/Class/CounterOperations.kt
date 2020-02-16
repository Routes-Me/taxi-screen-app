package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CounterOperations {
    private val date: Date? = Date()

    @SuppressLint("SimpleDateFormat")
    fun timeClock()= SimpleDateFormat("hh:mm").format(Date()) as String

    fun dayOfWeek()= DateFormat.format("EEEE", date) as String

    fun date() = "${monthOfYear()} ${dayOfMonth()}"

    private fun dayOfMonth() = DateFormat.format("dd", date) as String
    private fun monthOfYear() = DateFormat.format("MMM", date) as String
}