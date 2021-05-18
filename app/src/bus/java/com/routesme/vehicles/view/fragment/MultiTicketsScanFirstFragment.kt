package com.routesme.vehicles.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearSnapHelper
import com.routesme.vehicles.R
import com.routesme.vehicles.uplevels.CarrierInformation
import com.routesme.vehicles.view.adapter.TicketsButtonRecyclerViewAdapter
import kotlinx.android.synthetic.bus.fragment_multi_tickets_scan_first.view.*

class MultiTicketsScanFirstFragment : Fragment(), TicketsButtonRecyclerViewAdapter.ItemClickListener {
    private lateinit var selectTicketFragmentView: View
    private lateinit var ticketsAdapter: TicketsButtonRecyclerViewAdapter

    companion object {
        @get:Synchronized
        var instance = MultiTicketsScanFirstFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        selectTicketFragmentView = inflater.inflate(R.layout.fragment_multi_tickets_scan_first, container, false)
        initialize()
        return selectTicketFragmentView
    }

    private fun initialize(){
        setupTicketsButtons()
    }

    private fun setupTicketsButtons() {
        val tickets = CarrierInformation().tickets
        tickets?.let { tickets ->
            val snapHelper = LinearSnapHelper()
            ticketsAdapter = TicketsButtonRecyclerViewAdapter(activity,tickets,R.layout.ticket_button_row_white)
            ticketsAdapter.setClickListener(this)
            val ticketSize = tickets.size
            val layoutManager= GridLayoutManager(activity,2)
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (ticketSize.rem(2) == 0) 1
                    else{
                        when {
                            position == 0 -> if (ticketSize > 1) 1 else 2
                            position.rem(2) == 0 -> 2
                            else -> 1
                        }
                    }
                }
            }

            selectTicketFragmentView.ticketListRecyclerView.apply {
                this.layoutManager = layoutManager
                snapHelper.attachToRecyclerView(this)
                adapter  = ticketsAdapter
            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        Toast.makeText(activity,"Station:${ticketsAdapter.getItem(position).first_station}, Price:${ticketsAdapter.getItem(position).amount} FILS", Toast.LENGTH_SHORT).show()
    }
}