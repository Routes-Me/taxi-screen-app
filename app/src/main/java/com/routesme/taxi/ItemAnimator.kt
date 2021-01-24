package com.routesme.taxi

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Context
import android.view.animation.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_BANNER_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_EMPTY_VIDEO_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_VIDEO_DISCOUNT
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter.Companion.TYPE_WIFI

class ItemAnimator(var context: Context) : SimpleItemAnimator() {
    var objectAnimator: ObjectAnimator?=null
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        animation(null,holder)
        return true
    }

    override fun runPendingAnimations() {

    }

    override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {

        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {

        animation(oldHolder,newHolder)

        return true
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {

    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {

        animation(holder,null)
        return true
    }

    override fun endAnimations() {

    }

    private fun animation(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?) {

        if(newHolder != null){
            if(newHolder.itemViewType == TYPE_VIDEO_DISCOUNT || newHolder.itemViewType == TYPE_EMPTY_VIDEO_DISCOUNT){
                newHolder.itemView.pivotX = 0.0f
                newHolder.itemView.pivotY = -newHolder.itemView.height / 0.7f
                newHolder.itemView.cameraDistance = 12000f
                objectAnimator = ObjectAnimator.ofFloat(newHolder.itemView, "rotationX", 180f, 0f)
                objectAnimator!!.apply {
                    duration = 1000
                    AccelerateInterpolator()
                    start()
                }

            }else if(newHolder.itemViewType == TYPE_WIFI || newHolder.itemViewType == TYPE_BANNER_DISCOUNT){

                val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
                newHolder.itemView.startAnimation(zoomIn)

            }
        }

        if(oldHolder!= null){
            if(oldHolder.itemViewType == TYPE_VIDEO_DISCOUNT || oldHolder.itemViewType == TYPE_EMPTY_VIDEO_DISCOUNT){

                val set2 = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
                set2.interpolator = AccelerateInterpolator()
                set2.setTarget(oldHolder.itemView)
                set2.start()

            }else if(oldHolder.itemViewType == TYPE_WIFI || oldHolder.itemViewType == TYPE_BANNER_DISCOUNT){

                oldHolder.itemView.cameraDistance = 12000f
                oldHolder.itemView.pivotX =180f
                oldHolder.itemView.pivotY = 0f
                objectAnimator = ObjectAnimator.ofFloat(oldHolder.itemView, "rotationY", 0f, 180f)
                objectAnimator!!.apply {
                    duration = 1000
                    AccelerateDecelerateInterpolator()
                    start()
                }


            }

        }

    }
}
