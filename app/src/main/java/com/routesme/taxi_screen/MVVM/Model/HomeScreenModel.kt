package com.routesme.taxi_screen.MVVM.Model

interface IModeChanging {
    fun onModeChange()
}

interface ISideFragmentCell
class EmptyVideoDiscountCell(val image: String) : ISideFragmentCell
class VideoDiscountCell(val qrCode: QrCode?) : ISideFragmentCell
class LargeEmptyCell() : ISideFragmentCell
class DateCell(val clock: String, val weekDay: String, val monthDay: String) : ISideFragmentCell
class SmallEmptyCell() : ISideFragmentCell
class WifiCell(val name: String, val password: String) : ISideFragmentCell
class BannerDiscountCell( val qrCode: QrCode?) : ISideFragmentCell


interface QRCodeCallback {
    fun onVideoQRCodeChanged(qrCode: QrCode?)
    fun onBannerQRCodeChanged(qrCode: QrCode?)
}

data class ItemAnalytics(val id: Int = 0, val name: String = "item_name")