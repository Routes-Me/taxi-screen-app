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
    private lateinit var runnableTime: Runnable
    private val dateOperations = DateOperations.instance
    private val second: Long = 1000
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>

    companion object {
        val instance = SideMenuFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
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
            add(EmptyVideoDiscountCell(""))
            add(DateCell(dateOperations.timeClock(Date()), dateOperations.dayOfWeek(Date()), dateOperations.date(Date())))
            add(WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password)))
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        v.recyclerView.adapter = sideFragmentAdapter



        //val animator = DefaultItemAnimator(OvershootInterpolator(1f))
        //animator.changeDuration = 5000
      // animator.setInterpolator(OvershootInterpolator())

      v.recyclerView.itemAnimator = ItemAnimator(mContext)


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
        val position = 0
        sideFragmentCells[position] = if (qrCode != null) VideoDiscountCell(qrCode) else EmptyVideoDiscountCell("")
        //sideFragmentAdapter.notifyItemChanged(position)

        sideFragmentAdapter.notifyItemChanged(position)
        //sideFragmentAdapter.notifyItemInserted(position)
        //sideFragmentAdapter.notifyItemRemoved(position)

       // sideFragmentAdapter.notifyItemRemoved(0)
       // v.recyclerView[0].animate().rotationX(360F).duration = 10000
    }

    fun changeBannerQRCode(qrCode: QrCode?) {
        val position = 2
        sideFragmentCells[position] = if (qrCode != null && !qrCode.url.isNullOrEmpty()) BannerDiscountCell(qrCode) else WifiCell(getString(R.string.wifi_name), getString(R.string.wifi_password))
       // sideFragmentAdapter.notifyItemChanged(position)

        sideFragmentAdapter.notifyItemChanged(position)
        sideFragmentAdapter.notifyItemInserted(position)
      //  sideFragmentAdapter.notifyItemRemoved(position)

       // sideFragmentAdapter.notifyItemRemoved(2)
       // v.recyclerView[2].animate().alpha(0F)//.rotationX(360F).duration = 10000
    }
}