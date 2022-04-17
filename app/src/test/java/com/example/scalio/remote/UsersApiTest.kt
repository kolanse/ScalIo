package com.example.scalio.remote

import com.example.scalio.data.remote.UsersApi
import com.example.scalio.utils.BASE_URL
import com.example.scalio.utils.LOGIN_IN_QUALIFIER
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UsersApiTest {

    private val mockResponse = """
            
  { "total_count": 14354,
    "incomplete_results": true,
    "items": [
    {
      "login": "foo",
      "id": 33384,
      "node_id": "MDQ6VXNlcjMzMzg0",
      "avatar_url": "https://avatars.githubusercontent.com/u/33384?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/foo",
      "html_url": "https://github.com/foo",
      "followers_url": "https://api.github.com/users/foo/followers",
      "following_url": "https://api.github.com/users/foo/following{/other_user}",
      "gists_url": "https://api.github.com/users/foo/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/foo/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/foo/subscriptions",
      "organizations_url": "https://api.github.com/users/foo/orgs",
      "repos_url": "https://api.github.com/users/foo/repos",
      "events_url": "https://api.github.com/users/foo/events{/privacy}",
      "received_events_url": "https://api.github.com/users/foo/received_events",
      "type": "User",
      "site_admin": false,
      "score": 1.0
    },
    {
      "login": "foosel",
      "id": 83657,
      "node_id": "MDQ6VXNlcjgzNjU3",
      "avatar_url": "https://avatars.githubusercontent.com/u/83657?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/foosel",
      "html_url": "https://github.com/foosel",
      "followers_url": "https://api.github.com/users/foosel/followers",
      "following_url": "https://api.github.com/users/foosel/following{/other_user}",
      "gists_url": "https://api.github.com/users/foosel/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/foosel/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/foosel/subscriptions",
      "organizations_url": "https://api.github.com/users/foosel/orgs",
      "repos_url": "https://api.github.com/users/foosel/repos",
      "events_url": "https://api.github.com/users/foosel/events{/privacy}",
      "received_events_url": "https://api.github.com/users/foosel/received_events",
      "type": "User",
      "site_admin": false,
      "score": 1.0
    }
    ]
    }
    """.trimIndent()

    private lateinit var mockWebServer: MockWebServer
    /**
     * Helper method to get a UserApi instance
     */
    private fun getUserApiService(): UsersApi {
        val baseUrl = mockWebServer.url(BASE_URL)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(UsersApi::class.java)
    }

    @Test
    fun canResponseObjectBeParsed() = runBlocking {
        mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setBody(mockResponse))
        mockWebServer.start()

        val service = getUserApiService()

        val response = service.getUsers(LOGIN_IN_QUALIFIER, 1, 9)
        assertNotNull(response)
        assertTrue(response.incompleteResults == true)

        mockWebServer.shutdown()
    }
}
