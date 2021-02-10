package com.routesme.taxi.Class

import android.animation.ObjectAnimator
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import carbon.widget.RelativeLayout
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.bottom_bar_advertisement.*

class AnimatorHelper {
    private lateinit var videoObjectAnimator: ObjectAnimator
    private lateinit var imageObjectAnimator: ObjectAnimator
    private lateinit var emptyObjectAnimator: ObjectAnimator
    private lateinit var promotionObjectAnimator: ObjectAnimator
    private lateinit var zoomOut: Animation
    companion object{

        val instance = AnimatorHelper()

    }

   fun setVideoAnimator(playerView: RelativeLayout,backGroundView:RelativeLayout){

       videoObjectAnimator = ObjectAnimator.ofFloat(playerView, "rotationX", -180f, 0f)
       videoObjectAnimator.apply {
           setDuration(1500)
           AccelerateDecelerateInterpolator()
       }
       playerView.bringToFront()
    }



    fun setImageAnimator(addImageView: ImageView, bgImageView: ImageView){

        imageObjectAnimator = ObjectAnimator.ofFloat(addImageView, "rotationY", 0f, 90f)
        imageObjectAnimator.apply {
            setDuration(1500)
            AccelerateDecelerateInterpolator()

        }
        addImageView.bringToFront()
    }

    fun setEmptyBannerAnimation(emptyBannerLayout:RelativeLayout){
        emptyObjectAnimator = ObjectAnimator.ofFloat(emptyBannerLayout,"rotationX",180f, 0f)
        emptyObjectAnimator?.apply {
            duration = 1000
            AccelerateInterpolator()
            //addListener (onEnd = { if(videoBanner.visibility == View.VISIBLE) videoBanner.visibility = View.GONE else  videoBanner.visibility = View.VISIBLE},onStart = {if(emptyVideoBanner.visibility == View.VISIBLE)emptyVideoBanner.visibility = View.VISIBLE else emptyVideoBanner.visibility = View.GONE})
        }
        emptyBannerLayout.bringToFront()

    }

    fun setPromotionBannerAnimation(promotionLayout: RelativeLayout){

        promotionObjectAnimator = ObjectAnimator.ofFloat(promotionLayout,"rotationX",180f, 0f)
        promotionObjectAnimator?.apply {
            duration = 1000
            AccelerateInterpolator()
            //addListener (onEnd = {if(emptyVideoBanner.visibility == View.VISIBLE) emptyVideoBanner.visibility = View.GONE else emptyVideoBanner.visibility = View.VISIBLE},onStart = {if(videoBanner.visibility == View.VISIBLE) videoBanner.visibility=View.VISIBLE else videoBanner.visibility=View.GONE})
        }
        promotionLayout.bringToFront()

    }

    fun startImageAnimation(){

        imageObjectAnimator.start()

    }

    fun startVideoAnimation(){

        videoObjectAnimator.start()


    }

    fun startEmptyBannerAnimation(){

        emptyObjectAnimator.start()
    }

    fun startPromotionBanner(){

        promotionObjectAnimator.start()
    }


}