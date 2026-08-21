package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.export.ChatExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AiMediaStudioDialog(
    onSendPromptToChat: (prompt: String, useSearch: Boolean) -> Unit,
    onGenerateImageDirect: suspend (prompt: String, aspectRatio: String) -> Result<String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var selectedStudioTab by remember { mutableIntStateOf(0) } // 0: Image Studio, 1: Video/Veo Studio, 2: Google Search

    // Image Studio State
    var imagePrompt by remember { mutableStateOf("") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    var selectedStyle by remember { mutableStateOf("واقعي سينمائي") }
    var isGeneratingImage by remember { mutableStateOf(false) }
    var generatedImageUrlOrBase64 by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }

    // Video Studio State
    var videoIdea by remember { mutableStateOf("") }
    var videoStyle by remember { mutableStateOf("سينمائي 4K هوليوود") }
    var generatedVideoScript by remember { mutableStateOf<String?>(null) }

    // Search Studio State
    var searchQuery by remember { mutableStateOf("") }

    val styleOptions = listOf(
        "واقعي سينمائي" to "Photorealistic 8K cinematic lighting, highly detailed",
        "أنمي ورسم ياباني" to "Anime Makoto Shinkai style, vibrant colors, aesthetic",
        "تصميم ثلاثي الأبعاد 3D" to "3D Pixar render style, cute volumetric lighting",
        "لوحة فنية زيتية" to "Oil painting on textured canvas, masterpiece",
        "سايبربانك ومستقبلي" to "Cyberpunk futuristic neon glowing city aesthetic",
        "لوجو وهوية بصرية" to "Minimalist vector logo flat icon branding design"
    )

    val aspectRatios = listOf(
        "1:1" to "مربع (1:1)",
        "16:9" to "شاشة عريضة (16:9)",
        "9:16" to "عمودي / ستوري (9:16)",
        "4:3" to "كلاسيكي (4:3)"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "استوديو الإبداع والذكاء الاصطناعي",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "توليد وتعديل الصور، إخراج الفيديو، وبحث Google المباشر",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_media_studio_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Studio Tabs
                TabRow(
                    selectedTabIndex = selectedStudioTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedStudioTab == 0,
                        onClick = { selectedStudioTab = 0 },
                        text = { Text("توليد الصور", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedStudioTab == 1,
                        onClick = { selectedStudioTab = 1 },
                        text = { Text("إخراج الفيديو", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedStudioTab == 2,
                        onClick = { selectedStudioTab = 2 },
                        text = { Text("بحث Google", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Studio Content
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedStudioTab) {
                        0 -> {
                            // IMAGE GENERATION STUDIO
                            Text(
                                text = "وصف الصورة المطلوب توليدها:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = imagePrompt,
                                onValueChange = { imagePrompt = it },
                                placeholder = { Text("مثال: صقر عربي مهيب يقف على برج مستقبلي في الغروب...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("image_prompt_input"),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 2,
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            // Preset Styles
                            Text(
                                text = "الأسلوب الفني (Style):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                styleOptions.forEach { (name, promptModifier) ->
                                    val isSelected = selectedStyle == name
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier.clickable { selectedStyle = name }
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Aspect Ratio
                            Text(
                                text = "أبعاد الصورة (Aspect Ratio):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                aspectRatios.forEach { (ratio, label) ->
                                    val isSelected = selectedAspectRatio == ratio
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedAspectRatio = ratio }
                                    ) {
                                        Text(
                                            text = ratio,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Generate Image Button
                            Button(
                                onClick = {
                                    if (imagePrompt.isBlank()) {
                                        Toast.makeText(context, "الرجاء كتابة وصف للصورة أولاً", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isGeneratingImage = true
                                    imageError = null
                                    scope.launch {
                                        val fullPrompt = "$imagePrompt, ${styleOptions.firstOrNull { it.first == selectedStyle }?.second ?: ""}"
                                        val result = onGenerateImageDirect(fullPrompt, selectedAspectRatio)
                                        result.onSuccess { urlOrBase64 ->
                                            generatedImageUrlOrBase64 = urlOrBase64
                                            isGeneratingImage = false
                                        }.onFailure { err ->
                                            imageError = err.localizedMessage ?: "حدث خطأ أثناء توليد الصورة"
                                            isGeneratingImage = false
                                        }
                                    }
                                },
                                enabled = !isGeneratingImage && imagePrompt.isNotBlank(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_generate_image_button")
                            ) {
                                if (isGeneratingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("جاري توليد الصورة الذكية...")
                                } else {
                                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("توليد الصورة بواسطة Gemini 2.5 Flash Image", fontWeight = FontWeight.Bold)
                                }
                            }

                            // Image Result Display
                            if (generatedImageUrlOrBase64 != null) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (generatedImageUrlOrBase64!!.startsWith("data:image")) {
                                            val base64Data = generatedImageUrlOrBase64!!.substringAfter("base64,")
                                            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                            if (bitmap != null) {
                                                Image(
                                                    bitmap = bitmap.asImageBitmap(),
                                                    contentDescription = "الصورة المولدة",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        } else {
                                            AsyncImage(
                                                model = generatedImageUrlOrBase64,
                                                contentDescription = "الصورة المولدة",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onSendPromptToChat(
                                                "قم بتحليل وتعديل فكرة هذه الصورة: $imagePrompt (أسلوب $selectedStyle)",
                                                false
                                            )
                                            onDismiss()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("إرسال إلى المحادثة", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(imagePrompt))
                                            Toast.makeText(context, "تم نسخ وصف الصورة", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("نسخ الوصف", fontSize = 11.sp)
                                    }
                                }
                            }

                            if (imageError != null) {
                                Text(
                                    text = "⚠️ $imageError",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        1 -> {
                            // VIDEO & VEO STORYBOARD STUDIO
                            Text(
                                text = "مخرج واستوديو الفيديو الذكي (Google Veo Video Prompt):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = videoIdea,
                                onValueChange = { videoIdea = it },
                                placeholder = { Text("مثال: مشهد سيارة طائرة تجوب شوارع دبي المستقبلية ليلاً في المطر...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 2,
                                maxLines = 4
                            )

                            // Video Styles
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "سينمائي 4K هوليوود",
                                    "لقطة طائرة دروون Drone",
                                    "حركة بطيئة Slow Motion 120fps",
                                    "انيميشن ديزني ثلاثي الأبعاد",
                                    "وثائقي ناشيونال جيوغرافيك"
                                ).forEach { style ->
                                    val isSelected = videoStyle == style
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier.clickable { videoStyle = style }
                                    ) {
                                        Text(
                                            text = style,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (videoIdea.isBlank()) {
                                        Toast.makeText(context, "يرجى كتابة فكرة الفيديو", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val script = """
                                        🎬 مشهد فيديو Veo 3.1 الاحترافي:
                                        ------------------------------------
                                        📌 الفكرة: $videoIdea
                                        🎥 الأسلوب والتصوير: $videoStyle
                                        📐 نسبة العرض: 16:9 (1080p Cinematic)
                                        💡 الإضاءة: Volumetric ray tracing & moody atmospheric reflections
                                        🔊 المؤثرات الصوتية: Ambient synth wave & cinematic orchestral swell
                                        
                                        📝 أمر التوليد المباشر (Veo Prompt):
                                        "Cinematic $videoStyle shot of $videoIdea, 4k ultra realistic, highly detailed, slow camera pan, master lighting, 60fps."
                                    """.trimIndent()
                                    generatedVideoScript = script
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد سكريبت وأوامر إخراج الفيديو", fontWeight = FontWeight.Bold)
                            }

                            if (generatedVideoScript != null) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = generatedVideoScript!!,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    onSendPromptToChat(
                                                        "قم بكتابة قصة سينمائية كاملة وتقسيم المشاهد لهذا الفيديو: $videoIdea ($videoStyle)",
                                                        false
                                                    )
                                                    onDismiss()
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("إرسال للمحادثة", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(generatedVideoScript!!))
                                                    Toast.makeText(context, "تم نسخ سكريبت الفيديو", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("نسخ السكريبت", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // GOOGLE SEARCH GROUNDING STUDIO
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Language,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "ميزة ربط واستناد بحث Google المباشر",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "تمكين Gemini 3.5 Flash من جلب أحدث الأخبار والمعلومات المحدثة ومصادر الويب الحية.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "اسأل عن أي معلومة حديثة في العالم:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("مثال: ما هي آخر أخبار استكشاف الفضاء ومؤتمرات الذكاء الاصطناعي هذا الشهر؟") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 2,
                                maxLines = 4
                            )

                            Button(
                                onClick = {
                                    if (searchQuery.isNotBlank()) {
                                        onSendPromptToChat(searchQuery, true)
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "الرجاء كتابة سؤال أو استعلام للبحث", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("إرسال مع تفعيل بحث Google المباشر 🌐", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
