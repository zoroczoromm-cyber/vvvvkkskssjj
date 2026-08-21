package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Personas
import com.example.ui.components.AiMediaStudioDialog
import com.example.ui.components.AiVideoCreatorDialog
import com.example.ui.components.AppPreviewSandboxDialog
import com.example.ui.components.AuthDialog
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatMessageBubble
import com.example.ui.components.ChatSettingsDialog
import com.example.ui.components.ChatTopBar
import com.example.ui.components.ConversationListDrawer
import com.example.ui.components.ExportChatDialog
import com.example.ui.components.PersonaSelectionModal
import com.example.ui.components.PromptLibraryDialog
import com.example.ui.components.RenameConversationDialog
import com.example.ui.components.SearchMessagesDialog
import com.example.ui.components.StarredMessagesDialog
import com.example.ui.components.SubscriptionDialog
import com.example.ui.components.SuggestedStartersView
import com.example.ui.components.UsageStatisticsDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val currentConversation by viewModel.currentConversation.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val favoriteMessages by viewModel.favoriteMessages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isVoiceRecording by viewModel.isVoiceRecording.collectAsStateWithLifecycle()
    val voiceDurationSeconds by viewModel.voiceRecordingDuration.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val isPersonaModalOpen by viewModel.isPersonaModalOpen.collectAsStateWithLifecycle()
    val isStarredDialogOpen by viewModel.isStarredDialogOpen.collectAsStateWithLifecycle()
    val isSearchDialogOpen by viewModel.isSearchDialogOpen.collectAsStateWithLifecycle()
    val isSettingsDialogOpen by viewModel.isSettingsDialogOpen.collectAsStateWithLifecycle()
    val isRenameDialogOpen by viewModel.isRenameDialogOpen.collectAsStateWithLifecycle()
    val isPromptLibraryOpen by viewModel.isPromptLibraryOpen.collectAsStateWithLifecycle()
    val isStatsDialogOpen by viewModel.isStatsDialogOpen.collectAsStateWithLifecycle()
    val isExportDialogOpen by viewModel.isExportDialogOpen.collectAsStateWithLifecycle()
    val isMediaStudioOpen by viewModel.isMediaStudioOpen.collectAsStateWithLifecycle()
    val isGoogleSearchEnabled by viewModel.isGoogleSearchEnabled.collectAsStateWithLifecycle()
    val voiceSoundLevel by viewModel.voiceSoundLevel.collectAsStateWithLifecycle()
    val liveSpokenText by viewModel.liveSpokenText.collectAsStateWithLifecycle()
    val allTokenUsages by viewModel.allTokenUsages.collectAsStateWithLifecycle()
    val conversationToRename by viewModel.conversationToRename.collectAsStateWithLifecycle()

    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsStateWithLifecycle()
    val isSubscriptionDialogOpen by viewModel.isSubscriptionDialogOpen.collectAsStateWithLifecycle()
    val isUserProfileDialogOpen by viewModel.isUserProfileDialogOpen.collectAsStateWithLifecycle()
    val isAppPreviewDialogOpen by viewModel.isAppPreviewDialogOpen.collectAsStateWithLifecycle()
    val currentAppSpec by viewModel.currentAppSpec.collectAsStateWithLifecycle()
    val isVideoCreatorDialogOpen by viewModel.isVideoCreatorDialogOpen.collectAsStateWithLifecycle()
    val videoCreatorInitialPrompt by viewModel.videoCreatorInitialPrompt.collectAsStateWithLifecycle()

    val fontSizeMultiplier by viewModel.fontSizeMultiplier.collectAsStateWithLifecycle()
    val isFastReadingMode by viewModel.isFastReadingMode.collectAsStateWithLifecycle()
    val activeTone by viewModel.activeTone.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSuggestionsOverlay by remember { mutableStateOf(false) }

    val currentPersona = remember(currentConversation?.personaId) {
        Personas.getById(currentConversation?.personaId ?: "general")
    }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceRecording()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("يرجى منح إذن الميكروفون لاستخدام الإملاء الصوتي")
            }
        }
    }

    val onVoiceRecordingClick = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startVoiceRecording()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Enforce RTL for seamless Arabic typography and layout experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    ConversationListDrawer(
                        conversations = conversations,
                        currentConversationId = currentConversation?.id,
                        currentUser = activeUser,
                        onSelectConversation = { id ->
                            viewModel.selectConversation(id)
                            scope.launch { drawerState.close() }
                        },
                        onNewChat = {
                            viewModel.createNewChat()
                            scope.launch { drawerState.close() }
                        },
                        onOpenAppBuilder = {
                            viewModel.setAppPreviewDialogOpen(true)
                            scope.launch { drawerState.close() }
                        },
                        onOpenVideoStudio = {
                            viewModel.setVideoCreatorDialogOpen(true)
                            scope.launch { drawerState.close() }
                        },
                        onOpenSubscriptions = {
                            viewModel.setSubscriptionDialogOpen(true)
                            scope.launch { drawerState.close() }
                        },
                        onOpenUserProfile = {
                            viewModel.setUserProfileDialogOpen(true)
                            scope.launch { drawerState.close() }
                        },
                        onOpenAuth = {
                            viewModel.setAuthDialogOpen(true)
                            scope.launch { drawerState.close() }
                        },
                        onTogglePin = { viewModel.togglePin(it) },
                        onOpenRenameDialog = { viewModel.openRenameDialog(it) },
                        onDeleteConversation = { viewModel.deleteConversation(it) },
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    ChatTopBar(
                        conversation = currentConversation,
                        currentUser = activeUser,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onOpenPersonaModal = { viewModel.setPersonaModalOpen(true) },
                        onOpenSearchDialog = { viewModel.setSearchDialogOpen(true) },
                        onOpenStarredDialog = { viewModel.setStarredDialogOpen(true) },
                        onOpenSettingsDialog = { viewModel.setSettingsDialogOpen(true) },
                        onOpenStatsDialog = { viewModel.setStatsDialogOpen(true) },
                        onOpenExportDialog = { viewModel.setExportDialogOpen(true) },
                        onOpenMediaStudio = { viewModel.setMediaStudioOpen(true) },
                        onOpenAppBuilder = { viewModel.setAppPreviewDialogOpen(true) },
                        onOpenVideoStudio = { viewModel.setVideoCreatorDialogOpen(true) },
                        onOpenSubscriptions = { viewModel.setSubscriptionDialogOpen(true) },
                        onOpenUserProfile = { viewModel.setUserProfileDialogOpen(true) },
                        onOpenAuth = { viewModel.setAuthDialogOpen(true) },
                        onRenameConversation = {
                            currentConversation?.let { viewModel.openRenameDialog(it) }
                        },
                        onTogglePin = {
                            currentConversation?.let { viewModel.togglePin(it) }
                        },
                        onClearChat = { viewModel.clearCurrentChat() },
                        onNewChat = { viewModel.createNewChat() }
                    )
                },
                bottomBar = {
                    Column(modifier = Modifier.navigationBarsPadding().imePadding()) {
                        AnimatedVisibility(
                            visible = showSuggestionsOverlay && messages.isNotEmpty(),
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { it } + fadeOut()
                        ) {
                            SuggestedStartersView(
                                persona = currentPersona,
                                onSelectStarter = { prompt ->
                                    showSuggestionsOverlay = false
                                    viewModel.sendMessage(prompt)
                                }
                            )
                        }

                        ChatInputBar(
                            inputText = inputText,
                            onInputTextChanged = { viewModel.onInputTextChanged(it) },
                            onSendMessage = { viewModel.sendMessage() },
                            isGenerating = isGenerating,
                            isVoiceRecording = isVoiceRecording,
                            voiceDurationSeconds = voiceDurationSeconds,
                            voiceSoundLevel = voiceSoundLevel,
                            liveSpokenText = liveSpokenText,
                            isGoogleSearchEnabled = isGoogleSearchEnabled,
                            onToggleGoogleSearch = { viewModel.toggleGoogleSearch() },
                            onStartVoiceRecording = onVoiceRecordingClick,
                            onCancelVoiceRecording = { viewModel.cancelVoiceRecording() },
                            onFinishVoiceRecording = { viewModel.finishVoiceRecording() },
                            onToggleSuggestions = { showSuggestionsOverlay = !showSuggestionsOverlay },
                            onOpenPromptLibrary = { viewModel.setPromptLibraryOpen(true) },
                            onOpenMediaStudio = { viewModel.setMediaStudioOpen(true) }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (messages.isEmpty()) {
                        // Empty Chat with Persona Welcome & Starter Prompts
                        SuggestedStartersView(
                            persona = currentPersona,
                            onSelectStarter = { prompt ->
                                viewModel.sendMessage(prompt)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = if (isFastReadingMode) 44.dp else 10.dp, bottom = 12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val lastAssistantIndex = messages.indexOfLast { it.role == "assistant" }

                            itemsIndexed(messages, key = { _, msg -> msg.id }) { index, msg ->
                                ChatMessageBubble(
                                    message = msg,
                                    fontSizeMultiplier = fontSizeMultiplier,
                                    isFastReadingMode = isFastReadingMode,
                                    isLastAssistantMessage = (index == lastAssistantIndex),
                                    onToggleFavorite = {
                                        viewModel.toggleFavorite(msg.id, msg.isFavorite)
                                    },
                                    onDeleteMessage = {
                                        viewModel.deleteMessage(msg.id)
                                    },
                                    onRegenerate = {
                                        viewModel.regenerateLastMessage()
                                    },
                                    onSummarize = {
                                        viewModel.summarizeContent(msg.content)
                                    },
                                    onOpenAppPreview = { appPrompt ->
                                        viewModel.openAppPreviewFromPrompt(appPrompt)
                                    },
                                    onOpenVideoStudio = { videoPrompt ->
                                        viewModel.setVideoCreatorDialogOpen(true, videoPrompt)
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        // Floating Fast Reading Mode Indicator Pill
                        AnimatedVisibility(
                            visible = isFastReadingMode,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "وضع القراءة السريع مفعّل",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.setFastReadingMode(false) }
                                    ) {
                                        Text(
                                            text = "إيقاف",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs & Modals
        if (isAuthDialogOpen) {
            val activityContext = LocalContext.current
            AuthDialog(
                currentUser = activeUser,
                onSignUpWithFirebase = { email, pass, fullName, username, avatarUrl, onResult ->
                    viewModel.signUpWithFirebase(email, pass, fullName, username, avatarUrl, onResult)
                },
                onSignInWithFirebase = { email, pass, onResult ->
                    viewModel.signInWithFirebase(email, pass, onResult)
                },
                onSignInWithGoogle = { onResult ->
                    viewModel.signInWithGoogle(activityContext, onResult)
                },
                onDismiss = { viewModel.setAuthDialogOpen(false) }
            )
        }

        if (isUserProfileDialogOpen) {
            UserProfileDialog(
                currentUser = activeUser,
                onOpenSubscriptions = {
                    viewModel.setUserProfileDialogOpen(false)
                    viewModel.setSubscriptionDialogOpen(true)
                },
                onLogout = {
                    viewModel.logout()
                    viewModel.setUserProfileDialogOpen(false)
                },
                onDismiss = { viewModel.setUserProfileDialogOpen(false) }
            )
        }

        if (isSubscriptionDialogOpen) {
            SubscriptionDialog(
                currentUser = activeUser,
                onUpgradePlan = { planId, credits ->
                    viewModel.upgradePlan(planId, credits)
                    viewModel.setSubscriptionDialogOpen(false)
                },
                onDismiss = { viewModel.setSubscriptionDialogOpen(false) }
            )
        }

        if (isAppPreviewDialogOpen) {
            AppPreviewSandboxDialog(
                initialSpec = currentAppSpec,
                onDismiss = { viewModel.setAppPreviewDialogOpen(false) }
            )
        }

        if (isVideoCreatorDialogOpen) {
            AiVideoCreatorDialog(
                initialPrompt = videoCreatorInitialPrompt,
                onSendPromptToChat = { prompt ->
                    viewModel.setVideoCreatorDialogOpen(false)
                    viewModel.sendMessage(prompt)
                },
                onDismiss = { viewModel.setVideoCreatorDialogOpen(false) }
            )
        }

        if (isPersonaModalOpen) {
            PersonaSelectionModal(
                selectedPersonaId = currentConversation?.personaId ?: "general",
                onSelectPersona = { newPersonaId ->
                    viewModel.changePersona(newPersonaId)
                },
                onDismiss = { viewModel.setPersonaModalOpen(false) }
            )
        }

        if (isPromptLibraryOpen) {
            PromptLibraryDialog(
                onSelectPrompt = { promptText ->
                    viewModel.sendMessage(promptText)
                },
                onDismiss = { viewModel.setPromptLibraryOpen(false) }
            )
        }

        if (isStatsDialogOpen) {
            UsageStatisticsDialog(
                currentConversation = currentConversation,
                allMessages = messages,
                tokenUsages = allTokenUsages,
                allConversations = conversations,
                onDismiss = { viewModel.setStatsDialogOpen(false) }
            )
        }

        if (isMediaStudioOpen) {
            AiMediaStudioDialog(
                onSendPromptToChat = { prompt, useSearchGrounding ->
                    viewModel.sendMessage(customPrompt = prompt, withSearchGrounding = useSearchGrounding)
                },
                onGenerateImageDirect = { prompt, aspectRatio ->
                    viewModel.generateAiImageDirect(prompt, aspectRatio)
                },
                onDismiss = { viewModel.setMediaStudioOpen(false) }
            )
        }

        if (isExportDialogOpen) {
            ExportChatDialog(
                currentConversation = currentConversation,
                onFetchCurrentConversationData = { viewModel.getExportDataForCurrentConversation() },
                onFetchAllConversationsData = { viewModel.getExportDataForAllConversations() },
                onDismiss = { viewModel.setExportDialogOpen(false) }
            )
        }

        if (isStarredDialogOpen) {
            StarredMessagesDialog(
                starredMessages = favoriteMessages,
                onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                onDismiss = { viewModel.setStarredDialogOpen(false) }
            )
        }

        if (isSearchDialogOpen) {
            SearchMessagesDialog(
                searchQuery = searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                searchResults = searchResults,
                onSelectMessageConversation = { convId ->
                    viewModel.selectConversation(convId)
                },
                onDismiss = { viewModel.setSearchDialogOpen(false) }
            )
        }

        if (isSettingsDialogOpen) {
            ChatSettingsDialog(
                fontSizeMultiplier = fontSizeMultiplier,
                onFontSizeChanged = { viewModel.setFontSizeMultiplier(it) },
                isFastReadingMode = isFastReadingMode,
                onFastReadingModeChanged = { viewModel.setFastReadingMode(it) },
                activeTone = activeTone,
                onToneChanged = { viewModel.setActiveTone(it) },
                onDismiss = { viewModel.setSettingsDialogOpen(false) }
            )
        }

        if (isRenameDialogOpen && conversationToRename != null) {
            RenameConversationDialog(
                conversation = conversationToRename!!,
                onConfirm = { newTitle -> viewModel.confirmRename(newTitle) },
                onDismiss = { viewModel.closeRenameDialog() }
            )
        }
    }
}
