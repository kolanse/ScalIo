package com.example.scalio.data.model

import com.google.gson.annotations.SerializedName

data class UsersApiModel(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean?,
    @SerializedName("items")
    val items: List<UserApiItem>?,
    @SerializedName("total_count")
    val totalCount: Int?
)
