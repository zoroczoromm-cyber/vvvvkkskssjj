package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity

@Database(
    entities = [
        ConversationEntity::class,
        ChatMessageEntity::class,
        com.example.data.local.entity.TokenUsageEntity::class,
        com.example.data.local.entity.UserEntity::class,
        com.example.data.local.entity.GeneratedAppEntity::class,
        com.example.data.local.entity.GeneratedVideoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "dardasha_chat_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
