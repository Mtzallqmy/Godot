package com.example.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class UserRole {
    GUEST,
    STUDENT,
    EDITOR,
    REVIEWER,
    ADMIN;

    val canEditCms: Boolean get() = this == EDITOR || this == REVIEWER || this == ADMIN
    val canReviewCms: Boolean get() = this == REVIEWER || this == ADMIN
    val canPublishCms: Boolean get() = this == ADMIN

    companion object {
        fun fromRaw(raw: String?): UserRole =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: STUDENT
    }
}

enum class EditorialStatus {
    DRAFT,
    PENDING_REVIEW,
    APPROVED,
    PUBLISHED,
    NEEDS_UPDATE,
    ARCHIVED;

    companion object {
        fun fromRaw(raw: String?): EditorialStatus =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: DRAFT
    }
}

@Keep
@IgnoreExtraProperties
data class UserLearningProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val role: String = "STUDENT",
    val preferredLanguage: String = "en",
    val completedLessonIds: List<String> = emptyList(),
    val savedStructureIds: List<String> = emptyList(),
    val bookmarkedTermIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuizAttemptRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val difficulty: String = "ALL",
    val score: Long = 0L,
    val totalQuestions: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LearningActivityRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val activityType: String = "ANATOMY",
    val targetId: String = "",
    val titleEn: String = "",
    val titleAr: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CmsContentRecord(
    @DocumentId val id: String = "",
    val titleEn: String = "",
    val titleAr: String = "",
    val contentType: String = "PATHOLOGY",
    val systemId: String = "cardiovascular",
    val summaryEn: String = "",
    val summaryAr: String = "",
    val scientificReferences: String = "",
    val status: String = "DRAFT",
    val authorUid: String = "",
    val authorName: String = "",
    val reviewerUid: String? = null,
    val reviewerName: String? = null,
    val version: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class EditorialAuditLog(
    @DocumentId val id: String = "",
    val actorUid: String = "",
    val actorName: String = "",
    val action: String = "CREATE_DRAFT",
    val targetEntryId: String = "",
    val newStatus: String = "DRAFT",
    val note: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)
