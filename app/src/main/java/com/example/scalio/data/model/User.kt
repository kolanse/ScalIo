package com.example.scalio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("avatar_url")
    val avatarUrl: String,
    @ColumnInfo("login")
    val login: String,
    @ColumnInfo("type")
    val type: String
)
