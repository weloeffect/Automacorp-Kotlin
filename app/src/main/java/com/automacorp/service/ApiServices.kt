package com.automacorp.service

object ApiServices {
    val roomsApiService : RoomsApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create()) // (1)
            .baseUrl("http://automacorp.devmind.cleverapps.io/api/") // (2)
            .build()
            .create(RoomsApiService::class.java)
    }
}
