package com.routesme.taxi_screen.MVVM.Model

interface IModeChanging {
    fun onModeChange()
}

interface ISideFragmentCell
class DiscountCell(val details: String?, val url: String?) : ISideFragmentCell
class WifiCell(val name: String, val password: String, val qrCode: QrCode?) : ISideFragmentCell
class DateCell(val clock: String, val weekDay: String, val monthDay: String) : ISideFragmentCell

interface QRCodeCallback {
    fun onVideoQRCodeChanged(qrCode: QrCode?)
    fun onBannerQRCodeChanged(qrCode: QrCode?)
}

data class ItemAnalytics(val id: Int = 0, val name: String = "item_name")