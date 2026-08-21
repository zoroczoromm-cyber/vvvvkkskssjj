package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock()
    data class Header(val level: Int, val text: String) : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
    data class BlockQuote(val text: String) : MarkdownBlock()
}

fun parseMarkdown(content: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = content.lines()
    var index = 0

    while (index < lines.size) {
        val line = lines[index]
        val trimmed = line.trim()

        if (trimmed.startsWith("```")) {
            val language = trimmed.removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            index++
            while (index < lines.size && !lines[index].trim().startsWith("```")) {
                codeLines.add(lines[index])
                index++
            }
            blocks.add(MarkdownBlock.CodeBlock(language = language, code = codeLines.joinToString("\n")))
            index++
            continue
        }

        if (trimmed.startsWith("### ")) {
            blocks.add(MarkdownBlock.Header(3, trimmed.removePrefix("### ").trim()))
            index++
            continue
        }
        if (trimmed.startsWith("## ")) {
            blocks.add(MarkdownBlock.Header(2, trimmed.removePrefix("## ").trim()))
            index++
            continue
        }
        if (trimmed.startsWith("# ")) {
            blocks.add(MarkdownBlock.Header(1, trimmed.removePrefix("# ").trim()))
            index++
            continue
        }

        if (trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ")) {
            val bulletText = trimmed.substring(2).trim()
            blocks.add(MarkdownBlock.BulletItem(bulletText))
            index++
            continue
        }

        if (trimmed.startsWith("> ")) {
            blocks.add(MarkdownBlock.BlockQuote(trimmed.removePrefix("> ").trim()))
            index++
            continue
        }

        if (trimmed.isNotBlank()) {
            blocks.add(MarkdownBlock.Paragraph(line))
        }
        index++
    }
    return blocks
}

@Composable
fun BlinkingCursor(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSizeMultiplier: Float = 1.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .width((4 * fontSizeMultiplier).dp)
            .height((16 * fontSizeMultiplier).dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun FormattedMarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    fontSizeMultiplier: Float = 1.0f,
    isUserMessage: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    isTyping: Boolean = false
) {
    val blocks = remember(content) { parseMarkdown(content) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        blocks.forEachIndexed { index, block ->
            val isLastBlock = (index == blocks.size - 1)
            when (block) {
                is MarkdownBlock.Header -> {
                    val headerSize = when (block.level) {
                        1 -> 20.sp * fontSizeMultiplier
                        2 -> 18.sp * fontSizeMultiplier
                        else -> 16.sp * fontSizeMultiplier
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = formatInlineMarkdown(block.text),
                            fontSize = headerSize,
                            fontWeight = FontWeight.Bold,
                            color = if (isUserMessage) textColor else MaterialTheme.colorScheme.primary
                        )
                        if (isTyping && isLastBlock) {
                            Spacer(modifier = Modifier.width(4.dp))
                            BlinkingCursor(fontSizeMultiplier = fontSizeMultiplier)
                        }
                    }
                }

                is MarkdownBlock.Paragraph -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatInlineMarkdown(block.text),
                            fontSize = 15.sp * fontSizeMultiplier,
                            lineHeight = 22.sp * fontSizeMultiplier,
                            color = textColor,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isTyping && isLastBlock) {
                            Spacer(modifier = Modifier.width(4.dp))
                            BlinkingCursor(fontSizeMultiplier = fontSizeMultiplier)
                        }
                    }
                }

                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            fontSize = 18.sp * fontSizeMultiplier,
                            fontWeight = FontWeight.Bold,
                            color = if (isUserMessage) textColor else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = formatInlineMarkdown(block.text),
                            fontSize = 14.5.sp * fontSizeMultiplier,
                            lineHeight = 21.sp * fontSizeMultiplier,
                            color = textColor,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isTyping && isLastBlock) {
                            Spacer(modifier = Modifier.width(4.dp))
                            BlinkingCursor(fontSizeMultiplier = fontSizeMultiplier)
                        }
                    }
                }

                is MarkdownBlock.BlockQuote -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = formatInlineMarkdown(block.text),
                                fontSize = 14.sp * fontSizeMultiplier,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 20.sp * fontSizeMultiplier,
                                color = textColor,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (isTyping && isLastBlock) {
                                Spacer(modifier = Modifier.width(4.dp))
                                BlinkingCursor(fontSizeMultiplier = fontSizeMultiplier)
                            }
                        }
                    }
                }

                is MarkdownBlock.CodeBlock -> {
                    CodeBlockView(
                        language = block.language,
                        code = block.code,
                        fontSizeMultiplier = fontSizeMultiplier
                    )
                }
            }
        }
        if (blocks.isEmpty() && isTyping) {
            BlinkingCursor(fontSizeMultiplier = fontSizeMultiplier)
        }
    }
}

@Composable
fun CodeBlockView(
    language: String,
    code: String,
    fontSizeMultiplier: Float = 1.0f
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A))
    ) {
        // Code header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language.isNotBlank()) language else "code",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    isCopied = true
                    Toast.makeText(context, "تم نسخ الكود بنجاح", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        delay(2000)
                        isCopied = false
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "نسخ الكود",
                    tint = if (isCopied) Color(0xFF10B981) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Code content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp * fontSizeMultiplier,
                color = Color(0xFFE2E8F0),
                lineHeight = 18.sp * fontSizeMultiplier
            )
        }
    }
}

fun formatInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < text.length) {
            if (cursor + 1 < text.length && text[cursor] == '*' && text[cursor + 1] == '*') {
                val end = text.indexOf("**", cursor + 2)
                if (end != -1) {
                    val boldText = text.substring(cursor + 2, end)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldText)
                    }
                    cursor = end + 2
                    continue
                }
            } else if (text[cursor] == '`') {
                val end = text.indexOf('`', cursor + 1)
                if (end != -1) {
                    val codeInline = text.substring(cursor + 1, end)
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0x33000000),
                            fontSize = 13.sp
                        )
                    ) {
                        append(" $codeInline ")
                    }
                    cursor = end + 1
                    continue
                }
            }
            append(text[cursor])
            cursor++
        }
    }
}
