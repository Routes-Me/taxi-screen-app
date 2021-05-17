package com.routesme.vehicles.view.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.CarrierInformationModel


class TicketsButtonRecyclerViewAdapter internal constructor(val context: Context?, private val tickets: List<CarrierInformationModel.Ticket>, private val resource: Int) : RecyclerView.Adapter<TicketsButtonRecyclerViewAdapter.ViewHolder?>() {
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(resource, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.apply {
            tickets[position].first_station?.let { firstStationName.text = "from $it"; firstStationName.visibility = View.VISIBLE }
            tickets[position].amount?.let { price.text = it.toString() }

            ObjectAnimator.ofPropertyValuesHolder(
                    ticketLayout,
                    PropertyValuesHolder.ofFloat("scaleX", 1.03f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.03f)).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
        }
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val ticketLayout : LinearLayout = itemView.findViewById(R.id.ticketLayout)
        val firstStationName: TextView = itemView.findViewById(R.id.firstStationName_tv)
        val price: TextView = itemView.findViewById(R.id.price_tv)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    override fun getItemCount() = tickets.size

    fun getItem(id: Int): CarrierInformationModel.Ticket = tickets[id]

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}