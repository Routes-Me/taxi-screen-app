package com.routesme.vehicles.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.AdminConsoleHelper
import com.routesme.vehicles.view.adapter.viewholder.*

class AdminConsoleDetailsListAdapter(private val activity: Activity, private val list: List<ICell>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val adminConsoleHelper = AdminConsoleHelper(activity)

    companion object {
        private const val TYPE_LABEL = 0
        private const val TYPE_DETAIL = 1
        private const val TYPE_ACTION = 2
        private const val TYPE_DETAIL_ACTION = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_LABEL -> LabelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.label_cell_row, parent, false))
        TYPE_DETAIL -> DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detail_cell_row, parent, false))
        TYPE_ACTION -> ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.action_cell_row, parent, false)).listen { pos, _ ->
            val actionCell = list[pos] as ActionCell
            when (actionCell.action) {
                Actions.LogOff.title -> adminConsoleHelper.sendLogOffRequestToActvitiy()
                else -> Toast.makeText(activity,"Sync and Update Button clicked !", Toast.LENGTH_SHORT).show() //getCarrierInformation()
            }
        }
        else -> DetailActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detail_action_cell_row, parent, false)).listen { pos, _ ->
            val detailActionCell = list[pos] as DetailActionCell
            when (detailActionCell.action) {
                Actions.Launcher.title -> adminConsoleHelper.openDefaultLauncherSetting()
                Actions.General.title -> adminConsoleHelper.openAppGeneralSettings()
                Actions.SystemLogs.title -> adminConsoleHelper.openSystemLogs()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_LABEL -> onBindLabel(holder, list[position])
        TYPE_DETAIL -> onBindDetail(holder, list[position])
        TYPE_ACTION -> onBindAction(holder, list[position])
        else -> onBindDetailAction(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (list[position]) {
        is LabelCell -> TYPE_LABEL
        is DetailCell -> TYPE_DETAIL
        is ActionCell -> TYPE_ACTION
        else -> TYPE_DETAIL_ACTION
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

}