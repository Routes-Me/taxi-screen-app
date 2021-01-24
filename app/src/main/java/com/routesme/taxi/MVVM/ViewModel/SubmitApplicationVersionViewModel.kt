package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.MVVM.Model.SubmitApplicationVersionCredentials
import com.routesme.taxi.MVVM.Repository.SubmitApplicationVersionRepository

class SubmitApplicationVersionViewModel : ViewModel() {
    fun submitApplicationVersion(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials, context: Context) = SubmitApplicationVersionRepository(context).submitApplicationVersion(deviceId, submitApplicationVersionCredentials)
}