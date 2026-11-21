package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.academy.AcademyLesson
import com.example.ai.AiMedicalTutorRepository
import com.example.ai.AiStreamEvent
import com.example.ai.MedicalAiSafetyGuard
import com.example.ai.TutorInstructionMode
import com.example.ai.TutorMessage
import com.example.ai.ValidatedActionTarget
import com.example.ai.ValidatedTutorAction
import com.example.core.i18n.AppLanguage
import com.example.core.model.AnatomicalStructure
import com.example.core.model.BodySystem
import com.example.core.model.DictionaryCategory
import com.example.core.model.LearningGuideItem
import com.example.core.model.MedicalTerm
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.LocalAnatomyRepository
import com.example.data.repository.LocalAssetProvenanceRepository
import com.example.data.repository.LocalTerminologyRepository
import com.example.engine3d.Camera3DState
import com.example.engine3d.CameraPreset
import com.example.engine3d.CardiovascularSchematicBuilder
import com.example.engine3d.GltfModelParser
import com.example.engine3d.LightingConfig
import com.example.engine3d.Scene3D
import com.example.engine3d.ViewportBackdrop
import com.example.firebase.CmsContentRecord
import com.example.firebase.EditorialAuditLog
import com.example.firebase.EditorialStatus
import com.example.firebase.LearningActivityRecord
import com.example.firebase.MedVerseCloudRepository
import com.example.firebase.QuizAttemptRecord
import com.example.firebase.UserLearningProfile
import com.example.firebase.UserRole
import com.example.knowledge.KnowledgeEntityType
import com.example.knowledge.MedicalKnowledgeGraph
import com.example.knowledge.UnifiedSearchResult
import com.example.pathology.DiseaseRecord
import com.example.pathology.PathologySeed
import com.example.pharmacology.DrugClassRecord
import com.example.pharmacology.PharmacologySeed
import com.example.physiology.engine.CirculationFilterMode
import com.example.physiology.engine.PhysiologySimulationEngine
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.RenderQualityMode
import com.example.physiology.engine.SimulationState
import com.example.physiology.learning.GuidedLessonStep
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

enum class AppDestination(val route: String) {
    HOME("home"),
    ANATOMY("anatomy_explorer"),
    PHYSIOLOGY("physiology_lab"),
    PATHOLOGY("pathology_atlas"),
    PHARMACOLOGY("pharmacology_lab"),
    DICTIONARY("medical_dictionary"),
    AI_TUTOR("ai_medical_tutor"),
    ACADEMY("learning_academy"),
    ASSESSMENTS("assessments"),
    MY_LEARNING("my_learning"),
    ABOUT("about"),
    ADMIN("administration")
}

enum class LabModuleTab {
    LEARNING_CENTER,
    PHYSIOLOGY,
    PATHOLOGY,
    PHARMACOLOGY
}

data class MedVerseUiState(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val activeDestination: AppDestination = AppDestination.HOME,
    val activeLabTab: LabModuleTab = LabModuleTab.LEARNING_CENTER,

    // Anatomy Explorer Data & Selection
    val bodySystems: List<BodySystem> = emptyList(),
    val selectedSystemId: String = "cardiovascular",
    val structures: List<AnatomicalStructure> = emptyList(),
    val structureSearchQuery: String = "",
    val selectedStructureId: String = "heart",
    val learningGuides: List<LearningGuideItem> = emptyList(),
    val savedStructureIds: Set<String> = emptySet(),

    // 3D Viewport State
    val scene3D: Scene3D? = null,
    val cameraState: Camera3DState = Camera3DState(),
    val lightingConfig: LightingConfig = LightingConfig(),
    val backdrop: ViewportBackdrop = ViewportBackdrop.NAVY_GRID,
    val isolateSelected: Boolean = false,
    val xrayMode: Boolean = false,
    val panMode: Boolean = false,
    val isModelLoading: Boolean = true,
    val modelError: String? = null,
    val isFullscreenViewer: Boolean = false,
    val showProvenanceDialog: Boolean = false,
    val showDetailSheet: Boolean = false,

    // Phase 03 Physiology Lab UI & Persisted Progress State
    val isPhysiologyFullscreen: Boolean = false,
    val completedLessonIds: Set<String> = emptySet(),
    val correctQuizIds: Set<String> = emptySet(),

    // Phase 06 Pathology & Pharmacology Selection
    val selectedDiseaseId: String = "aortic_stenosis",
    val selectedDrugClassId: String = "beta_blockers",

    // Medical Dictionary State
    val dictionaryQuery: String = "",
    val selectedCategory: DictionaryCategory = DictionaryCategory.ALL,
    val bookmarkedOnly: Boolean = false,
    val bookmarkedTermIds: Set<String> = emptySet(),
    val filteredTerms: List<MedicalTerm> = emptyList(),
    val selectedTermId: String? = null,

    // Phase 06 Unified Smart Bilingual Search
    val unifiedSearchQuery: String = "",
    val unifiedSearchFilter: KnowledgeEntityType? = null,
    val unifiedSearchResults: List<UnifiedSearchResult> = emptyList(),

    // Phase 06 AI Medical Tutor (Firebase AI Logic)
    val aiMessages: List<TutorMessage> = emptyList(),
    val aiTutorMode: TutorInstructionMode = TutorInstructionMode.STANDARD,
    val isAiStreaming: Boolean = false,
    val lastAiQuestion: String? = null,

    // Phase 06 Cloud Auth, Learning Sync & Protected Medical CMS
    val authUid: String? = null,
    val authDisplayName: String? = null,
    val authEmail: String? = null,
    val userRole: UserRole = UserRole.GUEST,
    val cloudProfile: UserLearningProfile? = null,
    val quizAttempts: List<QuizAttemptRecord> = emptyList(),
    val activityLog: List<LearningActivityRecord> = emptyList(),
    val cmsRecords: List<CmsContentRecord> = emptyList(),
    val auditLogs: List<EditorialAuditLog> = emptyList(),
    val learningStatusMessage: String? = null,
    val cmsStatusMessage: String? = null
) {
    val isSignedIn: Boolean get() = !authUid.isNullOrBlank()

    val selectedStructure: AnatomicalStructure?
        get() = structures.firstOrNull { it.id == selectedStructureId }

    val selectedMeshNodeId: String?
        get() = selectedStructure?.meshNodeId ?: selectedStructureId

    val filteredStructures: List<AnatomicalStructure>
        get() {
            val q = LocalTerminologyRepository.normalizeArabicAndLatin(structureSearchQuery)
            if (q.isBlank()) return structures
            return structures.filter { s ->
                LocalTerminologyRepository.normalizeArabicAndLatin(s.englishName).contains(q) ||
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.arabicName).contains(q) ||
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.scientificTerminology).contains(q)
            }
        }

    val selectedTerm: MedicalTerm?
        get() = filteredTerms.firstOrNull { it.id == selectedTermId }
}

class MedVerseViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = UserPreferencesRepository(application.applicationContext)
    private val anatomyRepository = LocalAnatomyRepository()
    private val terminologyRepository = LocalTerminologyRepository()
    val provenanceRepository = LocalAssetProvenanceRepository()
    private val aiTutorRepository = AiMedicalTutorRepository()
    private val cloudRepository by lazy { MedVerseCloudRepository(application.applicationContext) }

    val simulationEngine = PhysiologySimulationEngine()
    val physiologyState: StateFlow<SimulationState> = simulationEngine.state

    private val _uiState = MutableStateFlow(
        MedVerseUiState(
            bodySystems = anatomyRepository.getBodySystems(),
            structures = anatomyRepository.getStructuresBySystem("cardiovascular"),
            learningGuides = anatomyRepository.getLearningGuides(),
            filteredTerms = terminologyRepository.getAllTerms()
        )
    )
    val uiState: StateFlow<MedVerseUiState> = _uiState.asStateFlow()

    private var aiStreamingJob: Job? = null
    private val cloudObserverJobs = mutableListOf<Job>()

    init {
        simulationEngine.initialize(scenarioId = "cardio_normal_cycle", autoStart = true)
        observePreferences()
        loadDefaultCardiovascularScene()
    }

    override fun onCleared() {
        aiStreamingJob?.cancel()
        cancelCloudObservers()
        simulationEngine.dispose()
        super.onCleared()
    }

    // -------------------------------------------------------------------------
    // Authenticated Cloud Lifecycle (Strict Zero-Query While Signed Out)
    // -------------------------------------------------------------------------

    fun prepareForSignOut() {
        cancelCloudObservers()
    }

    private fun cancelCloudObservers() {
        cloudObserverJobs.forEach { it.cancel() }
        cloudObserverJobs.clear()
    }

    fun onAuthUserChanged(user: FirebaseUser?) {
        val previousUid = _uiState.value.authUid
        if (user == null) {
            cancelCloudObservers()
            _uiState.update {
                it.copy(
                    authUid = null,
                    authDisplayName = null,
                    authEmail = null,
                    userRole = UserRole.GUEST,
                    cloudProfile = null,
                    quizAttempts = emptyList(),
                    activityLog = emptyList(),
                    cmsRecords = emptyList(),
                    auditLogs = emptyList(),
                    learningStatusMessage = null,
                    cmsStatusMessage = null
                )
            }
            return
        }

        if (previousUid == user.uid && cloudObserverJobs.isNotEmpty()) return

        cancelCloudObservers()
        _uiState.update {
            it.copy(
                authUid = user.uid,
                authDisplayName = user.displayName ?: user.email?.substringBefore("@") ?: "Learner",
                authEmail = user.email,
                learningStatusMessage = null
            )
        }

        val uid = user.uid
        val bootstrapJob = viewModelScope.launch {
            val role = cloudRepository.resolveEffectiveRole().getOrDefault(UserRole.STUDENT)
            _uiState.update { it.copy(userRole = role) }

            val current = _uiState.value
            cloudRepository.ensureUserProfile(
                displayName = current.authDisplayName ?: "MedVerse Learner",
                preferredLanguage = current.language.code,
                localCompletedLessons = current.completedLessonIds,
                localBookmarkedTerms = current.bookmarkedTermIds
            )

            startCloudObservers(uid, role)
        }
        cloudObserverJobs.add(bootstrapJob)
    }

    private fun startCloudObservers(uid: String, role: UserRole) {
        // 1. User Learning Profile Observer
        cloudObserverJobs.add(
            viewModelScope.launch {
                try {
                    cloudRepository.observeUserProfile(uid).collect { profile ->
                        if (profile != null) {
                            _uiState.update { it.copy(cloudProfile = profile) }
                            preferencesRepository.mergeCloudProgress(
                                completedLessons = profile.completedLessonIds.toSet(),
                                savedStructures = profile.savedStructureIds.toSet(),
                                bookmarkedTerms = profile.bookmarkedTermIds.toSet()
                            )
                        }
                    }
                } catch (e: Throwable) {
                    handleCloudObserverFailure(e, "profile")
                }
            }
        )

        // 2. Quiz Attempts Observer
        cloudObserverJobs.add(
            viewModelScope.launch {
                try {
                    cloudRepository.observeQuizAttempts(uid).collect { attempts ->
                        _uiState.update { it.copy(quizAttempts = attempts) }
                    }
                } catch (e: Throwable) {
                    handleCloudObserverFailure(e, "quizAttempts")
                }
            }
        )

        // 3. Learning Activity Log Observer
        cloudObserverJobs.add(
            viewModelScope.launch {
                try {
                    cloudRepository.observeActivityLog(uid).collect { logs ->
                        _uiState.update { it.copy(activityLog = logs) }
                    }
                } catch (e: Throwable) {
                    handleCloudObserverFailure(e, "activityLog")
                }
            }
        )

        // 4. CMS Content Observer (Students only query status == PUBLISHED; Editors+ see queue)
        cloudObserverJobs.add(
            viewModelScope.launch {
                try {
                    cloudRepository.observeCmsContent(canInspectDrafts = role.canEditCms).collect { entries ->
                        _uiState.update { it.copy(cmsRecords = entries) }
                    }
                } catch (e: Throwable) {
                    handleCloudObserverFailure(e, "cmsContent")
                }
            }
        )

        // 5. Append-Only Audit Log Observer (Reviewers & Admins only)
        if (role.canReviewCms) {
            cloudObserverJobs.add(
                viewModelScope.launch {
                    try {
                        cloudRepository.observeAuditLogs().collect { logs ->
                            _uiState.update { it.copy(auditLogs = logs) }
                        }
                    } catch (e: Throwable) {
                        handleCloudObserverFailure(e, "auditLogs")
                    }
                }
            )
        }
    }

    private fun handleCloudObserverFailure(error: Throwable, tag: String) {
        val fsError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (fsError == null && error is CancellationException) throw error
        Log.e("MedVerseCloud", "Observer failure [$tag]: ${fsError?.message ?: error.message}", error)
    }

    // -------------------------------------------------------------------------
    // Local Preferences & Progress Observation
    // -------------------------------------------------------------------------

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.bookmarkedTermIdsFlow.collect { bookmarks ->
                _uiState.update { state ->
                    val updatedTerms = terminologyRepository.searchTerms(
                        query = state.dictionaryQuery,
                        category = state.selectedCategory,
                        bookmarkedOnly = state.bookmarkedOnly,
                        bookmarkedIds = bookmarks
                    )
                    state.copy(
                        bookmarkedTermIds = bookmarks,
                        filteredTerms = updatedTerms
                    )
                }
            }
        }
        viewModelScope.launch {
            preferencesRepository.completedLessonIdsFlow.collect { completed ->
                _uiState.update { it.copy(completedLessonIds = completed) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.correctQuizIdsFlow.collect { correctIds ->
                _uiState.update { it.copy(correctQuizIds = correctIds) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.savedStructureIdsFlow.collect { savedIds ->
                _uiState.update { it.copy(savedStructureIds = savedIds) }
            }
        }
    }

    // -------------------------------------------------------------------------
    // 3D Scene & GLB Importer
    // -------------------------------------------------------------------------

    fun loadDefaultCardiovascularScene() {
        viewModelScope.launch {
            _uiState.update { it.copy(isModelLoading = true, modelError = null) }
            val scene = withContext(Dispatchers.Default) {
                CardiovascularSchematicBuilder.buildDefaultScene(provenanceRepository)
            }
            _uiState.update {
                it.copy(
                    scene3D = scene,
                    isModelLoading = false,
                    modelError = null,
                    cameraState = Camera3DState()
                )
            }
        }
    }

    fun importGlbModel(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isModelLoading = true, modelError = null) }
            val result = withContext(Dispatchers.IO) {
                GltfModelParser.loadFromUri(getApplication(), uri)
            }
            result.fold(
                onSuccess = { loadedScene ->
                    _uiState.update {
                        it.copy(
                            scene3D = loadedScene,
                            isModelLoading = false,
                            modelError = null,
                            cameraState = Camera3DState(),
                            isolateSelected = false,
                            xrayMode = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isModelLoading = false,
                            modelError = error.message ?: "Unable to parse selected GLB/GLTF file."
                        )
                    }
                }
            )
        }
    }

    // -------------------------------------------------------------------------
    // Language & Navigation
    // -------------------------------------------------------------------------

    fun toggleLanguage() {
        val next = _uiState.value.language.toggled()
        _uiState.update { it.copy(language = next) }
        viewModelScope.launch {
            preferencesRepository.setLanguage(next)
            if (_uiState.value.isSignedIn) {
                cloudRepository.syncProgressFields(preferredLanguage = next.code)
            }
        }
    }

    fun navigateTo(destination: AppDestination) {
        _uiState.update {
            it.copy(
                activeDestination = destination,
                isFullscreenViewer = false,
                isPhysiologyFullscreen = false
            )
        }
    }

    fun openLabModule(tab: LabModuleTab) {
        when (tab) {
            LabModuleTab.PHYSIOLOGY -> navigateTo(AppDestination.PHYSIOLOGY)
            LabModuleTab.PATHOLOGY -> navigateTo(AppDestination.PATHOLOGY)
            LabModuleTab.PHARMACOLOGY -> navigateTo(AppDestination.PHARMACOLOGY)
            LabModuleTab.LEARNING_CENTER -> navigateTo(AppDestination.ACADEMY)
        }
    }

    // -------------------------------------------------------------------------
    // Unified Smart Bilingual Search (Phase 06)
    // -------------------------------------------------------------------------

    fun updateUnifiedSearch(query: String, filter: KnowledgeEntityType? = _uiState.value.unifiedSearchFilter) {
        val results = MedicalKnowledgeGraph.searchAll(query, filter)
        _uiState.update {
            it.copy(
                unifiedSearchQuery = query,
                unifiedSearchFilter = filter,
                unifiedSearchResults = results
            )
        }
    }

    fun openUnifiedSearchResult(result: UnifiedSearchResult) {
        when (result.entityType) {
            KnowledgeEntityType.ANATOMY -> {
                result.targetStructureId?.let { openAnatomyStructure(it, openDetailSheet = false) }
            }
            KnowledgeEntityType.PHYSIOLOGY -> {
                result.targetPhysiologySystem?.let { openPhysiologySystem(it) }
            }
            KnowledgeEntityType.PATHOLOGY -> {
                result.targetDiseaseId?.let { openDisease(it) }
            }
            KnowledgeEntityType.PHARMACOLOGY -> {
                result.targetDrugClassId?.let { openDrugClass(it) }
            }
            KnowledgeEntityType.TERMINOLOGY -> {
                openDictionaryWithSearch(if (_uiState.value.language == AppLanguage.ARABIC) result.titleAr else result.titleEn)
                result.targetTermId?.let { selectDictionaryTerm(it) }
            }
            KnowledgeEntityType.ACADEMY_LESSON -> {
                navigateTo(AppDestination.ACADEMY)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Pathology & Pharmacology Actions (Phase 06)
    // -------------------------------------------------------------------------

    fun openDisease(diseaseId: String) {
        val d = PathologySeed.findById(diseaseId) ?: return
        _uiState.update {
            it.copy(
                activeDestination = AppDestination.PATHOLOGY,
                selectedDiseaseId = d.id,
                isFullscreenViewer = false,
                isPhysiologyFullscreen = false
            )
        }
        logActivityIfSignedIn("PATHOLOGY", d.id, d.englishName, d.arabicName)
    }

    fun selectDisease(diseaseId: String) {
        _uiState.update { it.copy(selectedDiseaseId = diseaseId) }
    }

    fun openDrugClass(drugClassId: String) {
        val drug = PharmacologySeed.findById(drugClassId) ?: return
        _uiState.update {
            it.copy(
                activeDestination = AppDestination.PHARMACOLOGY,
                selectedDrugClassId = drug.id,
                isFullscreenViewer = false,
                isPhysiologyFullscreen = false
            )
        }
        logActivityIfSignedIn("PHARMACOLOGY", drug.id, drug.englishName, drug.arabicName)
    }

    fun selectDrugClass(drugClassId: String) {
        _uiState.update { it.copy(selectedDrugClassId = drugClassId) }
    }

    // -------------------------------------------------------------------------
    // AI Medical Tutor (Firebase AI Logic Streaming + RAG Grounding)
    // -------------------------------------------------------------------------

    fun setAiTutorMode(mode: TutorInstructionMode) {
        _uiState.update { it.copy(aiTutorMode = mode) }
    }

    fun clearAiConversation() {
        aiStreamingJob?.cancel()
        _uiState.update { it.copy(aiMessages = emptyList(), isAiStreaming = false, lastAiQuestion = null) }
    }

    fun openAiTutorWithStructureContext(structureId: String) {
        val s = anatomyRepository.findStructureById(structureId)
        _uiState.update {
            it.copy(
                selectedStructureId = structureId,
                activeDestination = AppDestination.AI_TUTOR,
                showDetailSheet = false
            )
        }
        if (s != null) {
            val lang = _uiState.value.language
            val prompt = if (lang == AppLanguage.ARABIC) {
                "اشرح البنية التشريحية ووظيفة ${s.arabicName} (${s.englishName}) مع المصادر العلمية."
            } else {
                "Explain the anatomy, function, and clinical significance of the ${s.englishName} (${s.arabicName})."
            }
            askAiTutor(prompt)
        }
    }

    fun openAiTutorWithDiseaseContext(disease: DiseaseRecord) {
        _uiState.update {
            it.copy(
                selectedDiseaseId = disease.id,
                activeDestination = AppDestination.AI_TUTOR
            )
        }
        val lang = _uiState.value.language
        val prompt = if (lang == AppLanguage.ARABIC) {
            "اشرح الآلية الإمراضية في ${disease.arabicName} وقارنها بالوضع السليم."
        } else {
            "Explain the pathophysiology of ${disease.englishName} and contrast normal vs. diseased states."
        }
        askAiTutor(prompt)
    }

    fun openAiTutorWithDrugContext(drug: DrugClassRecord) {
        _uiState.update {
            it.copy(
                selectedDrugClassId = drug.id,
                activeDestination = AppDestination.AI_TUTOR
            )
        }
        val lang = _uiState.value.language
        val prompt = if (lang == AppLanguage.ARABIC) {
            "اشرح آلية عمل ${drug.arabicName} (${drug.prototypeGenericAr}) على الهدف الجزيئي مع أهم اعتبارات السلامة التعليمية."
        } else {
            "Explain the molecular mechanism of action of ${drug.englishName} (${drug.prototypeGenericEn}) and key educational safety considerations."
        }
        askAiTutor(prompt)
    }

    fun getActiveStudyContextLabel(): String {
        val s = _uiState.value
        val lang = s.language
        val struct = s.selectedStructure
        val disease = PathologySeed.findById(s.selectedDiseaseId)
        val drug = PharmacologySeed.findById(s.selectedDrugClassId)
        val physLabel = physiologyState.value.activeSystem.shortLabel.get(lang)
        val structLabel = struct?.let { if (lang == AppLanguage.ARABIC) it.arabicName else it.englishName } ?: ""
        val diseaseLabel = disease?.let { if (lang == AppLanguage.ARABIC) it.arabicName else it.englishName } ?: ""
        val drugLabel = drug?.let { if (lang == AppLanguage.ARABIC) it.prototypeGenericAr.substringBefore(" (") else it.prototypeGenericEn.substringBefore(" (") } ?: ""
        return listOf(structLabel, physLabel, diseaseLabel, drugLabel).filter { it.isNotBlank() }.joinToString(" • ")
    }

    fun retryLastAiQuestion() {
        val last = _uiState.value.lastAiQuestion ?: return
        askAiTutor(last)
    }

    fun askAiTutor(rawQuestion: String) {
        val sanitized = MedicalAiSafetyGuard.sanitizeInput(rawQuestion)
        if (sanitized.isBlank()) return

        val state = _uiState.value
        val lang = state.language
        val userMsg = TutorMessage(
            id = "u_${UUID.randomUUID().toString().take(8)}",
            isUser = true,
            text = sanitized
        )

        val isEmergency = MedicalAiSafetyGuard.isEmergencyQuery(sanitized)
        val isPersonal = MedicalAiSafetyGuard.isPersonalMedicalAdviceRequest(sanitized)

        val retrievedContext = MedicalKnowledgeGraph.retrieveGroundedContext(
            question = sanitized,
            activeStructureId = state.selectedStructureId,
            activePhysiologySystem = physiologyState.value.activeSystem,
            activeDiseaseId = state.selectedDiseaseId,
            activeDrugClassId = state.selectedDrugClassId
        )
        val validatedActions = MedicalAiSafetyGuard.resolveValidatedActions(sanitized, retrievedContext)
        val citations = retrievedContext.flatMap { it.sources }.distinctBy { it.id }

        // Immediate Emergency Red-Flag Safety Response (no risky delay)
        if (isEmergency) {
            val emergencyMsg = TutorMessage(
                id = "a_${UUID.randomUUID().toString().take(8)}",
                isUser = false,
                text = if (lang == AppLanguage.ARABIC) {
                    "تُشير عباراتك إلى أعراض طارئة محتملة. يرجى الاتصال فوراً بخدمات الإسعاف المحلية أو التوجه إلى أقرب قسم طوارئ. منصة عالم الطب التفاعلي مخصصة للتعليم الطبي فقط ولا يمكنها تقديم رعاية طارئة."
                } else {
                    "Your message mentions potentially acute or life-threatening symptoms. Please contact your local emergency services or go to the nearest emergency department immediately. MEDVERSE 3D is an educational platform only and cannot provide emergency medical care."
                },
                emergencyWarning = true,
                personalMedicalDisclaimer = true,
                validatedActions = validatedActions
            )
            _uiState.update {
                it.copy(
                    aiMessages = it.aiMessages + userMsg + emergencyMsg,
                    isAiStreaming = false,
                    lastAiQuestion = sanitized
                )
            }
            return
        }

        val assistantId = "a_${UUID.randomUUID().toString().take(8)}"
        val placeholderAssistant = TutorMessage(
            id = assistantId,
            isUser = false,
            text = "",
            citations = citations,
            personalMedicalDisclaimer = isPersonal,
            validatedActions = validatedActions,
            isStreaming = true
        )

        _uiState.update {
            it.copy(
                aiMessages = it.aiMessages + userMsg + placeholderAssistant,
                isAiStreaming = true,
                lastAiQuestion = sanitized
            )
        }

        val groundedPrompt = MedicalAiSafetyGuard.buildGroundedPrompt(
            sanitizedQuestion = sanitized,
            language = lang,
            mode = state.aiTutorMode,
            retrievedContext = retrievedContext,
            isPersonalRequest = isPersonal
        )

        aiStreamingJob?.cancel()
        aiStreamingJob = viewModelScope.launch {
            aiTutorRepository.streamGroundedAnswer(groundedPrompt).collect { event ->
                when (event) {
                    is AiStreamEvent.Streaming -> {
                        updateAssistantMessage(assistantId) {
                            it.copy(text = event.accumulatedText, isStreaming = true, errorMessage = null)
                        }
                    }
                    is AiStreamEvent.Completed -> {
                        updateAssistantMessage(assistantId) {
                            it.copy(text = event.finalText, isStreaming = false, errorMessage = null)
                        }
                        _uiState.update { it.copy(isAiStreaming = false) }
                        logActivityIfSignedIn("AI_TUTOR", assistantId, sanitized.take(80), sanitized.take(80))
                    }
                    is AiStreamEvent.Error -> {
                        updateAssistantMessage(assistantId) {
                            it.copy(isStreaming = false, errorMessage = event.message)
                        }
                        _uiState.update { it.copy(isAiStreaming = false) }
                    }
                }
            }
        }
    }

    private fun updateAssistantMessage(id: String, transform: (TutorMessage) -> TutorMessage) {
        _uiState.update { state ->
            state.copy(
                aiMessages = state.aiMessages.map { msg -> if (msg.id == id) transform(msg) else msg }
            )
        }
    }

    fun executeValidatedTutorAction(action: ValidatedTutorAction) {
        when (action.type) {
            ValidatedActionTarget.OPEN_ANATOMY_STRUCTURE -> openAnatomyStructure(action.targetId, openDetailSheet = false)
            ValidatedActionTarget.OPEN_PHYSIOLOGY_SYSTEM -> {
                val sys = PhysiologySystemId.entries.firstOrNull { it.id == action.targetId } ?: PhysiologySystemId.CARDIOVASCULAR
                openPhysiologySystem(sys)
            }
            ValidatedActionTarget.OPEN_PATHOLOGY_DISEASE -> openDisease(action.targetId)
            ValidatedActionTarget.OPEN_PHARMACOLOGY_CLASS -> openDrugClass(action.targetId)
        }
    }

    // -------------------------------------------------------------------------
    // Learning Academy, Assessments, Bookmarks & Privacy Actions
    // -------------------------------------------------------------------------

    fun toggleSavedStructure(structureId: String) {
        val struct = anatomyRepository.findStructureById(structureId) ?: return
        viewModelScope.launch {
            preferencesRepository.toggleSavedStructure(structureId)
            val nextSet = if (structureId in _uiState.value.savedStructureIds) {
                _uiState.value.savedStructureIds - structureId
            } else {
                _uiState.value.savedStructureIds + structureId
            }
            if (_uiState.value.isSignedIn) {
                cloudRepository.syncProgressFields(savedStructures = nextSet)
                cloudRepository.logLearningActivity("ANATOMY", struct.id, "Saved ${struct.englishName}", "حفظ ${struct.arabicName}")
            }
        }
    }

    fun toggleAcademyLessonCompleted(lessonId: String, lesson: AcademyLesson) {
        viewModelScope.launch {
            preferencesRepository.toggleLessonCompleted(lessonId)
            val nextSet = if (lessonId in _uiState.value.completedLessonIds) {
                _uiState.value.completedLessonIds - lessonId
            } else {
                _uiState.value.completedLessonIds + lessonId
            }
            if (_uiState.value.isSignedIn) {
                cloudRepository.syncProgressFields(completedLessons = nextSet)
                if (lessonId in nextSet) {
                    cloudRepository.logLearningActivity("LESSON", lessonId, lesson.title.en, lesson.title.ar)
                }
            }
        }
    }

    fun submitAssessmentAttempt(
        subject: String,
        difficulty: String,
        score: Int,
        totalQuestions: Int,
        correctQuestionIds: List<String>
    ) {
        viewModelScope.launch {
            correctQuestionIds.forEach { qId ->
                preferencesRepository.recordCorrectQuizAnswer(qId)
            }
            if (_uiState.value.isSignedIn) {
                cloudRepository.recordQuizAttempt(subject, difficulty, score, totalQuestions)
                cloudRepository.logLearningActivity(
                    activityType = "QUIZ",
                    targetId = subject,
                    titleEn = "Assessment $subject ($score/$totalQuestions)",
                    titleAr = "اختبار $subject ($score/$totalQuestions)"
                )
            }
        }
    }

    fun buildLearningExportJson(): String {
        val s = _uiState.value
        val lessonsJson = s.completedLessonIds.joinToString(",") { "\"$it\"" }
        val structuresJson = s.savedStructureIds.joinToString(",") { "\"$it\"" }
        val termsJson = s.bookmarkedTermIds.joinToString(",") { "\"$it\"" }
        return """
{
  "platform": "MEDVERSE 3D",
  "language": "${s.language.code}",
  "accountMode": "${if (s.isSignedIn) "AUTHENTICATED_CLOUD" else "LOCAL_GUEST"}",
  "role": "${s.userRole.name}",
  "completedLessonIds": [$lessonsJson],
  "savedStructureIds": [$structuresJson],
  "bookmarkedTermIds": [$termsJson],
  "recordedCloudQuizAttempts": ${s.quizAttempts.size}
}
        """.trimIndent()
    }

    fun resetLocalProgress() {
        viewModelScope.launch {
            preferencesRepository.resetAllLocalProgress()
            _uiState.update {
                it.copy(
                    learningStatusMessage = if (it.language == AppLanguage.ARABIC) {
                        "تمت إعادة ضبط التقدم المحلي على هذا الجهاز."
                    } else {
                        "Local learning progress has been reset on this device."
                    }
                )
            }
        }
    }

    fun deleteCloudLearningData() {
        if (!_uiState.value.isSignedIn) return
        viewModelScope.launch {
            val result = cloudRepository.deleteAllUserLearningData()
            preferencesRepository.resetAllLocalProgress()
            _uiState.update { state ->
                state.copy(
                    cloudProfile = null,
                    quizAttempts = emptyList(),
                    activityLog = emptyList(),
                    learningStatusMessage = result.fold(
                        onSuccess = {
                            if (state.language == AppLanguage.ARABIC) "تم حذف جميع بيانات التعلم السحابية نهائياً."
                            else "All cloud learning records have been permanently deleted."
                        },
                        onFailure = { err -> err.localizedMessage ?: "Failed to delete cloud learning data." }
                    )
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Protected Medical CMS Actions
    // -------------------------------------------------------------------------

    fun createCmsDraft(
        titleEn: String,
        titleAr: String,
        contentType: String,
        systemId: String,
        summaryEn: String,
        summaryAr: String,
        references: String
    ) {
        viewModelScope.launch {
            val result = cloudRepository.createCmsDraft(
                titleEn = titleEn,
                titleAr = titleAr,
                contentType = contentType,
                systemId = systemId,
                summaryEn = summaryEn,
                summaryAr = summaryAr,
                scientificReferences = references
            )
            _uiState.update { state ->
                state.copy(
                    cmsStatusMessage = result.fold(
                        onSuccess = { docId ->
                            if (state.language == AppLanguage.ARABIC) "تم إنشاء المسودة بنجاح ($docId)"
                            else "Bilingual CMS draft saved successfully ($docId)."
                        },
                        onFailure = { err -> err.localizedMessage ?: "CMS draft creation denied by security rules." }
                    )
                )
            }
        }
    }

    fun transitionCmsStatus(entry: CmsContentRecord, targetStatus: EditorialStatus, note: String?) {
        viewModelScope.launch {
            val result = cloudRepository.transitionCmsStatus(entry, targetStatus, note)
            _uiState.update { state ->
                state.copy(
                    cmsStatusMessage = result.fold(
                        onSuccess = {
                            if (state.language == AppLanguage.ARABIC) "تم تحديث حالة السجل إلى ${targetStatus.name}"
                            else "CMS entry transitioned to ${targetStatus.name}."
                        },
                        onFailure = { err -> err.localizedMessage ?: "Role permission denied for this editorial transition." }
                    )
                )
            }
        }
    }

    fun setLearningStatusMessage(message: String?) {
        _uiState.update { it.copy(learningStatusMessage = message) }
    }

    private fun logActivityIfSignedIn(type: String, targetId: String, titleEn: String, titleAr: String) {
        if (!_uiState.value.isSignedIn) return
        viewModelScope.launch {
            cloudRepository.logLearningActivity(type, targetId, titleEn, titleAr)
        }
    }

    // -------------------------------------------------------------------------
    // Anatomy Explorer & 3D Viewport Controls (Phase 01 Preserved)
    // -------------------------------------------------------------------------

    fun openAnatomyStructure(structureId: String, openDetailSheet: Boolean = false) {
        val structure = anatomyRepository.findStructureById(structureId) ?: return
        val shouldEnableXray = structure.id == "interventricular_septum"
        _uiState.update { state ->
            state.copy(
                activeDestination = AppDestination.ANATOMY,
                selectedSystemId = structure.systemId,
                selectedStructureId = structure.id,
                xrayMode = if (shouldEnableXray) true else state.xrayMode,
                cameraState = Camera3DState(
                    yawDeg = structure.coordinate.cameraYawDeg,
                    pitchDeg = structure.coordinate.cameraPitchDeg,
                    distance = structure.coordinate.cameraDistance,
                    panX = 0f,
                    panY = 0f
                ),
                showDetailSheet = openDetailSheet
            )
        }
        logActivityIfSignedIn("ANATOMY", structure.id, structure.englishName, structure.arabicName)
    }

    fun selectSystem(systemId: String) {
        _uiState.update { it.copy(selectedSystemId = systemId) }
    }

    fun selectStructure(structureId: String, focusCamera: Boolean = true, openSheetOnCompact: Boolean = false) {
        val structure = anatomyRepository.findStructureById(structureId) ?: return
        val enableXray = structure.id == "interventricular_septum"
        _uiState.update { state ->
            val nextCam = if (focusCamera) {
                Camera3DState(
                    yawDeg = structure.coordinate.cameraYawDeg,
                    pitchDeg = structure.coordinate.cameraPitchDeg,
                    distance = structure.coordinate.cameraDistance,
                    panX = 0f,
                    panY = 0f
                )
            } else {
                state.cameraState
            }
            state.copy(
                selectedStructureId = structure.id,
                cameraState = nextCam,
                xrayMode = if (enableXray) true else state.xrayMode,
                showDetailSheet = openSheetOnCompact || state.showDetailSheet
            )
        }
    }

    fun onMeshNodePicked(nodeId: String, openSheetOnCompact: Boolean = false) {
        val matched = anatomyRepository.findStructureByMeshNodeId(nodeId)
        if (matched != null) {
            selectStructure(matched.id, focusCamera = false, openSheetOnCompact = openSheetOnCompact)
        }
    }

    fun updateStructureSearch(query: String) {
        _uiState.update { it.copy(structureSearchQuery = query) }
    }

    fun updateCamera(cameraState: Camera3DState) {
        _uiState.update { it.copy(cameraState = cameraState) }
    }

    fun applyCameraPreset(preset: CameraPreset) {
        _uiState.update { it.copy(cameraState = it.cameraState.withPreset(preset)) }
    }

    fun togglePanMode() {
        _uiState.update { it.copy(panMode = !it.panMode) }
    }

    fun toggleIsolateSelected() {
        _uiState.update { it.copy(isolateSelected = !it.isolateSelected) }
    }

    fun toggleXrayMode() {
        _uiState.update { it.copy(xrayMode = !it.xrayMode) }
    }

    fun updateLighting(config: LightingConfig) {
        _uiState.update { it.copy(lightingConfig = config) }
    }

    fun cycleBackdrop() {
        _uiState.update { state ->
            val entries = ViewportBackdrop.entries
            val nextIdx = (entries.indexOf(state.backdrop) + 1) % entries.size
            state.copy(backdrop = entries[nextIdx])
        }
    }

    fun setFullscreenViewer(fullscreen: Boolean) {
        _uiState.update { it.copy(isFullscreenViewer = fullscreen) }
    }

    fun setShowProvenanceDialog(show: Boolean) {
        _uiState.update { it.copy(showProvenanceDialog = show) }
    }

    fun setShowDetailSheet(show: Boolean) {
        _uiState.update { it.copy(showDetailSheet = show) }
    }

    // -------------------------------------------------------------------------
    // Medical Dictionary Actions (Phase 01 Preserved + Cloud Sync)
    // -------------------------------------------------------------------------

    fun updateDictionaryQuery(query: String) {
        _uiState.update { state ->
            val results = terminologyRepository.searchTerms(
                query = query,
                category = state.selectedCategory,
                bookmarkedOnly = state.bookmarkedOnly,
                bookmarkedIds = state.bookmarkedTermIds
            )
            state.copy(dictionaryQuery = query, filteredTerms = results)
        }
    }

    fun openDictionaryWithSearch(query: String) {
        _uiState.update { state ->
            val results = terminologyRepository.searchTerms(
                query = query,
                category = DictionaryCategory.ALL,
                bookmarkedOnly = false,
                bookmarkedIds = state.bookmarkedTermIds
            )
            state.copy(
                activeDestination = AppDestination.DICTIONARY,
                dictionaryQuery = query,
                selectedCategory = DictionaryCategory.ALL,
                bookmarkedOnly = false,
                filteredTerms = results
            )
        }
    }

    fun selectDictionaryCategory(category: DictionaryCategory) {
        _uiState.update { state ->
            val results = terminologyRepository.searchTerms(
                query = state.dictionaryQuery,
                category = category,
                bookmarkedOnly = state.bookmarkedOnly,
                bookmarkedIds = state.bookmarkedTermIds
            )
            state.copy(selectedCategory = category, filteredTerms = results)
        }
    }

    fun toggleBookmarkedOnly() {
        _uiState.update { state ->
            val nextBookmarked = !state.bookmarkedOnly
            val results = terminologyRepository.searchTerms(
                query = state.dictionaryQuery,
                category = state.selectedCategory,
                bookmarkedOnly = nextBookmarked,
                bookmarkedIds = state.bookmarkedTermIds
            )
            state.copy(bookmarkedOnly = nextBookmarked, filteredTerms = results)
        }
    }

    fun selectDictionaryTerm(termId: String?) {
        _uiState.update { it.copy(selectedTermId = termId) }
    }

    fun toggleBookmark(termId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleBookmark(termId)
            val nextSet = if (termId in _uiState.value.bookmarkedTermIds) {
                _uiState.value.bookmarkedTermIds - termId
            } else {
                _uiState.value.bookmarkedTermIds + termId
            }
            if (_uiState.value.isSignedIn) {
                cloudRepository.syncProgressFields(bookmarkedTerms = nextSet)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Phase 03: Human Physiology Simulation Lab Actions (100% Preserved)
    // -------------------------------------------------------------------------

    fun stepSimulation(deltaSeconds: Float) = simulationEngine.step(deltaSeconds)
    fun selectPhysiologySystem(systemId: PhysiologySystemId) = simulationEngine.selectSystem(systemId)
    fun loadPhysiologyScenario(scenarioId: String) = simulationEngine.loadScenario(scenarioId)
    fun toggleSimulationPlayPause() = simulationEngine.togglePlayPause()
    fun resetSimulation() = simulationEngine.reset()
    fun setSimulationSpeed(speed: Float) = simulationEngine.setSpeed(speed)
    fun setSimulationParameter(paramId: String, value: Float) = simulationEngine.setParameter(paramId, value)
    fun resetSimulationParameters() = simulationEngine.resetParametersToScenarioDefaults()
    fun setCirculationFilter(filter: CirculationFilterMode) = simulationEngine.setCirculationFilter(filter)
    fun toggleConductionOverlay() = simulationEngine.toggleConductionOverlay()
    fun toggleSimulationLabels() = simulationEngine.toggleLabels()
    fun toggleZoomSynapse() = simulationEngine.toggleZoomSynapse()
    fun setSimulationQualityMode(mode: RenderQualityMode) = simulationEngine.setQualityMode(mode)
    fun setReducedMotion(enabled: Boolean) = simulationEngine.setReducedMotion(enabled)
    fun setPhysiologyFullscreen(fullscreen: Boolean) = _uiState.update { it.copy(isPhysiologyFullscreen = fullscreen) }
    fun selectSimulationOrganPart(partId: String) = simulationEngine.selectOrganPart(partId)

    fun openPhysiologySystem(systemId: PhysiologySystemId) {
        simulationEngine.selectSystem(systemId)
        simulationEngine.resume()
        _uiState.update {
            it.copy(
                activeDestination = AppDestination.PHYSIOLOGY,
                isFullscreenViewer = false,
                isPhysiologyFullscreen = false
            )
        }
        logActivityIfSignedIn("PHYSIOLOGY", systemId.id, systemId.shortLabel.en, systemId.shortLabel.ar)
    }

    fun openPhysiologyForAnatomyStructure(structureId: String) {
        val structure = anatomyRepository.findStructureById(structureId)
        val targetSystem = when (structure?.systemId) {
            "respiratory" -> PhysiologySystemId.RESPIRATORY
            "nervous" -> PhysiologySystemId.NEURAL
            "urinary" -> PhysiologySystemId.RENAL
            "digestive" -> PhysiologySystemId.DIGESTIVE
            else -> PhysiologySystemId.CARDIOVASCULAR
        }
        simulationEngine.selectSystem(targetSystem, preserveStructureId = structureId)
        simulationEngine.resume()
        _uiState.update {
            it.copy(
                activeDestination = AppDestination.PHYSIOLOGY,
                isFullscreenViewer = false,
                isPhysiologyFullscreen = false,
                showDetailSheet = false
            )
        }
    }

    fun openPhysiologyForBodySystem(bodySystemId: String) {
        val targetSystem = when (bodySystemId) {
            "respiratory" -> PhysiologySystemId.RESPIRATORY
            "nervous" -> PhysiologySystemId.NEURAL
            "urinary" -> PhysiologySystemId.RENAL
            "digestive" -> PhysiologySystemId.DIGESTIVE
            else -> PhysiologySystemId.CARDIOVASCULAR
        }
        openPhysiologySystem(targetSystem)
    }

    fun returnToAnatomyFromPhysiology(structureId: String) {
        val existingStructure = anatomyRepository.findStructureById(structureId)
        _uiState.update { it.copy(isPhysiologyFullscreen = false) }
        if (existingStructure != null) {
            openAnatomyStructure(existingStructure.id, openDetailSheet = false)
        } else {
            val mappedSystem = when (physiologyState.value.activeSystem) {
                PhysiologySystemId.CARDIOVASCULAR -> "cardiovascular"
                PhysiologySystemId.RESPIRATORY -> "respiratory"
                PhysiologySystemId.NEURAL -> "nervous"
                PhysiologySystemId.RENAL -> "urinary"
                PhysiologySystemId.DIGESTIVE -> "digestive"
            }
            _uiState.update {
                it.copy(
                    activeDestination = AppDestination.ANATOMY,
                    selectedSystemId = mappedSystem
                )
            }
        }
    }

    fun applyGuidedLessonStep(step: GuidedLessonStep) {
        simulationEngine.loadScenario(step.scenarioId, preserveStructureId = step.highlightPartId)
        step.targetProgress?.let { progress -> simulationEngine.seekCycleProgress(progress) }
        if (step.pauseOnEnter) simulationEngine.pause() else simulationEngine.resume()
    }

    fun completeLesson(lessonId: String) {
        viewModelScope.launch {
            preferencesRepository.markLessonCompleted(lessonId)
            if (_uiState.value.isSignedIn) {
                val updated = _uiState.value.completedLessonIds + lessonId
                cloudRepository.syncProgressFields(completedLessons = updated)
            }
        }
    }

    fun recordQuizCorrect(questionId: String) {
        viewModelScope.launch {
            preferencesRepository.recordCorrectQuizAnswer(questionId)
        }
    }

    fun resetQuizProgress() {
        viewModelScope.launch {
            preferencesRepository.resetQuizProgress()
        }
    }
}
