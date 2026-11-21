package com.example.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Authenticated Cloud Firestore repository initialized exclusively against the named database
 * R.string.firestore_database_id. Never self-authenticates; enforces fail-fast requireUserId().
 */
class MedVerseCloudRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        const val BOOTSTRAPPED_ADMIN_EMAIL = "zydealwajee712@gmail.com"
    }

    private val auth get() = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Google Sign-In is required before accessing cloud storage.")
    }

    /**
     * Resolves verified server-side role from /admins, /reviewers, /editors collections
     * and verified bootstrapped admin email.
     */
    suspend fun resolveEffectiveRole(): Result<UserRole> {
        val currentUser = auth.currentUser ?: return Result.success(UserRole.GUEST)
        val uid = currentUser.uid
        if (currentUser.isEmailVerified && currentUser.email.equals(BOOTSTRAPPED_ADMIN_EMAIL, ignoreCase = true)) {
            return Result.success(UserRole.ADMIN)
        }
        return try {
            val adminDoc = db.collection("admins").document(uid).get().await()
            if (adminDoc.exists()) return Result.success(UserRole.ADMIN)

            val reviewerDoc = db.collection("reviewers").document(uid).get().await()
            if (reviewerDoc.exists()) return Result.success(UserRole.REVIEWER)

            val editorDoc = db.collection("editors").document(uid).get().await()
            if (editorDoc.exists()) return Result.success(UserRole.EDITOR)

            Result.success(UserRole.STUDENT)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "admins/$uid")
            Result.success(UserRole.STUDENT)
        }
    }

    /**
     * Ensures /users/{uid} exists with strict STUDENT default (or ADMIN for bootstrapped verified admin).
     */
    suspend fun ensureUserProfile(
        displayName: String,
        preferredLanguage: String,
        localCompletedLessons: Set<String>,
        localBookmarkedTerms: Set<String>
    ): Result<Unit> {
        var path = "users/unauthenticated"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val docRef = db.collection("users").document(uid)
            val snap = docRef.get().await()
            val safeName = displayName.trim().ifBlank { "MedVerse Learner" }.take(100)
            val safeLang = if (preferredLanguage == "ar") "ar" else "en"

            if (!snap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "role" to "STUDENT",
                    "preferredLanguage" to safeLang,
                    "completedLessonIds" to localCompletedLessons.take(200),
                    "savedStructureIds" to emptyList<String>(),
                    "bookmarkedTermIds" to localBookmarkedTerms.take(200),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                val existing = snap.toObject(UserLearningProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                val mergedLessons = ((existing?.completedLessonIds ?: emptyList()) + localCompletedLessons).distinct().take(200)
                val mergedTerms = ((existing?.bookmarkedTermIds ?: emptyList()) + localBookmarkedTerms).distinct().take(200)
                docRef.update(
                    mapOf(
                        "preferredLanguage" to safeLang,
                        "completedLessonIds" to mergedLessons,
                        "bookmarkedTermIds" to mergedTerms,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeUserProfile(uid: String): Flow<UserLearningProfile?> = flow {
        require(uid.isNotBlank()) { "UID must not be blank" }
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(UserLearningProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun syncProgressFields(
        completedLessons: Set<String>? = null,
        savedStructures: Set<String>? = null,
        bookmarkedTerms: Set<String>? = null,
        preferredLanguage: String? = null
    ): Result<Unit> {
        var path = "users/unauthenticated"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
            completedLessons?.let { updates["completedLessonIds"] = it.take(200) }
            savedStructures?.let { updates["savedStructureIds"] = it.take(200) }
            bookmarkedTerms?.let { updates["bookmarkedTermIds"] = it.take(200) }
            preferredLanguage?.let { updates["preferredLanguage"] = if (it == "ar") "ar" else "en" }
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    fun observeQuizAttempts(uid: String): Flow<List<QuizAttemptRecord>> = flow {
        require(uid.isNotBlank()) { "UID must not be blank" }
        val path = "users/$uid/quizAttempts"
        emitAll(
            db.collection("users").document(uid).collection("quizAttempts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(30)
                .snapshots()
                .map { it.toObjects(QuizAttemptRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun recordQuizAttempt(
        subject: String,
        difficulty: String,
        score: Int,
        totalQuestions: Int
    ): Result<Unit> {
        var path = "users/unauthenticated/quizAttempts"
        return try {
            val uid = requireUserId()
            val safeTotal = totalQuestions.coerceIn(1, 100)
            val safeScore = score.coerceIn(0, safeTotal)
            val safeDiff = if (difficulty in setOf("BEGINNER", "INTERMEDIATE", "ADVANCED", "ALL")) difficulty else "ALL"
            val docRef = db.collection("users").document(uid).collection("quizAttempts").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "subject" to subject.trim().ifBlank { "general" }.take(60),
                "difficulty" to safeDiff,
                "score" to safeScore,
                "totalQuestions" to safeTotal,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeActivityLog(uid: String): Flow<List<LearningActivityRecord>> = flow {
        require(uid.isNotBlank()) { "UID must not be blank" }
        val path = "users/$uid/activityLog"
        emitAll(
            db.collection("users").document(uid).collection("activityLog")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(25)
                .snapshots()
                .map { it.toObjects(LearningActivityRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun logLearningActivity(
        activityType: String,
        targetId: String,
        titleEn: String,
        titleAr: String
    ): Result<Unit> {
        var path = "users/unauthenticated/activityLog"
        return try {
            val uid = requireUserId()
            val validTypes = setOf("ANATOMY", "PHYSIOLOGY", "PATHOLOGY", "PHARMACOLOGY", "LESSON", "QUIZ", "AI_TUTOR")
            val safeType = if (activityType in validTypes) activityType else "ANATOMY"
            val docRef = db.collection("users").document(uid).collection("activityLog").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "activityType" to safeType,
                "targetId" to targetId.trim().ifBlank { "unknown" }.take(100),
                "titleEn" to titleEn.trim().ifBlank { "Learning Activity" }.take(160),
                "titleAr" to titleAr.trim().ifBlank { "نشاط تعليمي" }.take(160),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    /**
     * Privacy right: deletes all stored user learning documents (attempts, activities, and profile).
     */
    suspend fun deleteAllUserLearningData(): Result<Unit> {
        var path = "users/unauthenticated"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val attempts = db.collection("users").document(uid).collection("quizAttempts").get().await()
            attempts.documents.forEach { it.reference.delete().await() }

            val activities = db.collection("users").document(uid).collection("activityLog").get().await()
            activities.documents.forEach { it.reference.delete().await() }

            db.collection("users").document(uid).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Medical CMS & Editorial Governance Workflows (Role-Protected)
    // -------------------------------------------------------------------------

    fun observeCmsContent(canInspectDrafts: Boolean): Flow<List<CmsContentRecord>> = flow {
        val uid = requireUserId()
        val path = "cmsContent"
        val query: Query = if (canInspectDrafts) {
            db.collection("cmsContent").limit(50)
        } else {
            // Strictly aligns with firestore.rules `allow list: if isSignedIn() && (resource.data.status == 'PUBLISHED' || isEditor())`
            db.collection("cmsContent").whereEqualTo("status", "PUBLISHED").limit(50)
        }
        emitAll(
            query.snapshots()
                .map { snap ->
                    snap.toObjects(CmsContentRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.updatedAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeAuditLogs(): Flow<List<EditorialAuditLog>> = flow {
        val uid = requireUserId()
        val path = "auditLogs"
        emitAll(
            db.collection("auditLogs")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(30)
                .snapshots()
                .map { it.toObjects(EditorialAuditLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createCmsDraft(
        titleEn: String,
        titleAr: String,
        contentType: String,
        systemId: String,
        summaryEn: String,
        summaryAr: String,
        scientificReferences: String
    ): Result<String> {
        var path = "cmsContent"
        return try {
            val uid = requireUserId()
            val actorName = (auth.currentUser?.displayName ?: auth.currentUser?.email ?: "Editor").take(100)
            require(titleEn.trim().length in 3..160) { "English title must be 3–160 characters." }
            require(titleAr.trim().length in 3..160) { "Arabic title must be 3–160 characters." }
            require(summaryEn.trim().length in 10..4000) { "English content must be 10–4000 characters." }
            require(summaryAr.trim().length in 10..4000) { "Arabic content must be 10–4000 characters." }
            require(scientificReferences.trim().length in 3..1000) { "Scientific references must be 3–1000 characters." }

            val docRef = db.collection("cmsContent").document()
            path = docRef.path
            val payload = mapOf(
                "titleEn" to titleEn.trim(),
                "titleAr" to titleAr.trim(),
                "contentType" to contentType,
                "systemId" to systemId.trim().ifBlank { "cardiovascular" }.take(64),
                "summaryEn" to summaryEn.trim(),
                "summaryAr" to summaryAr.trim(),
                "scientificReferences" to scientificReferences.trim(),
                "status" to EditorialStatus.DRAFT.name,
                "authorUid" to uid,
                "authorName" to actorName,
                "version" to 1,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            appendAuditLog(uid, actorName, "CREATE_DRAFT", docRef.id, EditorialStatus.DRAFT.name, "Created bilingual draft")
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun transitionCmsStatus(
        entry: CmsContentRecord,
        targetStatus: EditorialStatus,
        note: String? = null
    ): Result<Unit> {
        var path = "cmsContent/${entry.id}"
        return try {
            val uid = requireUserId()
            require(entry.id.isNotBlank()) { "Entry ID must not be blank" }
            val actorName = (auth.currentUser?.displayName ?: auth.currentUser?.email ?: "Reviewer").take(100)
            val updates = mutableMapOf<String, Any>(
                "status" to targetStatus.name,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val actionName = when (targetStatus) {
                EditorialStatus.PENDING_REVIEW -> {
                    updates["version"] = entry.version + 1L
                    "SUBMIT_REVIEW"
                }
                EditorialStatus.APPROVED -> {
                    updates["reviewerUid"] = uid
                    updates["reviewerName"] = actorName
                    "APPROVE"
                }
                EditorialStatus.NEEDS_UPDATE -> {
                    updates["reviewerUid"] = uid
                    updates["reviewerName"] = actorName
                    "REQUEST_UPDATE"
                }
                EditorialStatus.PUBLISHED -> "PUBLISH"
                EditorialStatus.ARCHIVED -> "ARCHIVE"
                EditorialStatus.DRAFT -> "EDIT_DRAFT"
            }

            val docRef = db.collection("cmsContent").document(entry.id)
            path = docRef.path
            docRef.update(updates).await()
            appendAuditLog(uid, actorName, actionName, entry.id, targetStatus.name, note)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    private suspend fun appendAuditLog(
        actorUid: String,
        actorName: String,
        action: String,
        targetEntryId: String,
        newStatus: String,
        note: String?
    ) {
        val logRef = db.collection("auditLogs").document()
        val payload = mapOf(
            "actorUid" to actorUid,
            "actorName" to actorName.take(100),
            "action" to action,
            "targetEntryId" to targetEntryId.take(128),
            "newStatus" to newStatus,
            "note" to note?.trim()?.take(500),
            "createdAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
        logRef.set(payload).await()
    }
}
