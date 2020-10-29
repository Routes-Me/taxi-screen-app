package com.routesme.taxi.Class.SideFragmentAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.R

class SideFragmentAdapter(private val list: List<ISideFragmentCell>, private val activity: FragmentActivity?) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    companion object {
        private const val TYPE_EMPTY_VIDEO_DISCOUNT =0
        private const val TYPE_VIDEO_DISCOUNT = 1
        private const val TYPE_LARGE_EMPTY = 2
        private const val TYPE_DATE= 3
        private const val TYPE_SMALL_EMPTY = 4
        private const val TYPE_WIFI = 5
        private const val TYPE_BANNER_DISCOUNT = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_EMPTY_VIDEO_DISCOUNT -> ViewHolderEmptyVideoDiscount(LayoutInflater.from(parent.context).inflate(R.layout.empty_video_discount_cell, parent, false))
        TYPE_VIDEO_DISCOUNT -> ViewHolderVideoDiscount(LayoutInflater.from(parent.context).inflate(R.layout.video_discount_cell, parent, false))
        TYPE_LARGE_EMPTY -> ViewHolderLargeEmpty(LayoutInflater.from(parent.context).inflate(R.layout.large_empty_cell, parent, false))
        TYPE_DATE -> ViewHolderDate(LayoutInflater.from(parent.context).inflate(R.layout.date_cell, parent, false))
        TYPE_SMALL_EMPTY -> ViewHolderSmallEmpty(LayoutInflater.from(parent.context).inflate(R.layout.small_empty_cell, parent, false))
        TYPE_WIFI -> ViewHolderWifi(LayoutInflater.from(parent.context).inflate(R.layout.wifi_cell, parent, false))
        else -> ViewHolderBannerDiscount(LayoutInflater.from(parent.context).inflate(R.layout.banner_discount_cell, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_EMPTY_VIDEO_DISCOUNT -> onBindEmptyVideoDiscount(holder,activity)
        TYPE_VIDEO_DISCOUNT ->  onBindVideoDiscount(holder, list[position], activity)
        TYPE_LARGE_EMPTY -> onBindLargeEmpty()
        TYPE_DATE -> onBindDate(holder, list[position])
        TYPE_SMALL_EMPTY -> onBindSmallEmpty()
        TYPE_WIFI -> onBindWifi(holder, list[position])
        else ->  onBindBannerDiscount(holder, list[position], activity)
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (list[position]) {
        is EmptyVideoDiscountCell -> TYPE_EMPTY_VIDEO_DISCOUNT
        is VideoDiscountCell -> TYPE_VIDEO_DISCOUNT
        is LargeEmptyCell -> TYPE_LARGE_EMPTY
        is DateCell -> TYPE_DATE
        is SmallEmptyCell -> TYPE_SMALL_EMPTY
        is WifiCell -> TYPE_WIFI
        else ->  TYPE_BANNER_DISCOUNT
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}