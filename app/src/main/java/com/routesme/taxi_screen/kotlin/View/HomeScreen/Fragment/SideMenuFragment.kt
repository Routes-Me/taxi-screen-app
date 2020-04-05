package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi_screen.kotlin.Class.DateOperations
import com.routesme.taxi_screen.kotlin.Model.IThemeMode
import com.routesme.taxi_screen.kotlin.Model.ThemeMode
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*

class SideMenuFragment : Fragment() {
    private lateinit var sideMenuFragmentView: View
    private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private lateinit var counterOperations: DateOperations
    private val second: Long = 1000

    //Dark Mode
    private lateinit var iThemeMode :IThemeMode
    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("HH:mm")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sideMenuFragmentView = inflater.inflate(R.layout.side_menu_fragment, container, false)

        iThemeMode = context as IThemeMode

        return sideMenuFragmentView
    }

    override fun onResume() {
        super.onResume()
        if (isAnteMeridiem()){iThemeMode.mode(ThemeMode.Light)}else{iThemeMode.mode(ThemeMode.Dark)}
        timeSetUp()
    }

    @SuppressLint("SetTextI18n")
    private fun timeSetUp() {
        counterOperations = DateOperations()
        runnableTime = Runnable {
            sideMenuFragmentView.clock_tv.text = counterOperations.timeClock(Date())
            sideMenuFragmentView.dayOfWeek_tv.text = "${counterOperations.dayOfWeek(Date())},"
            sideMenuFragmentView.dayOfMonth_tv.text = counterOperations.date(Date())

            if (currentDate() == parseDate("06:01") || currentDate() == parseDate("18:01")){
                if (isAnteMeridiem()){iThemeMode.mode(ThemeMode.Light)}else{iThemeMode.mode(ThemeMode.Dark)}
            }

            handlerTime.postDelayed(runnableTime, second * 60)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, second)
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