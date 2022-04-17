package com.example.scalio.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.scalio.data.local.AppDataBase
import com.example.scalio.data.model.RemoteKeys
import com.example.scalio.data.model.User
import com.example.scalio.data.model.UserMapper
import com.example.scalio.data.remote.UsersApi
import com.example.scalio.utils.Constants
import javax.inject.Inject

@ExperimentalPagingApi
class UserRemoteMediator @Inject constructor(
    private val query: String,
    private val userApi: UsersApi,
    private val appDataBase: AppDataBase,
    private val userMapper: UserMapper
) : RemoteMediator<Int, User>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiQuery = query + Constants.LOGIN_IN_QUALIFIER
            val apiResponse = userApi.getUsers(apiQuery, page, state.config.pageSize)
            val users = apiResponse.items?.let {
                userMapper.mapToLocalList(it)
            } ?: return MediatorResult.Error(Exception())
            val endOfPaginationReached = users.isEmpty()

            appDataBase.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    appDataBase.remoteKeysDao().clearRemoteKeys()
                    appDataBase.usersDao().clearUsers()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = users.map {
                    RemoteKeys(prevKey = prevKey, nextKey = nextKey)
                }

                appDataBase.remoteKeysDao().insertAll(keys)
                appDataBase.usersDao().insertAll(users)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {

            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, User>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user ->
                appDataBase.remoteKeysDao().remoteKeysUserId(user.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, User>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { user ->
                appDataBase.remoteKeysDao().remoteKeysUserId(user.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, User>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { userId ->
                appDataBase.remoteKeysDao().remoteKeysUserId(userId)
            }
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}
