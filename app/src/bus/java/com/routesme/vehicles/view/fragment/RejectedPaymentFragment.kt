package com.routesme.vehicles.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.vehicles.R
import com.routesme.vehicles.data.PaymentRejectCauses
import kotlinx.android.synthetic.bus.fragment_rejected_payment.view.*

class RejectedPaymentFragment : Fragment() {
    private lateinit var rejectedPaymentFragmentView: View

    companion object {
        @get:Synchronized
        var instance = RejectedPaymentFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rejectedPaymentFragmentView = inflater.inflate(R.layout.fragment_rejected_payment, container, false)

        return rejectedPaymentFragmentView
    }

    fun displayRejectCause(rejectCauses: PaymentRejectCauses){
        rejectedPaymentFragmentView.rejectedMessage_tv.text = rejectCauses.message
    }
}