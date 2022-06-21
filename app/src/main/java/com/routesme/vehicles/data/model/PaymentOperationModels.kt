package com.routesme.vehicles.data.model

import com.routesme.vehicles.App
import com.routesme.vehicles.R
import java.io.Serializable

data class PaymentResult(val userID: String, val userName: String, val isApproved: Boolean, val rejectedReason: String? = null): Serializable
enum class PaymentRejectCauses(val message: String){Expired(App.instance.getString(R.string.qrcode_expired_message))}