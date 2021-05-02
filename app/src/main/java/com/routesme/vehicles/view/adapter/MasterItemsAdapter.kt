package com.routesme.vehicles.view.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.MasterItem
import com.routesme.vehicles.view.activity.AdminConsoleItemDetailActivity
import com.routesme.vehicles.view.activity.AdminConsolePanel
import com.routesme.vehicles.view.fragment.ItemDetailFragment
import kotlinx.android.synthetic.main.item_list_content.view.*

class MasterItemsAdapter(private val parentActivity: AdminConsolePanel, private val MasterItems: List<MasterItem>, private val twoPane: Boolean) : RecyclerView.Adapter<MasterItemsAdapter.ViewHolder>() {

    private var rowIndex: Int = 0
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener {
            val item = it.tag as MasterItem
            rowIndex = item.id
            notifyDataSetChanged()
            if (twoPane) {
                val fragment = ItemDetailFragment(parentActivity).apply { arguments = Bundle().apply { putInt(ItemDetailFragment.ARG_ITEM_ID, rowIndex) } }
                parentActivity.supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment).commit()
            }else{
                val intent = Intent(parentActivity, AdminConsoleItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailFragment.ARG_ITEM_ID, rowIndex)
                }
                parentActivity.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_content, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = MasterItems[position]
        holder.itemTitle.text = item.type
        with(holder.itemView) { tag = item; setOnClickListener(onClickListener) }
        holder.listItem.apply {
            if (rowIndex == position) setBackgroundResource(R.drawable.list_item_style_selected) else {
                setBackgroundResource(R.drawable.list_item_style_not_selected)
            }
        }
    }

    override fun getItemCount() = MasterItems.size
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.title
        val listItem: RelativeLayout = view.list_item
    }
}