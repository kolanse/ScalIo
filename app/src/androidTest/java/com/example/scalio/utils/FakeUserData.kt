package com.example.scalio.utils

import com.example.scalio.data.model.User
import com.example.scalio.data.model.UserApiItem

object FakeUserData {

    private val fakeUser = UserApiItem(
        "avatar_url",
        "events_url",
        "followers_url",
        "following_url",
        "gists_url",
        "gravatar_id",
        "html_url",
        0,
        "git",
        "node_id",
        "organizations_url",
        "received_events_url",
        "repos_url",
        20,
        false,
        "starred_url",
        "subscriptions_url",
        "User",
        "url"
    )

    fun generateFakeUserApiModel(): List<UserApiItem> {

        return (0..PER_PAGE_TEST).map {
            fakeUser.copy(login = "$FAKE_SEARCH_QUERY$it", id = it)
        }
    }

    fun getFakeUserModel(): List<User> {
        return generateFakeUserApiModel().map {
            User(
                id = it.id!!,
                avatarUrl = it.avatarUrl!!,
                login = it.login!!,
                type = it.type!!
            )
        }
    }

    const val FAKE_SEARCH_QUERY = "git"
    const val PER_PAGE_TEST = 9
}
