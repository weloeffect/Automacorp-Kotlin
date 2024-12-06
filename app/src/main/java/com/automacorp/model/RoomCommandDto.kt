package com.automacorp.model

data class RoomCommandDto(
    val name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val floor: Int = 1,
    // Set to the default building ID (useful when you have not created screens to manage buildings)
    val buildingId: Long = -10
)
