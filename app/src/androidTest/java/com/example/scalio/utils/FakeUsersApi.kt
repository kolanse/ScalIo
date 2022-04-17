package com.example.scalio.utils

import com.example.scalio.data.model.UserApiItem
import com.example.scalio.data.model.UsersApiModel
import com.example.scalio.data.remote.UsersApi
import com.example.scalio.utils.FakeUserData.PER_PAGE_TEST

class FakeUsersApi(private val fakeItems: List<UserApiItem>?) : UsersApi {

    override suspend fun getUsers(login: String, page: Int, perPage: Int): UsersApiModel {
        return UsersApiModel(
            false,
            fakeItems,
            PER_PAGE_TEST
        )
    }
}
