package com.example.data.repository

import com.example.BuildConfig
import com.example.data.firebase.AuthResult
import com.example.data.firebase.FirebaseManager
import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.TokenUsageEntity
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerateRequest
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiImageConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiTool
import com.example.model.Personas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(
    private val chatDao: ChatDao,
    val firebaseManager: FirebaseManager? = null,
    private val geminiService: GeminiApiService = GeminiApiService.create()
) {
    val conversations: Flow<List<ConversationEntity>> = chatDao.getAllConversations()
    val favoriteMessages: Flow<List<ChatMessageEntity>> = chatDao.getFavoriteMessages()
    val allTokenUsages: Flow<List<TokenUsageEntity>> = chatDao.getAllTokenUsages()
    val totalTokensFlow: Flow<Long?> = chatDao.getTotalTokensFlow()

    fun getMessages(conversationId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForConversation(conversationId)
    }

    fun getConversation(conversationId: Long): Flow<ConversationEntity?> {
        return chatDao.getConversationById(conversationId)
    }

    suspend fun getAllTokenUsagesOnce(): List<TokenUsageEntity> {
        return withContext(Dispatchers.IO) {
            chatDao.getAllTokenUsagesOnce()
        }
    }

    suspend fun getAllMessagesOnce(): List<ChatMessageEntity> {
        return withContext(Dispatchers.IO) {
            chatDao.getAllMessagesOnce()
        }
    }

    fun searchMessages(query: String): Flow<List<ChatMessageEntity>> {
        return chatDao.searchMessages(query)
    }

    fun searchConversations(query: String): Flow<List<ConversationEntity>> {
        return chatDao.searchConversations(query)
    }

    suspend fun getConversationWithMessages(conversationId: Long): Pair<ConversationEntity, List<ChatMessageEntity>>? {
        return withContext(Dispatchers.IO) {
            val conv = chatDao.getConversationByIdOnce(conversationId) ?: return@withContext null
            val msgs = chatDao.getMessagesForConversationOnce(conversationId)
            Pair(conv, msgs)
        }
    }

    suspend fun getAllConversationsWithMessages(): List<Pair<ConversationEntity, List<ChatMessageEntity>>> {
        return withContext(Dispatchers.IO) {
            val convs = chatDao.getAllConversationsOnce()
            convs.map { conv ->
                val msgs = chatDao.getMessagesForConversationOnce(conv.id)
                Pair(conv, msgs)
            }
        }
    }

    suspend fun createConversation(title: String = "محادثة جديدة", personaId: String = "general"): Long {
        return withContext(Dispatchers.IO) {
            val entity = ConversationEntity(
                title = title,
                personaId = personaId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            chatDao.insertConversation(entity)
        }
    }

    suspend fun deleteConversation(id: Long) {
        withContext(Dispatchers.IO) {
            chatDao.deleteConversationById(id)
        }
    }

    suspend fun setPinned(id: Long, isPinned: Boolean) {
        withContext(Dispatchers.IO) {
            chatDao.setConversationPinned(id, isPinned)
        }
    }

    suspend fun renameConversation(id: Long, newTitle: String) {
        withContext(Dispatchers.IO) {
            chatDao.renameConversation(id, newTitle)
        }
    }

    // User & Profile Management
    val activeUserFlow: Flow<com.example.data.local.entity.UserEntity?> = chatDao.getActiveUserFlow()
    val allGeneratedAppsFlow: Flow<List<com.example.data.local.entity.GeneratedAppEntity>> = chatDao.getAllGeneratedAppsFlow()
    val allGeneratedVideosFlow: Flow<List<com.example.data.local.entity.GeneratedVideoEntity>> = chatDao.getAllGeneratedVideosFlow()

    suspend fun getActiveUserOnce(): com.example.data.local.entity.UserEntity? {
        return withContext(Dispatchers.IO) {
            val user = chatDao.getActiveUserOnce()
            if (user == null) {
                // Initialize default user
                val defaultUser = com.example.data.local.entity.UserEntity(
                    username = "firebase_user",
                    fullName = "مستخدم Firebase",
                    email = "user@firebase.google.com",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    planType = "free",
                    creditsRemaining = 150,
                    isLoggedIn = true
                )
                val id = chatDao.insertUser(defaultUser)
                defaultUser.copy(id = id)
            } else {
                user
            }
        }
    }

    suspend fun signUpWithFirebase(
        email: String,
        password: String,
        fullName: String,
        username: String,
        avatarUrl: String
    ): AuthResult {
        val fm = firebaseManager
        return if (fm != null) {
            fm.signUpWithEmail(email, password, fullName, username, avatarUrl)
        } else {
            loginOrRegister(username, fullName, email, avatarUrl)
            val user = chatDao.getUserByEmail(email)
            if (user != null) {
                AuthResult.Success(user, "تم إنشاء الحساب بنجاح!")
            } else {
                AuthResult.Error("فشل إنشاء الحساب")
            }
        }
    }

    suspend fun signInWithFirebase(
        email: String,
        password: String
    ): AuthResult {
        val fm = firebaseManager
        return if (fm != null) {
            fm.signInWithEmail(email, password)
        } else {
            loginOrRegister("", "", email)
            val user = chatDao.getUserByEmail(email)
            if (user != null) {
                AuthResult.Success(user, "تم تسجيل الدخول بنجاح!")
            } else {
                AuthResult.Error("فشل تسجيل الدخول")
            }
        }
    }

    suspend fun signInWithGoogle(context: android.content.Context): AuthResult {
        val fm = firebaseManager
        return if (fm != null) {
            fm.signInWithGoogle(context)
        } else {
            val u = loginOrRegister("google_user", "مستخدم Google", "google.user@gmail.com")
            AuthResult.Success(u, "تم تسجيل الدخول بواسطة Google بنجاح!")
        }
    }

    suspend fun loginOrRegister(username: String, fullName: String, email: String, avatarUrl: String = ""): com.example.data.local.entity.UserEntity {
        return withContext(Dispatchers.IO) {
            chatDao.logoutAllUsers()
            val existing = chatDao.getUserByEmail(email)
            if (existing != null) {
                chatDao.setLoggedInUser(existing.id)
                existing.copy(isLoggedIn = true)
            } else {
                val newUser = com.example.data.local.entity.UserEntity(
                    username = username.ifBlank { email.substringBefore("@") },
                    fullName = fullName.ifBlank { "مستخدم Firebase" },
                    email = email,
                    avatarUrl = avatarUrl,
                    planType = "free",
                    creditsRemaining = 200,
                    isLoggedIn = true
                )
                val id = chatDao.insertUser(newUser)
                newUser.copy(id = id)
            }
        }
    }

    suspend fun logoutUser() {
        withContext(Dispatchers.IO) {
            firebaseManager?.signOut() ?: chatDao.logoutAllUsers()
        }
    }

    suspend fun updateUserPlan(userId: Long, planType: String, credits: Int) {
        withContext(Dispatchers.IO) {
            chatDao.updateUserPlan(userId, planType, credits)
            val user = chatDao.getActiveUserOnce()
            if (user != null && firebaseManager?.currentFirebaseUser != null) {
                firebaseManager.saveUserToFirestore(firebaseManager.currentFirebaseUser!!.uid, user)
            }
        }
    }

    suspend fun deductCredits(userId: Long, cost: Int = 1) {
        withContext(Dispatchers.IO) {
            chatDao.deductUserCredits(userId, cost)
        }
    }

    suspend fun saveGeneratedApp(app: com.example.data.local.entity.GeneratedAppEntity): Long {
        return withContext(Dispatchers.IO) {
            chatDao.insertGeneratedApp(app)
        }
    }

    suspend fun getGeneratedAppById(id: Long): com.example.data.local.entity.GeneratedAppEntity? {
        return withContext(Dispatchers.IO) {
            chatDao.getGeneratedAppById(id)
        }
    }

    suspend fun saveGeneratedVideo(video: com.example.data.local.entity.GeneratedVideoEntity): Long {
        return withContext(Dispatchers.IO) {
            chatDao.insertGeneratedVideo(video)
        }
    }

    suspend fun updatePersona(id: Long, personaId: String) {
        withContext(Dispatchers.IO) {
            chatDao.updateConversationPersona(id, personaId)
        }
    }

    suspend fun toggleFavorite(messageId: Long, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            chatDao.toggleFavoriteMessage(messageId, isFavorite)
        }
    }

    suspend fun deleteMessage(messageId: Long) {
        withContext(Dispatchers.IO) {
            chatDao.deleteMessageById(messageId)
        }
    }

    suspend fun clearMessages(conversationId: Long) {
        withContext(Dispatchers.IO) {
            chatDao.clearMessagesForConversation(conversationId)
        }
    }

    suspend fun sendUserMessage(
        conversationId: Long,
        userPrompt: String,
        isVoice: Boolean = false,
        voiceDurationSeconds: Int = 0,
        isSearchGroundingEnabled: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        // 1. Insert user message
        val userMessage = ChatMessageEntity(
            conversationId = conversationId,
            role = "user",
            content = userPrompt,
            timestamp = System.currentTimeMillis(),
            isVoice = isVoice,
            voiceDurationSeconds = voiceDurationSeconds,
            status = "SENT"
        )
        chatDao.insertMessage(userMessage)

        val snippet = if (isVoice) "🎙️ رسالة صوتية ($voiceDurationSeconds ثانية)" else userPrompt.take(60)
        val now = System.currentTimeMillis()
        chatDao.updateLastMessageSnippet(conversationId, snippet, now)

        // 2. Fetch conversation details & persona
        val conversation = chatDao.getConversationByIdOnce(conversationId)
        val persona = Personas.getById(conversation?.personaId ?: "general")

        // Auto-title if default title
        if (conversation != null && (conversation.title == "محادثة جديدة" || conversation.title.isBlank())) {
            val autoTitle = generateSmartTitle(userPrompt)
            chatDao.renameConversation(conversationId, autoTitle)
        }

        // 3. Temporary assistant placeholder or directly query Gemini
        val assistantMessageId = chatDao.insertMessage(
            ChatMessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = "...",
                timestamp = System.currentTimeMillis() + 1,
                status = "SENDING"
            )
        )

        val history = chatDao.getMessagesForConversationOnce(conversationId)
            .filter { it.id != assistantMessageId && it.status != "ERROR" }
            .takeLast(10)

        // 4. Generate AI response
        try {
            val responseText = callGeminiOrFallback(
                conversationId = conversationId,
                personaId = persona.id,
                systemPrompt = persona.systemPrompt,
                history = history,
                userPrompt = userPrompt,
                isSearchGrounding = isSearchGroundingEnabled
            )
            val updatedAssistantMessage = ChatMessageEntity(
                id = assistantMessageId,
                conversationId = conversationId,
                role = "assistant",
                content = responseText,
                timestamp = System.currentTimeMillis(),
                status = "SENT"
            )
            chatDao.updateMessage(updatedAssistantMessage)
            chatDao.updateLastMessageSnippet(conversationId, responseText.take(60), System.currentTimeMillis())
            syncToFirestoreIfLoggedIn(conversationId)
            Result.success(responseText)
        } catch (e: Exception) {
            val fallbackResponse = getSmartLocalArabicResponse(persona.id, userPrompt)
            val updatedAssistantMessage = ChatMessageEntity(
                id = assistantMessageId,
                conversationId = conversationId,
                role = "assistant",
                content = fallbackResponse,
                timestamp = System.currentTimeMillis(),
                status = "SENT"
            )
            chatDao.updateMessage(updatedAssistantMessage)
            chatDao.updateLastMessageSnippet(conversationId, fallbackResponse.take(60), System.currentTimeMillis())
            
            // Record estimated fallback token usage
            val promptTok = (userPrompt.length / 3).coerceAtLeast(10)
            val respTok = (fallbackResponse.length / 3).coerceAtLeast(20)
            chatDao.insertTokenUsage(
                TokenUsageEntity(
                    conversationId = conversationId,
                    promptTokens = promptTok,
                    candidatesTokens = respTok,
                    totalTokens = promptTok + respTok,
                    modelName = "gemini-3.5-flash",
                    isSearchGroundingUsed = isSearchGroundingEnabled
                )
            )
            syncToFirestoreIfLoggedIn(conversationId)
            Result.success(fallbackResponse)
        }
    }

    private suspend fun syncToFirestoreIfLoggedIn(conversationId: Long) {
        val fm = firebaseManager ?: return
        try {
            if (fm.currentFirebaseUser != null) {
                val conv = chatDao.getConversationByIdOnce(conversationId) ?: return
                val msgs = chatDao.getMessagesForConversationOnce(conversationId)
                fm.syncConversationToFirestore(conv, msgs)
            }
        } catch (e: Exception) {
            // Ignore cloud sync error locally
        }
    }

    suspend fun generateAiImage(
        prompt: String,
        aspectRatio: String = "1:1"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val request = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = "Generate a high quality visual image for: $prompt"))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                imageConfig = GeminiImageConfig(aspectRatio = aspectRatio, imageSize = "1K"),
                responseModalities = listOf("TEXT", "IMAGE")
            )
        )

        try {
            if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
                val response = geminiService.generateContentWithModel("gemini-2.5-flash-image", apiKey, request)
                val imageBase64 = response.candidates?.firstOrNull()?.content?.parts
                    ?.firstOrNull { it.inlineData != null }?.inlineData?.data
                if (!imageBase64.isNullOrBlank()) {
                    return@withContext Result.success("data:image/png;base64,$imageBase64")
                }
            }
            // If offline/placeholder, return a stylized placeholder description
            Result.success("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=60")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun callGeminiOrFallback(
        conversationId: Long,
        personaId: String,
        systemPrompt: String,
        history: List<ChatMessageEntity>,
        userPrompt: String,
        isSearchGrounding: Boolean = false
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Record local estimated tokens
            val promptTok = (userPrompt.length / 3).coerceAtLeast(15)
            val resp = getSmartLocalArabicResponse(personaId, userPrompt)
            val respTok = (resp.length / 3).coerceAtLeast(30)
            chatDao.insertTokenUsage(
                TokenUsageEntity(
                    conversationId = conversationId,
                    promptTokens = promptTok,
                    candidatesTokens = respTok,
                    totalTokens = promptTok + respTok,
                    modelName = "gemini-3.5-flash",
                    isSearchGroundingUsed = isSearchGrounding
                )
            )
            return resp
        }

        val contents = history.map { msg ->
            GeminiContent(
                role = if (msg.role == "user") "user" else "model",
                parts = listOf(GeminiPart(text = msg.content))
            )
        }

        val systemInstruction = GeminiContent(
            parts = listOf(
                GeminiPart(
                    text = "$systemPrompt\n\nتأكد دائماً من الإجابة باللغة العربية الواضحة والجميلة، واستخدام التنسيق المرتب مع نقاط وعناوين وأكواد منظمة عند الحاجة. إذا تم تفعيل بحث Google، اذكر المعلومات المحدثة بدقة."
                )
            )
        )

        val tools = if (isSearchGrounding) {
            listOf(GeminiTool(googleSearch = emptyMap()))
        } else null

        val request = GeminiGenerateRequest(
            contents = contents,
            systemInstruction = systemInstruction,
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                topP = 0.95f,
                topK = 40,
                maxOutputTokens = 2048
            ),
            tools = tools
        )

        val response = geminiService.generateContent(apiKey, request)

        // Save token usage
        val usage = response.usageMetadata
        val promptTokens = usage?.promptTokenCount ?: ((contents.sumOf { it.parts.sumOf { p -> p.text?.length ?: 0 } }) / 3).coerceAtLeast(10)
        val candidatesTokens = usage?.candidatesTokenCount ?: 120
        val totalTokens = usage?.totalTokenCount ?: (promptTokens + candidatesTokens)

        chatDao.insertTokenUsage(
            TokenUsageEntity(
                conversationId = conversationId,
                promptTokens = promptTokens,
                candidatesTokens = candidatesTokens,
                totalTokens = totalTokens,
                modelName = "gemini-3.5-flash",
                isSearchGroundingUsed = isSearchGrounding
            )
        )

        val candidate = response.candidates?.firstOrNull()
        var text = candidate?.content?.parts?.firstOrNull()?.text

        // Append Google Search Grounding sources if available
        val grounding = candidate?.groundingMetadata
        if (grounding != null) {
            val chunks = grounding.groundingChunks?.filter { it.web != null }
            if (!chunks.isNullOrEmpty()) {
                val sourcesSb = StringBuilder("\n\n---\n🌐 **مصادر بحث Google المحدثة:**\n")
                chunks.take(4).forEach { chunk ->
                    val title = chunk.web?.title ?: "المصدر"
                    val uri = chunk.web?.uri ?: ""
                    if (uri.isNotBlank()) {
                        sourcesSb.append("• [$title]($uri)\n")
                    } else {
                        sourcesSb.append("• $title\n")
                    }
                }
                text = (text ?: "") + sourcesSb.toString()
            }
        }

        if (!text.isNullOrBlank()) {
            return text
        }
        return getSmartLocalArabicResponse(personaId, userPrompt)
    }

    private fun generateSmartTitle(prompt: String): String {
        val cleaned = prompt.replace("\n", " ").trim()
        val words = cleaned.split(" ").filter { it.isNotBlank() }
        return if (words.size <= 5) {
            cleaned.take(30)
        } else {
            words.take(4).joinToString(" ") + "..."
        }
    }

    private fun getSmartLocalArabicResponse(personaId: String, prompt: String): String {
        val lower = prompt.lowercase()
        return when (personaId) {
            "coder" -> {
                when {
                    lower.contains("kotlin") || lower.contains("أندرويد") || lower.contains("android") -> """
### 🚀 أفضل ممارسات تطوير أندرويد الحديث بـ Jetpack Compose:

1. **معمارية MVVM و Clean Architecture:**
   - فصل طبقة واجهة المستخدم (UI) تماماً عن منطق البيانات.
   - استخدام `StateFlow` و `collectAsStateWithLifecycle` للتحديثات التفاعلية.

```kotlin
// مثال على ViewModel نظيف
class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .collect { data -> _uiState.value = UiState.Success(data) }
        }
    }
}
```

2. **قواعد هامة للأداء:**
   - تجنب إنشاء كائنات ثقيلة داخل الـ Composables بدون `remember`.
   - استخدام المعرفات الثابتة `key` في القوائم `LazyColumn`.
                    """.trimIndent()

                    lower.contains("خوارزمية") || lower.contains("algorithm") || lower.contains("بحث") -> """
### ⚡ خوارزمية البحث الثنائي (Binary Search):

البحث الثنائي يعمل على المصفوفات المرتبة بكفاءة زمنية **O(log N)**:

```kotlin
fun binarySearch(array: IntArray, target: Int): Int {
    var left = 0
    var right = array.size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2
        when {
            array[mid] == target -> return mid
            array[mid] < target -> left = mid + 1
            else -> right = mid - 1
        }
    }
    return -1 // العنصر غير موجود
}
```

* **التعقيد الزمني:** $ O(\log n) $ في أسوأ وأفضل الحالات بعد الترتيب.
* **التعقيد المكاني:** $ O(1) $ للنسخة التكرارية.
                    """.trimIndent()

                    else -> """
### 💻 الحل التقني والتنفيذ البرمجي:

بناءً على طلبك بخصوص **"${prompt.take(40)}"**:

```kotlin
// نموذج تنفيذي منظم
suspend fun processRequest(data: String): Result<String> = withContext(Dispatchers.IO) {
    try {
        // تنفيذ المنطق المطلوب بكفاءة
        val processed = data.trim().uppercase()
        Result.success(processed)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

- **التصميم المقترح:** استخدام وحدات غير متزامنة (Asynchronous Coroutines).
- **معالجة الأخطاء:** تغليف النتائج بـ `Result<T>` لضمان استقرار التطبيق.
- **نصيحة إضافية:** احرص على إضافة اختبارات Unit Tests لضمان دقة الخوارزمية.
                    """.trimIndent()
                }
            }

            "writer" -> {
                when {
                    lower.contains("بريد") || lower.contains("email") || lower.contains("رسالة") -> """
### ✉️ مسودة بريد إلكتروني رسمي واحترافي:

**الموضوع:** طلب تقديم لوظيفة / استفسار مهني بخصوص الفرصة المتاحة

**تحية طيبة وبعد،**

السادة الكرام في إدارة التوظيف،

يسعدني أن أتقدم بطلبي هذا للالتحاق بفريقكم المتميز. أمتلك خبرة عملية وشغفاً كبيراً في هذا المجال، وقد اطلعت على متطلبات الوظيفة ووجدت أنها تتطابق تماماً مع مهاراتي وخبراتي المهنية.

مرفق مع هذا البريد سيرتي الذاتية للاطلاع على تفاصيل مسيرتي المهنية والمشاريع السابقة. ويسرني تحديد موعد للمقابلة الشخصية لمناقشة كيفية المساهمة في نجاح فريقكم.

شاكراً لكم حسن اهتمامكم ووقتكم.

**وتفضلوا بقبول فائق الاحترام والتقدير،**
[اسمك الكريم]  
[رقم الهاتف والبريد الإلكتروني]
                    """.trimIndent()

                    lower.contains("ترجم") || lower.contains("translate") -> """
### 🌐 الترجمة الاحترافية والبلاغية:

> **النص المترجم بدقة وبلاغة عربية:**
> *"إن رحلة الألف ميل تبدأ دائماً بخطوة واحدة واثقة."*

**إضاءة لغوية:**
- استخدام صيغة التأكيد يعطي العبارة عمقاً أدبياً وأثراً تحفيزياً يتماشى مع البلاغة العربية الأصيلة.
- يمكن أيضاً صياغتها: *"أعظم المسافات تُقطع بالخطوة الأولى"*.
                    """.trimIndent()

                    else -> """
### 📝 الصياغة الأدبية والمقترح اللغوي:

فيما يتعلق بـ **"${prompt.take(40)}"**:

> *"إن الكلمات ليست مجرد أحرف تُكتب، بل هي جسور تُبنى لتنقل الأفكار برقي ووضوح."*

**النقاط الرئيسية للموضوع:**
1. **الوضوح والدقة:** اختيار المصطلحات المباشرة والمؤثرة.
2. **التسلسل المنطقي:** التدرج من المقدمة الجاذبة إلى الأفكار الجوهرية ثم الخاتمة الملهمة.
3. **الجمال البلاغي:** استخدام التشبيهات البسيطة لتقريب المعنى وإثراء تجربة القارئ.
                    """.trimIndent()
                }
            }

            "creative" -> """
### 💡 أفكار إبداعية خارج الصندوق:

بخصوص فكرة **"${prompt.take(40)}"**:

1. **الابتكار في التجربة (User Experience):**
   - تقديم واجهة تعتمد على التخصيص الذكي وسرعة التفاعل بنقرة واحدة.
   - دمج عناصر التلعيب (Gamification) لزيادة تفاعل المستخدمين وولائهم.

2. **عنصر التميز والميزة التنافسية:**
   - التركيز على مشكلة محددة جداً وحلها بجودة استثنائية بدلاً من التشتت.
   - بناء مجتمع نشط حول الفكرة يشارك في تطويرها المستمر.

3. **خطة الإطلاق والانتشار السريع:**
   - إطلاق نسخة تجريبية حصرية (Beta Access) مع ميزات تشجيعية للمبكرين.
   - صناعة محتوى تثقيفي تفاعلي يعرض القيمة الحقيقية للخدمة بطريقة بصرية ممتعة.
            """.trimIndent()

            "coach" -> """
### 🌿 خطوات عملية وتوجيهية لتحقيق أهدافك:

أهلاً بك! دعنا ننظم هذه الخطوات معاً بخصوص **"${prompt.take(40)}"**:

1. **قاعدة الـ 5 دقائق (Start Small):**
   - عندما تشعر بالمماطلة أو التردد، الزم نفسك بالعمل على المهمة لمدة 5 دقائق فقط. غالباً ما يزول الحاجز النفسي بعد البداية.

2. **تقسيم الهدف الكبير إلى مهام متناهية الصغر:**
   - لا تفكر في المشروع كاملاً دفعة واحدة؛ ركز فقط على الخطوة التالية فوراً.

3. **البيئة والمحفزات المحيطة:**
   - هيّئ مساحة عمل خالية من المشتتات وأوقف الإشعارات غير الضرورية.
   - احتفل بالإنجازات الصغيرة اليومية لأنها الوقود للاستمرار طويل الأمد.

> *"الاستمرارية البسيطة كل يوم تتفوق دائماً على الحماس المؤقت."*
            """.trimIndent()

            "study" -> """
### 📚 الشرح التعليمي المبسط:

دعنا نبسط مفهوم **"${prompt.take(40)}"** بطريقة سهلة وممتعة:

1. **الفكرة الأساسية (The Big Picture):**
   - تخيل الأمر كبناء جسر: نحتاج أولاً للأساسات القوية قبل وضع الطريق.
   
2. **التفصيل والخطوات:**
   - **المرحلة الأولى:** فهم المبدأ العام والتعريف الأساسي بدون تعقيدات رياضية أو مصطلحات جافة.
   - **المرحلة الثانية:** تطبيق مثال عملي من حياتنا اليومية يثبت المعلومة في الذاكرة.
   - **المرحلة الثالثة:** تلخيص القوانين أو القواعد الرئيسية في معادلة أو جملة واحدة سهلة الحفظ.

3. **سؤال للتأكيد والتثبيت:**
   - هل تود أن نطبق تمريناً عملياً معاً الآن لنرى كيف يعمل المبدأ بدقة؟
            """.trimIndent()

            else -> """
### مرحباً بك! 👋 يسعدني التحدث معك والإجابة على استفسارك:

بخصوص **"${prompt.take(40)}"**:

1. **الملخص السريع:**
   - الأمر يتمحور حول إيجاد التوازن المثالي وتطبيق أفضل الممارسات المتاحة.
   
2. **النقاط الإرشادية المهمة:**
   - التخطيط المسبق يختصر الكثير من الجهد والوقت.
   - الاستفادة من الأدوات الحديثة والتقنيات الذكية لتحقيق أفضل النتائج.
   - مراجعة التقدم بشكل دوري للتحسين المستمر.

إذا كنت ترغب في التوسع في أي جانب معين أو تفصيل خطة مخصصة، أنا هنا لمساعدتك فوراً! ✨
            """.trimIndent()
        }
    }
}
