package com.routesme.taxi_screen.MVVM.Model

import java.io.Serializable

data class PaymentData(var driverToken: String = "", var paymentAmount: Double = 0.0) : Serializable
data class PaymentMessage(var identifier: String? = null, var udid: String? = null, var amount: Double? = null, var status: String? = null) : Serializable
data class PaymentProgressMessage(var identifier: String? = null, var status: String? = null) : Serializable
enum class PaymentStatus(val text: String) { Initiate("initiate"), Cancel("cancel"), Paid("paid"), Timeout("timeout") }