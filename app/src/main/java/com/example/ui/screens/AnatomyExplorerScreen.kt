package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.AnatomicalStructure
import com.example.core.model.BodySystem
import com.example.core.model.ModuleAvailability
import com.example.engine3d.Camera3DState
import com.example.engine3d.CameraPreset
import com.example.engine3d.Interactive3DViewport
import com.example.engine3d.LightingConfig
import com.example.ui.components.AvailabilityBadge
import com.example.ui.components.MedicalDetailPanel
import com.example.ui.components.systemIconForKey
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.viewmodel.MedVerseUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnatomyExplorerScreen(
    state: MedVerseUiState,
    onSelectSystem: (String) -> Unit,
    onSelectStructure: (String, Boolean) -> Unit,
    onStructureSearchChange: (String) -> Unit,
    onCameraChange: (Camera3DState) -> Unit,
    onCameraPreset: (CameraPreset) -> Unit,
    onNodePicked: (String, Boolean) -> Unit,
    onTogglePanMode: () -> Unit,
    onToggleIsolate: () -> Unit,
    onToggleXray: () -> Unit,
    onLightingChange: (LightingConfig) -> Unit,
    onCycleBackdrop: () -> Unit,
    onSetFullscreen: (Boolean) -> Unit,
    onImportGlbClick: () -> Unit,
    onOpenProvenance: () -> Unit,
    onRestoreDefaultModel: () -> Unit,
    onSetShowDetailSheet: (Boolean) -> Unit,
    onExploreFunction: (String) -> Unit,
    onExploreSystemPhysiology: (String) -> Unit,
    onToggleSaveStructure: (String) -> Unit = {},
    onAskAiTutorForStructure: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language = state.language

    // Fullscreen 3D Viewport Mode
    if (state.isFullscreenViewer) {
        BackHandler { onSetFullscreen(false) }
        Interactive3DViewport(
            scene = state.scene3D,
            language = language,
            selectedNodeId = state.selectedMeshNodeId,
            cameraState = state.cameraState,
            lightingConfig = state.lightingConfig,
            backdrop = state.backdrop,
            isolateSelected = state.isolateSelected,
            xrayMode = state.xrayMode,
            panMode = state.panMode,
            isLoading = state.isModelLoading,
            errorMessage = state.modelError,
            isFullscreen = true,
            onCameraChange = onCameraChange,
            onPresetSelected = onCameraPreset,
            onNodePicked = { onNodePicked(it, false) },
            onTogglePanMode = onTogglePanMode,
            onToggleIsolate = onToggleIsolate,
            onToggleXray = onToggleXray,
            onLightingChange = onLightingChange,
            onCycleBackdrop = onCycleBackdrop,
            onToggleFullscreen = { onSetFullscreen(false) },
            onImportGlbClick = onImportGlbClick,
            onOpenProvenance = onOpenProvenance,
            onResetDefaultModel = onRestoreDefaultModel,
            onInspectSelectedDetails = {
                onSetFullscreen(false)
                onSetShowDetailSheet(true)
            },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedThreePane = maxWidth >= 860.dp
        val selectedSystem = state.bodySystems.firstOrNull { it.id == state.selectedSystemId }

        if (isExpandedThreePane) {
            // Canonical 3-Pane Desktop / Tablet Medical Atlas Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Leading Pane: Systems + Searchable Cardiovascular Hierarchy
                Surface(
                    modifier = Modifier
                        .width(275.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    AnatomyHierarchyPane(
                        systems = state.bodySystems,
                        selectedSystemId = state.selectedSystemId,
                        structures = state.filteredStructures,
                        selectedStructureId = state.selectedStructureId,
                        searchQuery = state.structureSearchQuery,
                        language = language,
                        onSelectSystem = onSelectSystem,
                        onSelectStructure = { onSelectStructure(it, false) },
                        onSearchChange = onStructureSearchChange
                    )
                }

                // Center Pane: Large Interactive 3D Viewport
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Interactive3DViewport(
                        scene = state.scene3D,
                        language = language,
                        selectedNodeId = state.selectedMeshNodeId,
                        cameraState = state.cameraState,
                        lightingConfig = state.lightingConfig,
                        backdrop = state.backdrop,
                        isolateSelected = state.isolateSelected,
                        xrayMode = state.xrayMode,
                        panMode = state.panMode,
                        isLoading = state.isModelLoading,
                        errorMessage = state.modelError,
                        isFullscreen = false,
                        onCameraChange = onCameraChange,
                        onPresetSelected = onCameraPreset,
                        onNodePicked = { onNodePicked(it, false) },
                        onTogglePanMode = onTogglePanMode,
                        onToggleIsolate = onToggleIsolate,
                        onToggleXray = onToggleXray,
                        onLightingChange = onLightingChange,
                        onCycleBackdrop = onCycleBackdrop,
                        onToggleFullscreen = { onSetFullscreen(true) },
                        onImportGlbClick = onImportGlbClick,
                        onOpenProvenance = onOpenProvenance,
                        onResetDefaultModel = onRestoreDefaultModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Text(
                        text = UiStrings.schematicNotice.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MedTextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // Trailing Pane: Rich Tabbed Medical Detail Panel
                val activeStructure = state.selectedStructure
                if (activeStructure != null) {
                    MedicalDetailPanel(
                        structure = activeStructure,
                        allStructures = state.structures,
                        language = language,
                        onSelectRelatedStructure = { onSelectStructure(it, false) },
                        onExploreFunction = onExploreFunction,
                        isSaved = activeStructure.id in state.savedStructureIds,
                        onToggleSave = onToggleSaveStructure,
                        onAskAiTutor = onAskAiTutorForStructure,
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                    )
                }
            }
        } else {
            // Mobile / Compact Responsive Layout with Bottom Sheet Details
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("anatomy_explorer_compact"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Body Systems Horizontal Ribbon (All 10 Systems)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = UiStrings.systemsHeader.get(language),
                            style = MaterialTheme.typography.labelLarge,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.bodySystems.forEach { sys ->
                                val selected = sys.id == state.selectedSystemId
                                val active = sys.availability == ModuleAvailability.ACTIVE_PHASE_1
                                Surface(
                                    modifier = Modifier
                                        .clickable { onSelectSystem(sys.id) }
                                        .testTag("system_chip_${sys.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = when {
                                        selected -> MedCyan
                                        active -> MedCyanContainer
                                        else -> MedPanelNavy
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) MedCyan else MedBorderHairline
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = systemIconForKey(sys.iconKey),
                                            contentDescription = null,
                                            tint = if (selected) MedObsidianNavy else if (active) MedCyan else MedTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) sys.arabicName else sys.englishName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (selected) MedObsidianNavy else Color.White,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. If non-cardiovascular system selected, show transparent Phase 02 System Roadmap card
                if (selectedSystem != null && selectedSystem.availability != ModuleAvailability.ACTIVE_PHASE_1) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MedPanelNavy,
                            border = BorderStroke(1.dp, MedBorderHairline),
                            modifier = Modifier.fillMaxWidth()
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
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) selectedSystem.arabicName else selectedSystem.englishName,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AvailabilityBadge(selectedSystem.availability, language)
                                }
                                Text(
                                    text = selectedSystem.summary.get(language),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MedTextSecondary
                                )
                                Text(
                                    text = if (language == AppLanguage.ARABIC) {
                                        "تم تجهيز البنية الهرمية لهذا الجهاز، وسيتم دمج نماذجه ثلاثية الأبعاد المعتمدة في المرحلة 02. يمكنك استكشاف الجهاز القلبي الوعائي الكامل الآن أو استيراد نموذج GLB مباشرة."
                                    } else {
                                        "This system's hierarchical taxonomy is architected for Phase 02 verified 3D mesh integration. Explore the complete Cardiovascular System module now or import a GLB/GLTF asset directly."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedCyan
                                )
                                val supportsPhase3Physiology = selectedSystem.id in setOf(
                                    "cardiovascular", "respiratory", "nervous", "urinary", "digestive"
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (supportsPhase3Physiology) {
                                        Button(
                                            onClick = { onExploreSystemPhysiology(selectedSystem.id) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("system_explore_function_${selectedSystem.id}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MedTeal)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MonitorHeart,
                                                contentDescription = null,
                                                tint = MedObsidianNavy,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = UiStrings.exploreFunctionCta.get(language),
                                                color = MedObsidianNavy,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onSelectSystem("cardiovascular") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedCyan)
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) {
                                                "الجهاز القلبي الوعائي"
                                            } else {
                                                "Cardiovascular 3D"
                                            },
                                            color = MedObsidianNavy,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 3. Large Interactive 3D Viewport
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Interactive3DViewport(
                                scene = state.scene3D,
                                language = language,
                                selectedNodeId = state.selectedMeshNodeId,
                                cameraState = state.cameraState,
                                lightingConfig = state.lightingConfig,
                                backdrop = state.backdrop,
                                isolateSelected = state.isolateSelected,
                                xrayMode = state.xrayMode,
                                panMode = state.panMode,
                                isLoading = state.isModelLoading,
                                errorMessage = state.modelError,
                                isFullscreen = false,
                                onCameraChange = onCameraChange,
                                onPresetSelected = onCameraPreset,
                                onNodePicked = { onNodePicked(it, true) },
                                onTogglePanMode = onTogglePanMode,
                                onToggleIsolate = onToggleIsolate,
                                onToggleXray = onToggleXray,
                                onLightingChange = onLightingChange,
                                onCycleBackdrop = onCycleBackdrop,
                                onToggleFullscreen = { onSetFullscreen(true) },
                                onImportGlbClick = onImportGlbClick,
                                onOpenProvenance = onOpenProvenance,
                                onResetDefaultModel = onRestoreDefaultModel,
                                onInspectSelectedDetails = { onSetShowDetailSheet(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(355.dp)
                            )

                            // Selected Structure Action Banner -> Opens Tabbed Detail Bottom Sheet
                            state.selectedStructure?.let { selected ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSetShowDetailSheet(true) }
                                        .testTag("open_medical_detail_sheet_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = MedCyanContainer,
                                    border = BorderStroke(1.dp, MedCyan)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(selected.colorHexTag))
                                            )
                                            Column {
                                                Text(
                                                    text = "${selected.englishName}  •  ${selected.arabicName}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${selected.scientificTerminology} (${selected.taCode})",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                                    color = MedCyan
                                                )
                                            }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = MedCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = UiStrings.tabOverview.get(language),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MedCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Searchable Cardiovascular Structure Tree
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = UiStrings.structuresHeader.get(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = state.structureSearchQuery,
                                onValueChange = onStructureSearchChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("anatomy_structure_search"),
                                placeholder = {
                                    Text(
                                        text = UiStrings.filterStructuresPlaceholder.get(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MedTextSecondary
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = MedCyan)
                                },
                                singleLine = true,
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
                    }

                    items(state.filteredStructures, key = { it.id }) { structure ->
                        StructureRowCard(
                            structure = structure,
                            selected = structure.id == state.selectedStructureId,
                            language = language,
                            onClick = { onSelectStructure(structure.id, true) }
                        )
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }

            // Responsive Bottom Sheet for Medical Detail Panel on Mobile
            val activeStructure = state.selectedStructure
            if (state.showDetailSheet && activeStructure != null) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { onSetShowDetailSheet(false) },
                    sheetState = sheetState,
                    containerColor = MedPanelNavy,
                    dragHandle = null
                ) {
                    MedicalDetailPanel(
                        structure = activeStructure,
                        allStructures = state.structures,
                        language = language,
                        onSelectRelatedStructure = { relId -> onSelectStructure(relId, false) },
                        onExploreFunction = { structId ->
                            onSetShowDetailSheet(false)
                            onExploreFunction(structId)
                        },
                        isSaved = activeStructure.id in state.savedStructureIds,
                        onToggleSave = onToggleSaveStructure,
                        onAskAiTutor = { structId ->
                            onSetShowDetailSheet(false)
                            onAskAiTutorForStructure(structId)
                        },
                        onClose = { onSetShowDetailSheet(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(540.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnatomyHierarchyPane(
    systems: List<BodySystem>,
    selectedSystemId: String,
    structures: List<AnatomicalStructure>,
    selectedStructureId: String,
    searchQuery: String,
    language: AppLanguage,
    onSelectSystem: (String) -> Unit,
    onSelectStructure: (String) -> Unit,
    onSearchChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = UiStrings.systemsHeader.get(language),
                style = MaterialTheme.typography.titleSmall,
                color = MedCyan,
                fontWeight = FontWeight.Bold
            )
        }
        items(systems, key = { it.id }) { sys ->
            val selected = sys.id == selectedSystemId
            val active = sys.availability == ModuleAvailability.ACTIVE_PHASE_1
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSystem(sys.id) },
                shape = RoundedCornerShape(10.dp),
                color = if (selected) MedCyanContainer else MedDeepNavy,
                border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = systemIconForKey(sys.iconKey),
                            contentDescription = null,
                            tint = if (active) MedCyan else MedTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.ARABIC) sys.arabicName else sys.englishName,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = if (active) "10" else "P2",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) MedTeal else MedTextSecondary
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(6.dp))
            Text(
                text = UiStrings.structuresHeader.get(language),
                style = MaterialTheme.typography.titleSmall,
                color = MedCyan,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = UiStrings.filterStructuresPlaceholder.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MedTextSecondary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedCyan,
                    unfocusedBorderColor = MedBorderHairline,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
        items(structures, key = { it.id }) { structure ->
            StructureRowCard(
                structure = structure,
                selected = structure.id == selectedStructureId,
                language = language,
                onClick = { onSelectStructure(structure.id) }
            )
        }
    }
}

@Composable
private fun StructureRowCard(
    structure: AnatomicalStructure,
    selected: Boolean,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("structure_item_${structure.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MedCyanContainer else MedPanelNavy,
        border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(structure.colorHexTag))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (language == AppLanguage.ARABIC) structure.arabicName else structure.englishName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (language == AppLanguage.ARABIC) structure.englishName else structure.arabicName}  •  ${structure.scientificTerminology}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) MedCyan else MedTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = structure.organName.get(language),
                style = MaterialTheme.typography.labelSmall,
                color = MedTeal
            )
        }
    }
}
