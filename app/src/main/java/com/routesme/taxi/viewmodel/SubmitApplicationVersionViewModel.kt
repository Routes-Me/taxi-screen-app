package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.data.model.SubmitApplicationVersionCredentials
import com.routesme.taxi.data.repository.SubmitApplicationVersionRepository

class SubmitApplicationVersionViewModel : ViewModel() {
    fun submitApplicationVersion(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials, context: Context) = SubmitApplicationVersionRepository(context).submitApplicationVersion(deviceId, submitApplicationVersionCredentials)
}