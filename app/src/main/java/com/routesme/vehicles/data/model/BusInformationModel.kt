package com.routesme.vehicles.data.model

class BusInformationModel {

    data class BusInformationModel(val routeNumber: String? = null, val destination: String? = null, val tickets: List<Ticket>, val included: Included, val message: String? = null, val status: Boolean = false, val statusCode: Int = -999)

    data class Ticket(val ticket_id: String? = null, val amount: Double? = null, val currency_id: String? = null)
    data class Included (val currencies: List<Currency>)
    data class Currency (val currency_id: String? = null, val code: String? = null, val symbol: String? = null)

    class BusInformationResponse(busInformationModel: BusInformationModel? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null) : Response(mResponseErrors, mThrowable) {

        var busInformationModel: BusInformationModel? = null
        init {
          this.busInformationModel = busInformationModel
        }

        val isSuccess: Boolean
            get() = (mResponseErrors == null && mThrowable == null)
    }
}