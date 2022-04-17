package com.example.scalio.utils

import androidx.paging.*
import com.example.scalio.data.model.RemoteKeys
import com.example.scalio.data.model.User

/**
 ** user objects used for testing
 **/
val testUsers = arrayListOf(
    User(avatarUrl = "www.google.com", login = "Foos", type = "User"),
    User(avatarUrl = "www.google.com", login = "Fooas", type = "User"),
    User(avatarUrl = "www.google.com", login = "Foose", type = "User"),
    User(avatarUrl = "www.google.com", login = "Foops", type = "User"),
    User(avatarUrl = "www.google.com", login = "Foole", type = "User"),
    User(avatarUrl = "www.google.com", login = "Foold", type = "User"),
    User(avatarUrl = "www.google.com", login = "Fooke", type = "User")
)

const val TEST_LOGIN_QUERY = "Foo"

val testRemoteKeys = arrayListOf(
    RemoteKeys(userId = 1, prevKey = 1, nextKey = 2),
    RemoteKeys(userId = 2, prevKey = 2, nextKey = 3),
    RemoteKeys(userId = 3, prevKey = 3, nextKey = 4)
)

private val dcb = object : DifferCallback {
    override fun onChanged(position: Int, count: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}
