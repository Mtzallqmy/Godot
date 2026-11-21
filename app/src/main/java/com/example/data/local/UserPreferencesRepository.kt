package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.medVerseDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "medverse_phase1_prefs"
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE_CODE = stringPreferencesKey("selected_language_code")
        val BOOKMARKED_TERM_IDS = stringSetPreferencesKey("bookmarked_term_ids")
        val COMPLETED_LESSON_IDS = stringSetPreferencesKey("completed_lesson_ids")
        val CORRECT_QUIZ_IDS = stringSetPreferencesKey("correct_quiz_ids")
        val SAVED_STRUCTURE_IDS = stringSetPreferencesKey("saved_structure_ids")
    }

    val languageFlow: Flow<AppLanguage> = context.medVerseDataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.LANGUAGE_CODE])
    }

    val bookmarkedTermIdsFlow: Flow<Set<String>> = context.medVerseDataStore.data.map { prefs ->
        prefs[Keys.BOOKMARKED_TERM_IDS] ?: emptySet()
    }

    val completedLessonIdsFlow: Flow<Set<String>> = context.medVerseDataStore.data.map { prefs ->
        prefs[Keys.COMPLETED_LESSON_IDS] ?: emptySet()
    }

    val correctQuizIdsFlow: Flow<Set<String>> = context.medVerseDataStore.data.map { prefs ->
        prefs[Keys.CORRECT_QUIZ_IDS] ?: emptySet()
    }

    val savedStructureIdsFlow: Flow<Set<String>> = context.medVerseDataStore.data.map { prefs ->
        prefs[Keys.SAVED_STRUCTURE_IDS] ?: emptySet()
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.medVerseDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_CODE] = language.code
        }
    }

    suspend fun toggleBookmark(termId: String) {
        context.medVerseDataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKED_TERM_IDS] ?: emptySet()
            prefs[Keys.BOOKMARKED_TERM_IDS] = if (termId in current) {
                current - termId
            } else {
                current + termId
            }
        }
    }

    suspend fun toggleSavedStructure(structureId: String) {
        context.medVerseDataStore.edit { prefs ->
            val current = prefs[Keys.SAVED_STRUCTURE_IDS] ?: emptySet()
            prefs[Keys.SAVED_STRUCTURE_IDS] = if (structureId in current) {
                current - structureId
            } else {
                current + structureId
            }
        }
    }

    suspend fun markLessonCompleted(lessonId: String) {
        context.medVerseDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_LESSON_IDS] ?: emptySet()
            prefs[Keys.COMPLETED_LESSON_IDS] = current + lessonId
        }
    }

    suspend fun toggleLessonCompleted(lessonId: String) {
        context.medVerseDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_LESSON_IDS] ?: emptySet()
            prefs[Keys.COMPLETED_LESSON_IDS] = if (lessonId in current) {
                current - lessonId
            } else {
                current + lessonId
            }
        }
    }

    suspend fun recordCorrectQuizAnswer(questionId: String) {
        context.medVerseDataStore.edit { prefs ->
            val current = prefs[Keys.CORRECT_QUIZ_IDS] ?: emptySet()
            prefs[Keys.CORRECT_QUIZ_IDS] = current + questionId
        }
    }

    suspend fun resetQuizProgress() {
        context.medVerseDataStore.edit { prefs ->
            prefs[Keys.CORRECT_QUIZ_IDS] = emptySet()
        }
    }

    suspend fun resetAllLocalProgress() {
        context.medVerseDataStore.edit { prefs ->
            prefs[Keys.BOOKMARKED_TERM_IDS] = emptySet()
            prefs[Keys.COMPLETED_LESSON_IDS] = emptySet()
            prefs[Keys.CORRECT_QUIZ_IDS] = emptySet()
            prefs[Keys.SAVED_STRUCTURE_IDS] = emptySet()
        }
    }

    suspend fun mergeCloudProgress(
        completedLessons: Set<String>,
        savedStructures: Set<String>,
        bookmarkedTerms: Set<String>
    ) {
        context.medVerseDataStore.edit { prefs ->
            prefs[Keys.COMPLETED_LESSON_IDS] = (prefs[Keys.COMPLETED_LESSON_IDS] ?: emptySet()) + completedLessons
            prefs[Keys.SAVED_STRUCTURE_IDS] = (prefs[Keys.SAVED_STRUCTURE_IDS] ?: emptySet()) + savedStructures
            prefs[Keys.BOOKMARKED_TERM_IDS] = (prefs[Keys.BOOKMARKED_TERM_IDS] ?: emptySet()) + bookmarkedTerms
        }
    }
}
