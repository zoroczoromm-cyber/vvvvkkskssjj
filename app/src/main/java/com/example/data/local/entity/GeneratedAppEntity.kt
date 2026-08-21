package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_apps")
data class GeneratedAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appTitle: String,
    val appType: String, // "todo", "calculator", "weather", "ecommerce", "fitness", "notes", "quiz", "music", "custom"
    val description: String,
    val kotlinCode: String,
    val primaryColorHex: String = "#6200EE",
    val promptUsed: String,
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
