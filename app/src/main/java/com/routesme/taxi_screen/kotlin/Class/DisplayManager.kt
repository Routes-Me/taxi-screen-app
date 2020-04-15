package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

open class DisplayManager(val activity: Activity) {
    private lateinit var alarmManager: AlarmManager
    private lateinit var morningAlarm: PendingIntent
    private lateinit var eveningAlarm: PendingIntent

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("HH:mm")

    init {
        setAlarm()
    }

    private fun setAlarm() {
        val cal1 = Calendar.getInstance()
        cal1[Calendar.HOUR_OF_DAY] = 6
        cal1[Calendar.MINUTE] = 1
        cal1[Calendar.SECOND] = 0

        val cal2 = Calendar.getInstance()
        cal2[Calendar.HOUR_OF_DAY] = 18
        cal2[Calendar.MINUTE] = 1
        cal2[Calendar.SECOND] = 0

        val now = Calendar.getInstance()
        if (now.after(cal1)) cal1.add(Calendar.HOUR_OF_DAY, 24)
        if (now.after(cal2)) cal2.add(Calendar.HOUR_OF_DAY, 24)

        val intent = Intent(activity, TimeChangesReceiver::class.java)
        morningAlarm = PendingIntent.getBroadcast(activity, 0, intent, 0)
        eveningAlarm = PendingIntent.getBroadcast(activity, 1, intent, 0)

        alarmManager = activity.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal1.timeInMillis, DateUtils.DAY_IN_MILLIS, morningAlarm)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal2.timeInMillis, DateUtils.DAY_IN_MILLIS, eveningAlarm)
    }

    fun isAnteMeridiem() = currentDate().after(parseDate("06:00")) && currentDate().before(parseDate("18:00"))
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }

    private fun parseDate(time: String) = simpleDateFormat.parse(time)!!
}