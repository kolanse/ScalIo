package com.example.scalio.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.scalio.data.local.AppDataBase
import com.example.scalio.data.model.User
import com.example.scalio.data.model.UserMapper
import com.example.scalio.data.remote.UsersApi
import com.example.scalio.pagination.UserRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val appDataBase: AppDataBase,
    private val usersApi: UsersApi,
    private val userMapper: UserMapper
) : UsersRepository {

    override fun getUsers(login: String): Flow<PagingData<User>> {

        val userPagingSourcingFactory = { appDataBase.usersDao().getUsersByLogin(login) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                prefetchDistance = 4,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(
                query = login,
                usersApi,
                appDataBase,
                userMapper
            ),
            pagingSourceFactory = userPagingSourcingFactory

        ).flow
    }

    companion object {
        const val ITEMS_PER_PAGE = 9
    }
}
