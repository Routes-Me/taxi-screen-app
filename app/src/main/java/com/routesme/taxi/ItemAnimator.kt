package com.routesme.taxi

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.animation.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.routesme.taxi.Class.SideFragmentAdapter.VideoBannerAdapter.Companion.TYPE_BANNER
import com.routesme.taxi.Class.SideFragmentAdapter.VideoBannerAdapter.Companion.TYPE_EMPTY
import com.routesme.taxi.Class.SideFragmentAdapter.WifiQrCodeAdapter.Companion.TYPE_QRCODE
import com.routesme.taxi.Class.SideFragmentAdapter.WifiQrCodeAdapter.Companion.TYPE_WIFI

class ItemAnimator(var context: Context) : SimpleItemAnimator() {
    lateinit var objectAnimator: ObjectAnimator
    val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
    val set2 = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        Log.d("Animation","Add")
        animationUpdateNewLayout(holder)
        return true
    }

    override fun runPendingAnimations() {

    }

    override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        Log.d("Animation","Move FromX ${fromX} FromY ${fromY} toX ${toX} toY ${toY}")
        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {
        Log.d("Animation","Change Old ${oldHolder?.itemViewType}, New ${newHolder?.itemViewType}")
        animationUpdateNewLayout(oldHolder)
        //animationUpdateOldLayout(newHolder)
        return true
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        Log.d("Animation","End")
        dispatchAnimationFinished(item)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        Log.d("Animation","Remove")
       animationUpdateOldLayout(holder)
        return true
    }

    override fun endAnimations() {

        dispatchAnimationsFinished()
    }

    private fun animationUpdateOldLayout(oldHolder: RecyclerView.ViewHolder?){

        if(oldHolder?.itemViewType == TYPE_BANNER || oldHolder?.itemViewType == TYPE_EMPTY){
            set2.interpolator = AccelerateInterpolator()
            set2.setTarget(oldHolder.itemView)
            set2.start()

        }
        if(oldHolder?.itemViewType == TYPE_WIFI || oldHolder?.itemViewType == TYPE_QRCODE){

            oldHolder.itemView.cameraDistance = 12000f
            oldHolder.itemView.pivotX =180f
            oldHolder.itemView.pivotY = 0f
            objectAnimator = ObjectAnimator.ofFloat(oldHolder.itemView, "rotationY", 0f, 180f)
            objectAnimator?.apply {
                duration = 1000
                AccelerateDecelerateInterpolator()
                start()
            }

        }

    }

    private fun animationUpdateNewLayout( newHolder: RecyclerView.ViewHolder?) {
            if(newHolder?.itemViewType == TYPE_BANNER || newHolder?.itemViewType == TYPE_EMPTY){
                newHolder.itemView.pivotX = newHolder.itemView.height/1f
                newHolder.itemView.pivotY =newHolder.itemView.height/ 0.7f
                newHolder.itemView.cameraDistance = 12000f
                objectAnimator = ObjectAnimator.ofFloat(newHolder.itemView, "rotationY", 180f, 0f)
                objectAnimator?.apply {
                    duration = 2000
                    AccelerateInterpolator()
                    start()
                }

            }
            if(newHolder?.itemViewType == TYPE_WIFI || newHolder?.itemViewType == TYPE_QRCODE){


                newHolder?.itemView?.startAnimation(zoomIn)

            }
    }
}
