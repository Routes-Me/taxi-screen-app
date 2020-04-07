package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi_screen.kotlin.Class.DateOperations
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import java.util.*

class SideMenuFragment : Fragment() {
    private lateinit var sideMenuFragmentView: View
    private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private lateinit var counterOperations: DateOperations
    private val second: Long = 1000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sideMenuFragmentView = inflater.inflate(R.layout.side_menu_fragment, container, false)
        return sideMenuFragmentView
    }

    override fun onResume() {
        super.onResume()
        timeSetUp()
    }

    @SuppressLint("SetTextI18n")
    private fun timeSetUp() {
        counterOperations = DateOperations()
        runnableTime = Runnable {
            sideMenuFragmentView.clock_tv.text = counterOperations.timeClock(Date())
            sideMenuFragmentView.dayOfWeek_tv.text = "${counterOperations.dayOfWeek(Date())},"
            sideMenuFragmentView.dayOfMonth_tv.text = counterOperations.date(Date())

            handlerTime.postDelayed(runnableTime, second * 60)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, second)
    }
}