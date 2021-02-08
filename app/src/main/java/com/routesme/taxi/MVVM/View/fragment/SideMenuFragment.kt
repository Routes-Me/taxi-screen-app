package com.routesme.taxi.MVVM.View.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import carbon.widget.ExpandableRecyclerView
import carbon.widget.RecyclerView
import com.routesme.taxi.Class.DateOperations
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxi.ItemAnimator
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.events.PromotionEvent
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.side_menu_fragment.*
import kotlinx.android.synthetic.main.side_menu_fragment.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.lang.Runnable
import java.util.*


class SideMenuFragment : Fragment(),CoroutineScope by MainScope() {
    private lateinit var mView: View
    private lateinit var mContext: Context
    private val dateOperations = DateOperations.instance
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>
    private lateinit var presentJob : Job
    private var screenWidth:Int?=null
    //private lateinit var recyclerView: RecyclerView
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.side_menu_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
       // recyclerView = view.recyclerView as RecyclerView
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presentJob = Job()
        val metrics = DisplayMetrics()
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getMetrics(metrics)
        screenWidth = (metrics.widthPixels * 69) / 100
        setupRecyclerView()
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
        presentJob.cancel()
        //sideFragmentAdapter = null
        //recyclerView.adapter = null

    }

    private fun setupRecyclerView() {
        val date = Date()
        sideFragmentCells = mutableListOf<ISideFragmentCell>().apply {
            add(EmptyVideoDiscountCell(screenWidth!!))
            add(LargeEmptyCell())
            add(DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date)))
            add(SmallEmptyCell())
            add(WifiCell())
        }

        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        recyclerView.apply {
            adapter = sideFragmentAdapter
            itemAnimator = ItemAnimator(mContext)
        }
        setTime()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime() {
            CoroutineScope(Dispatchers.Main + presentJob).launch {
                while (isActive){
                    val date = Date()
                    sideFragmentCells[2] = DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date))
                    sideFragmentAdapter.notifyDataSetChanged()
                    delay(60 * 1000)
                }

            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(promotionData:PromotionEvent){
        try{
            when(promotionData.data.type){

                ContentType.Image.value -> changeBannerQRCode(promotionData.data)
                else -> changeVideoQRCode(promotionData.data)
            }
        } catch (e:Exception){

        }
    }

    private fun changeVideoQRCode(data: Data) {

        val promotion = data.promotion
        val position = 0
        if (promotion != null && promotion.isExist) sideFragmentCells[position] = VideoDiscountCell(data,screenWidth!!) else sideFragmentCells[position] = EmptyVideoDiscountCell(screenWidth!!)
        sideFragmentAdapter.apply {

            notifyItemRemoved(position)
            notifyItemInserted(position)
        }
    }

    private fun changeBannerQRCode(data: Data) {
        val promotion = data.promotion
        val position = 4
        if (promotion != null && promotion.isExist) sideFragmentCells.set(position,BannerDiscountCell(data)) else sideFragmentCells.set(position,WifiCell())
        sideFragmentAdapter.apply {

            notifyItemRemoved(position)
            notifyItemInserted(position)

        }
    }
}