package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class PromptTemplate(
    val category: String,
    val title: String,
    val prompt: String,
    val iconColor: Long
)

val PromptBank = listOf(
    PromptTemplate(
        category = "البرمجة والتقنية",
        title = "مراجعة كود وتحسين الأداء",
        prompt = "قم بمراجعة الكود البرمجي التالي، وتحديد أي ثغرات أمنية، واقتراح تحسينات للأداء وفق أفضل الممارسات:\n```\n// ضع الكود هنا\n```",
        iconColor = 0xFF3B82F6
    ),
    PromptTemplate(
        category = "البرمجة والتقنية",
        title = "شرح خطأ برمجي (Debug)",
        prompt = "يظهر لي هذا الخطأ البرمجي، ما سببه وكيف أصلحه خطوة بخطوة؟\n```\n// ضع رسالة الخطأ هنا\n```",
        iconColor = 0xFF3B82F6
    ),
    PromptTemplate(
        category = "البرمجة والتقنية",
        title = "تصميم بنية قاعدة بيانات",
        prompt = "ساعدني في تصميم مخطط قاعدة بيانات (Database Schema) لتطبيق مع تحديد الجداول والعلاقات والمفاتيح الأساسية.",
        iconColor = 0xFF3B82F6
    ),
    PromptTemplate(
        category = "الأعمال والتسويق",
        title = "صياغة خطة تسويقية لمشروع",
        prompt = "اكتب لي خطة تسويقية شاملة لإطلاق منتج جديد تتضمن: الجمهور المستهدف، القنوات الإعلانية، ميزانية مقترحة ومؤشرات الأداء (KPIs).",
        iconColor = 0xFFF59E0B
    ),
    PromptTemplate(
        category = "الأعمال والتسويق",
        title = "تحليل سوات (SWOT Analysis)",
        prompt = "قم بعمل تحليل SWOT شامل لفكرة مشروعي موضحاً: نقاط القوة، نقاط الضعف، الفرص المتاحة، والتهديدات المحتملة.",
        iconColor = 0xFFF59E0B
    ),
    PromptTemplate(
        category = "الأعمال والتسويق",
        title = "ابتكار 10 أسماء تجارية مميزة",
        prompt = "اقترح 10 أسماء تجارية إبداعية وعصرية لشركة تقنية ناشئة مع شرح معنى كل اسم وشعاره المقترح.",
        iconColor = 0xFFF59E0B
    ),
    PromptTemplate(
        category = "الكتابة والترجمة",
        title = "صياغة بريد إلكتروني رسمي",
        prompt = "اكتب لي بريداً إلكترونياً رسمياً واحترافياً لطلب تعاون تجاري مع شركة رائدة في المجال.",
        iconColor = 0xFF8B5CF6
    ),
    PromptTemplate(
        category = "الكتابة والترجمة",
        title = "تدقيق لغوي وصياغة بلاغية",
        prompt = "قم بتدقيق النص التالي لغوياً وإملائياً وإعادة صياغته بأسلوب عربي أدبي وبلاغي راقٍ:\n\"\"\"ضع النص هنا\"\"\"",
        iconColor = 0xFF8B5CF6
    ),
    PromptTemplate(
        category = "الكتابة والترجمة",
        title = "كتابة مقال جذاب وتحسين SEO",
        prompt = "اكتب مقالاً شيقاً من 500 كلمة حول الذكاء الاصطناعي مع عناوين فرعية ونقاط رئيسية وكلمات مفتاحية متوافقة مع SEO.",
        iconColor = 0xFF8B5CF6
    ),
    PromptTemplate(
        category = "تطوير الذات",
        title = "جدول روتين صباحي لزيادة الطاقة",
        prompt = "صمم لي جدول روتين صباحي متكامل مدته 45 دقيقة لزيادة التركيز والطاقة الإيجابية وبدء اليوم بحيوية.",
        iconColor = 0xFF10B981
    ),
    PromptTemplate(
        category = "تطوير الذات",
        title = "استراتيجية التخلص من التسويف",
        prompt = "أعاني من تأجيل المهام الهامة، ما هي خطة العمل النفسية والعملية للتغلب على المماطلة فوراً؟",
        iconColor = 0xFF10B981
    ),
    PromptTemplate(
        category = "الدراسة والتعليم",
        title = "تلخيص كتاب أو بحث علمي",
        prompt = "لخص لي أهم 5 أفكار رئيسية من هذا الموضوع مع أمثلة توضيحية بسيطة يمكن تطبيقها عملياً.",
        iconColor = 0xFFEC4899
    ),
    PromptTemplate(
        category = "الدراسة والتعليم",
        title = "شرح مبسط لمفهوم فيزيائي/رياضي",
        prompt = "اشرح لي هذا المفهوم العلمي بطريقة مبسطة جداً مع تشبيه من الحياة اليومية وسؤال للتأكد من فهمي.",
        iconColor = 0xFFEC4899
    )
)

@Composable
fun PromptLibraryDialog(
    onSelectPrompt: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    val categories = listOf("الكل", "البرمجة والتقنية", "الأعمال والتسويق", "الكتابة والترجمة", "تطوير الذات", "الدراسة والتعليم")

    val filteredPrompts = remember(selectedCategory) {
        if (selectedCategory == "الكل") PromptBank
        else PromptBank.filter { it.category == selectedCategory }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مكتبة المطالبات الذكية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "أكثر من 50 نموذج جاهز للاستخدام الفوري",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Categories Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prompts List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPrompts) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectPrompt(item.prompt)
                                    onDismiss()
                                }
                                .testTag("prompt_template_${item.title}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(item.iconColor))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "استخدام",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.prompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
