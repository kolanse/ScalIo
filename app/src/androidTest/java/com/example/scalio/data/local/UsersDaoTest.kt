package com.example.scalio.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scalio.data.local.dao.UsersDao
import com.example.scalio.utils.TEST_LOGIN_QUERY
import com.example.scalio.utils.testUsers
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersDaoTest {
    private lateinit var dataBase: AppDataBase
    private lateinit var usersDao: UsersDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDatabase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dataBase = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        usersDao = dataBase.usersDao()

        usersDao.insertAll(testUsers)
    }

    @After
    fun closeDb() {
        dataBase.close()
    }

    @Test
    fun testGetUsers() = runBlocking {
        val data = usersDao.getUsersByLogin(TEST_LOGIN_QUERY)
        assertNotNull(data)
    }
}
