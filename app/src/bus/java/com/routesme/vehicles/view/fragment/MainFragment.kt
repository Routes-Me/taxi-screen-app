package com.routesme.vehicles.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.routesme.vehicles.R
import com.routesme.vehicles.uplevels.CarrierInformation
import com.routesme.vehicles.view.adapter.PriceButtonRecyclerViewAdapter
import kotlinx.android.synthetic.bus.fragment_main.view.*

class MainFragment : Fragment() {
    private lateinit var mainFragmentView: View
    private lateinit var ticketsAdapter: PriceButtonRecyclerViewAdapter

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
       // setupPricesButtons()
    }

    private fun setupPricesButtons() {
        val tickets = CarrierInformation().tickets
        tickets?.let { tickets ->
            ticketsAdapter = PriceButtonRecyclerViewAdapter(activity,tickets, R.layout.price_button_row_blue)
            mainFragmentView.pricesListRecyclerView.apply {
                layoutManager = GridLayoutManager(activity,2)
                adapter  = ticketsAdapter
            }
        }
    }
}