package com.routesme.screen.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.screen.MVVM.Repository.ContentRepository

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)
}