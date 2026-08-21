package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String = "",
    val planType: String = "free", // "free", "pro", "ultra"
    val creditsRemaining: Int = 100,
    val totalPromptsUsed: Int = 0,
    val appsCreatedCount: Int = 0,
    val videosCreatedCount: Int = 0,
    val imagesCreatedCount: Int = 0,
    val isLoggedIn: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
