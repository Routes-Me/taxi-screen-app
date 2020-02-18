package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi_screen.kotlin.Class.CounterOperations
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*

class SideMenuFragment : Fragment() {
    private lateinit var sideMenuFragmentView: View
    private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private lateinit var counterOperations: CounterOperations

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sideMenuFragmentView = inflater.inflate(R.layout.side_menu_fragment, container, false)

        initialize()

        return sideMenuFragmentView
    }
    private fun initialize(){
        timeSetUp()
    }
    @SuppressLint("SetTextI18n")
    private fun timeSetUp() {
        counterOperations = CounterOperations()
        runnableTime = Runnable {
            sideMenuFragmentView.timeClock_tv.text = counterOperations.timeClock()
            sideMenuFragmentView.timeDate_tv.text = "${counterOperations.dayOfWeek()}, \n ${counterOperations.date()}"
            handlerTime.postDelayed(runnableTime, 1000)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, 1000)
    }
}