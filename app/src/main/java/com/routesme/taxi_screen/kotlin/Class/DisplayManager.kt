package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.content.Context
import com.routesme.taxi_screen.kotlin.Model.IThemeMode
import com.routesme.taxi_screen.kotlin.Model.ThemeMode
import java.text.SimpleDateFormat
import java.util.*

open class DisplayManager(context: Context) {

    private var iThemeMode = context as IThemeMode

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("HH:mm")

    init {
        if (isAnteMeridiem()){iThemeMode.mode(ThemeMode.Light)}else{iThemeMode.mode(ThemeMode.Dark)}
    }

    private fun isAnteMeridiem() = currentDate().after(parseDate("06:00")) && currentDate().before(parseDate("18:00"))
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }
    private fun parseDate(time: String) = simpleDateFormat.parse(time)!!
}