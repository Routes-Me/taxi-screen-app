package com.routesme.vehicles.data

data class ReadQrCode(val content: String, val isApproved: Boolean, val rejectCauses: PaymentRejectCauses? = null)
enum class PaymentRejectCauses(val message: String){Expired("Your qr code is expired, please make sure to present from the App")}