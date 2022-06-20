package com.routesme.vehicles.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import com.routesme.vehicles.uplevels.CarrierInformation
import kotlinx.android.synthetic.bus.fragment_price.view.*

class PriceFragment : Fragment() {

    private lateinit var priceFragmentView: View

    companion object {
        @get:Synchronized
        var instance = PriceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        priceFragmentView = inflater.inflate(R.layout.fragment_price, container, false)
        initialize()
        return priceFragmentView
    }

    private fun initialize(){
        val activatedBusInfo = ActivatedBusInfo()
        priceFragmentView.ticketPrice_tv.text = activatedBusInfo.busPriceByFils.toString()
    /*
        val tickets = CarrierInformation().tickets
        tickets?.let {
            priceFragmentView.ticketPrice_tv.text = it.first().amount.toString()
        }
        */
    }

}