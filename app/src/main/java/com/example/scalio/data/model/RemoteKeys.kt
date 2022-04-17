package com.example.scalio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("user_id")
    val userId: Int = 0,
    @ColumnInfo("prev_key")
    val prevKey: Int?,
    @ColumnInfo("next_key")
    val nextKey: Int?
)
