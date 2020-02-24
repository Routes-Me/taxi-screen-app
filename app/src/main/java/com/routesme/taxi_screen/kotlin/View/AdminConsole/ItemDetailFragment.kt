package com.routesme.taxi_screen.kotlin.View.AdminConsole

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.routesme.taxi_screen.kotlin.View.AdminConsole.dummy.AdminConsoleLists
import com.routesme.taxiscreen.R

class ItemDetailFragment : Fragment() {

    lateinit var selectedlist:List<AdminConsoleLists.DetailCell>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
               val item = AdminConsoleLists.MASTER_ITEMS[it.getInt(ARG_ITEM_ID)]
                selectedlist = when(item.type){
                    AdminConsoleLists.ItemType.Info -> AdminConsoleLists.INFO_CELLS
                    AdminConsoleLists.ItemType.Account -> AdminConsoleLists.ACCOUNT_CELLS
                    AdminConsoleLists.ItemType.Settings -> AdminConsoleLists.SETTINGS_CELLS
                }
               // Toast.makeText(activity,"cell: $selectedlist", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)
        return rootView
    }
    companion object {
            const val ARG_ITEM_ID = "itemId"
    }
}