package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.ModuleAvailability
import com.example.core.simulation.PhaseTwoArchitectureRegistry
import com.example.ui.components.AvailabilityBadge
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.viewmodel.LabModuleTab
import com.example.ui.viewmodel.MedVerseUiState

@Composable
fun MedicalLabsScreen(
    state: MedVerseUiState,
    onSelectLabTab: (LabModuleTab) -> Unit,
    onOpenStructureIn3D: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = state.language
    val tabs = listOf(
        Triple(LabModuleTab.LEARNING_CENTER, UiStrings.modLearningCenter.get(language), Icons.Default.School),
        Triple(LabModuleTab.PHYSIOLOGY, UiStrings.modPhysiology.get(language), Icons.Default.MonitorHeart),
        Triple(LabModuleTab.PATHOLOGY, UiStrings.modPathology.get(language), Icons.Default.Biotech),
        Triple(LabModuleTab.PHARMACOLOGY, UiStrings.modPharmacology.get(language), Icons.Default.Medication)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Horizontal Module Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 960.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { (tab, title, icon) ->
                val selected = state.activeLabTab == tab
                Surface(
                    modifier = Modifier
                        .clickable { onSelectLabTab(tab) }
                        .testTag("lab_tab_${tab.name.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) MedCyan else MedPanelNavy,
                    border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (selected) MedObsidianNavy else MedCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MedObsidianNavy else Color.White,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 960.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (state.activeLabTab) {
                LabModuleTab.LEARNING_CENTER -> {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MedCyanContainer.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = UiStrings.modLearningCenter.get(language),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AvailabilityBadge(ModuleAvailability.ACTIVE_PHASE_1, language)
                                }
                                Text(
                                    text = if (language == AppLanguage.ARABIC) {
                                        "مسارات تعليمية منهجية مبنية على محتوى المرحلة 01 المراجع علمياً للجهاز القلبي الوعائي، مرتبطة مباشرة بالعارض ثلاثي الأبعاد."
                                    } else {
                                        "Structured cardiovascular study guides built directly on Phase 01 medically reviewed anatomy data and deep-linked to the 3D Explorer."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MedTextSecondary
                                )
                            }
                        }
                    }

                    items(state.learningGuides, key = { it.id }) { guide ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MedPanelNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
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
                                    ReviewStatusBadge(status = guide.reviewStatus, language = language)
                                    Text(
                                        text = "${guide.durationMinutes} min",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                        color = MedCyan
                                    )
                                }
                                Text(
                                    text = guide.title.get(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = guide.subtitle.get(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                                HorizontalDivider(color = MedBorderHairline)
                                guide.steps.forEach { step ->
                                    Text(
                                        text = step.get(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                                Button(
                                    onClick = { onOpenStructureIn3D(guide.targetAnatomyId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = MedObsidianNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    Text(
                                        text = UiStrings.openIn3DCta.get(language),
                                        color = MedObsidianNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                LabModuleTab.PHYSIOLOGY -> {
                    item {
                        PhaseTwoContractHeaderCard(
                            title = UiStrings.modPhysiology.get(language),
                            interfaceName = "PhysiologySimulationEngine • EducationalAnimationTimeline",
                            description = if (language == AppLanguage.ARABIC) {
                                "تم تأسيس العقود البرمجية لمحرك محاكاة وظائف الأعضاء والجدول الزمني للدورة القلبية للمرحلة 02. لا تُعرض أرقام محاكاة عشوائية في المرحلة 01 التزاماً بالدقة العلمية."
                            } else {
                                "Phase 01 defines strict typed interfaces for validated hemodynamic equations and cardiac-cycle timelines. Arbitrary animated numbers are deliberately excluded until validated mathematical models are integrated in Phase 02."
                            },
                            language = language
                        )
                    }

                    item {
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "معاملات الإدخال المخططة (نطاقات مرجعية)"
                            } else {
                                "Architected Simulation Parameters (Reference Ranges)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(PhaseTwoArchitectureRegistry.plannedPhysiologySpecs, key = { it.id }) { spec ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MedPanelNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = spec.label.get(language),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Range: ${spec.minValue.toInt()}–${spec.maxValue.toInt()} ${spec.unit}  •  Ref: ${spec.clinicalReferenceRange}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedTeal
                                )
                                Text(
                                    text = "Tier: ${spec.fidelityTier.name}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedPendingAmber
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MedDeepNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timeline, contentDescription = null, tint = MedCyan, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) {
                                            "مراحل الجدول الزمني للدورة القلبية (المرحلة 02)"
                                        } else {
                                            "Cardiac Cycle Animation Keyframes (Phase 02 Contract)"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                PhaseTwoArchitectureRegistry.plannedCardiacCycleTimeline.forEach { kf ->
                                    Text(
                                        text = "• [${(kf.normalizedTime * 100).toInt()}%] ${kf.label.get(language)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MedTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                LabModuleTab.PATHOLOGY -> {
                    item {
                        PhaseTwoContractHeaderCard(
                            title = UiStrings.modPathology.get(language),
                            interfaceName = "PathologyComparisonEngine",
                            description = if (language == AppLanguage.ARABIC) {
                                "البنية البرمجية لمقارنة النماذج التشريحية السليمة مع الحالات المرضية جاهزة للمرحلة 02، مع ربط مباشر بالبنى السليمة المتوفرة حالياً في العارض ثلاثي الأبعاد."
                            } else {
                                "Architecture ready for side-by-side Normal vs. Pathological 3D mesh comparison in Phase 02. Jump directly to the healthy reference structure in the 3D Explorer below."
                            },
                            language = language
                        )
                    }

                    items(PhaseTwoArchitectureRegistry.plannedPathologyComparisons, key = { it.conditionId }) { spec ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MedPanelNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = spec.conditionName.get(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = spec.normalReferenceSummary.get(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTeal
                                )
                                Text(
                                    text = spec.pathologicalChangeSummary.get(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                                Text(
                                    text = "Required Phase 02 Meshes: ${spec.requiredAssetVariants.joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedPendingAmber
                                )
                                Surface(
                                    modifier = Modifier.clickable { onOpenStructureIn3D(spec.targetAnatomyId) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MedCyanContainer,
                                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) {
                                            "فحص البنية السليمة في العارض 3D"
                                        } else {
                                            "Inspect Healthy Structure in 3D"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MedCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                LabModuleTab.PHARMACOLOGY -> {
                    item {
                        PhaseTwoContractHeaderCard(
                            title = UiStrings.modPharmacology.get(language),
                            interfaceName = "DrugMechanismVisualizationEngine",
                            description = if (language == AppLanguage.ARABIC) {
                                "عقود تصور آليات عمل الأدوية والمستقبلات الجزيئية مهيأة للمرحلة 02 لأغراض التعليم الطبي فقط دون أي جرعات أو وصفات."
                            } else {
                                "Receptor-target and pharmacodynamic visualization interfaces prepared for Phase 02. Strictly educational mechanism overviews—no individualized dosing or prescriptions."
                            },
                            language = language
                        )
                    }

                    items(PhaseTwoArchitectureRegistry.plannedPharmacologySpecs, key = { it.mechanismId }) { spec ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MedPanelNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = spec.drugClass.get(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = spec.molecularTarget,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedCyan
                                )
                                Text(
                                    text = spec.mechanismSummary.get(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                                Text(
                                    text = spec.educationalWarning.get(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MedPendingAmber
                                )
                            }
                        }
                    }

                    item { SafetyDisclaimerCard(language = language) }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun PhaseTwoContractHeaderCard(
    title: String,
    interfaceName: String,
    description: String,
    language: AppLanguage
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MedDeepNavy,
        border = BorderStroke(1.dp, MedPendingAmber.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Architecture, contentDescription = null, tint = MedPendingAmber, modifier = Modifier.size(20.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                AvailabilityBadge(ModuleAvailability.ARCHITECTURE_READY_PHASE_2, language)
            }
            Text(
                text = interfaceName,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                color = MedCyan
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MedTextSecondary
            )
        }
    }
}
