package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxiscreen.R

class SideFragmentAdapter(private val list: List<ISideFragmentCell>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    var onItemClick: ((ISideFragmentCell) -> Unit)? = null
    companion object {
        private const val TYPE_DATE= 0
        private const val TYPE_WIFI = 1
        private const val TYPE_EMPTY_VIDEO_DISCOUNT =2
        private const val TYPE_BANNER_DISCOUNT = 3
        private const val TYPE_VIDEO_DISCOUNT = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_DATE -> ViewHolderDate(LayoutInflater.from(parent.context).inflate(R.layout.date_cell, parent, false))
        TYPE_WIFI -> ViewHolderWifi(LayoutInflater.from(parent.context).inflate(R.layout.wifi_cell, parent, false))
        TYPE_EMPTY_VIDEO_DISCOUNT -> ViewHolderEmptyVideoDiscount(LayoutInflater.from(parent.context).inflate(R.layout.empty_video_discount_cell, parent, false))
        TYPE_BANNER_DISCOUNT -> ViewHolderBannerDiscount(LayoutInflater.from(parent.context).inflate(R.layout.banner_discount_cell, parent, false))
        else -> ViewHolderVideoDiscount(LayoutInflater.from(parent.context).inflate(R.layout.video_discount_cell, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_DATE -> onBindDate(holder, list[position])
        TYPE_WIFI -> onBindWifi(holder, list[position])
        TYPE_EMPTY_VIDEO_DISCOUNT -> onBindEmptyVideoDiscount(holder, list[position])
        TYPE_BANNER_DISCOUNT -> onBindBannerDiscount(holder, list[position])
        else ->  onBindVideoDiscount(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (list[position]) {
        is DateCell -> TYPE_DATE
        is WifiCell -> TYPE_WIFI
        is EmptyVideoDiscountCell -> TYPE_EMPTY_VIDEO_DISCOUNT
        is BannerDiscountCell -> TYPE_BANNER_DISCOUNT
        else -> TYPE_VIDEO_DISCOUNT
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}