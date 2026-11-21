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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.academy.AcademyDiscipline
import com.example.academy.AssessmentQuestion
import com.example.academy.AssessmentQuestionType
import com.example.academy.DifficultyTier
import com.example.academy.LearningAcademySeed
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
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
fun AssessmentsScreen(
    language: AppLanguage,
    isSignedIn: Boolean,
    onSubmitAttempt: (subject: String, difficulty: String, score: Int, totalQuestions: Int, correctQuestionIds: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDiscipline by remember { mutableStateOf<AcademyDiscipline?>(null) }
    var selectedDifficulty by remember { mutableStateOf<DifficultyTier?>(null) }
    var randomizeOrder by remember { mutableStateOf(false) }
    var sessionSeed by remember { mutableIntStateOf(0) }

    val activeQuestions = remember(selectedDiscipline, selectedDifficulty, randomizeOrder, sessionSeed) {
        val base = LearningAcademySeed.assessmentQuestions.filter { q ->
            (selectedDiscipline == null || q.subject == selectedDiscipline) &&
                (selectedDifficulty == null || q.difficulty == selectedDifficulty)
        }
        if (randomizeOrder) base.shuffled() else base
    }

    var currentIndex by remember(activeQuestions) { mutableIntStateOf(0) }
    var selectedIndices by remember(currentIndex, activeQuestions) { mutableStateOf<Set<Int>>(emptySet()) }
    var orderedSequence by remember(currentIndex, activeQuestions) {
        val q = activeQuestions.getOrNull(currentIndex)
        mutableStateOf(
            if (q != null && q.orderedItems.isNotEmpty()) q.orderedItems.indices.toList().reversed() else emptyList()
        )
    }
    val matchSelections = remember(currentIndex, activeQuestions) { mutableStateMapOf<Int, Int>() }
    var checkedCurrent by remember(currentIndex, activeQuestions) { mutableStateOf(false) }
    var currentIsCorrect by remember(currentIndex, activeQuestions) { mutableStateOf(false) }
    val correctIdsInSession = remember(activeQuestions) { mutableStateOf<Set<String>>(emptySet()) }
    var sessionCompleted by remember(activeQuestions) { mutableStateOf(false) }

    val currentQuestion = activeQuestions.getOrNull(currentIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("assessments_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header + Filters + Randomize Toggle
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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = MedCyan, modifier = Modifier.size(26.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = UiStrings.modAssessments.get(language),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "8 أنماط اختبار طبي تفاعلي مع تغذية راجعة علمية موثقة"
                                } else {
                                    "All 8 medical assessment formats with immediate cited explanations"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        }
                    }

                    // Subject filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssessmentFilterChip(
                            label = if (language == AppLanguage.ARABIC) "كل التخصصات" else "All Subjects",
                            selected = selectedDiscipline == null,
                            onClick = { selectedDiscipline = null }
                        )
                        AcademyDiscipline.entries.forEach { disc ->
                            AssessmentFilterChip(
                                label = disc.label.get(language),
                                selected = selectedDiscipline == disc,
                                onClick = { selectedDiscipline = disc }
                            )
                        }
                    }

                    // Difficulty + Randomize row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AssessmentFilterChip(
                                label = if (language == AppLanguage.ARABIC) "كل المستويات" else "All Levels",
                                selected = selectedDifficulty == null,
                                onClick = { selectedDifficulty = null }
                            )
                            DifficultyTier.entries.forEach { tier ->
                                AssessmentFilterChip(
                                    label = tier.label.get(language),
                                    selected = selectedDifficulty == tier,
                                    onClick = { selectedDifficulty = tier }
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, tint = MedTeal, modifier = Modifier.size(16.dp))
                            Switch(
                                checked = randomizeOrder,
                                onCheckedChange = { randomizeOrder = it },
                                modifier = Modifier.testTag("assessment_randomize_switch"),
                                colors = SwitchDefaults.colors(checkedThumbColor = MedObsidianNavy, checkedTrackColor = MedTeal)
                            )
                        }
                    }
                }
            }
        }

        if (activeQuestions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 960.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) {
                            "لا توجد أسئلة مطابقة للتصفية الحالية. اختر (كل التخصصات) أو (كل المستويات)."
                        } else {
                            "No assessment questions match this filter combination. Reset filters to view all 8 interactive question types."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MedTextSecondary,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else if (sessionCompleted) {
            item {
                val finalScore = correctIdsInSession.value.size
                val total = activeQuestions.size
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 960.dp)
                        .testTag("assessment_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = MedVerifiedContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MedVerifiedGreen)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedVerifiedGreen, modifier = Modifier.size(44.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "اكتمل الاختبار بنجاح!" else "Assessment Session Complete",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$finalScore / $total (${(finalScore * 100 / total.coerceAtLeast(1))}%)",
                            style = MaterialTheme.typography.displaySmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSignedIn) {
                                if (language == AppLanguage.ARABIC) "تم حفظ النتيجة تلقائياً في سجل التعلم السحابي." else "Score saved automatically to your cloud learning history."
                            } else {
                                if (language == AppLanguage.ARABIC) "تم حفظ التقدم محلياً. سجّل الدخول بحساب Google للمزامنة السحابية." else "Progress saved locally. Sign in with Google in My Learning to sync cloud attempts."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                        Button(
                            onClick = {
                                sessionSeed += 1
                                currentIndex = 0
                                correctIdsInSession.value = emptySet()
                                sessionCompleted = false
                            },
                            modifier = Modifier.testTag("assessment_restart_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "إعادة الاختبار" else "Start New Attempt",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else if (currentQuestion != null) {
            item {
                QuestionInteractiveCard(
                    question = currentQuestion,
                    index = currentIndex,
                    total = activeQuestions.size,
                    language = language,
                    selectedIndices = selectedIndices,
                    orderedSequence = orderedSequence,
                    matchSelections = matchSelections,
                    checked = checkedCurrent,
                    isCorrect = currentIsCorrect,
                    onToggleOption = { idx ->
                        if (checkedCurrent) return@QuestionInteractiveCard
                        selectedIndices = if (currentQuestion.type == AssessmentQuestionType.MULTIPLE_SELECT) {
                            if (idx in selectedIndices) selectedIndices - idx else selectedIndices + idx
                        } else {
                            setOf(idx)
                        }
                    },
                    onMoveOrderItem = { pos, delta ->
                        if (checkedCurrent) return@QuestionInteractiveCard
                        val target = pos + delta
                        if (target in orderedSequence.indices) {
                            val mutable = orderedSequence.toMutableList()
                            val tmp = mutable[pos]
                            mutable[pos] = mutable[target]
                            mutable[target] = tmp
                            orderedSequence = mutable
                        }
                    },
                    onSelectMatch = { leftIdx, rightIdx ->
                        if (!checkedCurrent) {
                            matchSelections[leftIdx] = rightIdx
                        }
                    },
                    onCheckAnswer = {
                        val passed = evaluateQuestion(
                            question = currentQuestion,
                            selectedIndices = selectedIndices,
                            orderedSequence = orderedSequence,
                            matchSelections = matchSelections
                        )
                        currentIsCorrect = passed
                        checkedCurrent = true
                        if (passed) {
                            correctIdsInSession.value = correctIdsInSession.value + currentQuestion.id
                        }
                    },
                    onNext = {
                        if (currentIndex + 1 < activeQuestions.size) {
                            currentIndex += 1
                        } else {
                            sessionCompleted = true
                            val subjectName = selectedDiscipline?.name ?: "ALL_DISCIPLINES"
                            val diffName = selectedDifficulty?.name ?: "ALL"
                            onSubmitAttempt(
                                subjectName,
                                diffName,
                                correctIdsInSession.value.size,
                                activeQuestions.size,
                                correctIdsInSession.value.toList()
                            )
                        }
                    }
                )
            }
        }

        item { Spacer(Modifier.height(14.dp)) }
    }
}

private fun evaluateQuestion(
    question: AssessmentQuestion,
    selectedIndices: Set<Int>,
    orderedSequence: List<Int>,
    matchSelections: Map<Int, Int>
): Boolean {
    return when (question.type) {
        AssessmentQuestionType.PHYSIOLOGY_ORDERING -> {
            orderedSequence == question.orderedItems.indices.toList()
        }
        AssessmentQuestionType.TERMINOLOGY_MATCHING,
        AssessmentQuestionType.DRUG_MECHANISM_MATCH -> {
            question.matchPairs.indices.all { idx -> matchSelections[idx] == idx }
        }
        else -> {
            selectedIndices == question.correctIndices
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionInteractiveCard(
    question: AssessmentQuestion,
    index: Int,
    total: Int,
    language: AppLanguage,
    selectedIndices: Set<Int>,
    orderedSequence: List<Int>,
    matchSelections: Map<Int, Int>,
    checked: Boolean,
    isCorrect: Boolean,
    onToggleOption: (Int) -> Unit,
    onMoveOrderItem: (Int, Int) -> Unit,
    onSelectMatch: (Int, Int) -> Unit,
    onCheckAnswer: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 960.dp)
            .testTag("assessment_question_card_${question.id}"),
        shape = RoundedCornerShape(20.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(shape = RoundedCornerShape(8.dp), color = MedCyanContainer) {
                    Text(
                        text = "${index + 1} / $total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = MedDeepNavy, border = BorderStroke(1.dp, MedBorderHairline)) {
                    Text(
                        text = question.type.label.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MedTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = MedDeepNavy, border = BorderStroke(1.dp, MedBorderHairline)) {
                    Text(
                        text = question.difficulty.label.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MedTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = question.prompt.get(language),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Render input control based on exact question format
            when (question.type) {
                AssessmentQuestionType.PHYSIOLOGY_ORDERING -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        orderedSequence.forEachIndexed { pos, itemIdx ->
                            val itemText = question.orderedItems[itemIdx].get(language)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MedDeepNavy,
                                border = BorderStroke(1.dp, MedBorderHairline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${pos + 1}. $itemText",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row {
                                        IconButton(
                                            onClick = { onMoveOrderItem(pos, -1) },
                                            enabled = !checked && pos > 0
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = MedCyan)
                                        }
                                        IconButton(
                                            onClick = { onMoveOrderItem(pos, 1) },
                                            enabled = !checked && pos < orderedSequence.lastIndex
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = MedCyan)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AssessmentQuestionType.TERMINOLOGY_MATCHING,
                AssessmentQuestionType.DRUG_MECHANISM_MATCH -> {
                    val rightChoices = remember(question.id) {
                        question.matchPairs.mapIndexed { i, p -> i to p.second }.reversed()
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        question.matchPairs.forEachIndexed { leftIdx, pair ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MedDeepNavy,
                                border = BorderStroke(1.dp, MedBorderHairline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = pair.first,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MedCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        rightChoices.forEach { (origRightIdx, rightLabel) ->
                                            val chosen = matchSelections[leftIdx] == origRightIdx
                                            Surface(
                                                modifier = Modifier.clickable(enabled = !checked) {
                                                    onSelectMatch(leftIdx, origRightIdx)
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (chosen) MedTeal else MedPanelNavy,
                                                border = BorderStroke(1.dp, if (chosen) MedTeal else MedBorderHairline)
                                            ) {
                                                Text(
                                                    text = rightLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (chosen) MedObsidianNavy else Color.White,
                                                    fontWeight = if (chosen) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    val isMulti = question.type == AssessmentQuestionType.MULTIPLE_SELECT
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        question.options.forEachIndexed { idx, opt ->
                            val selected = idx in selectedIndices
                            val border = when {
                                checked && idx in question.correctIndices -> MedVerifiedGreen
                                checked && selected && idx !in question.correctIndices -> MedAlertRed
                                selected -> MedCyan
                                else -> MedBorderHairline
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !checked) { onToggleOption(idx) }
                                    .testTag("assessment_opt_${question.id}_$idx"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) MedCyanContainer.copy(alpha = 0.45f) else MedDeepNavy,
                                border = BorderStroke(1.dp, border)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isMulti) {
                                        Checkbox(
                                            checked = selected,
                                            onCheckedChange = { if (!checked) onToggleOption(idx) },
                                            colors = CheckboxDefaults.colors(checkedColor = MedCyan, checkmarkColor = MedObsidianNavy)
                                        )
                                    } else {
                                        RadioButton(
                                            selected = selected,
                                            onClick = { if (!checked) onToggleOption(idx) },
                                            colors = RadioButtonDefaults.colors(selectedColor = MedCyan)
                                        )
                                    }
                                    Text(
                                        text = opt.get(language),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Immediate Feedback + Citation
            if (checked) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCorrect) MedVerifiedContainer.copy(alpha = 0.6f) else MedAlertContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, if (isCorrect) MedVerifiedGreen else MedAlertRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isCorrect) MedVerifiedGreen else MedAlertRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isCorrect) {
                                    if (language == AppLanguage.ARABIC) "إجابة صحيحة!" else "Correct!"
                                } else {
                                    if (language == AppLanguage.ARABIC) "راجع الشرح العلمي أدناه:" else "Review the scientific explanation:"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = question.explanation.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = "${question.source.title} (${question.source.organization})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MedCyan
                        )
                    }
                }
            }

            // Submit / Next Button
            if (!checked) {
                Button(
                    onClick = onCheckAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("assessment_check_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "تحقق من الإجابة" else "Check Answer",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("assessment_next_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MedTeal)
                ) {
                    Text(
                        text = if (index + 1 < total) {
                            if (language == AppLanguage.ARABIC) "السؤال التالي" else "Next Question"
                        } else {
                            if (language == AppLanguage.ARABIC) "إنهاء وعرض النتيجة" else "Finish & Save Score"
                        },
                        color = MedTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AssessmentFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MedCyan else MedDeepNavy,
        border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MedObsidianNavy else Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
        )
    }
}
