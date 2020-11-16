package com.routesme.taxi.MVVM.Model

interface IModeChanging {
    fun onModeChange()
}

interface ISideFragmentCell
class EmptyVideoDiscountCell : ISideFragmentCell
class VideoDiscountCell(val data: Data) : ISideFragmentCell
class LargeEmptyCell : ISideFragmentCell
class DateCell(val clock: String, val weekDay: String, val monthDay: String) : ISideFragmentCell
class SmallEmptyCell : ISideFragmentCell
class WifiCell() : ISideFragmentCell
class BannerDiscountCell(val data: Data) : ISideFragmentCell