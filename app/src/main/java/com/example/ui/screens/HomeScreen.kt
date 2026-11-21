package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.ModuleAvailability
import com.example.engine3d.Camera3DState
import com.example.engine3d.CameraPreset
import com.example.engine3d.Interactive3DViewport
import com.example.engine3d.LightingConfig
import com.example.knowledge.MedicalKnowledgeGraph
import com.example.knowledge.UnifiedSearchResult
import com.example.ui.components.MedVerseFooter
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.components.systemIconForKey
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
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MedVerseUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    state: MedVerseUiState,
    onNavigate: (AppDestination) -> Unit,
    onOpenStructure: (String) -> Unit,
    onOpenDictionary: (String) -> Unit,
    onUnifiedSearchChange: (String) -> Unit,
    onSelectUnifiedResult: (UnifiedSearchResult) -> Unit,
    onCameraChange: (Camera3DState) -> Unit,
    onCameraPreset: (CameraPreset) -> Unit,
    onNodePicked: (String) -> Unit,
    onToggleXray: () -> Unit,
    onLightingChange: (LightingConfig) -> Unit,
    onCycleBackdrop: () -> Unit,
    onOpenProvenance: () -> Unit,
    onImportGlbClick: () -> Unit,
    onRestoreDefaultModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = state.language

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. World-Class Phase 06 Hero Banner + Unified Bilingual Smart Search
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                shape = RoundedCornerShape(24.dp),
                color = MedDeepNavy,
                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.40f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_medverse_1795214353415),
                        contentDescription = stringResource(id = R.string.cd_hero_backdrop),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                        alpha = 0.45f
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MedObsidianNavy.copy(alpha = 0.52f),
                                        MedObsidianNavy.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MedCyanContainer.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.65f))
                        ) {
                            Text(
                                text = UiStrings.phaseBadge.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = UiStrings.heroHeading.get(language),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = UiStrings.heroDescription.get(language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MedTextSecondary
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onNavigate(AppDestination.ANATOMY) },
                                modifier = Modifier.testTag("hero_explore_3d_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedCyan, contentColor = MedObsidianNavy),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(UiStrings.launchExplorerCta.get(language), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onNavigate(AppDestination.AI_TUTOR) },
                                modifier = Modifier.testTag("hero_ai_tutor_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedTeal, contentColor = MedObsidianNavy),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(UiStrings.openAiTutorCta.get(language), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onNavigate(AppDestination.PHYSIOLOGY) },
                                modifier = Modifier.testTag("hero_physiology_lab_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.65f)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = MedCyan, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(UiStrings.openPhysiologyCta.get(language), style = MaterialTheme.typography.labelLarge, color = Color.White)
                            }
                        }

                        // Smart Bilingual Unified Knowledge Graph Search Input
                        OutlinedTextField(
                            value = state.unifiedSearchQuery,
                            onValueChange = onUnifiedSearchChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_quick_search_input"),
                            placeholder = {
                                Text(
                                    text = UiStrings.quickSearchPlaceholder.get(language),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MedTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = MedCyan)
                            },
                            trailingIcon = {
                                if (state.unifiedSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { onUnifiedSearchChange("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MedTextSecondary)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    val first = state.unifiedSearchResults.firstOrNull()
                                    if (first != null) onSelectUnifiedResult(first) else onOpenDictionary(state.unifiedSearchQuery)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedCyan,
                                unfocusedBorderColor = MedBorderHairline,
                                focusedContainerColor = MedObsidianNavy.copy(alpha = 0.88f),
                                unfocusedContainerColor = MedObsidianNavy.copy(alpha = 0.78f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Live Categorized Unified Search Results Dropdown Card
                        if (state.unifiedSearchQuery.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MedObsidianNavy.copy(alpha = 0.96f),
                                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("unified_search_results_card")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (state.unifiedSearchResults.isEmpty()) {
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) {
                                                "لا توجد نتائج مطابقة. جرّب: الأبهر، الصمام التاجي، الربو، فوروسيميد، أو Aorta."
                                            } else {
                                                "No direct match. Try: Aorta, Mitral Valve, Asthma, Furosemide, or الأبهر."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MedTextSecondary
                                        )
                                    } else {
                                        Text(
                                            text = if (language == AppLanguage.ARABIC) {
                                                "نتائج البحث الموحّد (${state.unifiedSearchResults.size})"
                                            } else {
                                                "Unified Medical Graph Matches (${state.unifiedSearchResults.size})"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MedCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                        state.unifiedSearchResults.take(6).forEach { hit ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onSelectUnifiedResult(hit) }
                                                    .testTag("unified_result_${hit.id}"),
                                                shape = RoundedCornerShape(10.dp),
                                                color = MedPanelNavy,
                                                border = BorderStroke(1.dp, MedBorderHairline)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = MedicalKnowledgeGraph.formatMatchSnippet(hit, language),
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Surface(shape = RoundedCornerShape(6.dp), color = MedCyanContainer) {
                                                            Text(
                                                                text = hit.entityType.label.get(language),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = MedCyan,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = hit.matchReason.get(language),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MedTeal
                                                    )
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

        // 2. Live Interactive 3D Cardiovascular Preview Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = UiStrings.sectionInteractivePreview.get(language),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = UiStrings.gestureHint.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clickable { onNavigate(AppDestination.ANATOMY) }
                            .testTag("home_open_full_viewer"),
                        shape = RoundedCornerShape(10.dp),
                        color = MedCyanContainer,
                        border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = UiStrings.openIn3DCta.get(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }

                Interactive3DViewport(
                    scene = state.scene3D,
                    language = language,
                    selectedNodeId = state.selectedMeshNodeId,
                    cameraState = state.cameraState,
                    lightingConfig = state.lightingConfig,
                    backdrop = state.backdrop,
                    isolateSelected = false,
                    xrayMode = state.xrayMode,
                    panMode = false,
                    isLoading = state.isModelLoading,
                    errorMessage = state.modelError,
                    isFullscreen = false,
                    compactPreview = true,
                    onCameraChange = onCameraChange,
                    onPresetSelected = onCameraPreset,
                    onNodePicked = onNodePicked,
                    onTogglePanMode = {},
                    onToggleIsolate = {},
                    onToggleXray = onToggleXray,
                    onLightingChange = onLightingChange,
                    onCycleBackdrop = onCycleBackdrop,
                    onToggleFullscreen = { onNavigate(AppDestination.ANATOMY) },
                    onImportGlbClick = onImportGlbClick,
                    onOpenProvenance = onOpenProvenance,
                    onResetDefaultModel = onRestoreDefaultModel,
                    onInspectSelectedDetails = {
                        state.selectedStructure?.let { onOpenStructure(it.id) } ?: onNavigate(AppDestination.ANATOMY)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }

        // 3. Complete Platform Modules Grid (All Phase 06 Active Modules)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = UiStrings.sectionSubjectModules.get(language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                val modules = listOf(
                    PlatformModuleCardItem(
                        title = UiStrings.navAnatomy.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "10 بنى قلبية + مستورد GLB" else "10 Cardiac Structures • GLB",
                        badge = UiStrings.statusActivePhase3.get(language),
                        icon = Icons.Default.ViewInAr,
                        accent = MedCyan,
                        onClick = { onNavigate(AppDestination.ANATOMY) },
                        tag = "home_mod_anatomy"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modPhysiology.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "5 محاكيات أعضاء + ECG + 15 سيناريو" else "5 Organ Sims • 15 Scenarios",
                        badge = UiStrings.statusActivePhase3.get(language),
                        icon = Icons.Default.MonitorHeart,
                        accent = MedTeal,
                        onClick = { onNavigate(AppDestination.PHYSIOLOGY) },
                        tag = "home_mod_physiology"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modPathology.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "مقارنة السليم بالمريض لـ 6 أمراض" else "Normal vs. Diseased • 6 Atlas",
                        badge = UiStrings.statusPhase6Live.get(language),
                        icon = Icons.Default.Biotech,
                        accent = MedCyan,
                        onClick = { onNavigate(AppDestination.PATHOLOGY) },
                        tag = "home_mod_pathology"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modPharmacology.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "6 فئات دوائية + آليات المستقبلات" else "6 Drug Classes • DailyMed",
                        badge = UiStrings.statusPhase6Live.get(language),
                        icon = Icons.Default.Medication,
                        accent = MedTeal,
                        onClick = { onNavigate(AppDestination.PHARMACOLOGY) },
                        tag = "home_mod_pharmacology"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modAiTutor.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "شرح تفاعلي موثق بـ Gemini AI" else "Grounded RAG + Safety Tutor",
                        badge = UiStrings.statusPhase6Live.get(language),
                        icon = Icons.Default.AutoAwesome,
                        accent = MedCyan,
                        onClick = { onNavigate(AppDestination.AI_TUTOR) },
                        tag = "home_mod_ai_tutor"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modDictionaryFull.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "18 مصطلحاً طبياً عربي–إنجليزي" else "18 Bilingual TA2 Entries",
                        badge = UiStrings.statusActivePhase3.get(language),
                        icon = Icons.Default.Book,
                        accent = MedTeal,
                        onClick = { onOpenDictionary("") },
                        tag = "home_mod_dictionary"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modLearningCenter.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "4 مسارات منظمة مع تتبع التقدم" else "4 Bilingual Courses & Lessons",
                        badge = UiStrings.statusPhase6Live.get(language),
                        icon = Icons.Default.School,
                        accent = MedCyan,
                        onClick = { onNavigate(AppDestination.ACADEMY) },
                        tag = "home_mod_academy"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modAssessments.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "8 أنماط اختبار طبي تفاعلي" else "All 8 Assessment Formats",
                        badge = UiStrings.statusPhase6Live.get(language),
                        icon = Icons.Default.Quiz,
                        accent = MedTeal,
                        onClick = { onNavigate(AppDestination.ASSESSMENTS) },
                        tag = "home_mod_assessments"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.modMyLearning.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "مزامنة سحابية + سجل الاختبارات" else "Cloud Sync • History • Export",
                        badge = if (state.isSignedIn) "Synced" else "Local",
                        icon = Icons.Default.Person,
                        accent = MedCyan,
                        onClick = { onNavigate(AppDestination.MY_LEARNING) },
                        tag = "home_mod_my_learning"
                    ),
                    PlatformModuleCardItem(
                        title = UiStrings.navAdmin.get(language),
                        subtitle = if (language == AppLanguage.ARABIC) "CMS طبي محمي بالصلاحيات" else "RBAC Medical CMS & Audit",
                        badge = "RBAC",
                        icon = Icons.Default.AdminPanelSettings,
                        accent = MedTeal,
                        onClick = { onNavigate(AppDestination.ADMIN) },
                        tag = "home_mod_admin"
                    )
                )

                modules.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { mod ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(onClick = mod.onClick)
                                    .testTag(mod.tag),
                                shape = RoundedCornerShape(16.dp),
                                color = MedPanelNavy,
                                border = BorderStroke(1.dp, mod.accent.copy(alpha = 0.45f))
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
                                        Icon(
                                            imageVector = mod.icon,
                                            contentDescription = mod.title,
                                            tint = mod.accent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MedVerifiedContainer
                                        ) {
                                            Text(
                                                text = mod.badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MedVerifiedGreen,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = mod.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = mod.subtitle,
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
            }
        }

        // 4. Featured Anatomical Systems (All 10 Systems)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = UiStrings.sectionFeaturedSystems.get(language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                state.bodySystems.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { system ->
                            val isActive = system.availability == ModuleAvailability.ACTIVE_PHASE_1
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigate(AppDestination.ANATOMY) }
                                    .testTag("home_system_${system.id}"),
                                shape = RoundedCornerShape(16.dp),
                                color = if (isActive) MedCyanContainer.copy(alpha = 0.45f) else MedDeepNavy,
                                border = BorderStroke(1.dp, if (isActive) MedCyan else MedBorderHairline)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = systemIconForKey(system.iconKey),
                                            contentDescription = system.englishName,
                                            tint = if (isActive) MedCyan else MedTextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        if (isActive) {
                                            Text(
                                                text = "${system.structureCount} 3D",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MedTeal,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) system.arabicName else system.englishName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) system.englishName else system.arabicName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isActive) MedCyan else MedTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Guided Learning Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = UiStrings.sectionLearningCards.get(language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                state.learningGuides.forEach { guide ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenStructure(guide.targetAnatomyId) }
                            .testTag("home_guide_${guide.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = MedPanelNavy,
                        border = BorderStroke(1.dp, MedBorderHairline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ReviewStatusBadge(status = guide.reviewStatus, language = language)
                                Text(
                                    text = "${guide.durationMinutes} min",
                                    style = MaterialTheme.typography.labelSmall,
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
                        }
                    }
                }
            }
        }

        // 6. Project Introduction + Safety Notice + Branded Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SafetyDisclaimerCard(language = language)
                MedVerseFooter(language = language, onOpenAbout = { onNavigate(AppDestination.ABOUT) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private data class PlatformModuleCardItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit,
    val tag: String
)
