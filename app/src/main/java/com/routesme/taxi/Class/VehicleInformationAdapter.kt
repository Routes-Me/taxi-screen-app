package com.routesme.taxi.Class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi.MVVM.Model.VehicleInformationModel.Item
import com.routesme.taxi.R

class VehicleInformationAdapter(val context: Context, private var listItemArrayList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var index = -1
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    var onItemClick: ((Item) -> Unit)? = null

    companion object {
        private const val LAYOUT_HEADER = 0
        private const val LAYOUT_ITEM_NORMAL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        holder = when (viewType) {
            LAYOUT_HEADER -> {
                val view = inflater.inflate(R.layout.header_row, parent, false)
                MyViewHolderHeader(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.normal_list_row, parent, false)
                MyViewHolderChild(view)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == LAYOUT_HEADER) {
            val itemHolder = holder as MyViewHolderHeader
            itemHolder.headerTitle.text = listItemArrayList[position].itemName
        } else {
            val itemHolder = holder as MyViewHolderChild
            itemHolder.itemName.text = listItemArrayList[position].itemName
            itemHolder.rowLayout.setOnClickListener {
                index = position
                notifyDataSetChanged()
                onItemClick?.invoke(listItemArrayList[position])
            }
            if (index == position) {
                itemHolder.rowLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.border_background))
            } else {
                itemHolder.rowLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            }
        }
    }

    internal inner class MyViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headerTitle: TextView = itemView.findViewById<View>(R.id.headerTitle) as TextView
    }

    internal inner class MyViewHolderChild(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.itemName)
        var rowLayout: LinearLayout = itemView.findViewById(R.id.rowLayout)
    }

    override fun getItemViewType(position: Int): Int {
        return if (listItemArrayList[position].isHeader) {
            LAYOUT_HEADER
        } else {
                LAYOUT_ITEM_NORMAL
        }
    }

    override fun getItemCount() = listItemArrayList.size

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

}

