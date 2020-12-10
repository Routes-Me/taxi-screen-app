package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.MVVM.Repository.ContentRepository

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)
}