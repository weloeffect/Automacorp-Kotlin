package com.automacorp.model

data class RoomDto(
    val id: Long,
    var name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val windows: List<WindowDto>,
)