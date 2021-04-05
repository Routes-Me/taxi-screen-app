package com.routesme.vehicles.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.Data

class ImageBannerAdapter(context: Context, data: List<Data>) : RecyclerView.Adapter<ImageBannerAdapter.ViewHolder?>() {
    private val context: Context = context
    private var list: List<Data> = data


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.side_banner_image) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create view holder to hold reference
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_side_banner, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].url?.let {
            Glide.with(context).load(it).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(holder.imageView)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

}

