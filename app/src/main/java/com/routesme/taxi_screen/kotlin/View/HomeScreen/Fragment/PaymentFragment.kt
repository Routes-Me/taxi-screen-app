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
import kotlinx.android.synthetic.main.payment_fragment.view.*

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
        Toast.makeText(activity,"Amount: ${paymentData.paymentAmount} KD",Toast.LENGTH_LONG).show()
        paymentFragmentView.amountTv.text = paymentData.paymentAmount.toString()
    }
}