package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.UserEntity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val user: UserEntity, val message: String) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
}

class FirebaseManager(
    private val context: Context,
    private val chatDao: ChatDao
) {
    private val tag = "FirebaseManager"

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Firebase Auth not initialized: ${e.message}")
            null
        }

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Firebase Firestore not initialized: ${e.message}")
            null
        }

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    /**
     * Sign Up with Email and Password on Firebase Servers
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        username: String,
        avatarUrl: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext AuthResult.Error("تعذر الاتصال بخوادم Firebase. تأكد من إعدادات الاتصال بالإنترنت.")
        }

        try {
            val authResult = authInstance.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
                ?: return@withContext AuthResult.Error("فشل إنشاء الحساب عبر Firebase")

            // Update Firebase User Profile (Display Name & Photo)
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName.ifBlank { username })
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Construct User Entity
            val userEntity = UserEntity(
                username = username.ifBlank { email.substringBefore("@") },
                fullName = fullName.ifBlank { "مستخدم الذكاء الاصطناعي" },
                email = email.trim(),
                avatarUrl = avatarUrl,
                planType = "free",
                creditsRemaining = 150,
                isLoggedIn = true,
                createdAt = System.currentTimeMillis()
            )

            // Save to Firestore Cloud
            saveUserToFirestore(firebaseUser.uid, userEntity)

            // Save / Update locally in Room
            chatDao.logoutAllUsers()
            val existing = chatDao.getUserByEmail(email.trim())
            val localId = if (existing != null) {
                chatDao.updateUser(userEntity.copy(id = existing.id))
                existing.id
            } else {
                chatDao.insertUser(userEntity)
            }

            AuthResult.Success(
                user = userEntity.copy(id = localId),
                message = "تم إنشاء الحساب وربطه بخوادم Firebase بنجاح! 🚀"
            )
        } catch (e: Exception) {
            Log.e(tag, "Sign up failed", e)
            val arabicError = parseFirebaseError(e)
            AuthResult.Error(arabicError)
        }
    }

    /**
     * Sign In with Email and Password on Firebase Servers
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext AuthResult.Error("تعذر الاتصال بخوادم Firebase. يرجى التحقق من الشبكة.")
        }

        try {
            val authResult = authInstance.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
                ?: return@withContext AuthResult.Error("فشل تسجيل الدخول عبر خوادم Firebase")

            // Retrieve profile from Cloud Firestore
            val cloudUser = fetchUserFromFirestore(firebaseUser.uid, firebaseUser)

            // Update Room local state
            chatDao.logoutAllUsers()
            val existing = chatDao.getUserByEmail(email.trim())
            val localId = if (existing != null) {
                val updated = cloudUser.copy(id = existing.id, isLoggedIn = true)
                chatDao.updateUser(updated)
                existing.id
            } else {
                chatDao.insertUser(cloudUser.copy(isLoggedIn = true))
            }

            // Trigger sync of conversations from Firestore Cloud
            syncConversationsFromFirestore(firebaseUser.uid)

            AuthResult.Success(
                user = cloudUser.copy(id = localId, isLoggedIn = true),
                message = "تم تسجيل الدخول بنجاح عبر فايربيس ومزامنة بياناتك السحابية!"
            )
        } catch (e: Exception) {
            Log.e(tag, "Sign in failed", e)
            val arabicError = parseFirebaseError(e)
            AuthResult.Error(arabicError)
        }
    }

    /**
     * Sign In with Google via Jetpack CredentialManager and Firebase Auth
     */
    suspend fun signInWithGoogle(
        activityContext: Context,
        serverClientId: String = ""
    ): AuthResult = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext AuthResult.Error("خدمات Firebase غير متوفرة حالياً")
        }

        try {
            val credentialManager = CredentialManager.create(activityContext)
            
            // Build Google ID Option
            val googleIdOption = GetSignInWithGoogleOption.Builder(
                serverClientId = serverClientId.ifBlank { "default-client-id" }
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = authInstance.signInWithCredential(authCredential).await()
                val firebaseUser = authResult.user
                    ?: return@withContext AuthResult.Error("تعذر مصادقة حساب Google")

                val cloudUser = fetchUserFromFirestore(firebaseUser.uid, firebaseUser)
                chatDao.logoutAllUsers()
                val existing = chatDao.getUserByEmail(firebaseUser.email ?: "")
                val localId = if (existing != null) {
                    val updated = cloudUser.copy(id = existing.id, isLoggedIn = true)
                    chatDao.updateUser(updated)
                    existing.id
                } else {
                    chatDao.insertUser(cloudUser.copy(isLoggedIn = true))
                }

                syncConversationsFromFirestore(firebaseUser.uid)

                AuthResult.Success(
                    user = cloudUser.copy(id = localId, isLoggedIn = true),
                    message = "تم تسجيل الدخول بحساب Google بنجاح عبر Firebase!"
                )
            } else {
                AuthResult.Error("نوع المصادقة غير مدعوم")
            }
        } catch (e: GetCredentialException) {
            Log.e(tag, "Google CredentialManager error: ${e.message}")
            AuthResult.Error("تم إلغاء أو تعذر إكمال تسجيل الدخول بواسطة Google: ${e.message}")
        } catch (e: Exception) {
            Log.e(tag, "Google Sign in error", e)
            AuthResult.Error(parseFirebaseError(e))
        }
    }

    /**
     * Sign out user from Firebase and local state
     */
    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            auth?.signOut()
            chatDao.logoutAllUsers()
        } catch (e: Exception) {
            Log.e(tag, "Sign out error", e)
            chatDao.logoutAllUsers()
        }
    }

    /**
     * Sync user data to Firestore
     */
    suspend fun saveUserToFirestore(uid: String, user: UserEntity) = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext
        try {
            val userMap = hashMapOf(
                "uid" to uid,
                "email" to user.email,
                "fullName" to user.fullName,
                "username" to user.username,
                "avatarUrl" to user.avatarUrl,
                "planType" to user.planType,
                "creditsRemaining" to user.creditsRemaining,
                "totalPromptsUsed" to user.totalPromptsUsed,
                "appsCreatedCount" to user.appsCreatedCount,
                "videosCreatedCount" to user.videosCreatedCount,
                "imagesCreatedCount" to user.imagesCreatedCount,
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection("users").document(uid).set(userMap, SetOptions.merge()).await()
            Log.d(tag, "User profile saved to Cloud Firestore for UID: $uid")
        } catch (e: Exception) {
            Log.e(tag, "Failed to save user to Firestore: ${e.message}")
        }
    }

    /**
     * Fetch user data from Firestore
     */
    private suspend fun fetchUserFromFirestore(uid: String, firebaseUser: FirebaseUser): UserEntity = withContext(Dispatchers.IO) {
        val fs = firestore
        if (fs != null) {
            try {
                val doc = fs.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    return@withContext UserEntity(
                        username = doc.getString("username") ?: firebaseUser.email?.substringBefore("@") ?: "user",
                        fullName = doc.getString("fullName") ?: firebaseUser.displayName ?: "مستخدم الذكاء الاصطناعي",
                        email = doc.getString("email") ?: firebaseUser.email ?: "",
                        avatarUrl = doc.getString("avatarUrl") ?: firebaseUser.photoUrl?.toString() ?: "",
                        planType = doc.getString("planType") ?: "free",
                        creditsRemaining = (doc.getLong("creditsRemaining") ?: 150).toInt(),
                        totalPromptsUsed = (doc.getLong("totalPromptsUsed") ?: 0).toInt(),
                        appsCreatedCount = (doc.getLong("appsCreatedCount") ?: 0).toInt(),
                        videosCreatedCount = (doc.getLong("videosCreatedCount") ?: 0).toInt(),
                        imagesCreatedCount = (doc.getLong("imagesCreatedCount") ?: 0).toInt(),
                        isLoggedIn = true
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching user doc: ${e.message}")
            }
        }

        // Fallback to Firebase User Info
        UserEntity(
            username = firebaseUser.email?.substringBefore("@") ?: "user",
            fullName = firebaseUser.displayName ?: "مستخدم الذكاء الاصطناعي",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
            planType = "free",
            creditsRemaining = 150,
            isLoggedIn = true
        )
    }

    /**
     * Sync local conversation to Cloud Firestore
     */
    suspend fun syncConversationToFirestore(
        conversation: ConversationEntity,
        messages: List<ChatMessageEntity>
    ) = withContext(Dispatchers.IO) {
        val uid = currentFirebaseUser?.uid ?: return@withContext
        val fs = firestore ?: return@withContext
        try {
            val convData = hashMapOf(
                "id" to conversation.id,
                "title" to conversation.title,
                "personaId" to conversation.personaId,
                "isPinned" to conversation.isPinned,
                "lastMessageSnippet" to conversation.lastMessageSnippet,
                "messageCount" to conversation.messageCount,
                "updatedAt" to conversation.updatedAt,
                "createdAt" to conversation.createdAt
            )
            fs.collection("users").document(uid)
                .collection("conversations").document(conversation.id.toString())
                .set(convData, SetOptions.merge()).await()

            // Save messages
            for (msg in messages) {
                val msgData = hashMapOf(
                    "id" to msg.id,
                    "conversationId" to msg.conversationId,
                    "role" to msg.role,
                    "content" to msg.content,
                    "timestamp" to msg.timestamp,
                    "isFavorite" to msg.isFavorite,
                    "isVoice" to msg.isVoice,
                    "voiceDurationSeconds" to msg.voiceDurationSeconds
                )
                fs.collection("users").document(uid)
                    .collection("conversations").document(conversation.id.toString())
                    .collection("messages").document(msg.id.toString())
                    .set(msgData, SetOptions.merge())
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync conversation to Cloud Firestore: ${e.message}")
        }
    }

    /**
     * Restore / Sync conversations from Cloud Firestore
     */
    private suspend fun syncConversationsFromFirestore(uid: String) = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext
        try {
            val snapshot = fs.collection("users").document(uid)
                .collection("conversations").get().await()

            for (doc in snapshot.documents) {
                val title = doc.getString("title") ?: "محادثة سحابية"
                val personaId = doc.getString("personaId") ?: "general"
                val isPinned = doc.getBoolean("isPinned") ?: false
                val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                val snippet = doc.getString("lastMessageSnippet") ?: ""

                val existing = chatDao.getAllConversationsOnce().find { it.title == title }
                val convId = if (existing == null) {
                    chatDao.insertConversation(
                        ConversationEntity(
                            title = title,
                            personaId = personaId,
                            isPinned = isPinned,
                            lastMessageSnippet = snippet,
                            createdAt = createdAt,
                            updatedAt = updatedAt
                        )
                    )
                } else {
                    existing.id
                }

                // Sync messages
                val msgSnapshot = doc.reference.collection("messages").get().await()
                for (msgDoc in msgSnapshot.documents) {
                    val role = msgDoc.getString("role") ?: "assistant"
                    val content = msgDoc.getString("content") ?: ""
                    val ts = msgDoc.getLong("timestamp") ?: System.currentTimeMillis()
                    val isFav = msgDoc.getBoolean("isFavorite") ?: false

                    val existingMsgs = chatDao.getMessagesForConversationOnce(convId)
                    if (existingMsgs.none { it.content == content && it.timestamp == ts }) {
                        chatDao.insertMessage(
                            ChatMessageEntity(
                                conversationId = convId,
                                role = role,
                                content = content,
                                timestamp = ts,
                                isFavorite = isFav
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to restore conversations from Cloud Firestore: ${e.message}")
        }
    }

    private fun parseFirebaseError(exception: Exception): String {
        val msg = exception.message ?: ""
        return when {
            msg.contains("email-already-in-use", ignoreCase = true) -> "هذا البريد الإلكتروني مسجل مسبقاً في Firebase. يرجى تسجيل الدخول بدلاً من ذلك."
            msg.contains("invalid-email", ignoreCase = true) -> "صيغة البريد الإلكتروني غير صحيحة."
            msg.contains("wrong-password", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) -> "كلمة المرور أو البريد الإلكتروني غير صحيح."
            msg.contains("user-not-found", ignoreCase = true) -> "لا يوجد حساب مسجل بهذا البريد الإلكتروني في خوادم Firebase."
            msg.contains("weak-password", ignoreCase = true) -> "كلمة المرور ضعيفة. يرجى إدخال 6 خانات على الأقل."
            msg.contains("network-error", ignoreCase = true) || msg.contains("UNAVAILABLE", ignoreCase = true) -> "تعذر الاتصال بالخادم السحابي. يرجى التحقق من اتصالك بالإنترنت."
            else -> "حدث خطأ في المصادقة مع Firebase: ${exception.localizedMessage ?: msg}"
        }
    }
}
