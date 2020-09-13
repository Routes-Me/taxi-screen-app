package com.routesme.taxi_screen.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.MVVM.Repository.ContentRepository

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)
}