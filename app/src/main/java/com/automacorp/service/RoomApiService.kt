package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowCommandDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus
import retrofit2.Call
import retrofit2.http.*

interface RoomsApiService {
    @GET("rooms")
    fun findAll(): Call<List<RoomDto>>

    @GET("rooms/{id}")
    fun findById(@Path("id") id: Long): Call<RoomDto>

    @POST("rooms")
    fun createRoom(@Body room: RoomCommandDto): Call<RoomDto>

    @PUT("rooms/{id}")
    fun updateRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>

    @DELETE("rooms/{id}")
    fun deleteRoom(@Path("id") id: Long): Call<Void>

    @GET("rooms/{roomId}/windows")
    fun findWindowsByRoomId(@Path("roomId") roomId: Long): Call<List<WindowDto>>

    @PUT("windows/{windowId}")
    fun updateWindowStatus(@Path("windowId") windowId: Long, @Body command: WindowCommandDto): Call<WindowDto>
}