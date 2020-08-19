package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.routesme.taxi_screen.kotlin.Model.IModeChanging
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class DisplayManager() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var morningAlarm: PendingIntent
    private lateinit var eveningAlarm: PendingIntent
    private val registeredActivities = ArrayList<Activity>()

    companion object {
        val instance = DisplayManager()
    }

    fun setAlarm(context: Context) {
        val cal1 = Calendar.getInstance()
        cal1[Calendar.HOUR_OF_DAY] = 0
        cal1[Calendar.MINUTE] = 1
        cal1[Calendar.SECOND] = 0

        val cal2 = Calendar.getInstance()
        cal2[Calendar.HOUR_OF_DAY] = 12
        cal2[Calendar.MINUTE] = 1
        cal2[Calendar.SECOND] = 0

        val now = Calendar.getInstance()
        if (now.after(cal1)) cal1.add(Calendar.HOUR_OF_DAY, 24)
        if (now.after(cal2)) cal2.add(Calendar.HOUR_OF_DAY, 24)

        val intent = Intent(context, ModeChangesReceiver::class.java)
        morningAlarm = PendingIntent.getBroadcast(context, 0, intent, 0)
        eveningAlarm = PendingIntent.getBroadcast(context, 1, intent, 0)

        alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal1.timeInMillis, morningAlarm)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal2.timeInMillis, eveningAlarm)
    }

    fun isAnteMeridiem() = currentDate().after(parseDate("00:00")) && currentDate().before(parseDate("12:00"))
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDate(time: String) = SimpleDateFormat("HH:mm").parse(time)

    fun registerActivity(activity: Activity) {
        registeredActivities.add(activity)
    }

    fun unregisterActivity(activity: Activity) {
        registeredActivities.remove(activity)
    }

    fun notifyRegisteredActivity() {
        for (activity in registeredActivities) {
            (activity as IModeChanging).onModeChange()
        }
    }
}