package com.example.model

data class GeneratedAppSpec(
    val id: Long = 0,
    val title: String,
    val type: String, // "todo", "calculator", "weather", "ecommerce", "fitness", "notes", "quiz", "music", "custom"
    val description: String,
    val primaryColorHex: String = "#6200EE",
    val accentColorHex: String = "#03DAC5",
    val kotlinCode: String,
    val features: List<String> = emptyList(),
    val architectureNote: String = "Clean Architecture with Jetpack Compose & StateFlow",
    val prompt: String = ""
)

object AppTemplates {
    fun parseOrGenerateAppSpec(prompt: String): GeneratedAppSpec {
        val lower = prompt.lowercase()
        return when {
            lower.contains("مهام") || lower.contains("todo") || lower.contains("تاسك") -> {
                GeneratedAppSpec(
                    title = "تطبيق تنظيم المهام الذكي (Smart TaskFlow)",
                    type = "todo",
                    description = "تطبيق لإدارة المهام اليومية مع إحصائيات الإنجاز وفئات مختلفة وتنبيهات الأولوية.",
                    primaryColorHex = "#3F51B5",
                    accentColorHex = "#FF4081",
                    features = listOf("إضافة وحذف المهام فوراً", "تحديد مستوى الأولوية (عاجل/عادي)", "مؤشر نسبة الإنجاز المباشر", "تصفية المهام المكتملة"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - Smart TaskFlow App
                        @Composable
                        fun TaskFlowApp() {
                            var tasks by remember { mutableStateOf(listOf("دراسة الذكاء الاصطناعي", "ممارسة التمارين الرياضية", "مراجعة كود التطبيق")) }
                            var newTaskText by remember { mutableStateOf("") }
                            
                            Scaffold(
                                topBar = { TopAppBar(title = { Text("مهامي الذكية") }) },
                                floatingActionButton = {
                                    FloatingActionButton(onClick = { /* Add Task */ }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            ) { padding ->
                                LazyColumn(modifier = Modifier.padding(padding)) {
                                    items(tasks) { task ->
                                        TaskCard(task = task)
                                    }
                                }
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("حاسب") || lower.contains("calculator") || lower.contains("حساب") -> {
                GeneratedAppSpec(
                    title = "الحاسبة الذكية المتطورة (NovaCalc AI)",
                    type = "calculator",
                    description = "حاسبة متقدمة تدعم العمليات الحسابية والنسب المئوية مع سجل حسابات فوري.",
                    primaryColorHex = "#FF5722",
                    accentColorHex = "#FFC107",
                    features = listOf("عمليات الجمع والطرح والضرب والقسمة", "حساب النسب المئوية", "سجل المعاملات السابقة", "تأثيرات بصرية عند النقر"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - NovaCalc AI
                        @Composable
                        fun NovaCalcApp() {
                            var display by remember { mutableStateOf("0") }
                            var memory by remember { mutableStateOf(0.0) }
                            
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                Text(
                                    text = display,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                                )
                                CalculatorKeypad(onKeyClick = { key -> /* Calculate */ })
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("متجر") || lower.contains("تسوق") || lower.contains("سلة") || lower.contains("e-commerce") || lower.contains("store") -> {
                GeneratedAppSpec(
                    title = "متجر إلكتروني فاخر (LuxeStore E-Commerce)",
                    type = "ecommerce",
                    description = "واجهة متجر تسوق عصري مع بطاقات منتجات جذابة وسلة مشتريات ديناميكية وحساب الخصم.",
                    primaryColorHex = "#1E88E5",
                    accentColorHex = "#FF8F00",
                    features = listOf("كتالوج منتجات تفاعلي", "إضافة وحذف العناصر من السلة", "حساب الإجمالي والضريبة تلقائياً", "زر إتمام الطلب الفوري"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - LuxeStore
                        @Composable
                        fun LuxeStoreApp() {
                            var cartCount by remember { mutableStateOf(0) }
                            val products = remember { listOf(Product("سماعات لاسلكية", 199), Product("ساعة ذكية", 299)) }
                            
                            Scaffold(
                                topBar = { StoreHeader(cartCount = cartCount) }
                            ) { padding ->
                                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(padding)) {
                                    items(products) { product ->
                                        ProductCard(product, onAddToCart = { cartCount++ })
                                    }
                                }
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("طقس") || lower.contains("weather") || lower.contains("حرارة") || lower.contains("مطر") -> {
                GeneratedAppSpec(
                    title = "رادار الطقس الذكي (AeroWeather Pro)",
                    type = "weather",
                    description = "تطبيق طقس حي يعرض درجات الحرارة وسرعة الرياح والرطوبة وتوقعات الساعات والأيام القادمة.",
                    primaryColorHex = "#00ACC1",
                    accentColorHex = "#80DEEA",
                    features = listOf("عرض حالة الطقس الحالية والحرارة", "توقعات كل 3 ساعات", "مؤشرات جودة الهواء والرياح", "رسوم بيانية لدرجات الحرارة"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - AeroWeather Pro
                        @Composable
                        fun AeroWeatherApp() {
                            var currentTemp by remember { mutableStateOf(28) }
                            var condition by remember { mutableStateOf("مشمس مع نسيم خفيف") }
                            
                            Column(modifier = Modifier.fillMaxSize().background(WeatherGradient)) {
                                WeatherHero(temp = currentTemp, condition = condition)
                                HourlyForecastRow()
                                WeeklyDetailsList()
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("رياضة") || lower.contains("لياقة") || lower.contains("سعرات") || lower.contains("fitness") || lower.contains("تمارين") -> {
                GeneratedAppSpec(
                    title = "مساعد اللياقة والنشاط (FitPulse AI)",
                    type = "fitness",
                    description = "تطبيق تتبع النشاط البدني وحساب السعرات المحروقة ومؤشر شرب الماء والأهداف اليومية.",
                    primaryColorHex = "#43A047",
                    accentColorHex = "#76FF03",
                    features = listOf("عداد الخطوات والمسافة المقطوعة", "حاسبة السعرات الحرارية", "تتبع شرب الماء التفاعلي", "مؤشر دائري للتقدم اليومي"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - FitPulse AI
                        @Composable
                        fun FitPulseApp() {
                            var steps by remember { mutableStateOf(6420) }
                            var waterGlasses by remember { mutableStateOf(5) }
                            
                            Scaffold { padding ->
                                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                                    CircularActivityProgress(progress = steps / 10000f)
                                    HydrationTracker(glasses = waterGlasses, onAdd = { waterGlasses++ })
                                    WorkoutQuickList()
                                }
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("ملاحظات") || lower.contains("notes") || lower.contains("مفكرة") || lower.contains("تدوين") -> {
                GeneratedAppSpec(
                    title = "المفكرة الذكية السريعة (ZenNotes AI)",
                    type = "notes",
                    description = "تطبيق تدوين أفكار وملاحظات مع تنظيم بالألوان، وسوم، وتلخيص تلقائي بالذكاء الاصطناعي.",
                    primaryColorHex = "#FB8C00",
                    accentColorHex = "#FFE082",
                    features = listOf("كتابة وتعديل الملاحظات فوراً", "تلوين البطاقات وتصنيفها", "شريط بحث سريع", "تثبيت الملاحظات الهامة في الأعلى"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - ZenNotes AI
                        @Composable
                        fun ZenNotesApp() {
                            var notes by remember { mutableStateOf(listOf("خطة إطلاق المشروع الجديد", "أفكار لتطوير تطبيق أندرويد", "قائمة الكتب المقترحة")) }
                            var searchQuery by remember { mutableStateOf("") }
                            
                            Scaffold(
                                topBar = { SearchNotesBar(query = searchQuery, onQueryChange = { searchQuery = it }) }
                            ) { padding ->
                                StaggeredGridNotes(notes = notes, modifier = Modifier.padding(padding))
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            lower.contains("مسابقة") || lower.contains("اختبار") || lower.contains("quiz") || lower.contains("اسئلة") -> {
                GeneratedAppSpec(
                    title = "تطبيق التحدي والمسابقات (QuizMaster AI)",
                    type = "quiz",
                    description = "تطبيق مسابقات ذكي يحتوي على أسئلة تفاعلية متنوعة، توقيت زمني، واحتساب دقيق للنقاط.",
                    primaryColorHex = "#8E24AA",
                    accentColorHex = "#EA80FC",
                    features = listOf("أسئلة اختيار من متعدد تفاعلية", "تأثيرات فورية عند الإجابة الصحيحة والخاطئة", "عداد نقاط وسرعة الإجابة", "لوحة النتائج النهائية"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - QuizMaster AI
                        @Composable
                        fun QuizMasterApp() {
                            var currentQuestionIndex by remember { mutableStateOf(0) }
                            var score by remember { mutableStateOf(0) }
                            
                            Scaffold(
                                topBar = { QuizScoreBar(score = score) }
                            ) { padding ->
                                QuizQuestionScreen(
                                    question = questions[currentQuestionIndex],
                                    onAnswerSelected = { isCorrect ->
                                        if (isCorrect) score += 10
                                        currentQuestionIndex++
                                    },
                                    modifier = Modifier.padding(padding)
                                )
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
            else -> {
                GeneratedAppSpec(
                    title = "تطبيق مخصص ذكي (Custom AI Studio App)",
                    type = "custom",
                    description = "تطبيق أندرويد متكامل ومصمم خصيصاً بناءً على طلبك بالاعتماد على Jetpack Compose و Material Design 3.",
                    primaryColorHex = "#6750A4",
                    accentColorHex = "#D0BCFF",
                    features = listOf("واجهة تفاعلية حديثة M3", "إدارة الحالة باستخدام StateFlow & remember", "أزرار وبطاقات ذكية متجاوبة", "دعم كامل للغة العربية والاتجاه RTL"),
                    kotlinCode = """
                        // Kotlin & Jetpack Compose - Custom AI Studio App
                        @Composable
                        fun GeneratedCustomApp() {
                            var counter by remember { mutableStateOf(1) }
                            var statusText by remember { mutableStateOf("التطبيق يعمل بكفاءة عالية") }
                            
                            Scaffold(
                                topBar = {
                                    CenterAlignedTopAppBar(
                                        title = { Text("تطبيقك المخصص") }
                                    )
                                }
                            ) { padding ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "مرحباً بك في تطبيقك المولد بالذكاء الاصطناعي!", style = MaterialTheme.typography.titleMedium)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(text = statusText, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(onClick = { /* تفاعل */ }) {
                                                Text("اضغط لتجربة التطبيق")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    """.trimIndent(),
                    prompt = prompt
                )
            }
        }
    }
}
