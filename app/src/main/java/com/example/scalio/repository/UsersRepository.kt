package com.example.scalio.repository

import androidx.paging.PagingData
import com.example.scalio.data.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    fun getUsers(login: String): Flow<PagingData<User>>
}
