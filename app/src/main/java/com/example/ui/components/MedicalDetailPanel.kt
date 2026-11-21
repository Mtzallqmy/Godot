package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.AnatomicalStructure
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicalDetailPanel(
    structure: AnatomicalStructure,
    allStructures: List<AnatomicalStructure>,
    language: AppLanguage,
    onSelectRelatedStructure: (String) -> Unit,
    onExploreFunction: ((String) -> Unit)? = null,
    isSaved: Boolean = false,
    onToggleSave: ((String) -> Unit)? = null,
    onAskAiTutor: ((String) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(structure.id) { mutableIntStateOf(0) }
    val tabs = listOf(
        UiStrings.tabOverview.get(language),
        UiStrings.tabAnatomy.get(language),
        UiStrings.tabFunction.get(language),
        UiStrings.tabClinical.get(language),
        UiStrings.tabReferences.get(language)
    )

    Surface(
        modifier = modifier.testTag("medical_detail_panel"),
        shape = RoundedCornerShape(20.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Simultaneous Bilingual Header + Scientific Nomenclature (TA2)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedDeepNavy)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(structure.colorHexTag))
                        )
                        Column {
                            Text(
                                text = if (language == AppLanguage.ARABIC) structure.arabicName else structure.englishName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.ARABIC) structure.englishName else structure.arabicName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MedCyan
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onToggleSave != null) {
                            IconButton(
                                onClick = { onToggleSave(structure.id) },
                                modifier = Modifier.testTag("save_structure_button")
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = if (language == AppLanguage.ARABIC) "حفظ البنية التشريحية" else "Save anatomical structure",
                                    tint = if (isSaved) MedTeal else MedTextSecondary
                                )
                            }
                        }
                        if (onClose != null) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.testTag("close_detail_panel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close details",
                                    tint = MedTextSecondary
                                )
                            }
                        }
                    }
                }

                // Hierarchy Breadcrumb + Scientific Latin TA2 + Review Status
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MedObsidianNavy,
                        border = BorderStroke(1.dp, MedBorderHairline)
                    ) {
                        Text(
                            text = "${structure.scientificTerminology} • ${structure.taCode}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                            color = MedTeal,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                    ReviewStatusBadge(status = structure.reviewStatus, language = language)
                }

                // Cross-module Action Row: Explore Function + Ask AI Tutor
                if (onExploreFunction != null || onAskAiTutor != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onExploreFunction != null) {
                            Button(
                                onClick = { onExploreFunction(structure.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detail_explore_function_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MedCyan,
                                    contentColor = MedObsidianNavy
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonitorHeart,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(
                                    text = UiStrings.exploreFunctionCta.get(language),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (onAskAiTutor != null) {
                            OutlinedButton(
                                onClick = { onAskAiTutor(structure.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detail_ask_ai_tutor_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MedTeal)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MedTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(
                                    text = UiStrings.openAiTutorCta.get(language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MedTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 5-Tab Bar: Overview | Anatomy | Function | Clinical Notes | References
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val active = selectedTab == index
                        Surface(
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .testTag("detail_tab_$index"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) MedCyan else MedPanelNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (active) MedObsidianNavy else Color.White,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MedBorderHairline)

            // Tab Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Overview Tab
                        DetailSectionBlock(
                            icon = Icons.Default.MedicalInformation,
                            title = UiStrings.labelDescription.get(language),
                            body = structure.generalDescription.get(language)
                        )
                        DetailSectionBlock(
                            icon = Icons.Default.LocationOn,
                            title = UiStrings.labelLocation.get(language),
                            body = structure.anatomicalLocation.get(language)
                        )
                        // 3D Node Coordinate Mapping Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedDeepNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = UiStrings.labelMeshMapping.get(language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MedCyan
                                )
                                Text(
                                    text = "node_id: ${structure.meshNodeId}  |  pos(${structure.coordinate.x}, ${structure.coordinate.y}, ${structure.coordinate.z})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = Color.White
                                )
                                Text(
                                    text = "${UiStrings.hierarchyLabel.get(language)}: ${structure.organName.get(language)} → ${if (language == AppLanguage.ARABIC) structure.arabicName else structure.englishName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedTextSecondary
                                )
                            }
                        }
                    }

                    1 -> {
                        // Anatomy Tab
                        DetailSectionBlock(
                            icon = Icons.Default.Biotech,
                            title = UiStrings.labelStructure.get(language),
                            body = structure.structureDetails.get(language)
                        )
                        DetailSectionBlock(
                            icon = Icons.Default.AccountTree,
                            title = UiStrings.labelRelationships.get(language),
                            body = structure.relationships.get(language)
                        )
                    }

                    2 -> {
                        // Function Tab
                        DetailSectionBlock(
                            icon = Icons.Default.Biotech,
                            title = UiStrings.labelFunction.get(language),
                            body = structure.biologicalFunction.get(language)
                        )
                    }

                    3 -> {
                        // Clinical Notes Tab
                        DetailSectionBlock(
                            icon = Icons.Default.MedicalInformation,
                            title = UiStrings.labelClinicalRelevance.get(language),
                            body = structure.clinicalRelevance.get(language)
                        )
                        SafetyDisclaimerCard(language = language)
                    }

                    4 -> {
                        // References Tab
                        Text(
                            text = UiStrings.labelSources.get(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        structure.sources.forEach { source ->
                            ReferenceSourceCard(source = source, language = language)
                        }
                    }
                }

                // Related Structures Cross-Navigation (visible across tabs)
                val related = structure.relatedStructureIds.mapNotNull { relId ->
                    allStructures.firstOrNull { it.id == relId }
                }
                if (related.isNotEmpty()) {
                    HorizontalDivider(color = MedBorderHairline)
                    Text(
                        text = UiStrings.labelRelatedTopics.get(language),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        related.forEach { rel ->
                            Surface(
                                modifier = Modifier
                                    .clickable { onSelectRelatedStructure(rel.id) }
                                    .testTag("related_structure_${rel.id}"),
                                shape = RoundedCornerShape(10.dp),
                                color = MedCyanContainer,
                                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(rel.colorHexTag))
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) rel.arabicName else rel.englishName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DetailSectionBlock(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedCyan,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MedCyan,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}
