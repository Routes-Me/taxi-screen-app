package com.routesme.taxi_screen.kotlin.SideFragmentAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.Model.DiscountCell
import com.routesme.taxi_screen.kotlin.Model.ISideFragmentCell
import com.routesme.taxi_screen.kotlin.Model.WifiCell
import com.routesme.taxiscreen.R

class SideFragmentAdapter(private val list: List<ISideFragmentCell>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    var onItemClick: ((ISideFragmentCell) -> Unit)? = null
    companion object {
        private const val TYPE_DATE= 0
        private const val TYPE_WIFI = 1
        private const val TYPE_DISCOUNT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_DISCOUNT -> ViewHolderDiscount(LayoutInflater.from(parent.context).inflate(R.layout.discount_cell_row, parent, false))
        TYPE_WIFI -> ViewHolderWifi(LayoutInflater.from(parent.context).inflate(R.layout.wifi_cell_row, parent, false))
        else -> ViewHolderDate(LayoutInflater.from(parent.context).inflate(R.layout.date_cell_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_DISCOUNT -> onBindDiscount(holder, list[position])
        TYPE_WIFI -> onBindWifi(holder, list[position])
        else -> onBindDate(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (list[position]) {
        is DiscountCell -> TYPE_DISCOUNT
        is WifiCell -> TYPE_WIFI
        else -> TYPE_DATE
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}