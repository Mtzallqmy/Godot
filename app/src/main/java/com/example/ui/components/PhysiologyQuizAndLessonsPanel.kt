package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.learning.GuidedLessonStep
import com.example.physiology.learning.GuidedPhysiologyLesson
import com.example.physiology.learning.PhysiologyLearningSeed
import com.example.physiology.learning.PhysiologyQuizQuestion
import com.example.physiology.learning.QuizQuestionFormat
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen

@Composable
fun GuidedLessonsPanel(
    activeSystem: PhysiologySystemId,
    completedLessonIds: Set<String>,
    language: AppLanguage,
    onApplyLessonStep: (GuidedLessonStep) -> Unit,
    onCompleteLesson: (String) -> Unit,
    onOpenQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lessons = remember(activeSystem) {
        val primary = PhysiologyLearningSeed.guidedLessons.filter { it.systemId == activeSystem }
        val others = PhysiologyLearningSeed.guidedLessons.filter { it.systemId != activeSystem }
        primary + others
    }
    var activeLessonId by remember(activeSystem) { mutableStateOf(lessons.first().lessonId) }
    var activeStepIdx by remember(activeLessonId) { mutableIntStateOf(0) }
    val activeLesson = lessons.firstOrNull { it.lessonId == activeLessonId } ?: lessons.first()
    val currentStep = activeLesson.steps[activeStepIdx.coerceIn(0, activeLesson.steps.lastIndex)]
    val isLessonDone = activeLesson.lessonId in completedLessonIds

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("guided_lessons_panel"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Lesson Selector Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MedCyan, modifier = Modifier.size(18.dp))
                Text(
                    text = if (language.isRtl) "الدروس التفاعلية الموجهة" else "Guided Interactive Lessons",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${completedLessonIds.size}/${PhysiologyLearningSeed.guidedLessons.size}",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                color = MedVerifiedGreen
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            lessons.forEach { lesson ->
                val selected = lesson.lessonId == activeLesson.lessonId
                val done = lesson.lessonId in completedLessonIds
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activeLessonId = lesson.lessonId
                            activeStepIdx = 0
                            onApplyLessonStep(lesson.steps.first())
                        }
                        .testTag("lesson_card_${lesson.lessonId}"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) MedCyanContainer else MedDeepNavy,
                    border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.title.get(language),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lesson.subtitle.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        }
                        if (done) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = MedVerifiedGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Step Interactive Controller
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MedPanelNavy,
            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${activeStepIdx + 1} / ${activeLesson.steps.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                        color = MedCyan
                    )
                    if (isLessonDone) {
                        Text(
                            text = if (language.isRtl) "مكتمل (يمكن الإعادة)" else "Completed • Replayable",
                            style = MaterialTheme.typography.labelSmall,
                            color = MedVerifiedGreen
                        )
                    }
                }
                Text(
                    text = currentStep.title.get(language),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentStep.explanation.get(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (activeStepIdx > 0) {
                                activeStepIdx -= 1
                                onApplyLessonStep(activeLesson.steps[activeStepIdx])
                            } else {
                                onApplyLessonStep(activeLesson.steps[0])
                            }
                        },
                        border = BorderStroke(1.dp, MedBorderHairline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (activeStepIdx > 0) {
                                if (language.isRtl) "السابق" else "Previous"
                            } else {
                                if (language.isRtl) "تطبيق الخطوة" else "Focus Step"
                            },
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            if (activeStepIdx < activeLesson.steps.lastIndex) {
                                activeStepIdx += 1
                                onApplyLessonStep(activeLesson.steps[activeStepIdx])
                            } else {
                                onCompleteLesson(activeLesson.lessonId)
                                onOpenQuiz()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lesson_next_step_btn")
                    ) {
                        Text(
                            text = if (activeStepIdx < activeLesson.steps.lastIndex) {
                                if (language.isRtl) "الخطوة التالية" else "Next Step"
                            } else {
                                if (language.isRtl) "إنهاء وانتقل للاختبار" else "Complete & Quiz"
                            },
                            color = MedObsidianNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhysiologyQuizPanel(
    correctQuizIds: Set<String>,
    language: AppLanguage,
    onRecordCorrect: (String) -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions = PhysiologyLearningSeed.quizQuestions
    var currentIndex by remember { mutableIntStateOf(0) }
    val q = questions[currentIndex]

    var selectedOption by remember(q.id) { mutableStateOf<Int?>(null) }
    val orderedSelection = remember(q.id) { mutableStateListOf<Int>() }
    val matchedPairs = remember(q.id) { mutableStateListOf<Pair<String, String>>() }
    var activeLeftEn by remember(q.id) { mutableStateOf<String?>(null) }
    var submitted by remember(q.id) { mutableStateOf(false) }
    var isCorrect by remember(q.id) { mutableStateOf(false) }

    // Deterministic non-canonical presentation order for ORDER_EVENTS and MATCH_BILINGUAL_TERMS
    val shuffledOrderIndices = remember(q.id) {
        if (q.sequenceItems.size == 4) listOf(2, 0, 3, 1) else q.sequenceItems.indices.toList().reversed()
    }
    val shuffledRightArabic = remember(q.id) {
        val arList = q.matchPairs.map { it.second }
        if (arList.size == 4) listOf(arList[2], arList[0], arList[3], arList[1]) else arList.reversed()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("physiology_quiz_panel"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quiz Header + Score Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Quiz, contentDescription = null, tint = MedCyan, modifier = Modifier.size(18.dp))
                Text(
                    text = if (language.isRtl) "الاختبار الفسيولوجي التفاعلي" else "Interactive Physiology Quiz",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${correctQuizIds.size}/${questions.size}",
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFamily),
                    color = MedVerifiedGreen
                )
                if (correctQuizIds.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Reset quiz progress",
                        tint = MedTextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onResetProgress)
                    )
                }
            }
        }

        // Question Selector Strip
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            questions.forEachIndexed { idx, item ->
                val solved = item.id in correctQuizIds
                val active = idx == currentIndex
                Surface(
                    modifier = Modifier.clickable { currentIndex = idx },
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        active -> MedCyan
                        solved -> MedVerifiedContainer
                        else -> MedDeepNavy
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            active -> MedCyan
                            solved -> MedVerifiedGreen
                            else -> MedBorderHairline
                        }
                    )
                ) {
                    Text(
                        text = "Q${idx + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                        color = if (active) MedObsidianNavy else if (solved) MedVerifiedGreen else Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Question Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MedPanelNavy,
            border = BorderStroke(1.dp, MedBorderHairline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedObsidianNavy,
                    border = BorderStroke(1.dp, MedTeal.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = q.format.label.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MedTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = q.prompt.get(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Optional Schematic Diagram Prompt (for INTERPRET_DIAGRAM)
                if (q.diagramHint != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MedObsidianNavy,
                        border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = q.diagramHint.get(language),
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = JetBrainsMonoFamily),
                            color = MedCyan,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                when (q.format) {
                    QuizQuestionFormat.SINGLE_CHOICE,
                    QuizQuestionFormat.IDENTIFY_STRUCTURE,
                    QuizQuestionFormat.INTERPRET_DIAGRAM -> {
                        q.options.forEachIndexed { idx, opt ->
                            val picked = selectedOption == idx
                            val borderColor = when {
                                submitted && idx == q.correctOptionIndex -> MedVerifiedGreen
                                submitted && picked && idx != q.correctOptionIndex -> MedAlertRed
                                picked -> MedCyan
                                else -> MedBorderHairline
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !submitted) { selectedOption = idx }
                                    .testTag("quiz_option_$idx"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (picked) MedCyanContainer else MedDeepNavy,
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Text(
                                    text = opt.get(language),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (!submitted) {
                            Button(
                                onClick = {
                                    if (selectedOption != null) {
                                        submitted = true
                                        isCorrect = selectedOption == q.correctOptionIndex
                                        if (isCorrect) onRecordCorrect(q.id)
                                    }
                                },
                                enabled = selectedOption != null,
                                colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_submit_btn")
                            ) {
                                Text(
                                    text = if (language.isRtl) "تحقق من الإجابة" else "Check Answer",
                                    color = MedObsidianNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    QuizQuestionFormat.ORDER_EVENTS -> {
                        Text(
                            text = if (language.isRtl) {
                                "انقر على الخطوات بالترتيب الصحيح (1 إلى ${q.sequenceItems.size}):"
                            } else {
                                "Tap steps in physiological order (1 to ${q.sequenceItems.size}):"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                        shuffledOrderIndices.forEach { canonicalIdx ->
                            val rank = orderedSelection.indexOf(canonicalIdx)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !submitted) {
                                        if (canonicalIdx in orderedSelection) {
                                            orderedSelection.remove(canonicalIdx)
                                        } else {
                                            orderedSelection.add(canonicalIdx)
                                        }
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (rank >= 0) MedCyanContainer else MedDeepNavy,
                                border = BorderStroke(1.dp, if (rank >= 0) MedCyan else MedBorderHairline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = q.sequenceItems[canonicalIdx].get(language),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    if (rank >= 0) {
                                        Text(
                                            text = "#${rank + 1}",
                                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFamily),
                                            color = MedCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        if (!submitted) {
                            Button(
                                onClick = {
                                    submitted = true
                                    isCorrect = orderedSelection.toList() == q.sequenceItems.indices.toList()
                                    if (isCorrect) onRecordCorrect(q.id)
                                },
                                enabled = orderedSelection.size == q.sequenceItems.size,
                                colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (language.isRtl) "تحقق من الترتيب" else "Check Order",
                                    color = MedObsidianNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    QuizQuestionFormat.MATCH_BILINGUAL_TERMS -> {
                        Text(
                            text = if (language.isRtl) {
                                "اختر مصطلحاً إنجليزياً ثم انقر على مقابله العربي:"
                            } else {
                                "Tap an English term, then tap its matching Arabic term:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                q.matchPairs.forEach { (en, _) ->
                                    val alreadyMatched = matchedPairs.any { it.first == en }
                                    val active = activeLeftEn == en
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !submitted && !alreadyMatched) {
                                                activeLeftEn = en
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            alreadyMatched -> MedVerifiedContainer
                                            active -> MedCyanContainer
                                            else -> MedDeepNavy
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (alreadyMatched) MedVerifiedGreen else if (active) MedCyan else MedBorderHairline
                                        )
                                    ) {
                                        Text(
                                            text = en,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                shuffledRightArabic.forEach { ar ->
                                    val alreadyMatched = matchedPairs.any { it.second == ar }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !submitted && !alreadyMatched && activeLeftEn != null) {
                                                val left = activeLeftEn ?: return@clickable
                                                matchedPairs.add(left to ar)
                                                activeLeftEn = null
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (alreadyMatched) MedVerifiedContainer else MedDeepNavy,
                                        border = BorderStroke(1.dp, if (alreadyMatched) MedVerifiedGreen else MedBorderHairline)
                                    ) {
                                        Text(
                                            text = ar,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (!submitted) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        matchedPairs.clear()
                                        activeLeftEn = null
                                    },
                                    border = BorderStroke(1.dp, MedBorderHairline),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (language.isRtl) "إعادة" else "Clear", color = Color.White)
                                }
                                Button(
                                    onClick = {
                                        submitted = true
                                        isCorrect = matchedPairs.toSet() == q.matchPairs.toSet()
                                        if (isCorrect) onRecordCorrect(q.id)
                                    },
                                    enabled = matchedPairs.size == q.matchPairs.size,
                                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (language.isRtl) "تحقق" else "Verify",
                                        color = MedObsidianNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Immediate Explanatory Feedback
                if (submitted) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCorrect) MedVerifiedContainer else MedAlertContainer,
                        border = BorderStroke(1.dp, if (isCorrect) MedVerifiedGreen else MedAlertRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isCorrect) {
                                    if (language.isRtl) "إجابة صحيحة!" else "Correct!"
                                } else {
                                    if (language.isRtl) "راجع التفسير العلمي أدناه" else "Review the scientific explanation below"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isCorrect) MedVerifiedGreen else MedPendingAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = q.explanation.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "${q.source.organization}: ${q.source.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                submitted = false
                                selectedOption = null
                                orderedSelection.clear()
                                matchedPairs.clear()
                                activeLeftEn = null
                            },
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (language.isRtl) "حاول مجدداً" else "Retry", color = Color.White)
                        }
                        if (currentIndex < questions.lastIndex) {
                            Button(
                                onClick = { currentIndex += 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (language.isRtl) "السؤال التالي" else "Next Question",
                                    color = MedObsidianNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
