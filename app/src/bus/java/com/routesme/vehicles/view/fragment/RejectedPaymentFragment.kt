package com.routesme.vehicles.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.data.PaymentRejectCauses
import kotlinx.android.synthetic.bus.fragment_rejected_payment.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RejectedPaymentFragment : Fragment() {
    private lateinit var rejectedPaymentFragmentView: View

    companion object {
        @get:Synchronized
        var instance = RejectedPaymentFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rejectedPaymentFragmentView = inflater.inflate(R.layout.fragment_rejected_payment, container, false)

        return rejectedPaymentFragmentView
    }
/*
    fun displayRejectCause(rejectCauses: PaymentRejectCauses){
        rejectedPaymentFragmentView.rejectedMessage_tv.text = rejectCauses.message
    }
    */

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(rejectCauses: PaymentRejectCauses){
        rejectedPaymentFragmentView.rejectedMessage_tv.text = rejectCauses.message
    }
}