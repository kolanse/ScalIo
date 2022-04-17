package com.example.scalio.data.model

import com.example.scalio.utils.BaseMapper
import javax.inject.Inject

class UserMapper @Inject constructor() : BaseMapper<UserApiItem, User> {

    override fun mapToLocal(userApiItem: UserApiItem): User {
        return User(
            avatarUrl = userApiItem.avatarUrl ?: "",
            login = userApiItem.login ?: "",
            type = userApiItem.type ?: ""
        )
    }
}
