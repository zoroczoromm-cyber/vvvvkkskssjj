package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.export.ChatExporter
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ExportChatDialog(
    currentConversation: ConversationEntity?,
    onFetchCurrentConversationData: suspend () -> Pair<ConversationEntity, List<ChatMessageEntity>>?,
    onFetchAllConversationsData: suspend () -> List<Pair<ConversationEntity, List<ChatMessageEntity>>>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // Scope: "current" or "all"
    var selectedScope by remember { mutableStateOf(if (currentConversation != null) "current" else "all") }
    // Format: "json" or "text"
    var selectedFormat by remember { mutableStateOf("json") }

    var isLoading by remember { mutableStateOf(true) }
    var exportPreviewContent by remember { mutableStateOf("") }
    var messageCount by remember { mutableStateOf(0) }
    var conversationCount by remember { mutableStateOf(1) }

    fun refreshExport() {
        isLoading = true
        scope.launch {
            if (selectedScope == "current") {
                val data = onFetchCurrentConversationData()
                if (data != null) {
                    val (conv, msgs) = data
                    messageCount = msgs.size
                    conversationCount = 1
                    val content = withContext(Dispatchers.Default) {
                        if (selectedFormat == "json") {
                            ChatExporter.exportConversationToJson(conv, msgs)
                        } else {
                            ChatExporter.exportConversationToText(conv, msgs)
                        }
                    }
                    exportPreviewContent = content
                } else {
                    exportPreviewContent = if (selectedFormat == "json") "{ \"error\": \"لا توجد محادثة محددة\" }" else "لا توجد محادثة محددة للتصدير."
                    messageCount = 0
                }
            } else {
                val allData = onFetchAllConversationsData()
                conversationCount = allData.size
                messageCount = allData.sumOf { it.second.size }
                val content = withContext(Dispatchers.Default) {
                    if (selectedFormat == "json") {
                        ChatExporter.exportAllConversationsToJson(allData)
                    } else {
                        ChatExporter.exportAllConversationsToText(allData)
                    }
                }
                exportPreviewContent = content
            }
            isLoading = false
        }
    }

    LaunchedEffect(selectedScope, selectedFormat) {
        refreshExport()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تصدير سجل المحادثات",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "حفظ ومشاركة السجل من قاعدة بيانات Room",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_export_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scope Selector Tabs (Current vs All)
                Text(
                    text = "نطاق التصدير:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionChip(
                        title = "المحادثة الحالية",
                        subtitle = currentConversation?.title?.take(20) ?: "الدردشة الحالية",
                        icon = Icons.Default.ChatBubbleOutline,
                        isSelected = selectedScope == "current",
                        onClick = { selectedScope = "current" },
                        modifier = Modifier.weight(1f)
                    )

                    OptionChip(
                        title = "كل المحادثات",
                        subtitle = "نسخة احتياطية كاملة",
                        icon = Icons.Default.FolderZip,
                        isSelected = selectedScope == "all",
                        onClick = { selectedScope = "all" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Format Selector Tabs (JSON vs Text)
                Text(
                    text = "صيغة الملف:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionChip(
                        title = "ملف JSON (.json)",
                        subtitle = "منظم للمطورين والتطبيقات",
                        icon = Icons.Default.DataObject,
                        isSelected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" },
                        modifier = Modifier.weight(1f)
                    )

                    OptionChip(
                        title = "ملف نصي (.txt / .md)",
                        subtitle = "تنسيق Markdown للقراءة",
                        icon = Icons.Default.Description,
                        isSelected = selectedFormat == "text",
                        onClick = { selectedFormat = "text" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats and Preview Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معاينة الملف المصدّر:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (selectedScope == "current") "$messageCount رسالة" else "$conversationCount محادثات ($messageCount رسالة)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Live Preview Code/Text Container
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    AnimatedContent(
                        targetState = isLoading,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "preview_animation"
                    ) { loading ->
                        if (loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        } else {
                            val vScroll = rememberScrollState()
                            val hScroll = rememberScrollState()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .verticalScroll(vScroll)
                                    .horizontalScroll(hScroll)
                            ) {
                                Text(
                                    text = exportPreviewContent,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = if (selectedFormat == "json") FontFamily.Monospace else FontFamily.Default,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Share File & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val fileName = if (selectedScope == "current") {
                                val cleanTitle = (currentConversation?.title ?: "chat")
                                    .replace("\\s+".toRegex(), "_")
                                    .take(20)
                                if (selectedFormat == "json") "chat_${cleanTitle}.json" else "chat_${cleanTitle}.txt"
                            } else {
                                if (selectedFormat == "json") "all_chats_backup.json" else "all_chats_backup.txt"
                            }

                            val mimeType = if (selectedFormat == "json") "application/json" else "text/plain"
                            ChatExporter.shareContent(
                                context = context,
                                content = exportPreviewContent,
                                subject = fileName,
                                mimeType = mimeType
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("share_export_file_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedFormat == "json") "مشاركة كـ JSON" else "مشاركة كـ ملف نصي",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(exportPreviewContent))
                            val typeLabel = if (selectedFormat == "json") "JSON" else "النص"
                            Toast.makeText(context, "تم نسخ محتوى $typeLabel بنجاح إلى الحافظة", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_export_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نسخ", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionChip(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else null,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
