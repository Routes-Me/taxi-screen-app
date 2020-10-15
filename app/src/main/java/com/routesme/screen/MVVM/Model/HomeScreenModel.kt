package com.routesme.screen.MVVM.Model

interface IModeChanging {
    fun onModeChange()
}

interface ISideFragmentCell
class EmptyVideoDiscountCell(val image: String) : ISideFragmentCell
class VideoDiscountCell(val promotion: Promotion?) : ISideFragmentCell
class LargeEmptyCell() : ISideFragmentCell
class DateCell(val clock: String, val weekDay: String, val monthDay: String) : ISideFragmentCell
class SmallEmptyCell() : ISideFragmentCell
class WifiCell(val name: String, val password: String) : ISideFragmentCell
class BannerDiscountCell( val promotion: Promotion?) : ISideFragmentCell


interface QRCodeCallback {
    fun onVideoQRCodeChanged(promotion: Promotion?)
    fun onBannerQRCodeChanged(promotion: Promotion?)
}

data class ItemAnalytics(val id: Int = 0, val name: String = "item_name")