package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.routesme.taxi_screen.kotlin.Model.PaymentData
import com.routesme.taxiscreen.R

class PaymentFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var paymentFragmentView: View
    private  lateinit var paymentData: PaymentData

    companion object{
        @get:Synchronized
        var instance = PaymentFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        paymentFragmentView = inflater.inflate(R.layout.payment_fragment, container, false)
        initialize()
        return paymentFragmentView
    }

    private fun initialize(){
        paymentData = arguments?.getSerializable("paymentData") as PaymentData
        Toast.makeText(activity,"Device Id: ${paymentData.deviceId}, Amount: ${paymentData.paymentAmount} KD",Toast.LENGTH_LONG).show()
    }

}
