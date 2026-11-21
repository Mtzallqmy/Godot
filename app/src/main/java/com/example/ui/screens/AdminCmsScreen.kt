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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.academy.LearningAcademySeed
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.data.seed.CardiovascularAnatomySeed
import com.example.data.seed.MedicalDictionarySeed
import com.example.firebase.CmsContentRecord
import com.example.firebase.EditorialAuditLog
import com.example.firebase.EditorialStatus
import com.example.firebase.UserRole
import com.example.pathology.PathologySeed
import com.example.pharmacology.PharmacologySeed
import com.example.physiology.engine.PhysiologySystemId
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedPendingContainer
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminCmsScreen(
    language: AppLanguage,
    isSignedIn: Boolean,
    userEmail: String?,
    userRole: UserRole,
    cmsRecords: List<CmsContentRecord>,
    auditLogs: List<EditorialAuditLog>,
    cmsStatusMessage: String?,
    onGoogleSignInClick: () -> Unit,
    onCreateDraft: (titleEn: String, titleAr: String, contentType: String, systemId: String, summaryEn: String, summaryAr: String, references: String) -> Unit,
    onTransitionStatus: (entry: CmsContentRecord, targetStatus: EditorialStatus, note: String?) -> Unit,
    onOpenProvenance: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titleEn by remember { mutableStateOf("") }
    var titleAr by remember { mutableStateOf("") }
    var contentType by remember { mutableStateOf("PATHOLOGY") }
    var systemId by remember { mutableStateOf("cardiovascular") }
    var summaryEn by remember { mutableStateOf("") }
    var summaryAr by remember { mutableStateOf("") }
    var references by remember { mutableStateOf("NIH NHLBI / OpenStax Anatomy & Physiology 2e") }

    val contentTypes = listOf("ANATOMY", "PHYSIOLOGY", "PATHOLOGY", "PHARMACOLOGY", "TERMINOLOGY", "LESSON")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_cms_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Governance Header & Server-Side RBAC Verification Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(20.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MedCyan, modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = UiStrings.navAdmin.get(language),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "إدارة المحتوى الطبي والمراجعة العلمية المحمية بصلاحيات Firestore الخادمية"
                                } else {
                                    "Role-Protected Medical CMS, Editorial Review Queue & Audit Governance"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (userRole.canEditCms) MedVerifiedContainer else MedPendingContainer,
                            border = BorderStroke(1.dp, if (userRole.canEditCms) MedVerifiedGreen else MedPendingAmber)
                        ) {
                            Text(
                                text = userRole.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (userRole.canEditCms) MedVerifiedGreen else MedPendingAmber,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    if (!isSignedIn) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "يتطلب الوصول إلى لوحة الإدارة والمحتوى الطبي تسجيل الدخول الموثق عبر Google. تتحقق قواعد Firestore الخادمية من صلاحياتك تلقائياً."
                            } else {
                                "Authenticated Google Sign-In is required to verify Editor, Medical Reviewer, or Administrator privileges via server-enforced Firestore Security Rules."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                        Button(
                            onClick = onGoogleSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_google_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (language == AppLanguage.ARABIC) "تسجيل الدخول بحساب Google" else "Sign in with Google",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (!userRole.canEditCms) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedAlertContainer.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MedAlertRed.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MedAlertRed)
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) "صلاحية طالب فقط (${userEmail ?: ""})" else "Student Access Only (${userEmail ?: ""})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) {
                                            "تم حظر إنشاء أو نشر المحتوى الطبي خادمياً لحسابات الطلاب. يمكنك استعراض الكتالوج المنشور فقط."
                                        } else {
                                            "Medical CMS draft creation, review, and publishing are restricted server-side to verified Editors, Medical Reviewers, and Administrators."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MedTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (!cmsStatusMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MedCyanContainer.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = cmsStatusMessage,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Real Platform Content Inventory (Zero Fabricated Metrics)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "جرد المحتوى العلمي الفعلي (بدون أرقام وهمية)" else "Real Verified Content Inventory",
                            style = MaterialTheme.typography.titleMedium,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = onOpenProvenance,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MedTeal)
                        ) {
                            Text(UiStrings.toolProvenance.get(language), color = MedTeal, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    val inventory = listOf(
                        (if (language == AppLanguage.ARABIC) "بنى تشريحية 3D" else "3D Structures") to CardiovascularAnatomySeed.cardiovascularStructures.size,
                        (if (language == AppLanguage.ARABIC) "أنظمة الفسيولوجيا" else "Physiology Systems") to PhysiologySystemId.entries.size,
                        (if (language == AppLanguage.ARABIC) "سجلات الأمراض" else "Pathology Records") to PathologySeed.diseases.size,
                        (if (language == AppLanguage.ARABIC) "فئات الأدوية" else "Drug Classes") to PharmacologySeed.drugClasses.size,
                        (if (language == AppLanguage.ARABIC) "مصطلحات القاموس" else "Dictionary Terms") to MedicalDictionarySeed.curatedTerms.size,
                        (if (language == AppLanguage.ARABIC) "دورات الأكاديمية" else "Academy Courses") to LearningAcademySeed.courses.size,
                        (if (language == AppLanguage.ARABIC) "أسئلة الاختبارات" else "Assessment Items") to LearningAcademySeed.assessmentQuestions.size,
                        (if (language == AppLanguage.ARABIC) "سجلات CMS السحابية" else "Cloud CMS Docs") to cmsRecords.size
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        inventory.forEach { (label, count) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MedDeepNavy,
                                border = BorderStroke(1.dp, MedBorderHairline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "$count", style = MaterialTheme.typography.titleSmall, color = MedCyan, fontWeight = FontWeight.Bold)
                                    Text(text = label, style = MaterialTheme.typography.labelSmall, color = MedTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Medical CMS Bilingual Draft Editor (Editors / Reviewers / Admins Only)
        if (isSignedIn && userRole.canEditCms) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 960.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedTeal.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = MedTeal)
                            Text(
                                text = if (language == AppLanguage.ARABIC) "إنشاء مسودة محتوى طبي ثنائي اللغة (DRAFT)" else "Create Bilingual Medical CMS Draft",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            contentTypes.forEach { ct ->
                                val active = contentType == ct
                                Surface(
                                    modifier = Modifier.clickable { contentType = ct },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (active) MedTeal else MedDeepNavy,
                                    border = BorderStroke(1.dp, if (active) MedTeal else MedBorderHairline)
                                ) {
                                    Text(
                                        text = ct,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (active) MedObsidianNavy else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        CmsField(label = "English Title (3–160 chars)", value = titleEn, onValueChange = { titleEn = it }, tag = "cms_title_en")
                        CmsField(label = "العنوان بالعربية (3–160 حرفاً)", value = titleAr, onValueChange = { titleAr = it }, tag = "cms_title_ar")
                        CmsField(label = "Body System ID (e.g., cardiovascular, respiratory)", value = systemId, onValueChange = { systemId = it }, tag = "cms_system_id")
                        CmsField(label = "English Educational Summary (min 10 chars)", value = summaryEn, onValueChange = { summaryEn = it }, singleLine = false, tag = "cms_summary_en")
                        CmsField(label = "الملخص العلمي بالعربية (10 أحرف فأكثر)", value = summaryAr, onValueChange = { summaryAr = it }, singleLine = false, tag = "cms_summary_ar")
                        CmsField(label = "Scientific References & Attribution", value = references, onValueChange = { references = it }, tag = "cms_references")

                        Button(
                            onClick = {
                                onCreateDraft(titleEn, titleAr, contentType, systemId, summaryEn, summaryAr, references)
                                titleEn = ""
                                titleAr = ""
                                summaryEn = ""
                                summaryAr = ""
                            },
                            enabled = titleEn.trim().length >= 3 && titleAr.trim().length >= 3 && summaryEn.trim().length >= 10 && summaryAr.trim().length >= 10 && references.trim().length >= 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cms_create_draft_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedTeal, contentColor = MedObsidianNavy)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "حفظ كمسودة (DRAFT) في Firestore" else "Save Bilingual Draft to Firestore",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Editorial Review Queue & State Machine Actions
        if (isSignedIn) {
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
                            Icon(Icons.Default.GppGood, contentDescription = null, tint = MedCyan)
                            Text(
                                text = if (language == AppLanguage.ARABIC) "قائمة المراجعة العلمية والنشر (${cmsRecords.size})" else "Editorial Review Queue (${cmsRecords.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (cmsRecords.isEmpty()) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "لا توجد سجلات CMS في السحابة بعد. أنشئ أول مسودة أعلاه لتجربة دورة المراجعة الكاملة."
                                } else {
                                    "No cloud CMS documents yet. Create a bilingual draft above to run the DRAFT → PENDING_REVIEW → APPROVED → PUBLISHED workflow."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        } else {
                            cmsRecords.forEach { entry ->
                                val status = EditorialStatus.fromRaw(entry.status)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MedDeepNavy,
                                    border = BorderStroke(1.dp, MedBorderHairline),
                                    modifier = Modifier.fillMaxWidth()
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
                                                text = if (language == AppLanguage.ARABIC) entry.titleAr else entry.titleEn,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (status == EditorialStatus.PUBLISHED) MedVerifiedContainer else MedCyanContainer
                                            ) {
                                                Text(
                                                    text = "${status.name} v${entry.version}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (status == EditorialStatus.PUBLISHED) MedVerifiedGreen else MedCyan,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${entry.contentType} • ${entry.systemId} • ${entry.authorName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MedTeal
                                        )
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) entry.summaryAr else entry.summaryEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MedTextSecondary
                                        )
                                        Text(
                                            text = "Ref: ${entry.scientificReferences}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MedCyan
                                        )

                                        // Role-enforced Editorial Transition Buttons
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (userRole.canEditCms && status in setOf(EditorialStatus.DRAFT, EditorialStatus.NEEDS_UPDATE)) {
                                                OutlinedButton(
                                                    onClick = { onTransitionStatus(entry, EditorialStatus.PENDING_REVIEW, "Submitted for scientific review") },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(1.dp, MedCyan)
                                                ) {
                                                    Text(if (language == AppLanguage.ARABIC) "إرسال للمراجعة" else "Submit for Review", color = MedCyan)
                                                }
                                            }
                                            if (userRole.canReviewCms && status == EditorialStatus.PENDING_REVIEW) {
                                                Button(
                                                    onClick = { onTransitionStatus(entry, EditorialStatus.APPROVED, "Scientifically approved") },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MedVerifiedGreen, contentColor = MedObsidianNavy)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                                                    Spacer(Modifier.size(4.dp))
                                                    Text(if (language == AppLanguage.ARABIC) "اعتماد علمي" else "Approve", fontWeight = FontWeight.Bold)
                                                }
                                                OutlinedButton(
                                                    onClick = { onTransitionStatus(entry, EditorialStatus.NEEDS_UPDATE, "Revision requested by reviewer") },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(1.dp, MedPendingAmber)
                                                ) {
                                                    Text(if (language == AppLanguage.ARABIC) "طلب تعديل" else "Request Update", color = MedPendingAmber)
                                                }
                                            }
                                            if (userRole.canPublishCms && status == EditorialStatus.APPROVED) {
                                                Button(
                                                    onClick = { onTransitionStatus(entry, EditorialStatus.PUBLISHED, "Published to platform") },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                                                ) {
                                                    Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(15.dp))
                                                    Spacer(Modifier.size(4.dp))
                                                    Text(if (language == AppLanguage.ARABIC) "نشر رسمي" else "Publish", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            if (userRole.canPublishCms && status != EditorialStatus.ARCHIVED) {
                                                OutlinedButton(
                                                    onClick = { onTransitionStatus(entry, EditorialStatus.ARCHIVED, "Archived by administrator") },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(1.dp, MedBorderHairline)
                                                ) {
                                                    Text(if (language == AppLanguage.ARABIC) "أرشفة" else "Archive", color = MedTextSecondary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Append-Only Editorial Audit Log (Reviewers & Admins)
        if (isSignedIn && userRole.canReviewCms) {
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
                        Text(
                            text = if (language == AppLanguage.ARABIC) "سجل التدقيق التحريري (${auditLogs.size})" else "Append-Only Editorial Audit Log (${auditLogs.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                        if (auditLogs.isEmpty()) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "لا توجد أحداث تدقيق بعد." else "No editorial audit events recorded yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        } else {
                            auditLogs.take(10).forEach { log ->
                                Text(
                                    text = "• [${log.action} → ${log.newStatus}] ${log.actorName} (${log.targetEntryId.take(8)}): ${log.note ?: ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(14.dp)) }
    }
}

@Composable
private fun CmsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    tag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        label = { Text(label, style = MaterialTheme.typography.labelSmall, color = MedTextSecondary) },
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedCyan,
            unfocusedBorderColor = MedBorderHairline,
            focusedContainerColor = MedDeepNavy,
            unfocusedContainerColor = MedDeepNavy,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
