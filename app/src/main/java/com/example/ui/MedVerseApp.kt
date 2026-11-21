package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.firebase.GoogleAuthHelper
import com.example.ui.components.AssetProvenanceDialog
import com.example.ui.components.LanguageToggleChip
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AdminCmsScreen
import com.example.ui.screens.AiMedicalTutorScreen
import com.example.ui.screens.AnatomyExplorerScreen
import com.example.ui.screens.AssessmentsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningAcademyScreen
import com.example.ui.screens.MedicalDictionaryScreen
import com.example.ui.screens.MyLearningScreen
import com.example.ui.screens.PathologyAtlasScreen
import com.example.ui.screens.PharmacologyLabScreen
import com.example.ui.screens.PhysiologyLabScreen
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedGreen
import com.example.ui.theme.MedVerseTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MedVerseViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private enum class PrimaryNavSlot {
    HOME, ANATOMY, LABS, AI_TUTOR, PLATFORM
}

private data class PrimarySlotSpec(
    val slot: PrimaryNavSlot,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelEn: String,
    val labelAr: String,
    val testTag: String
)

private data class PlatformMenuEntry(
    val destination: AppDestination,
    val icon: ImageVector,
    val labelEn: String,
    val labelAr: String,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedVerseApp(
    viewModel: MedVerseViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val physiologyState by viewModel.physiologyState.collectAsStateWithLifecycle()
    val language = state.language
    val isAnyFullscreen = state.isFullscreenViewer || state.isPhysiologyFullscreen

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var showPlatformHubSheet by remember { mutableStateOf(false) }

    // Observe Firebase Auth state reactively and bind cloud listeners ONLY when signed in
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            viewModel.onAuthUserChanged(auth.currentUser)
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    // Attempt silent Google Auto Sign-In once on startup (never blocks Guest learning)
    LaunchedEffect(Unit) {
        GoogleAuthHelper.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            scope = coroutineScope,
            onAuthSuccess = { viewModel.onAuthUserChanged(Firebase.auth.currentUser) },
            onUnauthenticated = { /* Remain gracefully in Guest Local mode */ }
        )
    }

    val triggerGoogleSignIn: () -> Unit = {
        GoogleAuthHelper.onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            scope = coroutineScope,
            onAuthSuccess = {
                viewModel.onAuthUserChanged(Firebase.auth.currentUser)
                viewModel.setLearningStatusMessage(
                    if (language == AppLanguage.ARABIC) "تم تسجيل الدخول بحساب Google بنجاح." else "Signed in with Google successfully."
                )
            },
            onAuthError = { msg -> viewModel.setLearningStatusMessage(msg) },
            onAuthCancelled = {
                viewModel.setLearningStatusMessage(
                    if (language == AppLanguage.ARABIC) "تم إلغاء تسجيل الدخول." else "Google Sign-In was cancelled."
                )
            }
        )
    }

    val triggerSignOut: () -> Unit = {
        GoogleAuthHelper.signOut(
            credentialManager = credentialManager,
            scope = coroutineScope,
            onBeforeAuthSignOut = { viewModel.prepareForSignOut() },
            onSignOutComplete = { viewModel.onAuthUserChanged(null) }
        )
    }

    // Zero-permission Android SAF picker for .glb and .gltf medical 3D assets
    val glbPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importGlbModel(uri)
        }
    }

    val openGlbImporter: () -> Unit = {
        glbPickerLauncher.launch(arrayOf("model/gltf-binary", "model/gltf+json", "application/octet-stream", "*/*"))
    }

    val primarySlots = listOf(
        PrimarySlotSpec(PrimaryNavSlot.HOME, Icons.Filled.Home, Icons.Outlined.Home, UiStrings.navHome.en, UiStrings.navHome.ar, "nav_home"),
        PrimarySlotSpec(PrimaryNavSlot.ANATOMY, Icons.Filled.ViewInAr, Icons.Outlined.ViewInAr, UiStrings.navAnatomy.en, UiStrings.navAnatomy.ar, "nav_anatomy"),
        PrimarySlotSpec(PrimaryNavSlot.LABS, Icons.Filled.Science, Icons.Outlined.Science, UiStrings.navLabs.en, UiStrings.navLabs.ar, "nav_labs"),
        PrimarySlotSpec(PrimaryNavSlot.AI_TUTOR, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, UiStrings.navAiTutor.en, UiStrings.navAiTutor.ar, "nav_ai_tutor"),
        PrimarySlotSpec(PrimaryNavSlot.PLATFORM, Icons.Filled.Apps, Icons.Outlined.Apps, UiStrings.navExplore.en, UiStrings.navExplore.ar, "nav_platform_hub")
    )

    val activePrimarySlot = when (state.activeDestination) {
        AppDestination.HOME -> PrimaryNavSlot.HOME
        AppDestination.ANATOMY -> PrimaryNavSlot.ANATOMY
        AppDestination.PHYSIOLOGY, AppDestination.PATHOLOGY, AppDestination.PHARMACOLOGY -> PrimaryNavSlot.LABS
        AppDestination.AI_TUTOR -> PrimaryNavSlot.AI_TUTOR
        else -> PrimaryNavSlot.PLATFORM
    }

    val isLabScreen = state.activeDestination in setOf(
        AppDestination.PHYSIOLOGY,
        AppDestination.PATHOLOGY,
        AppDestination.PHARMACOLOGY
    )

    MedVerseTheme(language = language) {
        if (state.activeDestination != AppDestination.HOME && !isAnyFullscreen) {
            BackHandler {
                viewModel.navigateTo(AppDestination.HOME)
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MedObsidianNavy)
        ) {
            val useNavigationRail = maxWidth >= 780.dp

            Scaffold(
                containerColor = MedObsidianNavy,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    if (!isAnyFullscreen) {
                        Column {
                            MedVerseTopBar(
                                language = language,
                                isSignedIn = state.isSignedIn,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onOpenProvenance = { viewModel.setShowProvenanceDialog(true) },
                                onOpenMyLearning = { viewModel.navigateTo(AppDestination.MY_LEARNING) }
                            )
                            if (isLabScreen) {
                                LabsSubNavStrip(
                                    activeDestination = state.activeDestination,
                                    language = language,
                                    onSelectDestination = viewModel::navigateTo
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    if (!useNavigationRail && !isAnyFullscreen) {
                        NavigationBar(
                            containerColor = MedDeepNavy,
                            contentColor = Color.White,
                            windowInsets = WindowInsets.navigationBars,
                            tonalElevation = 0.dp
                        ) {
                            primarySlots.forEach { slot ->
                                val selected = activePrimarySlot == slot.slot
                                val label = if (language.isRtl) slot.labelAr else slot.labelEn
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        when (slot.slot) {
                                            PrimaryNavSlot.HOME -> viewModel.navigateTo(AppDestination.HOME)
                                            PrimaryNavSlot.ANATOMY -> viewModel.navigateTo(AppDestination.ANATOMY)
                                            PrimaryNavSlot.LABS -> {
                                                if (!isLabScreen) viewModel.navigateTo(AppDestination.PHYSIOLOGY)
                                            }
                                            PrimaryNavSlot.AI_TUTOR -> viewModel.navigateTo(AppDestination.AI_TUTOR)
                                            PrimaryNavSlot.PLATFORM -> showPlatformHubSheet = true
                                        }
                                    },
                                    modifier = Modifier.testTag(slot.testTag),
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) slot.selectedIcon else slot.unselectedIcon,
                                            contentDescription = label
                                        )
                                    },
                                    label = {
                                        Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MedCyan,
                                        selectedTextColor = MedCyan,
                                        indicatorColor = MedCyanContainer,
                                        unselectedIconColor = MedTextSecondary,
                                        unselectedTextColor = MedTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavigationRail && !isAnyFullscreen) {
                        NavigationRail(
                            containerColor = MedDeepNavy,
                            contentColor = Color.White,
                            modifier = Modifier
                                .fillMaxHeight()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            primarySlots.forEach { slot ->
                                val selected = activePrimarySlot == slot.slot
                                val label = if (language.isRtl) slot.labelAr else slot.labelEn
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = {
                                        when (slot.slot) {
                                            PrimaryNavSlot.HOME -> viewModel.navigateTo(AppDestination.HOME)
                                            PrimaryNavSlot.ANATOMY -> viewModel.navigateTo(AppDestination.ANATOMY)
                                            PrimaryNavSlot.LABS -> {
                                                if (!isLabScreen) viewModel.navigateTo(AppDestination.PHYSIOLOGY)
                                            }
                                            PrimaryNavSlot.AI_TUTOR -> viewModel.navigateTo(AppDestination.AI_TUTOR)
                                            PrimaryNavSlot.PLATFORM -> showPlatformHubSheet = true
                                        }
                                    },
                                    modifier = Modifier.testTag(slot.testTag),
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) slot.selectedIcon else slot.unselectedIcon,
                                            contentDescription = label
                                        )
                                    },
                                    label = { Text(text = label, maxLines = 1) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = MedCyan,
                                        selectedTextColor = MedCyan,
                                        indicatorColor = MedCyanContainer,
                                        unselectedIconColor = MedTextSecondary,
                                        unselectedTextColor = MedTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .let { mod ->
                                if (isAnyFullscreen) mod.windowInsetsPadding(WindowInsets.safeDrawing) else mod
                            }
                    ) {
                        when (state.activeDestination) {
                            AppDestination.HOME -> HomeScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onOpenStructure = { viewModel.openAnatomyStructure(it, openDetailSheet = false) },
                                onOpenDictionary = { viewModel.openDictionaryWithSearch(it) },
                                onUnifiedSearchChange = { viewModel.updateUnifiedSearch(it) },
                                onSelectUnifiedResult = viewModel::openUnifiedSearchResult,
                                onCameraChange = viewModel::updateCamera,
                                onCameraPreset = viewModel::applyCameraPreset,
                                onNodePicked = { viewModel.onMeshNodePicked(it, openSheetOnCompact = false) },
                                onToggleXray = viewModel::toggleXrayMode,
                                onLightingChange = viewModel::updateLighting,
                                onCycleBackdrop = viewModel::cycleBackdrop,
                                onOpenProvenance = { viewModel.setShowProvenanceDialog(true) },
                                onImportGlbClick = openGlbImporter,
                                onRestoreDefaultModel = viewModel::loadDefaultCardiovascularScene
                            )

                            AppDestination.ANATOMY -> AnatomyExplorerScreen(
                                state = state,
                                onSelectSystem = viewModel::selectSystem,
                                onSelectStructure = { id, openSheet ->
                                    viewModel.selectStructure(id, focusCamera = true, openSheetOnCompact = openSheet)
                                },
                                onStructureSearchChange = viewModel::updateStructureSearch,
                                onCameraChange = viewModel::updateCamera,
                                onCameraPreset = viewModel::applyCameraPreset,
                                onNodePicked = { nodeId, openSheet ->
                                    viewModel.onMeshNodePicked(nodeId, openSheetOnCompact = openSheet)
                                },
                                onTogglePanMode = viewModel::togglePanMode,
                                onToggleIsolate = viewModel::toggleIsolateSelected,
                                onToggleXray = viewModel::toggleXrayMode,
                                onLightingChange = viewModel::updateLighting,
                                onCycleBackdrop = viewModel::cycleBackdrop,
                                onSetFullscreen = viewModel::setFullscreenViewer,
                                onImportGlbClick = openGlbImporter,
                                onOpenProvenance = { viewModel.setShowProvenanceDialog(true) },
                                onRestoreDefaultModel = viewModel::loadDefaultCardiovascularScene,
                                onSetShowDetailSheet = viewModel::setShowDetailSheet,
                                onExploreFunction = viewModel::openPhysiologyForAnatomyStructure,
                                onExploreSystemPhysiology = viewModel::openPhysiologyForBodySystem,
                                onToggleSaveStructure = viewModel::toggleSavedStructure,
                                onAskAiTutorForStructure = viewModel::openAiTutorWithStructureContext
                            )

                            AppDestination.PHYSIOLOGY -> PhysiologyLabScreen(
                                simState = physiologyState,
                                language = language,
                                completedLessonIds = state.completedLessonIds,
                                correctQuizIds = state.correctQuizIds,
                                isFullscreen = state.isPhysiologyFullscreen,
                                onStepSimulation = viewModel::stepSimulation,
                                onSelectSystem = viewModel::selectPhysiologySystem,
                                onLoadScenario = viewModel::loadPhysiologyScenario,
                                onTogglePlayPause = viewModel::toggleSimulationPlayPause,
                                onResetSimulation = viewModel::resetSimulation,
                                onSetSpeed = viewModel::setSimulationSpeed,
                                onSetParameter = viewModel::setSimulationParameter,
                                onResetParameters = viewModel::resetSimulationParameters,
                                onSetCirculationFilter = viewModel::setCirculationFilter,
                                onToggleConduction = viewModel::toggleConductionOverlay,
                                onToggleLabels = viewModel::toggleSimulationLabels,
                                onToggleZoomSynapse = viewModel::toggleZoomSynapse,
                                onSetQualityMode = viewModel::setSimulationQualityMode,
                                onSetReducedMotion = viewModel::setReducedMotion,
                                onSetFullscreen = viewModel::setPhysiologyFullscreen,
                                onSelectOrganPart = viewModel::selectSimulationOrganPart,
                                onApplyLessonStep = viewModel::applyGuidedLessonStep,
                                onCompleteLesson = viewModel::completeLesson,
                                onRecordQuizCorrect = viewModel::recordQuizCorrect,
                                onResetQuizProgress = viewModel::resetQuizProgress,
                                onReturnToAnatomy = viewModel::returnToAnatomyFromPhysiology
                            )

                            AppDestination.PATHOLOGY -> PathologyAtlasScreen(
                                selectedDiseaseId = state.selectedDiseaseId,
                                language = language,
                                onSelectDisease = viewModel::selectDisease,
                                onOpenAnatomyStructure = { viewModel.openAnatomyStructure(it, openDetailSheet = false) },
                                onOpenPhysiologySystem = viewModel::openPhysiologySystem,
                                onOpenDrugClass = viewModel::openDrugClass,
                                onAskAiTutor = viewModel::openAiTutorWithDiseaseContext
                            )

                            AppDestination.PHARMACOLOGY -> PharmacologyLabScreen(
                                selectedDrugClassId = state.selectedDrugClassId,
                                language = language,
                                onSelectDrugClass = viewModel::selectDrugClass,
                                onOpenPhysiologySystem = viewModel::openPhysiologySystem,
                                onOpenDisease = viewModel::openDisease,
                                onAskAiTutor = viewModel::openAiTutorWithDrugContext
                            )

                            AppDestination.DICTIONARY -> MedicalDictionaryScreen(
                                state = state,
                                onQueryChange = viewModel::updateDictionaryQuery,
                                onCategorySelected = viewModel::selectDictionaryCategory,
                                onToggleBookmarkedOnly = viewModel::toggleBookmarkedOnly,
                                onSelectTerm = viewModel::selectDictionaryTerm,
                                onToggleBookmark = viewModel::toggleBookmark,
                                onOpenAnatomyIn3D = { viewModel.openAnatomyStructure(it, openDetailSheet = false) }
                            )

                            AppDestination.AI_TUTOR -> AiMedicalTutorScreen(
                                messages = state.aiMessages,
                                activeContextLabel = viewModel.getActiveStudyContextLabel(),
                                selectedMode = state.aiTutorMode,
                                isStreaming = state.isAiStreaming,
                                language = language,
                                onSelectMode = viewModel::setAiTutorMode,
                                onSendQuestion = viewModel::askAiTutor,
                                onRetryLast = viewModel::retryLastAiQuestion,
                                onClearChat = viewModel::clearAiConversation,
                                onExecuteAction = viewModel::executeValidatedTutorAction
                            )

                            AppDestination.ACADEMY -> LearningAcademyScreen(
                                completedLessonIds = state.completedLessonIds,
                                language = language,
                                onToggleLessonCompleted = viewModel::toggleAcademyLessonCompleted,
                                onOpenAnatomyStructure = { viewModel.openAnatomyStructure(it, openDetailSheet = false) },
                                onOpenPhysiologySystem = viewModel::openPhysiologySystem,
                                onOpenDisease = viewModel::openDisease,
                                onOpenDrugClass = viewModel::openDrugClass,
                                onOpenAssessments = { viewModel.navigateTo(AppDestination.ASSESSMENTS) }
                            )

                            AppDestination.ASSESSMENTS -> AssessmentsScreen(
                                language = language,
                                isSignedIn = state.isSignedIn,
                                onSubmitAttempt = viewModel::submitAssessmentAttempt
                            )

                            AppDestination.MY_LEARNING -> key(state.authUid ?: "guest") {
                                MyLearningScreen(
                                    language = language,
                                    isSignedIn = state.isSignedIn,
                                    userDisplayName = state.authDisplayName,
                                    userEmail = state.authEmail,
                                    userRole = state.userRole,
                                    completedLessonIds = state.completedLessonIds,
                                    savedStructureIds = state.savedStructureIds,
                                    bookmarkedTermIds = state.bookmarkedTermIds,
                                    quizAttempts = state.quizAttempts,
                                    activityLog = state.activityLog,
                                    statusMessage = state.learningStatusMessage,
                                    onGoogleSignInClick = triggerGoogleSignIn,
                                    onSignOutClick = triggerSignOut,
                                    onOpenStructure = { viewModel.openAnatomyStructure(it, openDetailSheet = false) },
                                    onOpenDictionary = { viewModel.openDictionaryWithSearch("") },
                                    onOpenAcademy = { viewModel.navigateTo(AppDestination.ACADEMY) },
                                    onBuildExportJson = viewModel::buildLearningExportJson,
                                    onResetLocalProgress = viewModel::resetLocalProgress,
                                    onDeleteCloudData = viewModel::deleteCloudLearningData
                                )
                            }

                            AppDestination.ABOUT -> AboutScreen(
                                language = language,
                                onOpenProvenance = { viewModel.setShowProvenanceDialog(true) }
                            )

                            AppDestination.ADMIN -> key(state.authUid ?: "guest_admin") {
                                AdminCmsScreen(
                                    language = language,
                                    isSignedIn = state.isSignedIn,
                                    userEmail = state.authEmail,
                                    userRole = state.userRole,
                                    cmsRecords = state.cmsRecords,
                                    auditLogs = state.auditLogs,
                                    cmsStatusMessage = state.cmsStatusMessage,
                                    onGoogleSignInClick = triggerGoogleSignIn,
                                    onCreateDraft = viewModel::createCmsDraft,
                                    onTransitionStatus = viewModel::transitionCmsStatus,
                                    onOpenProvenance = { viewModel.setShowProvenanceDialog(true) }
                                )
                            }
                        }
                    }
                }
            }

            // Unified Platform Navigation Drawer Sheet (All 12 Destinations)
            if (showPlatformHubSheet) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { showPlatformHubSheet = false },
                    sheetState = sheetState,
                    containerColor = MedPanelNavy
                ) {
                    PlatformNavigationSheet(
                        activeDestination = state.activeDestination,
                        language = language,
                        onSelectDestination = { dest ->
                            showPlatformHubSheet = false
                            viewModel.navigateTo(dest)
                        }
                    )
                }
            }

            if (state.showProvenanceDialog) {
                AssetProvenanceDialog(
                    metadata = state.scene3D?.metadata,
                    phaseTwoAssets = viewModel.provenanceRepository.getPhaseTwoRequiredAssets(),
                    language = language,
                    onImportGlbClick = openGlbImporter,
                    onRestoreDefault = viewModel::loadDefaultCardiovascularScene,
                    onDismiss = { viewModel.setShowProvenanceDialog(false) }
                )
            }
        }
    }
}

@Composable
private fun LabsSubNavStrip(
    activeDestination: AppDestination,
    language: AppLanguage,
    onSelectDestination: (AppDestination) -> Unit
) {
    val labTabs = listOf(
        Triple(AppDestination.PHYSIOLOGY, Icons.Default.MonitorHeart, UiStrings.navPhysiology.get(language)),
        Triple(AppDestination.PATHOLOGY, Icons.Default.Biotech, UiStrings.navPathology.get(language)),
        Triple(AppDestination.PHARMACOLOGY, Icons.Default.Medication, UiStrings.navPharmacology.get(language))
    )
    Surface(color = MedObsidianNavy, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labTabs.forEach { (dest, icon, label) ->
                val selected = activeDestination == dest
                Surface(
                    modifier = Modifier
                        .clickable { onSelectDestination(dest) }
                        .testTag("lab_subtab_${dest.route}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) MedCyan else MedPanelNavy,
                    border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selected) MedObsidianNavy else MedCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = label,
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

@Composable
private fun PlatformNavigationSheet(
    activeDestination: AppDestination,
    language: AppLanguage,
    onSelectDestination: (AppDestination) -> Unit
) {
    val entries = listOf(
        PlatformMenuEntry(AppDestination.DICTIONARY, Icons.Default.Book, UiStrings.navDictionary.en, UiStrings.navDictionary.ar, "menu_dictionary"),
        PlatformMenuEntry(AppDestination.ACADEMY, Icons.Default.School, UiStrings.navAcademy.en, UiStrings.navAcademy.ar, "menu_academy"),
        PlatformMenuEntry(AppDestination.ASSESSMENTS, Icons.Default.Quiz, UiStrings.navAssessments.en, UiStrings.navAssessments.ar, "menu_assessments"),
        PlatformMenuEntry(AppDestination.MY_LEARNING, Icons.Default.Person, UiStrings.navMyLearning.en, UiStrings.navMyLearning.ar, "menu_my_learning"),
        PlatformMenuEntry(AppDestination.PATHOLOGY, Icons.Default.Biotech, UiStrings.navPathology.en, UiStrings.navPathology.ar, "menu_pathology"),
        PlatformMenuEntry(AppDestination.PHARMACOLOGY, Icons.Default.Medication, UiStrings.navPharmacology.en, UiStrings.navPharmacology.ar, "menu_pharmacology"),
        PlatformMenuEntry(AppDestination.PHYSIOLOGY, Icons.Default.MonitorHeart, UiStrings.navPhysiology.en, UiStrings.navPhysiology.ar, "menu_physiology"),
        PlatformMenuEntry(AppDestination.ADMIN, Icons.Default.AdminPanelSettings, UiStrings.navAdmin.en, UiStrings.navAdmin.ar, "menu_admin"),
        PlatformMenuEntry(AppDestination.ABOUT, Icons.Default.Info, UiStrings.navAbout.en, UiStrings.navAbout.ar, "menu_about")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (language == AppLanguage.ARABIC) "جميع وحدات منصة عالم الطب التفاعلي" else "All MEDVERSE 3D Platform Modules",
            style = MaterialTheme.typography.titleMedium,
            color = MedCyan,
            fontWeight = FontWeight.Bold
        )
        entries.forEach { entry ->
            val selected = activeDestination == entry.destination
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDestination(entry.destination) }
                    .testTag(entry.testTag),
                shape = RoundedCornerShape(12.dp),
                color = if (selected) MedCyanContainer else MedDeepNavy,
                border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = entry.icon,
                        contentDescription = null,
                        tint = if (selected) MedCyan else MedTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (language.isRtl) entry.labelAr else entry.labelEn,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MedVerseTopBar(
    language: AppLanguage,
    isSignedIn: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenProvenance: () -> Unit,
    onOpenMyLearning: () -> Unit
) {
    Surface(
        color = MedDeepNavy,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_medverse_logo_1795214354550),
                        contentDescription = stringResource(id = R.string.cd_app_logo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Column {
                        Text(
                            text = UiStrings.brandTitle.get(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = UiStrings.brandSubtitle.get(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MedCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenMyLearning,
                        modifier = Modifier.testTag("topbar_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = UiStrings.navMyLearning.get(language),
                            tint = if (isSignedIn) MedVerifiedGreen else MedTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onOpenProvenance,
                        modifier = Modifier.testTag("topbar_provenance_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = UiStrings.toolProvenance.get(language),
                            tint = MedCyan
                        )
                    }
                    LanguageToggleChip(
                        language = language,
                        onToggle = onToggleLanguage
                    )
                }
            }
            HorizontalDivider(color = MedBorderHairline)
        }
    }
}
