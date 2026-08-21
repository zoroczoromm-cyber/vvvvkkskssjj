package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppTemplates
import com.example.model.GeneratedAppSpec

@Composable
fun AppPreviewSandboxDialog(
    initialSpec: GeneratedAppSpec?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var currentSpec by remember {
        mutableStateOf(initialSpec ?: AppTemplates.parseOrGenerateAppSpec("تطبيق إدارة مهام ذكي"))
    }

    // 0: Live Preview, 1: Code, 2: Specs & Architecture
    var selectedTab by remember { mutableIntStateOf(0) }
    var refinementPrompt by remember { mutableStateOf("") }
    var isRefining by remember { mutableStateOf(false) }
    var dynamicPrimaryColor by remember { mutableStateOf(Color(0xFF6750A4)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.97f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF00E5FF),
                            Color(0xFFE040FB),
                            Color(0xFFFFB300)
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
                    .padding(16.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "معاينة التطبيق التفاعلية (Live Sandbox)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = currentSpec.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "✨ كود تطبيق ${currentSpec.title} المولد بالذكاء الاصطناعي:\n\n${currentSpec.kotlinCode}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة كود التطبيق"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "مشاركة")
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(currentSpec.kotlinCode))
                                Toast.makeText(context, "تم نسخ كود التطبيق بالكامل! 📋", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الكود")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs (Preview / Code / Specs)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf(
                            Triple(0, "📱 المعاينة الحية", Icons.Default.PhoneAndroid),
                            Triple(1, "💻 الكود المصدري", Icons.Default.Code),
                            Triple(2, "⚙️ المواصفات والمعمارية", Icons.Default.Info)
                        ).forEach { (tabIndex, title, icon) ->
                            val isSelected = selectedTab == tabIndex
                            val bg by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .clickable { selectedTab = tabIndex }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = title,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Live Interactive Phone Mockup
                            InteractiveAppPreviewMockup(
                                spec = currentSpec,
                                primaryColor = dynamicPrimaryColor
                            )
                        }
                        1 -> {
                            // Code Viewer
                            SourceCodeViewer(code = currentSpec.kotlinCode)
                        }
                        2 -> {
                            // Architecture and Specs
                            AppArchitectureSpecsView(spec = currentSpec)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Prompt Refinement Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFE040FB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        OutlinedTextField(
                            value = refinementPrompt,
                            onValueChange = { refinementPrompt = it },
                            placeholder = {
                                Text("اكتب أمراً لتعديل التطبيق فوراً (مثال: غير اللون للأزرق الداكن)...", fontSize = 11.5.sp)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = {
                                if (refinementPrompt.isNotBlank()) {
                                    isRefining = true
                                    val lower = refinementPrompt.lowercase()
                                    if (lower.contains("ازرق") || lower.contains("أزرق") || lower.contains("blue")) {
                                        dynamicPrimaryColor = Color(0xFF1E88E5)
                                    } else if (lower.contains("اخضر") || lower.contains("أخضر") || lower.contains("green")) {
                                        dynamicPrimaryColor = Color(0xFF43A047)
                                    } else if (lower.contains("احمر") || lower.contains("أحمر") || lower.contains("red")) {
                                        dynamicPrimaryColor = Color(0xFFE53935)
                                    } else if (lower.contains("بنفسجي") || lower.contains("purple")) {
                                        dynamicPrimaryColor = Color(0xFF8E24AA)
                                    }
                                    currentSpec = AppTemplates.parseOrGenerateAppSpec(refinementPrompt)
                                    refinementPrompt = ""
                                    isRefining = false
                                    Toast.makeText(context, "تم تحديث المعاينة الحية فوراً! ⚡", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("apply_app_refinement_button")
                        ) {
                            if (isRefining) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "تطبيق التعديل", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveAppPreviewMockup(
    spec: GeneratedAppSpec,
    primaryColor: Color
) {
    // Phone Mockup Frame
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFF333333), RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Simulated Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF222222))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("09:41", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("5G  100% 🔋", color = Color.White, fontSize = 10.sp)
                    }
                }

                // Dynamic App Body according to type
                Box(modifier = Modifier.weight(1f)) {
                    when (spec.type) {
                        "todo" -> InteractiveTodoAppMock(primaryColor)
                        "calculator" -> InteractiveCalculatorMock(primaryColor)
                        "ecommerce" -> InteractiveEcommerceMock(primaryColor)
                        "weather" -> InteractiveWeatherMock(primaryColor)
                        "fitness" -> InteractiveFitnessMock(primaryColor)
                        "notes" -> InteractiveNotesMock(primaryColor)
                        "quiz" -> InteractiveQuizMock(primaryColor)
                        else -> InteractiveCustomAppMock(spec, primaryColor)
                    }
                }
            }
        }
    }
}

// 1. Interactive Todo App Mockup
@Composable
fun InteractiveTodoAppMock(primaryColor: Color) {
    val tasks = remember {
        mutableStateListOf(
            "مراجعة معمارية تطبيق Jetpack Compose" to true,
            "إطلاق خوارزميات الذكاء الاصطناعي" to false,
            "تصميم واجهات المستخدم العصرية M3" to true,
            "اختبار تجربة المستخدم وسرعة الاستجابة" to false
        )
    }
    var newTaskText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("قائمة مهامي الذكية", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                Text("${tasks.count { it.second }} من ${tasks.size} مكتملة", fontSize = 12.sp, color = Color.Gray)
            }
            Surface(
                color = primaryColor.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Text(
                    text = "${((tasks.count { it.second }.toFloat() / tasks.size.coerceAtLeast(1)) * 100).toInt()}%",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Add task input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                placeholder = { Text("أضف مهمة جديدة...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (newTaskText.isNotBlank()) {
                        tasks.add(newTaskText to false)
                        newTaskText = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Task Items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(tasks.indices.toList()) { index ->
                val (taskTitle, isDone) = tasks[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { tasks[index] = taskTitle to !isDone },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) primaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = { tasks[index] = taskTitle to it }
                        )
                        Text(
                            text = taskTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                            color = if (isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { tasks.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// 2. Interactive Calculator Mockup
@Composable
fun InteractiveCalculatorMock(primaryColor: Color) {
    var displayText by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableDoubleStateOf(0.0) }
    var operation by remember { mutableStateOf<String?>(null) }
    var shouldClearDisplay by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Screen
        Surface(
            modifier = Modifier.fillMaxWidth().height(90.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E1E)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = operation?.let { "$firstOperand $it" } ?: "",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = displayText,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Keypad Grid
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { label ->
                        val isOp = label in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = label in listOf("C", "±", "%")
                        val weight = if (label == "0") 2f else 1f

                        Button(
                            onClick = {
                                when (label) {
                                    "C" -> {
                                        displayText = "0"
                                        operation = null
                                        firstOperand = 0.0
                                    }
                                    "±" -> {
                                        val v = displayText.toDoubleOrNull() ?: 0.0
                                        displayText = (v * -1).toString().removeSuffix(".0")
                                    }
                                    in listOf("+", "-", "×", "÷") -> {
                                        firstOperand = displayText.toDoubleOrNull() ?: 0.0
                                        operation = label
                                        shouldClearDisplay = true
                                    }
                                    "=" -> {
                                        val second = displayText.toDoubleOrNull() ?: 0.0
                                        val res = when (operation) {
                                            "+" -> firstOperand + second
                                            "-" -> firstOperand - second
                                            "×" -> firstOperand * second
                                            "÷" -> if (second != 0.0) firstOperand / second else 0.0
                                            else -> second
                                        }
                                        displayText = res.toString().removeSuffix(".0")
                                        operation = null
                                        shouldClearDisplay = true
                                    }
                                    else -> {
                                        if (displayText == "0" || shouldClearDisplay) {
                                            displayText = label
                                            shouldClearDisplay = false
                                        } else {
                                            displayText += label
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(weight).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOp) primaryColor else if (isSpecial) Color(0xFF424242) else Color(0xFF2C2C2C)
                            )
                        ) {
                            Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 3. Interactive E-Commerce Mockup
@Composable
fun InteractiveEcommerceMock(primaryColor: Color) {
    var cartCount by remember { mutableIntStateOf(0) }
    var totalPrice by remember { mutableIntStateOf(0) }

    val products = listOf(
        Triple("سماعة لاسلكية عازلة للضوضاء", 149, "⭐ 4.9"),
        Triple("ساعة ذكية رياضية Ultra", 289, "⭐ 4.8"),
        Triple("كاميرا تصوير 4K احترافية", 450, "⭐ 4.9"),
        Triple("نظارة واقع افتراضي VR", 320, "⭐ 4.7")
    )

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("متجر LuxeStore", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                Text("أحدث المنتجات الإلكترونية", fontSize = 11.sp, color = Color.Gray)
            }
            Surface(
                color = primaryColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$cartCount ($totalPrice$)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(products) { (name, price, rating) ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, maxLines = 1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$$price", color = primaryColor, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Text(rating, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                cartCount++
                                totalPrice += price
                            },
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("إضافة للسلة +", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 4. Interactive Weather Mockup
@Composable
fun InteractiveWeatherMock(primaryColor: Color) {
    var selectedCity by remember { mutableStateOf("الرياض") }
    var currentTemp by remember { mutableIntStateOf(29) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B))))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("الرياض", "دبي", "القاهرة").forEach { city ->
                Text(
                    text = city,
                    color = if (selectedCity == city) Color.White else Color.White.copy(alpha = 0.6f),
                    fontWeight = if (selectedCity == city) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clickable {
                            selectedCity = city
                            currentTemp = when (city) {
                                "دبي" -> 31
                                "القاهرة" -> 26
                                else -> 29
                            }
                        }
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "☀️", fontSize = 48.sp)
        Text(text = "$currentTemp°", fontSize = 52.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "$selectedCity • مشمس ومستقر", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الرطوبة", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("22%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الرياح", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("14 كم/س", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الأشعة UV", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("3 معتدل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// 5. Interactive Fitness Mockup
@Composable
fun InteractiveFitnessMock(primaryColor: Color) {
    var steps by remember { mutableIntStateOf(7240) }
    var waterCups by remember { mutableIntStateOf(5) }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Text("نشاطي اليومي (FitPulse)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الخطوات المنجزة", fontSize = 12.sp, color = primaryColor)
                Text("$steps / 10,000", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (steps / 10000f).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { steps += 500 },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("+ أضف 500 خطوة تمرين", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تتبع شرب الماء: $waterCups أكواب", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Button(
                onClick = { waterCups++ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("+ كوب ماء", fontSize = 11.sp)
            }
        }
    }
}

// 6. Interactive Notes Mockup
@Composable
fun InteractiveNotesMock(primaryColor: Color) {
    val notes = remember {
        mutableStateListOf(
            "خطة إطلاق المشروع في الموعد المحدد",
            "أفكار لتطوير محرك الذكاء الاصطناعي التوليدي",
            "قائمة المهام اليومية مع فريق العمل"
        )
    }
    var noteInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("مفكرتي الذكية (ZenNotes)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = { Text("اكتب ملاحظة سريعة...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (noteInput.isNotBlank()) {
                        notes.add(0, noteInput)
                        noteInput = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(notes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = note,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// 7. Interactive Quiz Mockup
@Composable
fun InteractiveQuizMock(primaryColor: Color) {
    var score by remember { mutableIntStateOf(0) }
    var currentQ by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf<String?>(null) }

    val questions = listOf(
        Triple("ما هي لغة البرمجة الأساسية لتطبيقات أندرويد الحديثة؟", listOf("Kotlin", "PHP", "Ruby"), 0),
        Triple("ما هو إطار العمل الأحدث لبناء واجهات المستخدم في أندرويد؟", listOf("Jetpack Compose", "Flutter", "React Native"), 0),
        Triple("أي نموذج ذكاء اصطناعي طورته Google؟", listOf("Gemini", "Claude", "Llama"), 0)
    )

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("مسابقة الذكاء الاصطناعي", fontWeight = FontWeight.Bold, color = primaryColor)
            Text("النقاط: $score ⭐", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentQ < questions.size) {
            val q = questions[currentQ]
            Text(q.first, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(14.dp))

            q.second.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        if (index == q.third) {
                            score += 10
                            feedback = "إجابة صحيحة! 🎉 (+10 نقاط)"
                        } else {
                            feedback = "إجابة غير صحيحة، حاول مجدداً!"
                        }
                        currentQ++
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.85f))
                ) {
                    Text(option, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            feedback?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Bold)
            }
        } else {
            Text("🏆 مبروك! أنهيت المسابقة بنجاح", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("مجموع نقاطك النهائي: $score نقطة", fontSize = 14.sp, color = primaryColor)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { currentQ = 0; score = 0; feedback = null }) {
                Text("إعادة الاختبار 🔄")
            }
        }
    }
}

// 8. Custom AI App Mockup
@Composable
fun InteractiveCustomAppMock(spec: GeneratedAppSpec, primaryColor: Color) {
    var clickCount by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(spec.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(spec.description, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✨ حالة التطبيق التفاعلي: نشط ويعمل بامتياز", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { clickCount++ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("انقر للتفاعل ($clickCount)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SourceCodeViewer(code: String) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(12.dp)
    ) {
        Text(
            text = code,
            color = Color(0xFFD4D4D4),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        )
    }
}

@Composable
fun AppArchitectureSpecsView(spec: GeneratedAppSpec) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Text(
            text = "معمارية ومواصفات التطبيق المولد:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = spec.architectureNote,
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "الميزات المضمنة في التطبيق:",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        spec.features.forEach { feature ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(feature, fontSize = 12.5.sp)
            }
        }
    }
}
