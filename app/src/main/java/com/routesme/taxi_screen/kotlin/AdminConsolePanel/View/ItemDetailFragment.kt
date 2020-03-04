package com.routesme.taxi_screen.kotlin.AdminConsolePanel.View

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class.AdminConsoleDetailsListAdapter
import com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class.AdminConsoleLists
import com.routesme.taxi_screen.kotlin.Model.MasterItemType
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.item_detail_fragment.*

class ItemDetailFragment(activity: Activity) : Fragment() {
    companion object {
        const val ARG_ITEM_ID = "itemId"

    }
    private val adminConsoleLists = AdminConsoleLists(activity)


    private var detailsList = adminConsoleLists.infoCells

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSelectedItemList()
    }

    private fun getSelectedItemList(){
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val item = adminConsoleLists.masterItems[it.getInt(ARG_ITEM_ID)]
                detailsList = when (item.type) {
                    MasterItemType.Account -> adminConsoleLists.accountCells
                    MasterItemType.Settings -> adminConsoleLists.settingsCells
                    else -> adminConsoleLists.infoCells
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.item_detail_fragment, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ItemDetailsRecyclerView.apply { adapter = activity?.let { AdminConsoleDetailsListAdapter(it, detailsList) } }
    }
}