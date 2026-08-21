package com.example.data.export

import android.content.Context
import android.content.Intent
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.model.Personas
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatExporter {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    private val readableDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale("ar"))
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale("ar"))

    /**
     * Export a single conversation and its messages as a formatted JSON string.
     */
    fun exportConversationToJson(
        conversation: ConversationEntity,
        messages: List<ChatMessageEntity>
    ): String {
        val root = JSONObject()
        root.put("exportVersion", "1.0")
        root.put("exportSource", "Smart Chat AI - Android Local Database")
        root.put("exportedAt", isoDateFormat.format(Date()))
        root.put("exportedAtReadable", readableDateFormat.format(Date()))

        val persona = Personas.getById(conversation.personaId)
        val convJson = JSONObject().apply {
            put("id", conversation.id)
            put("title", conversation.title)
            put("personaId", conversation.personaId)
            put("personaTitleArabic", persona.titleArabic)
            put("createdAt", conversation.createdAt)
            put("createdAtReadable", readableDateFormat.format(Date(conversation.createdAt)))
            put("updatedAt", conversation.updatedAt)
            put("updatedAtReadable", readableDateFormat.format(Date(conversation.updatedAt)))
            put("isPinned", conversation.isPinned)
            put("totalMessages", messages.size)
        }
        root.put("conversation", convJson)

        val messagesArray = JSONArray()
        for (msg in messages) {
            val msgObj = JSONObject().apply {
                put("id", msg.id)
                put("conversationId", msg.conversationId)
                put("role", msg.role)
                put("content", msg.content)
                put("timestamp", msg.timestamp)
                put("timeReadable", timeFormat.format(Date(msg.timestamp)))
                put("isFavorite", msg.isFavorite)
                put("isVoice", msg.isVoice)
                put("voiceDurationSeconds", msg.voiceDurationSeconds)
                put("status", msg.status)
            }
            messagesArray.put(msgObj)
        }
        root.put("messages", messagesArray)

        return root.toString(2)
    }

    /**
     * Export a single conversation and its messages as clean Markdown / Plain Text.
     */
    fun exportConversationToText(
        conversation: ConversationEntity,
        messages: List<ChatMessageEntity>
    ): String {
        val persona = Personas.getById(conversation.personaId)
        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("💬 ").append(conversation.title).append("\n")
        sb.append("========================================\n")
        sb.append("👤 المستشار الذكي: ").append(persona.titleArabic).append(" (").append(persona.subtitleArabic).append(")\n")
        sb.append("📅 تاريخ الإنشاء: ").append(readableDateFormat.format(Date(conversation.createdAt))).append("\n")
        sb.append("📊 عدد الرسائل: ").append(messages.size).append(" رسالة\n")
        sb.append("----------------------------------------\n\n")

        for (msg in messages) {
            val roleLabel = when (msg.role) {
                "user" -> "👤 أنت"
                "assistant" -> "🤖 ${persona.titleArabic}"
                else -> "⚙️ النظام"
            }
            val time = timeFormat.format(Date(msg.timestamp))
            val star = if (msg.isFavorite) " ⭐ [مفضلة]" else ""
            val voice = if (msg.isVoice) " 🎙️ [صوتي: ${msg.voiceDurationSeconds} ث]" else ""

            sb.append("[$time] $roleLabel$star$voice:\n")
            sb.append(msg.content.trim()).append("\n\n")
            sb.append("----------------------------------------\n\n")
        }

        sb.append("تم التصدير من تطبيق الدردشة الذكي - قاعدة بيانات Room المحلية\n")
        return sb.toString()
    }

    /**
     * Export all conversations in the local Room database as a comprehensive backup JSON.
     */
    fun exportAllConversationsToJson(
        data: List<Pair<ConversationEntity, List<ChatMessageEntity>>>
    ): String {
        val root = JSONObject()
        root.put("exportVersion", "1.0")
        root.put("exportType", "FULL_ROOM_DATABASE_BACKUP")
        root.put("exportSource", "Smart Chat AI - Android Local Database")
        root.put("exportedAt", isoDateFormat.format(Date()))
        root.put("exportedAtReadable", readableDateFormat.format(Date()))
        root.put("totalConversations", data.size)
        root.put("totalMessages", data.sumOf { it.second.size })

        val conversationsArray = JSONArray()
        for ((conv, msgs) in data) {
            val persona = Personas.getById(conv.personaId)
            val convObj = JSONObject().apply {
                put("id", conv.id)
                put("title", conv.title)
                put("personaId", conv.personaId)
                put("personaTitleArabic", persona.titleArabic)
                put("createdAt", conv.createdAt)
                put("createdAtReadable", readableDateFormat.format(Date(conv.createdAt)))
                put("updatedAt", conv.updatedAt)
                put("updatedAtReadable", readableDateFormat.format(Date(conv.updatedAt)))
                put("isPinned", conv.isPinned)
                put("messageCount", msgs.size)

                val msgsArray = JSONArray()
                for (msg in msgs) {
                    msgsArray.put(JSONObject().apply {
                        put("id", msg.id)
                        put("conversationId", msg.conversationId)
                        put("role", msg.role)
                        put("content", msg.content)
                        put("timestamp", msg.timestamp)
                        put("timeReadable", timeFormat.format(Date(msg.timestamp)))
                        put("isFavorite", msg.isFavorite)
                        put("isVoice", msg.isVoice)
                        put("voiceDurationSeconds", msg.voiceDurationSeconds)
                        put("status", msg.status)
                    })
                }
                put("messages", msgsArray)
            }
            conversationsArray.put(convObj)
        }
        root.put("conversations", conversationsArray)

        return root.toString(2)
    }

    /**
     * Export all conversations as a combined readable Text document.
     */
    fun exportAllConversationsToText(
        data: List<Pair<ConversationEntity, List<ChatMessageEntity>>>
    ): String {
        val sb = StringBuilder()
        sb.append("====================================================\n")
        sb.append("📚 سجل كافة المحادثات - نسخة احتياطية محلية كاملة\n")
        sb.append("====================================================\n")
        sb.append("📅 تاريخ التصدير: ").append(readableDateFormat.format(Date())).append("\n")
        sb.append("📁 إجمالي المحادثات: ").append(data.size).append("\n")
        sb.append("💬 إجمالي الرسائل: ").append(data.sumOf { it.second.size }).append("\n")
        sb.append("====================================================\n\n")

        data.forEachIndexed { index, (conv, msgs) ->
            sb.append("\n========================================\n")
            sb.append("المحادثة #${index + 1}: ${conv.title}\n")
            sb.append("========================================\n")
            sb.append(exportConversationToText(conv, msgs))
            sb.append("\n\n")
        }

        return sb.toString()
    }

    /**
     * Share content via Android Intent Chooser
     */
    fun shareContent(
        context: Context,
        content: String,
        subject: String,
        mimeType: String = "text/plain"
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
        }
        context.startActivity(Intent.createChooser(sendIntent, subject))
    }
}
