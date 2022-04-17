package com.example.scalio.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scalio.data.model.User

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users_table WHERE login LIKE '%' || :query || '%' ")
    fun getUsersByLogin(query: String): PagingSource<Int, User>

    @Query("DELETE FROM users_table")
    suspend fun clearUsers()
}
