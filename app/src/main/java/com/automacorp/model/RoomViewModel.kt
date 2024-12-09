package com.automacorp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomList(
    val rooms: List<RoomDto> = emptyList(),
    val error: String? = null
)
class RoomViewModel: ViewModel() {
    val _room = MutableStateFlow<RoomDto?>(null)
    val room: StateFlow<RoomDto?> = _room.asStateFlow()
    
    val roomsState = MutableStateFlow(RoomList())

    private val _windows = MutableStateFlow<List<WindowDto>>(emptyList())
    val windows: StateFlow<List<WindowDto>> = _windows.asStateFlow()

    // Create a new room
    fun createRoom(roomCommand: RoomCommandDto) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val response = ApiServices.roomsApiService.createRoom(roomCommand).execute()
                if (response.isSuccessful) {
                    findAll() // Refresh the list after successful creation
                } else {
                    roomsState.value = RoomList(emptyList(), "Error: ${response.code()} ${response.message()}")
                }
            } catch (error: Exception) {
                error.printStackTrace()
                roomsState.value = RoomList(emptyList(), error.message ?: "Unknown error")
            }
        }
    }

    // Delete a room
    fun deleteRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteRoom(id).execute() }
                .onSuccess {
                    findAll() // Refresh the list after deletion
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString())
                }
        }
    }

    // Add error handling for existing methods
    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val response = ApiServices.roomsApiService.findAll().execute()
                if (response.isSuccessful) {
                    val rooms = response.body() ?: emptyList()
                    roomsState.value = RoomList(rooms)
                } else {
                    roomsState.value = RoomList(emptyList(), "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                roomsState.value = RoomList(emptyList(), e.stackTraceToString())
            }
        }
    }

    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findById(id).execute() }
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _room.value = response.body()
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _room.value = null
                }
        }
    }
    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature ?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature,
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    _room.value = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    _room.value = null
                }
        }
    }

    fun updateWindowStatus(windowId: Long, status: WindowStatus) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val command = WindowCommandDto(status)
                val response = ApiServices.roomsApiService.updateWindowStatus(windowId, command).execute()
                if (response.isSuccessful) {
                    // Refresh the room to get updated window status
                    _room.value?.let { findRoom(it.id) }
                }
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }
}