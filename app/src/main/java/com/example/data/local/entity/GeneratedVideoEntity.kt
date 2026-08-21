package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_videos")
data class GeneratedVideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val visualStyle: String, // "cinematic", "anime", "documentary", "cyberpunk", "drone"
    val cameraMovement: String, // "zoom_in", "pan_right", "orbit", "drone_flyover"
    val aspectRatio: String = "16:9",
    val durationSeconds: Int = 10,
    val storyboardJson: String = "",
    val narrationScript: String = "",
    val previewImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
