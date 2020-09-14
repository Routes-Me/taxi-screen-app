package com.routesme.taxi_screen

import android.animation.AnimatorInflater
import android.content.Context
import android.util.Log
import android.view.animation.BounceInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.routesme.taxiscreen.R

class ItemAnimator(var context: Context) : SimpleItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        Log.d("item-animation","animateAdd , Holder: ${holder?.itemViewType}")
        animation(null,holder)
        return true
    }

    override fun runPendingAnimations() {
    }

    override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        Log.d("item-animation","animateMove , Holder: ${holder?.itemViewType}")
        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {
        Log.d("item-animation","animateChange , Old Holder: ${oldHolder?.itemViewType} , New Holder: ${newHolder?.itemViewType}")
        animation(oldHolder,newHolder)

        return true
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        Log.d("item-animation","animateRemove , Holder: ${holder?.itemViewType}")
        animation(holder,null)
        return true
    }

    override fun endAnimations() {
    }

    private fun animation(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?) {
        val set1 = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in)
        set1.interpolator = BounceInterpolator()
        set1.setTarget(newHolder?.itemView)
        set1.start()

        val set2 = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
        set2.interpolator = BounceInterpolator()
        set2.setTarget(oldHolder?.itemView)
        set2.start()
    }
}
