package com.routesme.vehicles.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.color.MaterialColors
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.BusPaymentQrCodeDataModel
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import com.routesme.vehicles.uplevels.CarrierInformation
import kotlinx.android.synthetic.bus.fragment_main.view.*
import net.codecision.glidebarcode.model.Barcode

class MainFragment : Fragment() {
    private lateinit var activatedBusInfo: ActivatedBusInfo
    private lateinit var mainFragmentView: View
    private lateinit var priceFragment: PriceFragment
    private lateinit var multiTicketsSelectFirstFragment: MultiTicketsSelectFirstFragment

    companion object {
        @get:Synchronized
        var instance = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false)
        initialize()
        return mainFragmentView
    }

    private fun initialize(){
        activatedBusInfo = ActivatedBusInfo()
        displayTripInformation()
        getBusPaymentQrCode().let {
            Glide.with(this).load(it).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(mainFragmentView.qrcode_iv)
        }
        priceFragment = PriceFragment()
        multiTicketsSelectFirstFragment = MultiTicketsSelectFirstFragment()
        val tickets = CarrierInformation().tickets
        tickets?.let {
            if (it.size == 1) showFragment(priceFragment) else showFragment(multiTicketsSelectFirstFragment)
            //if (it.size > 2) mainFragmentView.logo_layout.visibility =  View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayTripInformation() {
        mainFragmentView.apply {
            routeNumber_tv.text = "Route ${activatedBusInfo.busRouteName}"
            routeWay_tv.text = activatedBusInfo.busDestination
            companyName_tv.text = "${activatedBusInfo.busCompany} - ${activatedBusInfo.busPlateNumber}"
        }
    }

    private fun getBusPaymentQrCode(): Barcode {
        val busPaymentQrCodeDataModel = BusPaymentQrCodeDataModel(activatedBusInfo.busId!!, activatedBusInfo.busPrice!!.trim().toDouble())
        val busPaymentQrCodeDataModelJson = Gson().toJson(busPaymentQrCodeDataModel)
        val qrCodeColor = MaterialColors.getColor(requireContext(), R.attr.homeScreenTextColor, Color.BLACK)
        return Barcode(busPaymentQrCodeDataModelJson, BarcodeFormat.QR_CODE, qrCodeColor, Color.TRANSPARENT)
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.main_fragment_container, fragment)
            childFragmentManager.fragments.forEach { if (it != fragment) hide(it) }
        }.commit()
    }
}