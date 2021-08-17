package com.routesme.vehicles.data.model

data class PublishNearby (val isPublish: Boolean)

data class NearbyData (var deviceId : String? = null, var plateNumber: String? = null, var terminalId: String? = null, var institutionId: String? = null)