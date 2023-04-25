package com.routesme.vehicles.data.model

import com.google.gson.annotations.SerializedName

class VehicleReferralInformationModel {
    data class Vehicles(val pagination: Pagination? = null, @SerializedName("data") val data: List<VehicleData>)
    data class VehicleData(val vehicleId: String? = null, val referralCode: String? = null, val referralURL: String? = null)


    class VehicleReferralResponse(data: List<VehicleData>? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null) : Response(mResponseErrors, mThrowable) {

        var data: VehicleData? = null

        init {
            this.data = data?.firstOrNull()
        }

        val isSuccess: Boolean
            get() = (mResponseErrors == null && mThrowable == null)
    }
}