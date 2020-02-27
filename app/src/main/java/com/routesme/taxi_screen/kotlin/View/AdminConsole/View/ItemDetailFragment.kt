package com.routesme.taxi_screen.kotlin.View.AdminConsole.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Class.DetailsListAdapter
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Model.AdminConsoleLists
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.item_detail_fragment.*

class ItemDetailFragment : Fragment() {
    companion object { const val ARG_ITEM_ID = "itemId" }
    private var detailsList = AdminConsoleLists.INFO_CELLS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val item = AdminConsoleLists.MASTER_ITEMS[it.getInt(ARG_ITEM_ID)]
                 detailsList= AdminConsoleLists.INFO_CELLS

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.item_detail_fragment, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ItemDetailsRecyclerView.apply { adapter = DetailsListAdapter(detailsList) }
    }
}