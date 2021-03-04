package com.routesme.taxi.data.model

interface IModeChanging {
    fun onModeChange()
}


interface on

interface ISideFragmentCell
class EmptyVideoDiscountCell(val screenWidth: Int) : ISideFragmentCell
class VideoDiscountCell(val data: Data, val screenWidth:Int) : ISideFragmentCell
class LargeEmptyCell : ISideFragmentCell
class DateCell(val clock: String, val weekDay: String, val monthDay: String) : ISideFragmentCell
class SmallEmptyCell : ISideFragmentCell
class WifiCell : ISideFragmentCell
class BannerDiscountCell(val data: Data) : ISideFragmentCell