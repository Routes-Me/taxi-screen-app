package com.routesme.taxi.MVVM.View.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.routesme.taxi.Class.DateOperations
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxi.ItemAnimator
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Runnable
import java.util.*


class SideMenuFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mContext: Context
    private val dateOperations = DateOperations.instance
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>
    private lateinit var presentJob : Job
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.side_menu_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presentJob = Job()
        setupRecyclerView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presentJob?.cancel()
    }

    private fun setupRecyclerView() {
        val date = Date()
        sideFragmentCells = mutableListOf<ISideFragmentCell>().apply {
            add(EmptyVideoDiscountCell())
            add(LargeEmptyCell())
            add(DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date)))
            add(SmallEmptyCell())
            add(WifiCell())
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells,activity)
        mView.recyclerView.apply {
            adapter = sideFragmentAdapter
            itemAnimator = ItemAnimator(mContext)
        }
        setTime()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime() {
        //presentJob?.let {
            CoroutineScope(Dispatchers.Main + presentJob).launch {
                while (isActive){
                    val date = Date()
                    sideFragmentCells[2] = DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date))
                    sideFragmentAdapter.notifyDataSetChanged()
                    delay(60 * 1000)
                }

            }

       // }

    }

    @Subscribe()
    fun onEvent(data: Data){

        when(data.type){

            ContentType.Image.value -> changeBannerQRCode(data)
            else -> changeVideoQRCode(data)
        }
    }

    private fun changeVideoQRCode(data: Data) {
        val promotion = data.promotion
        val position = 0
        sideFragmentCells[position] = if (promotion != null && promotion.isExist) VideoDiscountCell(data) else EmptyVideoDiscountCell()
        sideFragmentAdapter.apply {
            notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)
        }
    }

    private fun changeBannerQRCode(data: Data) {

        val promotion = data.promotion
        val position = 4
        sideFragmentCells[position] = if (promotion != null && promotion.isExist) BannerDiscountCell(data) else WifiCell()
        sideFragmentAdapter.apply {
            notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)
        }
    }
}