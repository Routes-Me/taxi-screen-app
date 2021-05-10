package com.routesme.vehicles.view.fragment

import android.os.Bundle
import android.util.Log
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
import com.routesme.vehicles.view.adapter.PriceButtonRecyclerViewAdapter
import kotlinx.android.synthetic.bus.fragment_tickets.view.*


class TicketsFragment : Fragment(), PriceButtonRecyclerViewAdapter.ItemClickListener {
    private lateinit var selectTicketFragmentView: View
    private lateinit var ticketsAdapter: PriceButtonRecyclerViewAdapter

    companion object {
        @get:Synchronized
        var instance = TicketsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        selectTicketFragmentView = inflater.inflate(R.layout.fragment_tickets, container, false)
        initialize()
        return selectTicketFragmentView
    }

    private fun initialize(){
        setupPricesButtons()
    }

    private fun setupPricesButtons() {

        val tickets = CarrierInformation().tickets
        tickets?.let { tickets ->
            val snapHelper = LinearSnapHelper()
            ticketsAdapter = PriceButtonRecyclerViewAdapter(activity,tickets)
            ticketsAdapter.setClickListener(this)
            val ticketSize = tickets.size
            val layoutManager= GridLayoutManager(activity,2)

            Log.d("ticketsGridView", "ticketSize: $ticketSize , Rem: ${ticketSize.rem(2)}")
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                  //  if ((position == 0 || position == 1) && ticketSize >1) return 2 else return 1

                    if (ticketSize > 1 ){
                        return when(position){
                            0,1 -> 1
                            else -> 2
                        }
                    }else return 2

                            /*
                            return when(position){

                                0,2 -> if (ticketSize > 1) 2 else 1
                                1 -> 2
                                2 -> 1
                                else -> 1
                            }
                            */

                    Log.d("ticketsGridView","Position: $position")
                    //if (position == 0) return 2
                    //else if (position == 1 && tickets.size == 2) return 2
                   // else return 1

                   // if (ticketSize % 2 == 0) return 2
                    //else if (position ==0 || position ==2) return 1
                    //else return 3
                   // if (ticketSize % 2 == 0 && (position == 0 || position == 2)) return 1
                    //else return 2
                }
            }

            selectTicketFragmentView.pricesListRecyclerView.apply {


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