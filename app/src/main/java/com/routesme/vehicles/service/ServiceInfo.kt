package com.routesme.vehicles.service

enum class ServiceInfo(val serviceId: Int, val channelId: String, val channelName: String){
    Tracking(1,"channel_1", "Live Tracking Channel"),
    RefreshToken(2,"channel_2", "Refresh Token Channel"),
    BusValidator(3,"channel_3", "Bus Validator Channel"),
    BusPayment(4,"channel_4","Bus Payment Channel")
}