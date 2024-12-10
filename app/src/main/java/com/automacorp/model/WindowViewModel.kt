package com.automacorp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WindowList(
    val windows: List<WindowDto> = emptyList(),
    val error: String? = null
)

class WindowViewModel : ViewModel() {
    val windowsState = MutableStateFlow(WindowList())
    
    fun findWindowsByRoomId(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val response = ApiServices.roomsApiService.findWindowsByRoomId(roomId).execute()
                if (response.isSuccessful) {
                    val windows = response.body() ?: emptyList()
                    windowsState.value = WindowList(windows)
                } else {
                    windowsState.value = WindowList(emptyList(), "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                windowsState.value = WindowList(emptyList(), e.stackTraceToString())
            }
        }
    }

    fun updateWindowStatus(windowId: Long, window: WindowDto, newStatus: WindowStatus) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val command = WindowCommandDto(windowStatus = newStatus)
                val response = ApiServices.roomsApiService.updateWindowStatus(windowId, command).execute()
                if (!response.isSuccessful) {
                    windowsState.value = WindowList(windowsState.value.windows, "Error: ${response.code()} ${response.message()}")
                } else {

                    findWindowsByRoomId(window.roomId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                windowsState.value = WindowList(windowsState.value.windows, e.stackTraceToString())
            }
        }
    }
} 