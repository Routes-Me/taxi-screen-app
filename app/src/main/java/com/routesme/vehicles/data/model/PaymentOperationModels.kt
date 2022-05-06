package com.routesme.vehicles.data.model

import com.routesme.vehicles.App
import com.routesme.vehicles.R

data class ReadQrCode(val content: String, val isApproved: Boolean, val rejectCauses: String? = null)
enum class PaymentRejectCauses(val message: String){Expired(App.instance.getString(R.string.qrcode_expired_message))}