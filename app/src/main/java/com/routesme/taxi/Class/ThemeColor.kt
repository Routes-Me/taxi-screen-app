package com.routesme.taxi.Class

import android.graphics.Color
import com.routesme.taxi.MVVM.Model.PromotionColors

data class ThemeColor(val promotionColors: PromotionColors) {

    companion object {
        private const val ThemeTintColor = 0xFF000000.toInt()
        private const val ThemeInvertedTintColor = 0xFFFFFFFF.toInt()
    }

    fun getColor(): Int {
        val color: Int
        val isAnteMeridiem = DisplayManager.instance.isAnteMeridiem()
        val tintColor = promotionColors.tintColor
        val invertedTintColor = promotionColors.invertedTintColor
        if (tintColor != null) {
            if (isAnteMeridiem) {
                color =  tintColor
            } else {
                color = invertedTintColor ?: tintColor
            }
        } else {
            if (isAnteMeridiem) {
                color = ThemeTintColor
            } else {
                color = ThemeInvertedTintColor
            }
        }
        return getRgbColor(color)
    }

    private fun getRgbColor(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.rgb(red,green,blue)
    }
}
