package com.routesme.vehicles.data.model

import com.routesme.vehicles.BuildConfig

data class BusLiveTrackingCredentials(var api_key: String = BuildConfig.NEW_API_KEY, var api_secret: String = BuildConfig.NEW_API_SECRET, var BusID: String? = null, var Latitude: Double? = null, var Longitude: Double? = null)

data class BusLiveTrackingDTO(val status: Boolean = false, val description: Any? = null)