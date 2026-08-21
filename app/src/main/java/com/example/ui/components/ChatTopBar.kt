package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.UserEntity
import com.example.model.Persona
import com.example.model.Personas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    conversation: ConversationEntity?,
    currentUser: UserEntity? = null,
    onOpenDrawer: () -> Unit,
    onOpenPersonaModal: () -> Unit,
    onOpenSearchDialog: () -> Unit,
    onOpenStarredDialog: () -> Unit,
    onOpenSettingsDialog: () -> Unit,
    onOpenStatsDialog: () -> Unit = {},
    onOpenExportDialog: () -> Unit = {},
    onOpenMediaStudio: () -> Unit = {},
    onOpenAppBuilder: () -> Unit = {},
    onOpenVideoStudio: () -> Unit = {},
    onOpenSubscriptions: () -> Unit = {},
    onOpenUserProfile: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onRenameConversation: () -> Unit,
    onTogglePin: () -> Unit,
    onClearChat: () -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val currentPersona = remember(conversation?.personaId) {
        Personas.getById(conversation?.personaId ?: "general")
    }

    Surface(
        shadowElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPersonaModal() }
                        .padding(vertical = 4.dp)
                ) {
                    // Persona avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(currentPersona.colorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentPersona.icon,
                            contentDescription = currentPersona.titleArabic,
                            tint = Color(currentPersona.colorHex),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = conversation?.title ?: "دردشة ذكية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (conversation?.isPinned == true) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "مثبتة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${currentPersona.titleArabic} • اضغط للتغيير",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("menu_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "قائمة المحادثات",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                // User Profile & Plan Indicator Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .testTag("top_user_profile_button")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (currentUser != null) onOpenUserProfile() else onOpenAuth()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentUser != null) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                            ) {
                                if (currentUser.avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = currentUser.avatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentUser.planType) {
                                    "ultra" -> "👑 ألترا"
                                    "pro" -> "⭐ برو"
                                    else -> "⚡ مجاني"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "دخول",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                IconButton(
                    onClick = onOpenSearchDialog,
                    modifier = Modifier.testTag("search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("more_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "المزيد من الخيارات",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("📱 صفحة معاينة التطبيقات (Live Sandbox)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                onOpenAppBuilder()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("🎬 استوديو صناعة الفيديو 4K") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = Color(0xFFE040FB)
                                )
                            },
                            onClick = {
                                onOpenVideoStudio()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("👑 باقات الاشتراك والترقية") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300)
                                )
                            },
                            onClick = {
                                onOpenSubscriptions()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("محادثة جديدة") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                onNewChat()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("الرسائل المفضلة") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B)
                                )
                            },
                            onClick = {
                                onOpenStarredDialog()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("تغيير المستشار الذكي") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            onClick = {
                                onOpenPersonaModal()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("تصدير السجل (JSON / Text)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                onOpenExportDialog()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("استوديو الصور والميديا (AI Studio)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                onOpenMediaStudio()
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("إحصائيات واستخدام الرموز (Charts)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            onClick = {
                                onOpenStatsDialog()
                                showMenu = false
                            }
                        )

                        if (conversation != null) {
                            DropdownMenuItem(
                                text = { Text("إعادة تسمية المحادثة") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DriveFileRenameOutline,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    onRenameConversation()
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(if (conversation.isPinned) "إلغاء التثبيت" else "تثبيت في الأعلى")
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = if (conversation.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    onTogglePin()
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "مسح رسائل المحادثة",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CleaningServices,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    onClearChat()
                                    showMenu = false
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("إعدادات وتخصيص") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                onOpenSettingsDialog()
                                showMenu = false
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
