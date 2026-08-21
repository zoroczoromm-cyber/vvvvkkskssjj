package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    suspend fun getAllConversationsOnce(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationByIdOnce(id: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: Long): Flow<ConversationEntity?>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesOnce(): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesForConversationOnce(conversationId: Long): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT 50")
    fun searchMessages(query: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE id = :id")
    suspend fun setConversationPinned(id: Long, isPinned: Boolean)

    @Query("UPDATE conversations SET title = :newTitle WHERE id = :id")
    suspend fun renameConversation(id: Long, newTitle: String)

    @Query("UPDATE conversations SET personaId = :personaId WHERE id = :id")
    suspend fun updateConversationPersona(id: Long, personaId: String)

    @Query("UPDATE conversations SET lastMessageSnippet = :snippet, updatedAt = :updatedAt, messageCount = messageCount + 1 WHERE id = :id")
    suspend fun updateLastMessageSnippet(id: Long, snippet: String, updatedAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("UPDATE chat_messages SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavoriteMessage(id: Long, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokenUsage(usage: com.example.data.local.entity.TokenUsageEntity): Long

    @Query("SELECT * FROM token_usages ORDER BY timestamp DESC")
    fun getAllTokenUsages(): Flow<List<com.example.data.local.entity.TokenUsageEntity>>

    @Query("SELECT * FROM token_usages ORDER BY timestamp DESC")
    suspend fun getAllTokenUsagesOnce(): List<com.example.data.local.entity.TokenUsageEntity>

    @Query("SELECT * FROM token_usages WHERE conversationId = :conversationId ORDER BY timestamp DESC")
    suspend fun getTokenUsagesForConversation(conversationId: Long): List<com.example.data.local.entity.TokenUsageEntity>

    @Query("SELECT SUM(totalTokens) FROM token_usages")
    fun getTotalTokensFlow(): Flow<Long?>

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearMessagesForConversation(conversationId: Long)

    // User & Profile Management
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<com.example.data.local.entity.UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUserOnce(): com.example.data.local.entity.UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): com.example.data.local.entity.UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: com.example.data.local.entity.UserEntity): Long

    @Update
    suspend fun updateUser(user: com.example.data.local.entity.UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :id")
    suspend fun setLoggedInUser(id: Long)

    @Query("UPDATE users SET planType = :planType, creditsRemaining = :credits WHERE id = :userId")
    suspend fun updateUserPlan(userId: Long, planType: String, credits: Int)

    @Query("UPDATE users SET creditsRemaining = creditsRemaining - :cost, totalPromptsUsed = totalPromptsUsed + 1 WHERE id = :userId")
    suspend fun deductUserCredits(userId: Long, cost: Int = 1)

    // Generated Apps (AI App Sandbox)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedApp(app: com.example.data.local.entity.GeneratedAppEntity): Long

    @Query("SELECT * FROM generated_apps ORDER BY createdAt DESC")
    fun getAllGeneratedAppsFlow(): Flow<List<com.example.data.local.entity.GeneratedAppEntity>>

    @Query("SELECT * FROM generated_apps WHERE id = :id LIMIT 1")
    suspend fun getGeneratedAppById(id: Long): com.example.data.local.entity.GeneratedAppEntity?

    // Generated Videos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedVideo(video: com.example.data.local.entity.GeneratedVideoEntity): Long

    @Query("SELECT * FROM generated_videos ORDER BY createdAt DESC")
    fun getAllGeneratedVideosFlow(): Flow<List<com.example.data.local.entity.GeneratedVideoEntity>>
}
