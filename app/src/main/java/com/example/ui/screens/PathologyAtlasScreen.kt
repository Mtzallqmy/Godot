package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.pathology.DiseaseRecord
import com.example.pathology.PathologySeed
import com.example.pathology.PathologyVisualType
import com.example.pharmacology.PharmacologySeed
import com.example.physiology.engine.PhysiologySystemId
import com.example.ui.components.ReferenceSourceCard
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
import com.example.ui.theme.MedArterialRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedSchematicContainer
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathologyAtlasScreen(
    selectedDiseaseId: String,
    language: AppLanguage,
    onSelectDisease: (String) -> Unit,
    onOpenAnatomyStructure: (String) -> Unit,
    onOpenPhysiologySystem: (PhysiologySystemId) -> Unit,
    onOpenDrugClass: (String) -> Unit,
    onAskAiTutor: (DiseaseRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val diseases = PathologySeed.diseases
    val selectedDisease = diseases.firstOrNull { it.id == selectedDiseaseId } ?: diseases.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pathology_atlas_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header & Disease Selector Ribbon
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Biotech,
                        contentDescription = null,
                        tint = MedCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Column {
                        Text(
                            text = UiStrings.modPathology.get(language),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "مقارنة تفاعلية بين البنية السليمة والتغيرات المرضية مع المصادر العلمية"
                            } else {
                                "Side-by-side Normal vs. Diseased visual comparisons grounded in NIH educational references"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    diseases.forEach { disease ->
                        val active = disease.id == selectedDisease.id
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectDisease(disease.id) }
                                .testTag("pathology_chip_${disease.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) MedCyan else MedPanelNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) disease.arabicName else disease.englishName,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (active) MedObsidianNavy else Color.White,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Disease Overview Banner + Badges
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
                    Text(
                        text = if (language == AppLanguage.ARABIC) selectedDisease.arabicName else selectedDisease.englishName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (language == AppLanguage.ARABIC) selectedDisease.englishName else selectedDisease.arabicName}  •  ${selectedDisease.icdCategory}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MedCyan
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReviewStatusBadge(status = selectedDisease.reviewStatus, language = language)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedSchematicContainer,
                            border = BorderStroke(1.dp, MedSchematicViolet.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = selectedDisease.validityLevel.badgeLabel.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MedSchematicViolet,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedDeepNavy,
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "تاريخ المراجعة: ${selectedDisease.lastReviewedDate}"
                                } else {
                                    "Reviewed: ${selectedDisease.lastReviewedDate}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MedTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Text(
                        text = selectedDisease.overview.get(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    // Cross-Module Deep Links
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenAnatomyStructure(selectedDisease.linkedAnatomyStructureId) },
                            modifier = Modifier.testTag("pathology_open_3d_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy)
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.openIn3DCta.get(language), fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onOpenPhysiologySystem(selectedDisease.linkedPhysiologySystem) },
                            modifier = Modifier.testTag("pathology_open_physiology_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedTeal)
                        ) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = MedTeal, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.openPhysiologyCta.get(language), color = MedTeal, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onAskAiTutor(selectedDisease) },
                            modifier = Modifier.testTag("pathology_ask_ai_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.7f))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.openAiTutorCta.get(language), color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Side-by-Side Normal vs. Diseased Interactive Visual Comparison
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.ARABIC) {
                        "المقارنة البصرية: الحالة السليمة مقابل الحالة المرضية"
                    } else {
                        "Visual Comparison: Normal vs. Diseased Anatomy"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ComparisonVisualCard(
                        title = if (language == AppLanguage.ARABIC) "البنية السليمة" else "Normal Anatomy",
                        summary = selectedDisease.normalStateSummary.get(language),
                        visualType = selectedDisease.visualType,
                        isDiseased = false,
                        accentColor = MedVerifiedGreen,
                        containerTint = MedVerifiedContainer,
                        modifier = Modifier.weight(1f)
                    )
                    ComparisonVisualCard(
                        title = if (language == AppLanguage.ARABIC) "التغير المرضي" else "Pathological State",
                        summary = selectedDisease.diseasedStateSummary.get(language),
                        visualType = selectedDisease.visualType,
                        isDiseased = true,
                        accentColor = MedAlertRed,
                        containerTint = MedAlertContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Step-by-step Pathophysiology + Educational Investigations
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = MedTeal, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "الآلية الإمراضية خطوة بخطوة" else "Step-by-Step Pathophysiology",
                            style = MaterialTheme.typography.titleMedium,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    selectedDisease.pathophysiologySteps.forEach { step ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedDeepNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = step.get(language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.ARABIC) "الفحوصات والوسائل التعليمية المرتبطة" else "Educational Investigation Concepts",
                        style = MaterialTheme.typography.titleSmall,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    selectedDisease.educationalInvestigations.forEach { inv ->
                        Text(
                            text = "• ${inv.get(language)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                    }

                    // Linked Drug Classes
                    val linkedDrugs = selectedDisease.linkedDrugClassIds.mapNotNull { PharmacologySeed.findById(it) }
                    if (linkedDrugs.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "الفئات الدوائية المرتبطة (مختبر الأدوية)" else "Related Drug Classes (Pharmacology Lab)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            linkedDrugs.forEach { drug ->
                                Surface(
                                    modifier = Modifier
                                        .clickable { onOpenDrugClass(drug.id) }
                                        .testTag("pathology_drug_link_${drug.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MedCyanContainer,
                                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) drug.arabicName else drug.englishName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Clinical Safety Notice & Verified References
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedAlertContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MedAlertRed.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedDisease.keyClinicalConcepts.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Text(
                    text = UiStrings.labelSources.get(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = MedCyan,
                    fontWeight = FontWeight.Bold
                )
                selectedDisease.sources.forEach { src ->
                    ReferenceSourceCard(source = src, language = language)
                }
                SafetyDisclaimerCard(language = language)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ComparisonVisualCard(
    title: String,
    summary: String,
    visualType: PathologyVisualType,
    isDiseased: Boolean,
    accentColor: Color,
    containerTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = containerTint
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp)
                    .background(MedObsidianNavy, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                PathologySchematicCanvas(
                    visualType = visualType,
                    isDiseased = isDiseased,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = MedTextSecondary
            )
        }
    }
}

@Composable
private fun PathologySchematicCanvas(
    visualType: PathologyVisualType,
    isDiseased: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = minOf(size.width, size.height) * 0.40f

        when (visualType) {
            PathologyVisualType.AORTIC_STENOSIS -> {
                // Outer aortic ring
                drawCircle(color = MedArterialRed.copy(alpha = 0.28f), radius = r, center = Offset(cx, cy))
                drawCircle(color = MedArterialRed, radius = r, center = Offset(cx, cy), style = Stroke(width = 6f))
                // 3 semilunar leaflets
                val openRadius = if (isDiseased) r * 0.22f else r * 0.62f
                for (i in 0 until 3) {
                    val angle = (i * 120.0 - 90.0) * PI / 180.0
                    val nextAngle = ((i + 1) * 120.0 - 90.0) * PI / 180.0
                    val p1 = Offset(cx + cos(angle).toFloat() * r, cy + sin(angle).toFloat() * r)
                    val p2 = Offset(cx + cos(nextAngle).toFloat() * r, cy + sin(nextAngle).toFloat() * r)
                    val midAngle = (angle + nextAngle) / 2.0
                    val ctrl = Offset(cx + cos(midAngle).toFloat() * openRadius, cy + sin(midAngle).toFloat() * openRadius)
                    val path = Path().apply {
                        moveTo(p1.x, p1.y)
                        quadraticTo(ctrl.x, ctrl.y, p2.x, p2.y)
                    }
                    drawPath(
                        path = path,
                        color = if (isDiseased) MedPendingAmber else MedCyan,
                        style = Stroke(width = if (isDiseased) 9f else 5f, cap = StrokeCap.Round)
                    )
                    if (isDiseased) {
                        // Calcific nodules on thickened cusps
                        drawCircle(color = Color(0xFFFDE68A), radius = 6f, center = ctrl)
                    }
                }
            }

            PathologyVisualType.CORONARY_ATHEROSCLEROSIS -> {
                // Arterial wall cross-section
                drawCircle(color = MedArterialRed.copy(alpha = 0.25f), radius = r, center = Offset(cx, cy))
                drawCircle(color = MedArterialRed, radius = r, center = Offset(cx, cy), style = Stroke(width = 8f))
                if (!isDiseased) {
                    drawCircle(color = MedArterialRed.copy(alpha = 0.65f), radius = r * 0.72f, center = Offset(cx, cy))
                } else {
                    // Eccentric yellow lipid plaque narrowing lumen
                    drawCircle(color = MedPendingAmber.copy(alpha = 0.85f), radius = r * 0.74f, center = Offset(cx, cy))
                    drawCircle(color = MedArterialRed.copy(alpha = 0.85f), radius = r * 0.30f, center = Offset(cx + r * 0.28f, cy))
                    drawCircle(color = Color(0xFFFDE68A), radius = r * 0.16f, center = Offset(cx - r * 0.25f, cy))
                }
            }

            PathologyVisualType.BRONCHIAL_ASTHMA -> {
                val muscleWidth = if (isDiseased) 14f else 6f
                drawCircle(
                    color = if (isDiseased) MedAlertRed else MedTeal,
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = muscleWidth)
                )
                val lumenR = if (isDiseased) r * 0.32f else r * 0.72f
                drawCircle(
                    color = if (isDiseased) MedPendingAmber.copy(alpha = 0.55f) else MedCyan.copy(alpha = 0.20f),
                    radius = r * 0.84f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = MedObsidianNavy,
                    radius = lumenR,
                    center = Offset(cx, cy)
                )
                if (isDiseased) {
                    drawCircle(color = Color(0xFFFDE68A).copy(alpha = 0.8f), radius = lumenR * 0.45f, center = Offset(cx + 4f, cy - 3f))
                }
            }

            PathologyVisualType.GLOMERULAR_INJURY -> {
                // Capillary top, basement membrane middle, Bowman's space bottom
                val yMembrane = cy
                drawLine(
                    color = if (isDiseased) MedPendingAmber else MedCyan,
                    start = Offset(size.width * 0.10f, yMembrane),
                    end = Offset(size.width * 0.90f, yMembrane),
                    strokeWidth = if (isDiseased) 11f else 6f,
                    cap = StrokeCap.Round
                )
                // Podocyte foot processes
                val slitCount = 5
                for (i in 0 until slitCount) {
                    val x = size.width * (0.18f + i * 0.16f)
                    drawRoundRect(
                        color = if (isDiseased) MedAlertRed.copy(alpha = 0.8f) else MedTeal,
                        topLeft = Offset(x - 10f, yMembrane + 6f),
                        size = Size(if (isDiseased) 26f else 18f, if (isDiseased) 10f else 18f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
                // Albumin molecules above vs leaking below
                drawCircle(color = Color(0xFFFBBF24), radius = 8f, center = Offset(cx - 24f, cy - 26f))
                drawCircle(color = Color(0xFFFBBF24), radius = 8f, center = Offset(cx + 18f, cy - 22f))
                if (isDiseased) {
                    drawCircle(color = Color(0xFFFBBF24), radius = 8f, center = Offset(cx - 8f, cy + 36f))
                    drawCircle(color = Color(0xFFFBBF24), radius = 8f, center = Offset(cx + 22f, cy + 40f))
                }
            }

            PathologyVisualType.PEPTIC_ULCER_GERD -> {
                // Mucus protective layer + gastric epithelial wall
                val wallTop = cy + 6f
                drawRoundRect(
                    color = MedMyocardiumTint,
                    topLeft = Offset(size.width * 0.08f, wallTop),
                    size = Size(size.width * 0.84f, size.height * 0.36f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                if (!isDiseased) {
                    // Thick intact mucus-bicarbonate shield
                    drawRoundRect(
                        color = MedTeal.copy(alpha = 0.75f),
                        topLeft = Offset(size.width * 0.08f, wallTop - 16f),
                        size = Size(size.width * 0.84f, 14f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                } else {
                    // Breached mucus + focal ulcer crater
                    drawRoundRect(
                        color = MedTeal.copy(alpha = 0.45f),
                        topLeft = Offset(size.width * 0.08f, wallTop - 10f),
                        size = Size(size.width * 0.25f, 8f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        color = MedTeal.copy(alpha = 0.45f),
                        topLeft = Offset(size.width * 0.67f, wallTop - 10f),
                        size = Size(size.width * 0.25f, 8f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Ulcer crater
                    drawCircle(color = MedObsidianNavy, radius = 22f, center = Offset(cx, wallTop + 6f))
                    drawCircle(color = MedAlertRed, radius = 22f, center = Offset(cx, wallTop + 6f), style = Stroke(width = 4f))
                }
                // H+ acid ions in gastric lumen
                for (i in 0..3) {
                    drawCircle(
                        color = MedPendingAmber,
                        radius = 4f,
                        center = Offset(size.width * (0.24f + i * 0.17f), cy - 28f)
                    )
                }
            }

            PathologyVisualType.DEMYELINATION_MS -> {
                // Central axon line
                drawLine(
                    color = MedCyan,
                    start = Offset(size.width * 0.08f, cy),
                    end = Offset(size.width * 0.92f, cy),
                    strokeWidth = 7f,
                    cap = StrokeCap.Round
                )
                // Myelin sheath segments
                val segments = listOf(0.14f to 0.34f, 0.40f to 0.60f, 0.66f to 0.86f)
                segments.forEachIndexed { idx, (startF, endF) ->
                    val broken = isDiseased && idx == 1
                    if (!broken) {
                        drawRoundRect(
                            color = MedTeal.copy(alpha = 0.85f),
                            topLeft = Offset(size.width * startF, cy - 16f),
                            size = Size(size.width * (endF - startF), 32f),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                    } else {
                        // Patchy demyelinated gap with leaking current arcs
                        drawRoundRect(
                            color = MedAlertRed.copy(alpha = 0.55f),
                            topLeft = Offset(size.width * startF, cy - 12f),
                            size = Size(size.width * (endF - startF), 24f),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 3f)
                        )
                        drawCircle(color = MedPendingAmber, radius = 5f, center = Offset(cx, cy - 22f))
                        drawCircle(color = MedPendingAmber, radius = 5f, center = Offset(cx, cy + 22f))
                    }
                }
            }
        }
    }
}

private val MedMyocardiumTint = Color(0xFFC24658)
