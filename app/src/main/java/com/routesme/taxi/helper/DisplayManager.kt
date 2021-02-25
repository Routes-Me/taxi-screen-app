package com.routesme.taxi.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.DisplayMetrics
import android.view.WindowManager
import com.routesme.taxi.service.receiver.ModeChangesReceiver
import com.routesme.taxi.data.model.IModeChanging
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class DisplayManager {
    private lateinit var alarmManager: AlarmManager
    private lateinit var morningAlarmIntent: PendingIntent
    private lateinit var eveningAlarmIntent: PendingIntent
    private var registeredActivities = ArrayList<Activity>()
    var currentMode: Mode = Mode.Light

    companion object {
        val instance = DisplayManager()
    }

    fun setAlarm(context: Context) {
        registeredActivities = ArrayList()
        val morningCalendar = Calendar.getInstance()
        morningCalendar[Calendar.HOUR_OF_DAY] = 5
        morningCalendar[Calendar.MINUTE] = 1
        morningCalendar[Calendar.SECOND] = 0
        val eveningCalendar = Calendar.getInstance()
        eveningCalendar[Calendar.HOUR_OF_DAY] = 17
        eveningCalendar[Calendar.MINUTE] = 1
        eveningCalendar[Calendar.SECOND] = 0
        val currentTime = Calendar.getInstance()
        if (currentTime.after(morningCalendar)) morningCalendar.add(Calendar.HOUR_OF_DAY, 24)
        if (currentTime.after(eveningCalendar)) eveningCalendar.add(Calendar.HOUR_OF_DAY, 24)
        val intent = Intent(context, ModeChangesReceiver::class.java)
        morningAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        eveningAlarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
        alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, morningCalendar.timeInMillis, AlarmManager.INTERVAL_DAY, morningAlarmIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, eveningCalendar.timeInMillis, AlarmManager.INTERVAL_DAY, eveningAlarmIntent)
    }

    fun isAnteMeridiem() = currentDate().after(parseDate("05:00")) && currentDate().before(parseDate("17:00"))

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

    fun wasRegistered(activity: Activity) = registeredActivities.contains(activity)

    fun unregisterActivity(activity: Activity) {
        registeredActivities.remove(activity)
    }

    fun notifyRegisteredActivity() {
        for (activity in registeredActivities) {
            (activity as IModeChanging).onModeChange()
        }
    }

    fun getDisplayWidth(context: Context):Int{

        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getMetrics(metrics)
        return (metrics.widthPixels * 69) / 100
    }
}
enum class Mode{Light,Dark}