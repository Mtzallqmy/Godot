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
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.pathology.PathologySeed
import com.example.pharmacology.DrugClassRecord
import com.example.pharmacology.PharmacologySeed
import com.example.pharmacology.ReceptorVisualType
import com.example.physiology.engine.PhysiologySystemId
import com.example.ui.components.ReferenceSourceCard
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedElectricBlue
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedSchematicContainer
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PharmacologyLabScreen(
    selectedDrugClassId: String,
    language: AppLanguage,
    onSelectDrugClass: (String) -> Unit,
    onOpenPhysiologySystem: (PhysiologySystemId) -> Unit,
    onOpenDisease: (String) -> Unit,
    onAskAiTutor: (DrugClassRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val drugClasses = PharmacologySeed.drugClasses
    val selected = drugClasses.firstOrNull { it.id == selectedDrugClassId } ?: drugClasses.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pharmacology_lab_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header & Drug Class Ribbon
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
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = MedTeal,
                        modifier = Modifier.size(26.dp)
                    )
                    Column {
                        Text(
                            text = UiStrings.modPharmacology.get(language),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "آليات العمل الدوائي والمستقبلات الجزيئية موثقة بمراجع DailyMed وRxNorm"
                            } else {
                                "Molecular targets, receptor binding schematics & DailyMed / RxNorm educational profiles"
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
                    drugClasses.forEach { drug ->
                        val active = drug.id == selected.id
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectDrugClass(drug.id) }
                                .testTag("pharmacology_chip_${drug.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) MedTeal else MedPanelNavy,
                            border = BorderStroke(1.dp, if (active) MedTeal else MedBorderHairline)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    drug.prototypeGenericAr.substringBefore(" (")
                                } else {
                                    drug.prototypeGenericEn.substringBefore(" (")
                                },
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

        // Drug Class Profile Card + Receptor Diagram
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(20.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedTeal.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) selected.arabicName else selected.englishName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) {
                            "${selected.englishName}  •  ${selected.prototypeGenericAr}"
                        } else {
                            "${selected.arabicName}  •  ${selected.prototypeGenericEn}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MedTeal
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReviewStatusBadge(status = selected.reviewStatus, language = language)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedObsidianNavy,
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Text(
                                text = selected.rxNormReferenceConcept,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                color = MedCyan,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedSchematicContainer,
                            border = BorderStroke(1.dp, MedSchematicViolet.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = selected.labelReferenceDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MedSchematicViolet,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Interactive Receptor / Molecular Target Schematic Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .background(MedObsidianNavy, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        ReceptorMechanismCanvas(
                            visualType = selected.visualType,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd),
                            shape = RoundedCornerShape(8.dp),
                            color = MedDeepNavy.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Text(
                                text = selected.validityLevel.badgeLabel.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = if (language == AppLanguage.ARABIC) "الهدف الجزيئي:" else "Molecular Target:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selected.molecularTarget.get(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Text(
                        text = if (language == AppLanguage.ARABIC) "آلية العمل الدوائي:" else "Mechanism of Action:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selected.mechanismOfAction.get(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenPhysiologySystem(selected.linkedPhysiologySystem) },
                            modifier = Modifier.testTag("pharmacology_open_physiology_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedTeal, contentColor = MedObsidianNavy)
                        ) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.openPhysiologyCta.get(language), fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onAskAiTutor(selected) },
                            modifier = Modifier.testTag("pharmacology_ask_ai_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MedCyan)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(UiStrings.openAiTutorCta.get(language), color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Stepwise Molecular Cascade + Pharmacodynamic Effects + Linked Pathology
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
                        text = if (language == AppLanguage.ARABIC) "تسلسل التأثير الخلوي والديناميكية الدوائية" else "Molecular Cascade & Pharmacodynamic Effects",
                        style = MaterialTheme.typography.titleMedium,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    selected.mechanismSteps.forEach { step ->
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

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MedCyanContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "الأثر الفسيولوجي العام" else "Net Physiological Effect",
                                style = MaterialTheme.typography.labelMedium,
                                color = MedCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selected.pharmacodynamicEffects.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }

                    val linkedDiseases = selected.linkedDiseaseIds.mapNotNull { PathologySeed.findById(it) }
                    if (linkedDiseases.isNotEmpty()) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "الحالات المرضية المرتبطة (أطلس الأمراض)" else "Linked Conditions in Pathology Atlas",
                            style = MaterialTheme.typography.titleSmall,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            linkedDiseases.forEach { d ->
                                Surface(
                                    modifier = Modifier
                                        .clickable { onOpenDisease(d.id) }
                                        .testTag("pharmacology_disease_link_${d.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MedDeepNavy,
                                    border = BorderStroke(1.dp, MedTeal.copy(alpha = 0.6f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Biotech, contentDescription = null, tint = MedTeal, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) d.arabicName else d.englishName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Educational Safety & Contraindications + Verified References
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MedAlertContainer.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, MedAlertRed.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = MedPendingAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "السلامة وموانع الاستعمال (للتعليم فقط — دون جرعات فردية)"
                                } else {
                                    "Safety & Contraindications (Educational Only — No Personal Dosing)"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = MedPendingAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selected.safetyAndContraindications.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = UiStrings.labelSources.get(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = MedCyan,
                    fontWeight = FontWeight.Bold
                )
                selected.sources.forEach { src ->
                    ReferenceSourceCard(source = src, language = language)
                }
                SafetyDisclaimerCard(language = language)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ReceptorMechanismCanvas(
    visualType: ReceptorVisualType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val membraneY = h * 0.56f

        // Phospholipid bilayer membrane baseline
        drawLine(
            color = MedBorderHairline,
            start = Offset(w * 0.04f, membraneY - 8f),
            end = Offset(w * 0.96f, membraneY - 8f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = MedBorderHairline,
            start = Offset(w * 0.04f, membraneY + 8f),
            end = Offset(w * 0.96f, membraneY + 8f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        val receptorColor = when (visualType) {
            ReceptorVisualType.BETA1_ANTAGONIST -> MedElectricBlue
            ReceptorVisualType.ACE_INHIBITION -> MedCyan
            ReceptorVisualType.BETA2_AGONIST -> MedVerifiedGreen
            ReceptorVisualType.NKCC2_BLOCKER -> MedTeal
            ReceptorVisualType.PROTON_PUMP_BLOCKER -> MedPendingAmber
            ReceptorVisualType.HMG_COA_INHIBITOR -> MedSchematicViolet
        }

        // Transmembrane receptor / enzyme protein block
        val recW = w * 0.22f
        val recH = h * 0.42f
        val recX = w * 0.39f
        val recY = membraneY - recH / 2f
        drawRoundRect(
            color = receptorColor.copy(alpha = 0.25f),
            topLeft = Offset(recX, recY),
            size = Size(recW, recH),
            cornerRadius = CornerRadius(18f, 18f)
        )
        drawRoundRect(
            color = receptorColor,
            topLeft = Offset(recX, recY),
            size = Size(recW, recH),
            cornerRadius = CornerRadius(18f, 18f),
            style = Stroke(width = 5f)
        )

        val isAgonist = visualType == ReceptorVisualType.BETA2_AGONIST

        // Drug molecule docked at active site
        drawCircle(
            color = if (isAgonist) MedVerifiedGreen else MedAlertRed,
            radius = 18f,
            center = Offset(w * 0.50f, recY + 6f)
        )
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = Offset(w * 0.50f, recY + 6f)
        )

        // Endogenous ligand / ion blocked or activated
        if (isAgonist) {
            // Downward intracellular cAMP activation pulses
            drawLine(
                color = MedVerifiedGreen,
                start = Offset(w * 0.50f, recY + recH + 6f),
                end = Offset(w * 0.50f, h * 0.92f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
            drawCircle(color = MedCyan, radius = 9f, center = Offset(w * 0.34f, h * 0.84f))
            drawCircle(color = MedCyan, radius = 9f, center = Offset(w * 0.66f, h * 0.84f))
        } else {
            // Deflected endogenous substrate (blocked competitive/covalent binding)
            drawCircle(
                color = MedPendingAmber,
                radius = 12f,
                center = Offset(w * 0.24f, h * 0.24f)
            )
            drawLine(
                color = MedAlertRed,
                start = Offset(w * 0.30f, h * 0.28f),
                end = Offset(w * 0.42f, recY - 8f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            // Reduced downstream signal indicator
            drawCircle(
                color = MedTextSecondary.copy(alpha = 0.5f),
                radius = 8f,
                center = Offset(w * 0.50f, h * 0.86f),
                style = Stroke(width = 3f)
            )
        }
    }
}
