package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import com.routesme.taxi_screen.kotlin.Model.IModeChanging
import java.text.SimpleDateFormat
import java.util.*

open class DisplayManager(val activity: Activity) {

    private val iModeChanging = activity as IModeChanging
    private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private val second: Long = 1000

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("HH:mm")

    init {
        startHandler()
    }

    @SuppressLint("SetTextI18n")
     fun startHandler() {
        runnableTime = Runnable {

            if (currentDate() == parseDate("06:01") || currentDate() == parseDate("18:01")){
                iModeChanging.onModeChange()
            }

            handlerTime.postDelayed(runnableTime, second * 60)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, second)
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