package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi_screen.kotlin.Class.DateOperations
import com.routesme.taxi_screen.kotlin.Model.DateCell
import com.routesme.taxi_screen.kotlin.Model.DiscountCell
import com.routesme.taxi_screen.kotlin.Model.ISideFragmentCell
import com.routesme.taxi_screen.kotlin.Model.WifiCell
import com.routesme.taxi_screen.kotlin.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import java.util.*

class SideMenuFragment : Fragment() {
    private lateinit var v: View
    private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private val dateOperations = DateOperations.instance
    private val second: Long = 1000
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>

    companion object {
        val instance = SideMenuFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.side_menu_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        v = view
        setupRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        // setTime()
        super.onResume()
    }

    override fun onPause() {
        //handlerTime.removeCallbacks(runnableTime)
        super.onPause()
    }

    private fun setupRecyclerView() {
        sideFragmentCells = mutableListOf<ISideFragmentCell>().apply {
            add(DiscountCell("Macdonalds offers a 30% discount", ""))
            add(WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password)))
            add(DateCell(dateOperations.timeClock(Date()), dateOperations.dayOfWeek(Date()), dateOperations.date(Date())))
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        v.recyclerView.adapter = sideFragmentAdapter
        /*
        sideFragmentAdapter.onItemClick = {
            val phoneNumberCell = it as PhoneNumberCell
        }
*/
        setTime()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime() {
        runnableTime = Runnable {
            sideFragmentCells[2] = DateCell(dateOperations.timeClock(Date()), dateOperations.dayOfWeek(Date()), dateOperations.date(Date()))
            sideFragmentAdapter.notifyDataSetChanged()
            handlerTime.postDelayed(runnableTime, second)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, second)
    }
}