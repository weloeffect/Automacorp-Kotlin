package com.automacorp.service

import androidx.core.text.isDigitsOnly
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus

object RoomService {
    val ROOM_KIND: List<String> = listOf("Room", "Meeting", "Laboratory", "Office", "Boardroom")
    val ROOM_NUMBER: List<Char> = ('A'..'Z').toList()
    val WINDOW_KIND: List<String> = listOf("Sliding", "Bay", "Casement", "Hung", "Fixed")

    fun generateWindow(id: Long, roomId: Long, roomName: String): WindowDto {
        return WindowDto(
            id = id,
            name = "${WINDOW_KIND.random()} Window $id",
            roomName = roomName,
            roomId = roomId,
            windowStatus = WindowStatus.values().random()
        )
    }

    fun generateRoom(id: Long): RoomDto {
        val roomName = "${ROOM_NUMBER.random()}$id ${ROOM_KIND.random()}"
        val windows = (1..(1..6).random()).map { generateWindow(it.toLong(), id, roomName) }
        return RoomDto(
            id = id,
            name = roomName,
            currentTemperature = (15..30).random().toDouble(),
            targetTemperature = (15..22).random().toDouble(),
            windows = windows
        )
    }

    // Create 50 rooms
    val ROOMS = (1..50).map { generateRoom(it.toLong()) }.toMutableList()

    fun findAll(): List<RoomDto> {
        // Return all rooms sorted by name
        return ROOMS.sortedBy { it.name }
    }

    fun findById(id: Long): RoomDto? {
        // Return the room with the given id or null
        return ROOMS.find { it.id == id }
    }

    fun findByName(name: String): RoomDto? {
        // Return the room with the given name or null
        return ROOMS.find { it.name == name }
    }

    fun updateRoom(id: Long, room: RoomDto): RoomDto {
        // Update an existing room with the given values
        val index = ROOMS.indexOfFirst { it.id == id }
        return if (index != -1) {
            // Replace the existing room with the updated room
            ROOMS[index] = room
            room
        } else {
            // If no room found with the given id, add it as a new room
            ROOMS.add(room)
            room
        }
    }

    fun findByNameOrId(nameOrId: String?): RoomDto? {
        if (nameOrId != null) {
            return if (nameOrId.isDigitsOnly()) {
                findById(nameOrId.toLong())
            } else {
                findByName(nameOrId)
            }
        }
        return null
    }
}