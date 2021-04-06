package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.SubmitApplicationVersionCredentials
import com.routesme.vehicles.data.repository.SubmitApplicationVersionRepository

class SubmitApplicationVersionViewModel : ViewModel() {
    fun submitApplicationVersion(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials, context: Context) = SubmitApplicationVersionRepository(context).submitApplicationVersion(deviceId, submitApplicationVersionCredentials)
}