package com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.routesme.taxi_screen.Class.DateOperations
import com.routesme.taxi_screen.Class.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxiscreen.R
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
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
            add(DiscountCell("", ""))
            add(DateCell(dateOperations.timeClock(Date()), dateOperations.dayOfWeek(Date()), dateOperations.date(Date())))
            add(WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password),null))
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        v.recyclerView.adapter = sideFragmentAdapter

        //val animator = DefaultItemAnimator(OvershootInterpolator(1f))
        //animator.changeDuration = 5000
      // animator.setInterpolator(OvershootInterpolator())

/*
        v.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            changeDuration = 5000
        }
        */

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
            sideFragmentCells[1] = DateCell(dateOperations.timeClock(Date()), dateOperations.dayOfWeek(Date()), dateOperations.date(Date()))
            sideFragmentAdapter.notifyDataSetChanged()
            handlerTime.postDelayed(runnableTime, second * 60)
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, second)
    }

    fun changeVideoQRCode(qrCode: QrCode?) {
        sideFragmentCells[0] = if (qrCode != null) DiscountCell(qrCode.details,qrCode.url) else DiscountCell(null,null)
        sideFragmentAdapter.notifyItemChanged(0)
       // v.recyclerView[0].animate().rotationX(360F).duration = 10000
    }

    fun changeBannerQRCode(qrCode: QrCode?) {
        sideFragmentCells[2] = if (qrCode != null && !qrCode.url.isNullOrEmpty()) WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password),qrCode) else WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password),null)
        sideFragmentAdapter.notifyItemChanged(2)
       // v.recyclerView[2].animate().alpha(0F)//.rotationX(360F).duration = 10000
    }
}