package com.example.scalio.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scalio.data.local.dao.RemoteKeysDao
import com.example.scalio.utils.testRemoteKeys
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest {

    private lateinit var dataBase: AppDataBase
    private lateinit var remoteKeysDao: RemoteKeysDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDatabase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dataBase = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        remoteKeysDao = dataBase.remoteKeysDao()

        remoteKeysDao.insertAll(testRemoteKeys)
    }

    @After
    fun closeDb() {
        dataBase.close()
    }

    @Test
    fun testLoadById() = runBlocking {
        val remoteKey = remoteKeysDao.remoteKeysUserId(testRemoteKeys[0].userId)

        assertNotNull(remoteKey)
        assertEquals(remoteKey?.userId, testRemoteKeys[0].userId)
        assertEquals(remoteKey?.nextKey, testRemoteKeys[0].nextKey)
        assertEquals(remoteKey?.prevKey, testRemoteKeys[0].prevKey)
    }

    @Test
    fun testClearDb() = runBlocking {
        remoteKeysDao.clearRemoteKeys()
        val remoteKey = remoteKeysDao.remoteKeysUserId(testRemoteKeys[0].userId)

        assertNull(remoteKey)
    }
}
