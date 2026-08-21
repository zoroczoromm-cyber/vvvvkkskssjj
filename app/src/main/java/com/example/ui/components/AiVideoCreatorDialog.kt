package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.StoryboardScene
import com.example.model.VideoGenerationRequest
import com.example.model.VideoGenerationResult
import com.example.model.VideoStudioHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiVideoCreatorDialog(
    initialPrompt: String = "",
    onSendPromptToChat: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var videoPrompt by remember { mutableStateOf(initialPrompt) }
    var selectedStyle by remember { mutableStateOf("cinematic") }
    var selectedCameraMovement by remember { mutableStateOf("dynamic") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var durationSeconds by remember { mutableIntStateOf(10) }
    var addVoiceover by remember { mutableStateOf(true) }

    var isGenerating by remember { mutableStateOf(false) }
    var generationResult by remember { mutableStateOf<VideoGenerationResult?>(null) }
    var isPlayingPreview by remember { mutableStateOf(false) }
    var currentScenePlayingIndex by remember { mutableIntStateOf(0) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    // Simulation playback loop
    LaunchedEffect(isPlayingPreview, generationResult) {
        if (isPlayingPreview && generationResult != null) {
            val totalSec = generationResult!!.totalDurationSeconds.coerceAtLeast(4)
            val stepTime = 100L
            val stepAmount = (stepTime.toFloat() / (totalSec * 1000f))
            while (isPlayingPreview) {
                delay(stepTime)
                playbackProgress = (playbackProgress + stepAmount).coerceIn(0f, 1f)
                val sceneCount = generationResult!!.scenes.size.coerceAtLeast(1)
                currentScenePlayingIndex = (playbackProgress * sceneCount).toInt().coerceIn(0, sceneCount - 1)
                if (playbackProgress >= 1f) {
                    playbackProgress = 0f
                    isPlayingPreview = false
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.96f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFE040FB),
                            Color(0xFFFF5722),
                            Color(0xFF00E5FF)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "استوديو صناعة الفيديو (AI Video Studio 4K)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFE040FB))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt Input
                OutlinedTextField(
                    value = videoPrompt,
                    onValueChange = { videoPrompt = it },
                    label = { Text("اكتب فكرة ومشهد الفيديو بالتفصيل:") },
                    placeholder = { Text("مثال: سيارة رياضية مستقبلية تسير في شوارع نيون ممطرة في طوكيو ليلاً مع زوايا كاميرا سينمائية...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Visual Style Selector
                Text(text = "اختر نمط الإخراج البصري (Visual Style):", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(VideoStudioHelper.visualStyles) { (label, key) ->
                        val isSelected = selectedStyle == key
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFE040FB) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedStyle = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Camera Movement Selector
                Text(text = "حركة الكاميرا والإضاءة (Camera Motion):", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(VideoStudioHelper.cameraMovements) { (label, key) ->
                        val isSelected = selectedCameraMovement == key
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCameraMovement = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aspect Ratio & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "أبعاد الفيديو:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("16:9" to "شاشة", "9:16" to "ريلز", "1:1" to "مربع").forEach { (ar, label) ->
                                val isSelected = selectedAspectRatio == ar
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedAspectRatio = ar }
                                ) {
                                    Text(
                                        text = "$ar\n$label",
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "المدة: $durationSeconds ثانية", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Slider(
                            value = durationSeconds.toFloat(),
                            onValueChange = { durationSeconds = it.toInt() },
                            valueRange = 5f..30f,
                            steps = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Voiceover Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "توليد سيناريو وتعليق صوتي ذكي (AI Voiceover):", fontSize = 12.sp)
                    Switch(
                        checked = addVoiceover,
                        onCheckedChange = { addVoiceover = it }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Generate Video Button
                Button(
                    onClick = {
                        if (videoPrompt.isBlank()) {
                            Toast.makeText(context, "يرجى كتابة فكرة الفيديو أولاً", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isGenerating = true
                        scope.launch {
                            delay(1600) // Fast GPU rendering simulation
                            val req = VideoGenerationRequest(
                                title = "فيديو: ${videoPrompt.take(20)}",
                                prompt = videoPrompt,
                                visualStyle = selectedStyle,
                                cameraMovement = selectedCameraMovement,
                                aspectRatio = selectedAspectRatio,
                                durationSeconds = durationSeconds,
                                addVoiceover = addVoiceover
                            )
                            generationResult = VideoStudioHelper.createStoryboard(req)
                            isGenerating = false
                            isPlayingPreview = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_video_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE040FB))
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري إخراج وتوليد مشاهد الفيديو بالذكاء الاصطناعي...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إنشاء وإخراج الفيديو الآن 🎬", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Generated Video Storyboard & Animated Canvas Player
                generationResult?.let { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE040FB))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Player Canvas Area
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF1A1A2E),
                                                Color(0xFF16213E),
                                                Color(0xFF0F3460)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Animated Camera Viewport Grid
                                AnimatedVideoCanvasSimulator(
                                    isPlaying = isPlayingPreview,
                                    aspectRatio = selectedAspectRatio,
                                    currentSceneTitle = result.scenes.getOrNull(currentScenePlayingIndex)?.title ?: "مشهد 1"
                                )

                                // Play / Pause overlay
                                IconButton(
                                    onClick = { isPlayingPreview = !isPlayingPreview },
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                // Progress Indicator at bottom
                                LinearProgressIndicator(
                                    progress = playbackProgress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .align(Alignment.BottomCenter),
                                    color = Color(0xFFE040FB)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Current Scene Narration
                            val activeScene = result.scenes.getOrNull(currentScenePlayingIndex)
                            activeScene?.let { scene ->
                                Text(
                                    text = "🎬 المشهد الحالي [${scene.sceneNumber}/${result.scenes.size}]: ${scene.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFE040FB)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📹 زاوية الكاميرا: ${scene.cameraDirection}",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🎙️ التعليق الصوتي: \"${scene.narrationLine}\"",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Scene Carousel
                            Text(text = "لوحة المشاهد السينمائية (Storyboard Scenes):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(result.scenes) { scene ->
                                    val isCurrent = scene.sceneNumber - 1 == currentScenePlayingIndex
                                    Card(
                                        modifier = Modifier
                                            .width(180.dp)
                                            .clickable {
                                                currentScenePlayingIndex = scene.sceneNumber - 1
                                                playbackProgress = (scene.sceneNumber - 1).toFloat() / result.scenes.size.toFloat()
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCurrent) Color(0xFFE040FB).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isCurrent) 1.5.dp else 0.5.dp,
                                            if (isCurrent) Color(0xFFE040FB) else Color.Gray.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(text = "مشهد ${scene.sceneNumber}: ${scene.durationSeconds} ثوانٍ", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(text = scene.description, fontSize = 10.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action: Share Storyboard
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        val text = "🎬 مشروع فيديو بالذكاء الاصطناعي: ${result.title}\n\n" +
                                                "النمط: ${result.visualStyle}\nالمدة: ${result.totalDurationSeconds} ثانية\n\n" +
                                                result.scenes.joinToString("\n\n") { "مشهد ${it.sceneNumber}: ${it.description}\nالكاميرا: ${it.cameraDirection}\nالتعليق: ${it.narrationLine}" }
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, text)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة سيناريو ومشاهد الفيديو"))
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاركة وتصدير السيناريو الكامل", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedVideoCanvasSimulator(
    isPlaying: Boolean,
    aspectRatio: String,
    currentSceneTitle: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_canvas")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background cinematic lighting sweep
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFE040FB).copy(alpha = 0.25f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.4f * (if (isPlaying) pulse else 1f)
            )
        )

        // Viewfinder Frame
        val frameWidth = if (aspectRatio == "9:16") w * 0.35f else if (aspectRatio == "1:1") h * 0.7f else w * 0.7f
        val frameHeight = if (aspectRatio == "9:16") h * 0.85f else if (aspectRatio == "1:1") h * 0.7f else h * 0.65f

        val left = (w - frameWidth) / 2
        val top = (h - frameHeight) / 2

        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(frameWidth, frameHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
            style = Stroke(width = 2f)
        )

        // Crosshairs in center
        drawLine(
            color = Color(0xFFE040FB).copy(alpha = 0.6f),
            start = Offset(w / 2 - 15f, h / 2),
            end = Offset(w / 2 + 15f, h / 2),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFFE040FB).copy(alpha = 0.6f),
            start = Offset(w / 2, h / 2 - 15f),
            end = Offset(w / 2, h / 2 + 15f),
            strokeWidth = 2f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) Color.Red else Color.Gray)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isPlaying) "REC 4K HDR • $currentSceneTitle" else "PAUSED • 4K HDR",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
