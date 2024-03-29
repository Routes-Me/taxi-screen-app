package com.routesme.taxi.AdminConsolePanel.View

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleDetailsListAdapter
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleLists
import com.routesme.taxi.AdminConsolePanel.Model.*
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.item_detail_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailFragment(activity: Activity) : Fragment() {
    companion object {
        const val ARG_ITEM_ID = "itemId"
        private val trackingDatabase = TrackingDatabase.invoke(App.instance)
        private val locationFeedsDao = trackingDatabase.locationFeedsDao()
    }
    private val adminConsoleLists = AdminConsoleLists(activity)
    private var detailsList = adminConsoleLists.infoCells

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSelectedItemList()
    }

    private fun getSelectedItemList() {
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val item = adminConsoleLists.masterItems[it.getInt(ARG_ITEM_ID)]
                detailsList = when (item.type) {
                    MasterItemType.Account -> adminConsoleLists.accountCells
                    MasterItemType.Settings -> adminConsoleLists.settingsCells
                    MasterItemType.Info -> adminConsoleLists.infoCells
                    else -> getSavedLocations()
                }
            }
        }
    }

    private fun getSavedLocations(): MutableList<DetailCell> {
        val savedLocationFeeds = locationFeedsDao.getFeeds()
        val liveTrackingCells = mutableListOf<DetailCell>().apply {
            add(DetailCell("Latitude, Longitude", "Time", true))
            for (location in savedLocationFeeds){
                add(DetailCell("${location.latitude}, ${location.longitude}",getTime(location.timestamp).toString(),true))
            }
        }
        return liveTrackingCells
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(seconds: Long): String? {
        val d = Date(seconds * 1000L)
        val df = SimpleDateFormat("HH:mm:ss")
        df.timeZone = TimeZone.getTimeZone("GMT")
        val time = df.format(d)
        return time
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.item_detail_fragment, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ItemDetailsRecyclerView.apply { adapter = activity?.let { AdminConsoleDetailsListAdapter(it, detailsList) } }
    }
}