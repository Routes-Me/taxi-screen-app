package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import com.routesme.taxi_screen.kotlin.Model.IThemeMode
import com.routesme.taxi_screen.kotlin.Model.ThemMode
import java.text.SimpleDateFormat
import java.util.*

open class DisplayManager: IThemeMode {

    override var mode: ThemMode = ThemMode.Dark

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("HH:mm")

    init {
       // Log.d("DispalyManager", "Time is ${isDay()}")
        mode = if (isDay()) {ThemMode.Light}else{ThemMode.Dark}
    }

    private fun isDay() = currentDate().after(parseDate("06:00")) && currentDate().before(parseDate("18:00"))

    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }

    private fun parseDate(time: String) = simpleDateFormat.parse(time)!!
}