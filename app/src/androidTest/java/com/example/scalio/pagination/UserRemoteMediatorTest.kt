package com.example.scalio.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scalio.data.local.AppDataBase
import com.example.scalio.data.model.User
import com.example.scalio.data.model.UserMapper
import com.example.scalio.utils.Constants
import com.example.scalio.utils.FakeUserData.FAKE_SEARCH_QUERY
import com.example.scalio.utils.FakeUserData.generateFakeUserApiModel
import com.example.scalio.utils.FakeUsersApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class UserRemoteMediatorTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val fakeQuery = FAKE_SEARCH_QUERY
    private lateinit var fakeUsersApi: FakeUsersApi
    private val mockDb = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AppDataBase::class.java,
    ).build()

    @Inject
    lateinit var userMapper: UserMapper

    @Before
    fun init() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun refreshReturnsSuccessOnMoreData() = runTest {
        fakeUsersApi = FakeUsersApi(generateFakeUserApiModel())
        val usersRemoteMediator = UserRemoteMediator(
            fakeQuery,
            fakeUsersApi,
            mockDb,
            userMapper
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(
                Constants.ITEMS_PER_PAGE
            ),
            Constants.ITEMS_PER_PAGE

        )
        val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshReturnsSuccessAndPaginationEnd() = runTest {
        fakeUsersApi = FakeUsersApi(emptyList())
        val usersRemoteMediator = UserRemoteMediator(
            fakeQuery,
            fakeUsersApi,
            mockDb,
            userMapper
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(Constants.ITEMS_PER_PAGE),
            Constants.ITEMS_PER_PAGE
        )
        val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshReturnsErrorWhenErrorOccurs() = runTest {
        fakeUsersApi = FakeUsersApi(null)
        val usersRemoteMediator = UserRemoteMediator(
            fakeQuery,
            fakeUsersApi,
            mockDb,
            userMapper
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(Constants.ITEMS_PER_PAGE),
            Constants.ITEMS_PER_PAGE
        )
        val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}
