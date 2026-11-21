package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.physiology.engine.CirculationFilterMode
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.RenderQualityMode
import com.example.physiology.engine.SimulationModelRegistry
import com.example.physiology.engine.SimulationState
import com.example.physiology.learning.GuidedLessonStep
import com.example.physiology.visuals.PhysiologyChartsPanel
import com.example.physiology.visuals.PhysiologySceneCanvas
import com.example.ui.components.GuidedLessonsPanel
import com.example.ui.components.PhysiologyQuizPanel
import com.example.ui.components.ReferenceSourceCard
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

@Composable
fun PhysiologyLabScreen(
    simState: SimulationState,
    language: AppLanguage,
    completedLessonIds: Set<String>,
    correctQuizIds: Set<String>,
    isFullscreen: Boolean,
    onStepSimulation: (Float) -> Unit,
    onSelectSystem: (PhysiologySystemId) -> Unit,
    onLoadScenario: (String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onResetSimulation: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetParameter: (String, Float) -> Unit,
    onResetParameters: () -> Unit,
    onSetCirculationFilter: (CirculationFilterMode) -> Unit,
    onToggleConduction: () -> Unit,
    onToggleLabels: () -> Unit,
    onToggleZoomSynapse: () -> Unit,
    onSetQualityMode: (RenderQualityMode) -> Unit,
    onSetReducedMotion: (Boolean) -> Unit,
    onSetFullscreen: (Boolean) -> Unit,
    onSelectOrganPart: (String) -> Unit,
    onApplyLessonStep: (GuidedLessonStep) -> Unit,
    onCompleteLesson: (String) -> Unit,
    onRecordQuizCorrect: (String) -> Unit,
    onResetQuizProgress: () -> Unit,
    onReturnToAnatomy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Frame-synchronized deterministic simulation clock driver (auto-cancels on dispose/pause)
    LaunchedEffect(simState.isRunning, simState.activeSystem) {
        if (!simState.isRunning) return@LaunchedEffect
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dtSec = ((frameNanos - lastNanos) / 1_000_000_000.0).toFloat()
                    onStepSimulation(dtSec)
                }
                lastNanos = frameNanos
            }
        }
    }

    // Fullscreen Laboratory Viewport
    if (isFullscreen) {
        BackHandler { onSetFullscreen(false) }
        PhysiologySceneCanvas(
            simState = simState,
            language = language,
            isFullscreen = true,
            onToggleFullscreen = { onSetFullscreen(false) },
            onToggleLabels = onToggleLabels,
            onToggleConduction = onToggleConduction,
            onToggleZoomSynapse = onToggleZoomSynapse,
            onSelectPart = onSelectOrganPart,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    var activeInspectorTab by remember { mutableIntStateOf(0) } // 0=Controls, 1=Science, 2=Lessons, 3=Quiz

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideWorkstation = maxWidth >= 860.dp

        if (isWideWorkstation) {
            // Desktop / Tablet 3-Pane Biomedical Simulation Workstation (Section 04)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Leading Pane: 5 Body Systems + Scenarios + Anatomy Integration Link
                Surface(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (language.isRtl) "أجهزة المحاكاة الفسيولوجية" else "Physiology Organ Labs",
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        PhysiologySystemId.entries.forEach { sys ->
                            val selected = simState.activeSystem == sys
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectSystem(sys) }
                                    .testTag("phys_sys_${sys.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) MedCyanContainer else MedDeepNavy,
                                border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = physiologyIconFor(sys),
                                        contentDescription = null,
                                        tint = if (selected) MedCyan else MedTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = sys.title.get(language),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MedBorderHairline)
                        ScenarioListSection(simState, language, onLoadScenario)

                        OutlinedButton(
                            onClick = { onReturnToAnatomy(simState.selectedOrganPartId ?: "heart") },
                            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (language.isRtl) "العودة للتشريح 3D" else "Return to 3D Anatomy",
                                color = MedCyan,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                // Center Pane: 3D Scene + Transport Controls + Real-Time Educational Charts
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhysiologySceneCanvas(
                        simState = simState,
                        language = language,
                        isFullscreen = false,
                        onToggleFullscreen = { onSetFullscreen(true) },
                        onToggleLabels = onToggleLabels,
                        onToggleConduction = onToggleConduction,
                        onToggleZoomSynapse = onToggleZoomSynapse,
                        onSelectPart = onSelectOrganPart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    )
                    SimulationTransportBar(
                        simState = simState,
                        language = language,
                        onTogglePlayPause = onTogglePlayPause,
                        onReset = onResetSimulation,
                        onSetSpeed = onSetSpeed,
                        onSetCirculationFilter = onSetCirculationFilter
                    )
                    PhysiologyChartsPanel(simState = simState, language = language)
                }

                // Trailing Pane: Tabbed Parameters / Science / Guided Lessons / Quiz Inspector
                Surface(
                    modifier = Modifier
                        .width(340.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InspectorTabRow(
                            activeTab = activeInspectorTab,
                            language = language,
                            onSelectTab = { activeInspectorTab = it }
                        )
                        InspectorTabContent(
                            activeTab = activeInspectorTab,
                            simState = simState,
                            language = language,
                            completedLessonIds = completedLessonIds,
                            correctQuizIds = correctQuizIds,
                            onSetParameter = onSetParameter,
                            onResetParameters = onResetParameters,
                            onSetQualityMode = onSetQualityMode,
                            onSetReducedMotion = onSetReducedMotion,
                            onApplyLessonStep = onApplyLessonStep,
                            onCompleteLesson = onCompleteLesson,
                            onOpenQuiz = { activeInspectorTab = 3 },
                            onRecordQuizCorrect = onRecordQuizCorrect,
                            onResetQuizProgress = onResetQuizProgress
                        )
                    }
                }
            }
        } else {
            // Mobile-First Responsive Physiology Lab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("physiology_lab_mobile"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Organ System Horizontal Selector
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PhysiologySystemId.entries.forEach { sys ->
                            val selected = simState.activeSystem == sys
                            Surface(
                                modifier = Modifier
                                    .clickable { onSelectSystem(sys) }
                                    .testTag("phys_sys_${sys.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) MedCyan else MedPanelNavy,
                                border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = physiologyIconFor(sys),
                                        contentDescription = null,
                                        tint = if (selected) MedObsidianNavy else MedCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = sys.shortLabel.get(language),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) MedObsidianNavy else Color.White,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Primary Interactive 3D/Scientific Viewport
                item {
                    PhysiologySceneCanvas(
                        simState = simState,
                        language = language,
                        isFullscreen = false,
                        onToggleFullscreen = { onSetFullscreen(true) },
                        onToggleLabels = onToggleLabels,
                        onToggleConduction = onToggleConduction,
                        onToggleZoomSynapse = onToggleZoomSynapse,
                        onSelectPart = onSelectOrganPart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                    )
                }

                // 3. Unified Transport Controls (Play/Pause, Reset, Playback Viewing Speed, Circuit Filter)
                item {
                    SimulationTransportBar(
                        simState = simState,
                        language = language,
                        onTogglePlayPause = onTogglePlayPause,
                        onReset = onResetSimulation,
                        onSetSpeed = onSetSpeed,
                        onSetCirculationFilter = onSetCirculationFilter
                    )
                }

                // 4. Synchronized Real-Time Educational Chart
                item {
                    PhysiologyChartsPanel(simState = simState, language = language)
                }

                // 5. Scenario Selector Strip + Anatomy Link
                item {
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
                            ScenarioListSection(simState, language, onLoadScenario)
                            OutlinedButton(
                                onClick = { onReturnToAnatomy(simState.selectedOrganPartId ?: "heart") },
                                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("return_to_anatomy_btn")
                            ) {
                                Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MedCyan, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (language.isRtl) "عرض البنية في أطلس التشريح 3D" else "Inspect Structure in 3D Anatomy Atlas",
                                    color = MedCyan,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                // 6. Tabbed Workbench: Parameters | Science & Sources | Guided Lessons | Quiz
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        InspectorTabRow(
                            activeTab = activeInspectorTab,
                            language = language,
                            onSelectTab = { activeInspectorTab = it }
                        )
                        InspectorTabContent(
                            activeTab = activeInspectorTab,
                            simState = simState,
                            language = language,
                            completedLessonIds = completedLessonIds,
                            correctQuizIds = correctQuizIds,
                            onSetParameter = onSetParameter,
                            onResetParameters = onResetParameters,
                            onSetQualityMode = onSetQualityMode,
                            onSetReducedMotion = onSetReducedMotion,
                            onApplyLessonStep = onApplyLessonStep,
                            onCompleteLesson = onCompleteLesson,
                            onOpenQuiz = { activeInspectorTab = 3 },
                            onRecordQuizCorrect = onRecordQuizCorrect,
                            onResetQuizProgress = onResetQuizProgress
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun SimulationTransportBar(
    simState: SimulationState,
    language: AppLanguage,
    onTogglePlayPause: () -> Unit,
    onReset: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetCirculationFilter: (CirculationFilterMode) -> Unit
) {
    val speedOptions = listOf(0.25f to "0.25×", 0.5f to "0.5×", 1.0f to "1.0×", 1.5f to "1.5×")

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("sim_play_pause_btn"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedCyan,
                            contentColor = MedObsidianNavy
                        )
                    ) {
                        Icon(
                            imageVector = if (simState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (simState.isRunning) "Pause" else "Play"
                        )
                    }
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("sim_reset_btn"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedDeepNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset simulation")
                    }
                }

                // Playback Viewing Speed (explicitly separate from physiological BPM!)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = MedTeal, modifier = Modifier.size(15.dp))
                    speedOptions.forEach { (speedVal, label) ->
                        val active = kotlin.math.abs(simState.playbackSpeed - speedVal) < 0.05f
                        Surface(
                            modifier = Modifier
                                .clickable { onSetSpeed(speedVal) }
                                .testTag("sim_speed_$label"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) MedCyanContainer else MedDeepNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                color = if (active) MedCyan else Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Circulation Filter for Cardiovascular System
            if (simState.activeSystem == PhysiologySystemId.CARDIOVASCULAR) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CirculationFilterMode.entries.forEach { mode ->
                        val selected = simState.circulationFilter == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSetCirculationFilter(mode) }
                                .testTag("circ_filter_${mode.name.lowercase()}"),
                            shape = RoundedCornerShape(9.dp),
                            color = if (selected) MedCyanContainer else MedDeepNavy,
                            border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = mode.label.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) MedCyan else MedTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenarioListSection(
    simState: SimulationState,
    language: AppLanguage,
    onLoadScenario: (String) -> Unit
) {
    val scenarios = remember(simState.activeSystem) {
        SimulationModelRegistry.getScenariosForSystem(simState.activeSystem)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (language.isRtl) "السيناريوهات التعليمية" else "Educational Scenarios",
            style = MaterialTheme.typography.titleSmall,
            color = MedCyan,
            fontWeight = FontWeight.Bold
        )
        scenarios.forEach { sc ->
            val active = sc.scenarioId == simState.activeScenarioId
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoadScenario(sc.scenarioId) }
                    .testTag("scenario_${sc.scenarioId}"),
                shape = RoundedCornerShape(12.dp),
                color = if (active) MedCyanContainer else MedDeepNavy,
                border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = if (language.isRtl) sc.titleArabic else sc.titleEnglish,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language.isRtl) sc.descriptionArabic else sc.descriptionEnglish,
                        style = MaterialTheme.typography.bodySmall,
                        color = MedTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun InspectorTabRow(
    activeTab: Int,
    language: AppLanguage,
    onSelectTab: (Int) -> Unit
) {
    val labels = listOf(
        if (language.isRtl) "المعاملات" else "Parameters",
        if (language.isRtl) "الشرح والمصادر" else "Science & Sources",
        if (language.isRtl) "الدروس" else "Lessons",
        if (language.isRtl) "الاختبار" else "Quiz"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        labels.forEachIndexed { idx, label ->
            val selected = activeTab == idx
            Surface(
                modifier = Modifier
                    .clickable { onSelectTab(idx) }
                    .testTag("phys_inspector_tab_$idx"),
                shape = RoundedCornerShape(10.dp),
                color = if (selected) MedCyan else MedDeepNavy,
                border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MedObsidianNavy else Color.White,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InspectorTabContent(
    activeTab: Int,
    simState: SimulationState,
    language: AppLanguage,
    completedLessonIds: Set<String>,
    correctQuizIds: Set<String>,
    onSetParameter: (String, Float) -> Unit,
    onResetParameters: () -> Unit,
    onSetQualityMode: (RenderQualityMode) -> Unit,
    onSetReducedMotion: (Boolean) -> Unit,
    onApplyLessonStep: (GuidedLessonStep) -> Unit,
    onCompleteLesson: (String) -> Unit,
    onOpenQuiz: () -> Unit,
    onRecordQuizCorrect: (String) -> Unit,
    onResetQuizProgress: () -> Unit
) {
    val model = remember(simState.activeSystem) { SimulationModelRegistry.getModel(simState.activeSystem) }
    val explanation = remember(simState.activeSystem) { SimulationModelRegistry.getExplanation(simState.activeSystem) }
    val currentPhase = remember(model, simState.currentPhaseId) {
        model.phases.firstOrNull { it.id == simState.currentPhaseId } ?: model.phases.first()
    }

    when (activeTab) {
        0 -> {
            // Parameters & Quality / Accessibility Panel (Sections 16 & 25)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Active Phase Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedDeepNavy,
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = currentPhase.name.get(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentPhase.summary.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

                // Editable Model Inputs
                model.parameterDefs.forEach { param ->
                    val currentVal = simState.parameterValues[param.id] ?: param.defaultValue
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MedDeepNavy,
                        border = BorderStroke(1.dp, MedBorderHairline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language.isRtl) param.nameArabic else param.nameEnglish,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language.isRtl) param.nameEnglish else param.nameArabic,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MedCyan
                                    )
                                }
                                Text(
                                    text = "${currentVal.toInt()} ${param.unit}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Slider(
                                value = currentVal,
                                onValueChange = { onSetParameter(param.id, it) },
                                valueRange = param.minValue..param.maxValue,
                                colors = SliderDefaults.colors(thumbColor = MedCyan, activeTrackColor = MedCyan),
                                modifier = Modifier.testTag("param_slider_${param.id}")
                            )

                            if (param.presets.isNotEmpty()) {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    param.presets.forEach { preset ->
                                        val selected = kotlin.math.abs(currentVal - preset) < 1f
                                        Surface(
                                            modifier = Modifier.clickable { onSetParameter(param.id, preset) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (selected) MedCyan else MedObsidianNavy,
                                            border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                                        ) {
                                            Text(
                                                text = "${preset.toInt()} ${param.unit}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                                color = if (selected) MedObsidianNavy else Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = param.description.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                            Text(
                                text = "${if (language.isRtl) "الاعتماد النموذجي" else "Model dependency"}: ${param.modelDependency.get(language)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedTeal
                            )
                            Text(
                                text = "${param.validityLevel.badgeLabel.get(language)}  •  Range: ${param.minValue.toInt()}–${param.maxValue.toInt()} ${param.unit}",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                color = MedPendingAmber
                            )
                        }
                    }
                }

                // Strictly Separated Derived Timing Outputs
                if (simState.derivedOutputs.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MedObsidianNavy,
                        border = BorderStroke(1.dp, MedBorderHairline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (language.isRtl) "المخرجات المشتقة الموثقة (توقيت فقط)" else "Verified Derived Outputs (Timing Only)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MedTeal,
                                fontWeight = FontWeight.Bold
                            )
                            simState.derivedOutputs.forEach { (k, v) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(k, style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily), color = MedTextSecondary)
                                    Text(v, style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily), color = Color.White)
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = onResetParameters,
                    border = BorderStroke(1.dp, MedBorderHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language.isRtl) "إعادة المعاملات للقيم الافتراضية" else "Reset Parameters to Defaults",
                        color = Color.White
                    )
                }

                // Performance Quality Mode & Reduced Motion Accessibility
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedDeepNavy,
                    border = BorderStroke(1.dp, MedBorderHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (language.isRtl) "جودة الرسومات وسهولة الوصول" else "Render Quality & Accessibility",
                            style = MaterialTheme.typography.labelLarge,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RenderQualityMode.entries.forEach { qMode ->
                                val active = simState.qualityMode == qMode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSetQualityMode(qMode) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (active) MedCyanContainer else MedObsidianNavy,
                                    border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                                ) {
                                    Text(
                                        text = qMode.label.get(language),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (active) MedCyan else Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language.isRtl) "وضع تقليل الحركة" else "Reduced Motion Mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Switch(
                                checked = simState.reducedMotion,
                                onCheckedChange = onSetReducedMotion,
                                colors = SwitchDefaults.colors(checkedThumbColor = MedObsidianNavy, checkedTrackColor = MedCyan)
                            )
                        }
                    }
                }
            }
        }

        1 -> {
            // Bilingual Scientific Explanations, Validity Level, Assumptions, Limitations & Sources (Sections 19 & 22)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedDeepNavy,
                    border = BorderStroke(1.dp, MedBorderHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = explanation.processName.get(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = explanation.processName.secondary(language),
                            style = MaterialTheme.typography.labelLarge,
                            color = MedCyan
                        )
                        ReviewStatusBadge(status = model.reviewStatus, language = language)
                        Text(
                            text = explanation.scientificDefinition.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        HorizontalDivider(color = MedBorderHairline)
                        Text(
                            text = if (language.isRtl) "البنى التشريحية المشاركة" else "Anatomical Structures",
                            style = MaterialTheme.typography.labelMedium,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(explanation.anatomicalStructures.get(language), style = MaterialTheme.typography.bodySmall, color = MedTextSecondary)

                        Text(
                            text = if (language.isRtl) "تسلسل الأحداث الفسيولوجية" else "Sequence of Events",
                            style = MaterialTheme.typography.labelMedium,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                        explanation.sequenceOfEvents.forEachIndexed { idx, step ->
                            Text("${idx + 1}. ${step.get(language)}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }

                        Text(
                            text = if (language.isRtl) "الأهمية السريرية والوظيفة" else "Biological Function & Clinical Relevance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MedTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(explanation.biologicalFunction.get(language), style = MaterialTheme.typography.bodySmall, color = Color.White)
                        Text(explanation.clinicalRelevance.get(language), style = MaterialTheme.typography.bodySmall, color = MedTextSecondary)
                    }
                }

                // Scientific Provenance, Equations, Assumptions & Limitations
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedObsidianNavy,
                    border = BorderStroke(1.dp, MedPendingAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${model.validityLevel.badgeLabel.get(language)} • v${model.version} (${model.lastReviewedDate})",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                            color = MedPendingAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text(model.equationNotes.get(language), style = MaterialTheme.typography.bodySmall, color = Color.White)
                        Text(model.assetProvenanceNote.get(language), style = MaterialTheme.typography.bodySmall, color = MedCyan)
                        model.limitations.forEach { lim ->
                            Text("• ${lim.get(language)}", style = MaterialTheme.typography.bodySmall, color = MedTextSecondary)
                        }
                    }
                }

                model.sources.forEach { src ->
                    ReferenceSourceCard(source = src, language = language)
                }
                SafetyDisclaimerCard(language = language)
            }
        }

        2 -> {
            GuidedLessonsPanel(
                activeSystem = simState.activeSystem,
                completedLessonIds = completedLessonIds,
                language = language,
                onApplyLessonStep = onApplyLessonStep,
                onCompleteLesson = onCompleteLesson,
                onOpenQuiz = onOpenQuiz
            )
        }

        3 -> {
            PhysiologyQuizPanel(
                correctQuizIds = correctQuizIds,
                language = language,
                onRecordCorrect = onRecordQuizCorrect,
                onResetProgress = onResetQuizProgress
            )
        }
    }
}

private fun physiologyIconFor(sys: PhysiologySystemId) = when (sys) {
    PhysiologySystemId.CARDIOVASCULAR -> Icons.Default.Favorite
    PhysiologySystemId.RESPIRATORY -> Icons.Default.Air
    PhysiologySystemId.NEURAL -> Icons.Default.Psychology
    PhysiologySystemId.RENAL -> Icons.Default.WaterDrop
    PhysiologySystemId.DIGESTIVE -> Icons.Default.Restaurant
}
