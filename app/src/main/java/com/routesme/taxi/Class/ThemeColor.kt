package com.routesme.taxi.Class

import android.graphics.Color

data class ThemeColor(val tintColor: Int?) {

    companion object {
        private const val ThemeTintColor = 0xFF000000.toInt()
        private const val ThemeInvertedTintColor = 0xFFFFFFFF.toInt()
    }

    fun getColor(): Int {
        val color: Int
        //val isAnteMeridiem = DisplayManager.instance.isAnteMeridiem()

        color = when (DisplayManager.instance.currentMode) {
            Mode.Light -> tintColor ?: ThemeTintColor
            else -> ThemeInvertedTintColor
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
