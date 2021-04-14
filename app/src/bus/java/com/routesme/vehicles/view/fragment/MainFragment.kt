package com.routesme.vehicles.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.DateOperations
import kotlinx.android.synthetic.bus.fragment_main.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MainFragment : Fragment() {
    private lateinit var mainFragmentView: View
    companion object {
        @get:Synchronized
        var instance = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false)
        initialize()
        return mainFragmentView
    }

    private fun initialize(){

    }
}