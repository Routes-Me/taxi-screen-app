package com.routesme.vehicles.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.vehicles.R
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import com.routesme.vehicles.uplevels.CarrierInformation
import kotlinx.android.synthetic.bus.fragment_main.view.*

class MainFragment : Fragment() {
    private lateinit var mainFragmentView: View
    private lateinit var priceFragment: PriceFragment
    private lateinit var multiTicketsSelectFirstFragment: MultiTicketsSelectFirstFragment

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
        displayTripInformation()
        priceFragment = PriceFragment()
        multiTicketsSelectFirstFragment = MultiTicketsSelectFirstFragment()
        val tickets = CarrierInformation().tickets
        tickets?.let {
            if (it.size == 1) showFragment(priceFragment) else showFragment(multiTicketsSelectFirstFragment)
            if (it.size > 2) mainFragmentView.logo_layout.visibility =  View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayTripInformation() {
        val activatedBusInfo = ActivatedBusInfo()
        mainFragmentView.apply {
            routeNumber_tv.text = "Route ${activatedBusInfo.busRouteName}"
            routeWay_tv.text = activatedBusInfo.busDestination
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.main_fragment_container, fragment)
            childFragmentManager.fragments.forEach { if (it != fragment) hide(it) }
        }.commit()
    }
}