package com.example.scalio.data.remote

import com.example.scalio.data.model.UsersApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {

    @GET("search/users")
    suspend fun getUsers(
        @Query("q") login: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UsersApiModel
}
