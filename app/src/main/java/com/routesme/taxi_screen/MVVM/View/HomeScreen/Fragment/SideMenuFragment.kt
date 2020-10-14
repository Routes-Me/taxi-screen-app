package com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi_screen.Class.DateOperations
import com.routesme.taxi_screen.Class.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxi_screen.ItemAnimator
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import java.util.*

class SideMenuFragment : Fragment() {
    private lateinit var v: View
    private lateinit var mContext: Context
    private lateinit var handlerTime: Handler
    private val dateOperations = DateOperations.instance
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>

    companion object {
        val instance = SideMenuFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.side_menu_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        v = view
        setupRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        handlerTime.removeCallbacks(timeRunnable)
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        val date = Date()
        sideFragmentCells = mutableListOf<ISideFragmentCell>().apply {
            add(EmptyVideoDiscountCell(""))
            add(LargeEmptyCell())
            add(DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date)))
            add(SmallEmptyCell())
            add(WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password)))
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        v.recyclerView.apply {
            adapter = sideFragmentAdapter
            itemAnimator = ItemAnimator(mContext)
        }
        setTime()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime() {
        handlerTime = Handler()
        handlerTime.post(timeRunnable)
    }

    private val timeRunnable: Runnable = object : Runnable {
        override fun run() {
            val date = Date()
            sideFragmentCells[2] = DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date))
            sideFragmentAdapter.notifyDataSetChanged()
            handlerTime.postDelayed(this, 60 * 1000)
        }
    }

    fun changeVideoQRCode(promotion: Promotion?) {
        val position = 0
        sideFragmentCells[position] = if (promotion != null) VideoDiscountCell(promotion) else EmptyVideoDiscountCell("")
        sideFragmentAdapter.apply {
            notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)
        }
    }

    fun changeBannerQRCode(promotion: Promotion?) {
        val position = 4
        sideFragmentCells[position] = if (promotion != null && !promotion.logoUrl.isNullOrEmpty()) BannerDiscountCell(promotion) else WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password))
        sideFragmentAdapter.apply {
            notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)
        }
    }
}