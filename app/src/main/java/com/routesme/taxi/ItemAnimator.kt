package com.routesme.taxi

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.animation.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_BANNER_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_EMPTY_VIDEO_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_VIDEO_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_WIFI

class ItemAnimator(var context: Context) : SimpleItemAnimator() {
    lateinit var objectAnimator: ObjectAnimator
    val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
    val set2 = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
       // animationUpdateNewLayout(holder)
        return true
    }

    override fun runPendingAnimations() {

    }

    override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {

        return true
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        dispatchAnimationFinished(item)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
       // animationUpdateOldLayout(holder)
        return true
    }

    override fun endAnimations() {
        dispatchAnimationsFinished()
    }

    private fun animationUpdateOldLayout(oldHolder: RecyclerView.ViewHolder?){

        if(oldHolder?.itemViewType == TYPE_VIDEO_DISCOUNT || oldHolder?.itemViewType == TYPE_EMPTY_VIDEO_DISCOUNT){
            set2.interpolator = AccelerateInterpolator()
            set2.setTarget(oldHolder.itemView)
            set2.start()

        }
        if(oldHolder?.itemViewType == TYPE_WIFI || oldHolder?.itemViewType == TYPE_BANNER_DISCOUNT){

            oldHolder.itemView.cameraDistance = 12000f
            oldHolder.itemView.pivotX =180f
            oldHolder.itemView.pivotY = 0f
            objectAnimator = ObjectAnimator.ofFloat(oldHolder.itemView, "rotationY", 0f, 180f)
            objectAnimator?.apply {
                duration = 2000
                AccelerateDecelerateInterpolator()
                start()
            }

        }

    }

    private fun animationUpdateNewLayout( newHolder: RecyclerView.ViewHolder?) {
            if(newHolder?.itemViewType == TYPE_VIDEO_DISCOUNT || newHolder?.itemViewType == TYPE_EMPTY_VIDEO_DISCOUNT){
                newHolder.itemView.pivotX = 0.0f
                newHolder.itemView.pivotY = -newHolder.itemView.height / 0.7f
                newHolder.itemView.cameraDistance = 12000f
                objectAnimator = ObjectAnimator.ofFloat(newHolder.itemView, "rotationX", 180f, 0f)
                objectAnimator?.apply {
                    duration = 2000
                    AccelerateInterpolator()
                    start()
                }

            }
            if(newHolder?.itemViewType == TYPE_WIFI || newHolder?.itemViewType == TYPE_BANNER_DISCOUNT){


                newHolder?.itemView?.startAnimation(zoomIn)

            }
    }
}
