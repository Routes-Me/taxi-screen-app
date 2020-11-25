package com.routesme.taxi

import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager

class BookFlipPageTransformer : ViewPager.PageTransformer {
    private val LEFT = -1
    private val RIGHT = 1
    private val CENTER = 0
    var scaleAmountPercent = 5f
    var isEnableScale = true

    override fun transformPage(page: View, position: Float) {
        val percentage = 1 - Math.abs(position)
        // Don't move pages once they are on left or right
        Log.d("Position", position.toString())
        if (position > CENTER && position <= RIGHT) {
            // This is behind page
            page.translationX = -position * page.width
            page.translationY = 0f
            page.rotation = 0f

            if (isEnableScale)
            {
                var amount = ((100 - scaleAmountPercent) + ( scaleAmountPercent * percentage)) / 100;
                setSize(page, position, amount);
            }
        } else {
            page.visibility = View.VISIBLE
            flipPage(page, position, percentage)
        }
    }

    private fun flipPage(page: View, position: Float, percentage: Float) {
        // Flip this page
        page.cameraDistance = -12000f
        setVisibility(page, position)
        setTranslation(page)
        setPivot(page, 0f, page.height * 0.5f)
        setRotation(page, position, percentage)
    }

    private fun setPivot(page: View, pivotX: Float, pivotY: Float) {
        page.pivotX = pivotX
        page.pivotY = pivotY
    }

    private fun setVisibility(page: View, position: Float) {
        if (position < 0.5 && position > -0.5) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }
    }

    private fun setTranslation(page: View) {
        val viewPager = page.parent as ViewPager
        val scroll = viewPager.scrollX - page.left
        page.translationX = scroll.toFloat()
    }

    private fun setSize(page: View, position: Float, percentage: Float) {
        page.setScaleX(if (position != 0f && position != 1f) percentage else 1f)
        page.setScaleY(if (position != 0f && position != 1f) percentage else 1f)
    }

    private fun setRotation(page: View, position: Float, percentage: Float) {
        if (position > 0) {

            page.rotationY = -180 * (percentage + 1)
        } else {
            page.rotationY = 180 * (percentage + 1)
        }
    }

}