package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthResult
import com.example.data.firebase.FirebaseManager
import com.example.data.local.ChatDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.TokenUsageEntity
import com.example.data.repository.ChatRepository
import com.example.ui.audio.VoiceDictationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatRepository
    val firebaseManager: FirebaseManager
    val voiceDictationManager: VoiceDictationManager

    init {
        val db = ChatDatabase.getDatabase(application)
        firebaseManager = FirebaseManager(application, db.chatDao())
        repository = ChatRepository(db.chatDao(), firebaseManager)
        voiceDictationManager = VoiceDictationManager(application)
    }

    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteMessages: StateFlow<List<ChatMessageEntity>> = repository.favoriteMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTokenUsages: StateFlow<List<TokenUsageEntity>> = repository.allTokenUsages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalTokens: StateFlow<Long?> = repository.totalTokensFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val activeUser: StateFlow<com.example.data.local.entity.UserEntity?> = repository.activeUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    private val _isSubscriptionDialogOpen = MutableStateFlow(false)
    val isSubscriptionDialogOpen: StateFlow<Boolean> = _isSubscriptionDialogOpen.asStateFlow()

    private val _isUserProfileDialogOpen = MutableStateFlow(false)
    val isUserProfileDialogOpen: StateFlow<Boolean> = _isUserProfileDialogOpen.asStateFlow()

    private val _isAppPreviewDialogOpen = MutableStateFlow(false)
    val isAppPreviewDialogOpen: StateFlow<Boolean> = _isAppPreviewDialogOpen.asStateFlow()

    private val _currentAppSpec = MutableStateFlow<com.example.model.GeneratedAppSpec?>(null)
    val currentAppSpec: StateFlow<com.example.model.GeneratedAppSpec?> = _currentAppSpec.asStateFlow()

    private val _isVideoCreatorDialogOpen = MutableStateFlow(false)
    val isVideoCreatorDialogOpen: StateFlow<Boolean> = _isVideoCreatorDialogOpen.asStateFlow()

    private val _videoCreatorInitialPrompt = MutableStateFlow("")
    val videoCreatorInitialPrompt: StateFlow<String> = _videoCreatorInitialPrompt.asStateFlow()

    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()

    val currentConversation: StateFlow<ConversationEntity?> = _currentConversationId
        .flatMapLatest { id ->
            if (id != null) repository.getConversation(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentConversationId
        .flatMapLatest { id ->
            if (id != null) repository.getMessages(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Google Search Grounding toggle
    private val _isGoogleSearchEnabled = MutableStateFlow(false)
    val isGoogleSearchEnabled: StateFlow<Boolean> = _isGoogleSearchEnabled.asStateFlow()

    // Voice dictation & SpeechRecognizer state
    private val _isVoiceRecording = MutableStateFlow(false)
    val isVoiceRecording: StateFlow<Boolean> = _isVoiceRecording.asStateFlow()

    val voiceSoundLevel: StateFlow<Float> = voiceDictationManager.soundLevel
    val liveSpokenText: StateFlow<String> = voiceDictationManager.liveSpokenText

    private val _voiceRecordingDuration = MutableStateFlow(0)
    val voiceRecordingDuration: StateFlow<Int> = _voiceRecordingDuration.asStateFlow()

    private var voiceTimerJob: Job? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<ChatMessageEntity>> = _searchQuery
        .flatMapLatest { q ->
            if (q.length >= 2) repository.searchMessages(q) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & sheet UI states
    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _isPersonaModalOpen = MutableStateFlow(false)
    val isPersonaModalOpen: StateFlow<Boolean> = _isPersonaModalOpen.asStateFlow()

    private val _isStarredDialogOpen = MutableStateFlow(false)
    val isStarredDialogOpen: StateFlow<Boolean> = _isStarredDialogOpen.asStateFlow()

    private val _isSearchDialogOpen = MutableStateFlow(false)
    val isSearchDialogOpen: StateFlow<Boolean> = _isSearchDialogOpen.asStateFlow()

    private val _isSettingsDialogOpen = MutableStateFlow(false)
    val isSettingsDialogOpen: StateFlow<Boolean> = _isSettingsDialogOpen.asStateFlow()

    private val _isRenameDialogOpen = MutableStateFlow(false)
    val isRenameDialogOpen: StateFlow<Boolean> = _isRenameDialogOpen.asStateFlow()

    private val _isPromptLibraryOpen = MutableStateFlow(false)
    val isPromptLibraryOpen: StateFlow<Boolean> = _isPromptLibraryOpen.asStateFlow()

    private val _isStatsDialogOpen = MutableStateFlow(false)
    val isStatsDialogOpen: StateFlow<Boolean> = _isStatsDialogOpen.asStateFlow()

    private val _isExportDialogOpen = MutableStateFlow(false)
    val isExportDialogOpen: StateFlow<Boolean> = _isExportDialogOpen.asStateFlow()

    private val _isMediaStudioOpen = MutableStateFlow(false)
    val isMediaStudioOpen: StateFlow<Boolean> = _isMediaStudioOpen.asStateFlow()

    private val _conversationToRename = MutableStateFlow<ConversationEntity?>(null)
    val conversationToRename: StateFlow<ConversationEntity?> = _conversationToRename.asStateFlow()

    private val _fontSizeMultiplier = MutableStateFlow(1.0f)
    val fontSizeMultiplier: StateFlow<Float> = _fontSizeMultiplier.asStateFlow()

    private val _isFastReadingMode = MutableStateFlow(false)
    val isFastReadingMode: StateFlow<Boolean> = _isFastReadingMode.asStateFlow()

    private val _activeTone = MutableStateFlow("friendly")
    val activeTone: StateFlow<String> = _activeTone.asStateFlow()

    init {
        // Automatically open or create initial conversation if needed
        viewModelScope.launch {
            repository.conversations.collect { list ->
                if (_currentConversationId.value == null && list.isNotEmpty()) {
                    _currentConversationId.value = list.first().id
                }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleGoogleSearch() {
        _isGoogleSearchEnabled.value = !_isGoogleSearchEnabled.value
    }

    fun setGoogleSearchEnabled(enabled: Boolean) {
        _isGoogleSearchEnabled.value = enabled
    }

    fun selectConversation(id: Long) {
        _currentConversationId.value = id
        _isDrawerOpen.value = false
    }

    fun createNewChat(personaId: String = "general", title: String = "محادثة جديدة") {
        viewModelScope.launch {
            val newId = repository.createConversation(title = title, personaId = personaId)
            _currentConversationId.value = newId
            _isDrawerOpen.value = false
            _isPersonaModalOpen.value = false
        }
    }

    fun setDrawerOpen(isOpen: Boolean) {
        _isDrawerOpen.value = isOpen
    }

    fun setPersonaModalOpen(isOpen: Boolean) {
        _isPersonaModalOpen.value = isOpen
    }

    fun setStarredDialogOpen(isOpen: Boolean) {
        _isStarredDialogOpen.value = isOpen
    }

    fun setSearchDialogOpen(isOpen: Boolean) {
        _isSearchDialogOpen.value = isOpen
        if (!isOpen) _searchQuery.value = ""
    }

    fun setSettingsDialogOpen(isOpen: Boolean) {
        _isSettingsDialogOpen.value = isOpen
    }

    fun setPromptLibraryOpen(isOpen: Boolean) {
        _isPromptLibraryOpen.value = isOpen
    }

    fun setStatsDialogOpen(isOpen: Boolean) {
        _isStatsDialogOpen.value = isOpen
    }

    fun setExportDialogOpen(isOpen: Boolean) {
        _isExportDialogOpen.value = isOpen
    }

    fun setMediaStudioOpen(isOpen: Boolean) {
        _isMediaStudioOpen.value = isOpen
    }

    fun setAuthDialogOpen(isOpen: Boolean) {
        _isAuthDialogOpen.value = isOpen
    }

    fun setSubscriptionDialogOpen(isOpen: Boolean) {
        _isSubscriptionDialogOpen.value = isOpen
    }

    fun setUserProfileDialogOpen(isOpen: Boolean) {
        _isUserProfileDialogOpen.value = isOpen
    }

    fun setAppPreviewDialogOpen(isOpen: Boolean, spec: com.example.model.GeneratedAppSpec? = null) {
        if (spec != null) {
            _currentAppSpec.value = spec
        }
        _isAppPreviewDialogOpen.value = isOpen
    }

    fun openAppPreviewFromPrompt(prompt: String) {
        val spec = com.example.model.AppTemplates.parseOrGenerateAppSpec(prompt)
        _currentAppSpec.value = spec
        _isAppPreviewDialogOpen.value = true
    }

    fun setVideoCreatorDialogOpen(isOpen: Boolean, initialPrompt: String = "") {
        _videoCreatorInitialPrompt.value = initialPrompt
        _isVideoCreatorDialogOpen.value = isOpen
    }

    fun signUpWithFirebase(
        email: String,
        password: String,
        fullName: String,
        username: String,
        avatarUrl: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.signUpWithFirebase(email, password, fullName, username, avatarUrl)
            onResult(result)
        }
    }

    fun signInWithFirebase(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.signInWithFirebase(email, password)
            onResult(result)
        }
    }

    fun signInWithGoogle(
        context: Context,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(context)
            onResult(result)
        }
    }

    fun loginOrRegister(username: String, fullName: String, email: String, avatarUrl: String) {
        viewModelScope.launch {
            repository.loginOrRegister(username, fullName, email, avatarUrl)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    fun upgradePlan(planId: String, credits: Int) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.updateUserPlan(user.id, planId, credits)
        }
    }

    suspend fun getExportDataForCurrentConversation(): Pair<ConversationEntity, List<ChatMessageEntity>>? {
        val convId = _currentConversationId.value ?: return null
        return repository.getConversationWithMessages(convId)
    }

    suspend fun getExportDataForAllConversations(): List<Pair<ConversationEntity, List<ChatMessageEntity>>> {
        return repository.getAllConversationsWithMessages()
    }

    suspend fun getAllMessagesOnce(): List<ChatMessageEntity> {
        return repository.getAllMessagesOnce()
    }

    suspend fun getAllTokenUsagesOnce(): List<TokenUsageEntity> {
        return repository.getAllTokenUsagesOnce()
    }

    suspend fun generateAiImageDirect(prompt: String, aspectRatio: String): Result<String> {
        return repository.generateAiImage(prompt, aspectRatio)
    }

    fun openRenameDialog(conversation: ConversationEntity) {
        _conversationToRename.value = conversation
        _isRenameDialogOpen.value = true
    }

    fun closeRenameDialog() {
        _conversationToRename.value = null
        _isRenameDialogOpen.value = false
    }

    fun confirmRename(newTitle: String) {
        val convo = _conversationToRename.value ?: return
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                repository.renameConversation(convo.id, newTitle.trim())
                closeRenameDialog()
            }
        }
    }

    fun togglePin(conversation: ConversationEntity) {
        viewModelScope.launch {
            repository.setPinned(conversation.id, !conversation.isPinned)
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_currentConversationId.value == id) {
                _currentConversationId.value = null
            }
        }
    }

    fun changePersona(personaId: String) {
        val currentId = _currentConversationId.value ?: return
        viewModelScope.launch {
            repository.updatePersona(currentId, personaId)
            _isPersonaModalOpen.value = false
        }
    }

    fun clearCurrentChat() {
        val currentId = _currentConversationId.value ?: return
        viewModelScope.launch {
            repository.clearMessages(currentId)
        }
    }

    fun toggleFavorite(messageId: Long, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(messageId, !currentIsFavorite)
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun setFontSizeMultiplier(multiplier: Float) {
        _fontSizeMultiplier.value = multiplier
    }

    fun setFastReadingMode(enabled: Boolean) {
        _isFastReadingMode.value = enabled
    }

    fun toggleFastReadingMode() {
        _isFastReadingMode.value = !_isFastReadingMode.value
    }

    fun setActiveTone(tone: String) {
        _activeTone.value = tone
    }

    fun sendMessage(customPrompt: String? = null, withSearchGrounding: Boolean? = null) {
        val prompt = (customPrompt ?: _inputText.value).trim()
        if (prompt.isBlank() || _isGenerating.value) return

        val useSearch = withSearchGrounding ?: _isGoogleSearchEnabled.value

        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = repository.createConversation()
                _currentConversationId.value = convId
            }

            activeUser.value?.let { user ->
                repository.deductCredits(user.id, cost = 1)
            }

            repository.sendUserMessage(
                conversationId = convId,
                userPrompt = prompt,
                isVoice = false,
                voiceDurationSeconds = 0,
                isSearchGroundingEnabled = useSearch
            )
            _isGenerating.value = false
        }
    }

    fun regenerateLastMessage() {
        val msgs = currentMessages.value
        val lastUserMessage = msgs.findLast { it.role == "user" }
        if (lastUserMessage != null && !_isGenerating.value) {
            val convId = _currentConversationId.value ?: return
            _isGenerating.value = true
            viewModelScope.launch {
                repository.sendUserMessage(
                    conversationId = convId,
                    userPrompt = lastUserMessage.content,
                    isSearchGroundingEnabled = _isGoogleSearchEnabled.value
                )
                _isGenerating.value = false
            }
        }
    }

    fun summarizeContent(content: String) {
        if (content.isBlank() || _isGenerating.value) return
        val summarizePrompt = "يرجى تلخيص النقاط الجوهرية للرسالة التالية واستخراج أهم التوصيات:\n\n$content"
        sendMessage(summarizePrompt)
    }

    // Android SpeechRecognizer Integration
    fun startVoiceRecording() {
        _isVoiceRecording.value = true
        _voiceRecordingDuration.value = 0

        voiceDictationManager.startListening(
            onResultReceived = { spokenResult ->
                if (spokenResult.isNotBlank()) {
                    _inputText.value = spokenResult
                }
            },
            onErrorOccurred = { _ ->
                // Keep local recording timer running or handle error gracefully
            }
        )

        voiceTimerJob?.cancel()
        voiceTimerJob = viewModelScope.launch {
            while (_isVoiceRecording.value) {
                delay(1000)
                _voiceRecordingDuration.value += 1
            }
        }
    }

    fun cancelVoiceRecording() {
        _isVoiceRecording.value = false
        voiceDictationManager.stopListening()
        voiceTimerJob?.cancel()
        _voiceRecordingDuration.value = 0
    }

    fun finishVoiceRecording() {
        val duration = _voiceRecordingDuration.value
        val spokenText = voiceDictationManager.liveSpokenText.value
        voiceDictationManager.stopListening()

        _isVoiceRecording.value = false
        voiceTimerJob?.cancel()
        _voiceRecordingDuration.value = 0

        val finalPrompt = if (spokenText.isNotBlank()) {
            spokenText
        } else if (_inputText.value.isNotBlank()) {
            _inputText.value
        } else {
            val simulatedVoiceTranscripts = listOf(
                "هل يمكنك تلخيص أهم استراتيجيات التعلم السريع بالذكاء الاصطناعي؟",
                "أريد أفكاراً إبداعية لتطوير مشروعي باللغة العربية.",
                "ما هي أفضل النصائح للحفاظ على الإنتاجية والتركيز؟",
                "كيف أكتب خطة عمل ناجحة واحترافية؟"
            )
            simulatedVoiceTranscripts.random()
        }

        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = repository.createConversation()
                _currentConversationId.value = convId
            }

            repository.sendUserMessage(
                conversationId = convId,
                userPrompt = finalPrompt,
                isVoice = true,
                voiceDurationSeconds = duration.coerceAtLeast(1),
                isSearchGroundingEnabled = _isGoogleSearchEnabled.value
            )
            _isGenerating.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceDictationManager.stopListening()
    }
}
