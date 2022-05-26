package com.routesme.vehicles.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.PaymentResult
import kotlinx.android.synthetic.bus.fragment_approved_payment.view.*

class ApprovedPaymentFragment : Fragment() {

    private lateinit var approvedPaymentFragmentView: View

    companion object {
        @get:Synchronized
        var instance = ApprovedPaymentFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        approvedPaymentFragmentView = inflater.inflate(R.layout.fragment_approved_payment, container, false)
        return approvedPaymentFragmentView
    }

    override fun onStart() {
        Log.d("getPaymentResultTest", "ApprovedPaymentFragment.. onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("getPaymentResultTest", "ApprovedPaymentFragment.. onResume")
        super.onResume()
    }

    private fun getPaymentResult(){
        arguments?.let {
            val paymentResult: PaymentResult = it.getSerializable("PaymentResult") as PaymentResult
            approvedPaymentFragmentView.userName_tv.text = paymentResult.userName
        }
    }
}