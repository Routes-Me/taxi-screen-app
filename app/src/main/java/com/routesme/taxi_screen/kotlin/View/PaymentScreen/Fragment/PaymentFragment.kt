package com.routesme.taxi_screen.kotlin.View.PaymentScreen.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.payment_fragment.view.*
import java.text.DecimalFormat

class PaymentFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var paymentFragmentView: View
    private var paymentAmount = 0.0
    private val amountPattern = DecimalFormat("0.000")
    val PAYMENT_AMOUNT = "PAYMENT_AMOUNT"

    companion object{
        @get:Synchronized
        var instance = PaymentFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.payment_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentFragmentView = view
        initialize()
    }

    private fun initialize(){
        paymentAmount = arguments?.getDouble(PAYMENT_AMOUNT,0.0)!!
        //Toast.makeText(activity,"Amount: ${paymentAmount} KD",Toast.LENGTH_LONG).show()
        paymentFragmentView.amountTv.text = amountPattern.format(paymentAmount).toString()
    }
}