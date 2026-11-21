package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.academy.AcademyCourse
import com.example.academy.AcademyLesson
import com.example.academy.DifficultyTier
import com.example.academy.LearningAcademySeed
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.physiology.engine.PhysiologySystemId
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearningAcademyScreen(
    completedLessonIds: Set<String>,
    language: AppLanguage,
    onToggleLessonCompleted: (String, AcademyLesson) -> Unit,
    onOpenAnatomyStructure: (String) -> Unit,
    onOpenPhysiologySystem: (PhysiologySystemId) -> Unit,
    onOpenDisease: (String) -> Unit,
    onOpenDrugClass: (String) -> Unit,
    onOpenAssessments: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf<DifficultyTier?>(null) }
    val allCourses = LearningAcademySeed.courses
    val filteredCourses = remember(selectedDifficulty) {
        if (selectedDifficulty == null) allCourses else allCourses.filter { it.difficulty == selectedDifficulty }
    }

    val totalLessons = allCourses.sumOf { it.lessons.size }
    val completedCount = allCourses.flatMap { it.lessons }.count { it.id in completedLessonIds }
    val overallProgress = if (totalLessons > 0) completedCount.toFloat() / totalLessons else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("learning_academy_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header & Overall Academy Progress
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(20.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = MedCyan, modifier = Modifier.size(28.dp))
                            Column {
                                Text(
                                    text = UiStrings.navAcademy.get(language),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (language == AppLanguage.ARABIC) {
                                        "مسارات طبية منظمة متكاملة مع التشريح 3D والمختبرات التفاعلية"
                                    } else {
                                        "Structured bilingual courses linked directly to 3D Anatomy & Interactive Labs"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = onOpenAssessments,
                            modifier = Modifier.testTag("academy_open_assessments_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedTeal)
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = null, tint = MedTeal, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.navAssessments.get(language), color = MedTeal, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "تقدم الأكاديمية الفعلي" else "Real Course Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = MedCyan
                        )
                        Text(
                            text = "$completedCount / $totalLessons ${if (language == AppLanguage.ARABIC) "دروس مكتملة" else "lessons completed"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MedTeal,
                        trackColor = MedDeepNavy
                    )

                    // Difficulty Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyFilterChip(
                            label = if (language == AppLanguage.ARABIC) "الكل (${allCourses.size})" else "All (${allCourses.size})",
                            selected = selectedDifficulty == null,
                            onClick = { selectedDifficulty = null },
                            tag = "academy_filter_all"
                        )
                        DifficultyTier.entries.forEach { tier ->
                            DifficultyFilterChip(
                                label = tier.label.get(language),
                                selected = selectedDifficulty == tier,
                                onClick = { selectedDifficulty = tier },
                                tag = "academy_filter_${tier.name}"
                            )
                        }
                    }
                }
            }
        }

        // Course Cards
        items(filteredCourses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                completedLessonIds = completedLessonIds,
                language = language,
                onToggleLessonCompleted = onToggleLessonCompleted,
                onOpenAnatomyStructure = onOpenAnatomyStructure,
                onOpenPhysiologySystem = onOpenPhysiologySystem,
                onOpenDisease = onOpenDisease,
                onOpenDrugClass = onOpenDrugClass
            )
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun DifficultyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MedCyan else MedDeepNavy,
        border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MedObsidianNavy else Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CourseCard(
    course: AcademyCourse,
    completedLessonIds: Set<String>,
    language: AppLanguage,
    onToggleLessonCompleted: (String, AcademyLesson) -> Unit,
    onOpenAnatomyStructure: (String) -> Unit,
    onOpenPhysiologySystem: (PhysiologySystemId) -> Unit,
    onOpenDisease: (String) -> Unit,
    onOpenDrugClass: (String) -> Unit
) {
    val completedInCourse = course.lessons.count { it.id in completedLessonIds }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 960.dp)
            .testTag("academy_course_${course.id}"),
        shape = RoundedCornerShape(18.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedCyanContainer,
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = course.discipline.label.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedDeepNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Text(
                        text = course.difficulty.label.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MedTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                ReviewStatusBadge(status = course.reviewStatus, language = language)
            }

            Text(
                text = course.title.get(language),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = course.description.get(language),
                style = MaterialTheme.typography.bodyMedium,
                color = MedTextSecondary
            )

            // Learning Objectives
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (language == AppLanguage.ARABIC) "أهداف التعلم ($completedInCourse/${course.lessons.size}):" else "Learning Objectives ($completedInCourse/${course.lessons.size} completed):",
                    style = MaterialTheme.typography.labelLarge,
                    color = MedCyan,
                    fontWeight = FontWeight.Bold
                )
                course.learningObjectives.forEach { obj ->
                    Text(
                        text = "• ${obj.get(language)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            // Lessons
            course.lessons.forEach { lesson ->
                val isCompleted = lesson.id in completedLessonIds
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("academy_lesson_${lesson.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCompleted) MedVerifiedContainer.copy(alpha = 0.45f) else MedDeepNavy,
                    border = BorderStroke(1.dp, if (isCompleted) MedVerifiedGreen.copy(alpha = 0.6f) else MedBorderHairline)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lesson.title.get(language),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${lesson.durationMinutes} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan
                            )
                        }

                        Text(
                            text = lesson.readingSummary.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )

                        Text(
                            text = lesson.keyTakeaway.get(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MedTeal
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onToggleLessonCompleted(lesson.id, lesson) },
                                modifier = Modifier.testTag("lesson_complete_btn_${lesson.id}"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isCompleted) MedVerifiedGreen else MedCyan)
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isCompleted) MedVerifiedGreen else MedCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(
                                    text = if (isCompleted) {
                                        if (language == AppLanguage.ARABIC) "مكتمل" else "Completed"
                                    } else {
                                        if (language == AppLanguage.ARABIC) "تحديد كمكتمل" else "Mark Complete"
                                    },
                                    color = if (isCompleted) MedVerifiedGreen else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            lesson.linkedAnatomyStructureId?.let { id ->
                                Button(
                                    onClick = { onOpenAnatomyStructure(id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                                ) {
                                    Text(UiStrings.openIn3DCta.get(language), fontWeight = FontWeight.Bold)
                                }
                            }
                            lesson.linkedPhysiologySystem?.let { sys ->
                                Button(
                                    onClick = { onOpenPhysiologySystem(sys) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedTeal, contentColor = MedObsidianNavy)
                                ) {
                                    Text(UiStrings.openPhysiologyCta.get(language), fontWeight = FontWeight.Bold)
                                }
                            }
                            lesson.linkedDiseaseId?.let { id ->
                                Button(
                                    onClick = { onOpenDisease(id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                                ) {
                                    Text(UiStrings.navPathology.get(language), fontWeight = FontWeight.Bold)
                                }
                            }
                            lesson.linkedDrugClassId?.let { id ->
                                Button(
                                    onClick = { onOpenDrugClass(id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedTeal, contentColor = MedObsidianNavy)
                                ) {
                                    Text(UiStrings.navPharmacology.get(language), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
