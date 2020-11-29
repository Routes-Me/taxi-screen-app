package com.routesme.taxi.Class.SideFragmentAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.R

class ImageViewPager(var context: Context, var list:List<Data>, var itemClick:RecycleViewClick): PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun getCount()=list.size
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Get the view from pager page layout
        val view = LayoutInflater.from(context).inflate(R.layout.row_feature_image_view,container,false)
        val imageView = view.findViewById(R.id.imageViewBannerImage) as AppCompatImageView
        container.addView(view)
        imageView.setOnClickListener {
           // itemClick.onItemClick(list[position])
        }
        imageView.context?.let {
             Glide.with(it).load(list[position].url).apply(RequestOptions().transforms(CenterCrop(),RoundedCorners(8))).placeholder(R.drawable.empty_promotion).error(R.drawable.empty_promotion).into(imageView)
            //Glide.with(it).load(list[position].url).into(imageView)
        }
        return view
    }

    fun getImageItem(position:Int) : Data{

        return list[position]!!

    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
    }
    interface RecycleViewClick{
        fun onItemClick(data: Data)
    }
}