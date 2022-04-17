package com.example.scalio

import com.example.scalio.data.model.UserMapper
import com.example.scalio.utils.TestUsersData
import junit.framework.Assert.assertEquals
import org.junit.Test

class UserMapperTest {

    @Test
    fun doesUserCorrectlyMapUser() {
        val userMapper = UserMapper()

        val actual = userMapper.mapToLocal(TestUsersData.fakeUserApi)

        assertEquals(TestUsersData.fakeUser, actual)
    }
}
