package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.academy.LearningAcademySeed
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.data.seed.CardiovascularAnatomySeed
import com.example.data.seed.MedicalDictionarySeed
import com.example.firebase.LearningActivityRecord
import com.example.firebase.QuizAttemptRecord
import com.example.firebase.UserRole
import com.example.ui.theme.JetBrainsMonoFamily
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
fun MyLearningScreen(
    language: AppLanguage,
    isSignedIn: Boolean,
    userDisplayName: String?,
    userEmail: String?,
    userRole: UserRole,
    completedLessonIds: Set<String>,
    savedStructureIds: Set<String>,
    bookmarkedTermIds: Set<String>,
    quizAttempts: List<QuizAttemptRecord>,
    activityLog: List<LearningActivityRecord>,
    statusMessage: String?,
    onGoogleSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onOpenStructure: (String) -> Unit,
    onOpenDictionary: () -> Unit,
    onOpenAcademy: () -> Unit,
    onBuildExportJson: () -> String,
    onResetLocalProgress: () -> Unit,
    onDeleteCloudData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    var exportedJson by remember { mutableStateOf<String?>(null) }
    var showDeleteCloudConfirm by remember { mutableStateOf(false) }

    val totalAcademyLessons = LearningAcademySeed.courses.sumOf { it.lessons.size }
    val nextRecommendedLessons = LearningAcademySeed.courses
        .flatMap { it.lessons }
        .filter { it.id !in completedLessonIds }
        .take(3)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_learning_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Account & Cloud Sync Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(20.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, if (isSignedIn) MedVerifiedGreen.copy(alpha = 0.6f) else MedCyan.copy(alpha = 0.45f))
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
                            Icon(
                                imageVector = if (isSignedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (isSignedIn) MedVerifiedGreen else MedCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = UiStrings.modMyLearning.get(language),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isSignedIn) {
                                        "${userDisplayName ?: "Learner"} (${userEmail ?: ""}) • ${userRole.name}"
                                    } else {
                                        if (language == AppLanguage.ARABIC) {
                                            "وضع الزائر (محلي على الجهاز) — سجّل الدخول للمزامنة السحابية الآمنة"
                                        } else {
                                            "Guest Local Mode — Sign in with Google for Cloud Firestore sync"
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSignedIn) MedVerifiedGreen else MedTextSecondary
                                )
                            }
                        }
                    }

                    if (!isSignedIn) {
                        Button(
                            onClick = onGoogleSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("my_learning_google_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (language == AppLanguage.ARABIC) "تسجيل الدخول بحساب Google للمزامنة السحابية" else "Sign in with Google to Sync Learning",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSignOutClick,
                            modifier = Modifier.testTag("my_learning_signout_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MedTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = if (language == AppLanguage.ARABIC) "تسجيل الخروج" else "Sign Out",
                                color = Color.White
                            )
                        }
                    }

                    if (!statusMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MedCyanContainer.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = statusMessage,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Real Recorded Learning Metrics (Zero Fake Numbers)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RealMetricTile(
                    value = "${completedLessonIds.size}/$totalAcademyLessons",
                    label = if (language == AppLanguage.ARABIC) "دروس مكتملة" else "Lessons Done",
                    accent = MedTeal,
                    modifier = Modifier.weight(1f)
                )
                RealMetricTile(
                    value = "${savedStructureIds.size}",
                    label = if (language == AppLanguage.ARABIC) "بنى محفوظة" else "Saved 3D",
                    accent = MedCyan,
                    modifier = Modifier.weight(1f)
                )
                RealMetricTile(
                    value = "${bookmarkedTermIds.size}",
                    label = if (language == AppLanguage.ARABIC) "مصطلحات" else "Saved Terms",
                    accent = MedVerifiedGreen,
                    modifier = Modifier.weight(1f)
                )
                RealMetricTile(
                    value = "${quizAttempts.size}",
                    label = if (language == AppLanguage.ARABIC) "محاولات سحابية" else "Cloud Quizzes",
                    accent = MedCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Saved 3D Anatomical Structures & Bookmarked Medical Terms
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(18.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedBorderHairline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = MedCyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "البنى التشريحية والمصطلحات المحفوظة" else "Saved 3D Structures & Bookmarked Terms",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val savedStructures = CardiovascularAnatomySeed.cardiovascularStructures.filter { it.id in savedStructureIds }
                    if (savedStructures.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "لم تحفظ أي بنية ثلاثية الأبعاد بعد. اضغط على أيقونة الحفظ داخل لوحة تفاصيل التشريح." else "No 3D structures saved yet. Tap the bookmark icon inside the 3D Anatomy detail panel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            savedStructures.forEach { s ->
                                Surface(
                                    modifier = Modifier.clickable { onOpenStructure(s.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MedCyanContainer,
                                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) s.arabicName else s.englishName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    val savedTerms = MedicalDictionarySeed.curatedTerms.filter { it.id in bookmarkedTermIds }
                    if (savedTerms.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            savedTerms.forEach { term ->
                                Surface(
                                    modifier = Modifier.clickable(onClick = onOpenDictionary),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MedDeepNavy,
                                    border = BorderStroke(1.dp, MedTeal.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) term.arabicName else term.englishName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MedTeal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Quiz Attempt History & Recent Educational Activity
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(18.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedBorderHairline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MedTeal, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "سجل الاختبارات والنشاط التعليمي" else "Quiz History & Recent Activity",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (quizAttempts.isEmpty() && activityLog.isEmpty()) {
                        Text(
                            text = if (isSignedIn) {
                                if (language == AppLanguage.ARABIC) "لا توجد محاولات مسجلة بعد. أكمل اختباراً أو درساً ليظهر هنا." else "No recorded attempts yet. Complete an assessment or lesson to see your real history here."
                            } else {
                                if (language == AppLanguage.ARABIC) "سجّل الدخول بحساب Google لتتبع سجل الاختبارات والنشاط عبر الأجهزة." else "Sign in with Google to persist cross-device quiz attempts and study logs."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                    } else {
                        quizAttempts.take(5).forEach { attempt ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MedDeepNavy,
                                border = BorderStroke(1.dp, MedBorderHairline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${attempt.subject} (${attempt.difficulty})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${attempt.score}/${attempt.totalQuestions}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MedCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        activityLog.take(5).forEach { act ->
                            Text(
                                text = "• [${act.activityType}] ${if (language == AppLanguage.ARABIC) act.titleAr else act.titleEn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 5. Recommended Next Lessons
        if (nextRecommendedLessons.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 960.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.School, contentDescription = null, tint = MedCyan, modifier = Modifier.size(20.dp))
                            Text(
                                text = if (language == AppLanguage.ARABIC) "الدروس المقترحة التالية" else "Recommended Next Lessons",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        nextRecommendedLessons.forEach { lesson ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = onOpenAcademy),
                                shape = RoundedCornerShape(10.dp),
                                color = MedDeepNavy,
                                border = BorderStroke(1.dp, MedBorderHairline)
                            ) {
                                Text(
                                    text = "${lesson.title.get(language)} (${lesson.durationMinutes} min)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Privacy Controls: Export JSON, Reset Local Progress, Delete Cloud Data
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(18.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedBorderHairline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "الخصوصية والتحكم بالبيانات" else "Data Export & Privacy Controls",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { exportedJson = onBuildExportJson() },
                            modifier = Modifier.testTag("my_learning_export_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MedCyan)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(if (language == AppLanguage.ARABIC) "تصدير سجل التعلم JSON" else "Export JSON Summary", color = Color.White)
                        }
                        OutlinedButton(
                            onClick = onResetLocalProgress,
                            modifier = Modifier.testTag("my_learning_reset_local_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MedTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(if (language == AppLanguage.ARABIC) "إعادة ضبط التقدم المحلي" else "Reset Local Progress", color = MedTextSecondary)
                        }
                        if (isSignedIn) {
                            OutlinedButton(
                                onClick = { showDeleteCloudConfirm = true },
                                modifier = Modifier.testTag("my_learning_delete_cloud_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MedAlertRed)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MedAlertRed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.size(6.dp))
                                Text(if (language == AppLanguage.ARABIC) "حذف بياناتي السحابية" else "Delete Cloud Data", color = MedAlertRed)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(14.dp)) }
    }

    if (exportedJson != null) {
        AlertDialog(
            onDismissRequest = { exportedJson = null },
            containerColor = MedPanelNavy,
            title = {
                Text(
                    text = if (language == AppLanguage.ARABIC) "سجل التعلم (JSON)" else "Learning Record Export (JSON)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = exportedJson.orEmpty(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = JetBrainsMonoFamily),
                    color = MedCyan
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(exportedJson.orEmpty()))
                        exportedJson = null
                    }
                ) {
                    Text(if (language == AppLanguage.ARABIC) "نسخ وإغلاق" else "Copy & Close", color = MedTeal)
                }
            }
        )
    }

    if (showDeleteCloudConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteCloudConfirm = false },
            containerColor = MedAlertContainer,
            title = {
                Text(
                    text = if (language == AppLanguage.ARABIC) "تأكيد حذف البيانات السحابية" else "Delete Cloud Learning Data?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.ARABIC) {
                        "سيؤدي هذا إلى حذف ملف التعلم وسجل الاختبارات والنشاط المخزّن في السحابة بشكل نهائي."
                    } else {
                        "This permanently deletes your cloud learning profile, quiz attempts, and activity logs from Firestore."
                    },
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteCloudConfirm = false
                        onDeleteCloudData()
                    }
                ) {
                    Text(if (language == AppLanguage.ARABIC) "حذف نهائي" else "Delete Permanently", color = MedAlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCloudConfirm = false }) {
                    Text(if (language == AppLanguage.ARABIC) "إلغاء" else "Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun RealMetricTile(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MedTextSecondary
            )
        }
    }
}
