package com.routesme.vehicles.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.PaymentResult
import kotlinx.android.synthetic.bus.fragment_rejected_payment.view.*

class RejectedPaymentFragment : Fragment() {
    private lateinit var rejectedPaymentFragmentView: View

    companion object {
        @get:Synchronized
        var instance = RejectedPaymentFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rejectedPaymentFragmentView = inflater.inflate(R.layout.fragment_rejected_payment, container, false)
        return rejectedPaymentFragmentView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden) getPaymentResult()
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        getPaymentResult()
        super.onResume()
    }

    private fun getPaymentResult(){
        arguments?.let {
            val paymentResult: PaymentResult = it.getSerializable("PaymentResult") as PaymentResult
            rejectedPaymentFragmentView.userName_tv.text = paymentResult.userName
            paymentResult.rejectedReason?.let {
                rejectedPaymentFragmentView.rejectedReasonMessage_tv.text = it
            }
        }
    }
}