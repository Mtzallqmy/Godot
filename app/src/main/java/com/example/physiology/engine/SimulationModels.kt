package com.example.physiology.engine

import com.example.core.i18n.LocalizedText
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ReferenceSource

/**
 * Mandatory three-tier scientific integrity classification (Section 03).
 * Never mislabel Level B or Level C as clinically validated Level A models.
 */
enum class ScientificValidityLevel(
    val code: String,
    val badgeLabel: LocalizedText,
    val explanation: LocalizedText
) {
    LEVEL_A_SCIENTIFICALLY_VALIDATED(
        code = "LEVEL A",
        badgeLabel = LocalizedText(
            en = "Level A • Validated Mathematical Model",
            ar = "المستوى A • نموذج رياضي مُتحقق علمياً"
        ),
        explanation = LocalizedText(
            en = "Documented computational model with verified equations, parameter bounds, and validation evidence for its stated domain.",
            ar = "نموذج حسابي موثق بمعادلات محددة ونطاقات معاملات وأدلة تحقق ضمن نطاقه المعلن."
        )
    ),
    LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL(
        code = "LEVEL B",
        badgeLabel = LocalizedText(
            en = "Level B • Educational Physiology Model",
            ar = "المستوى B • نموذج فسيولوجي تعليمي"
        ),
        explanation = LocalizedText(
            en = "Scientifically informed educational representation with documented cycle timing and assumptions; not validated for clinical prediction or individual diagnosis.",
            ar = "تمثيل تعليمي مستند إلى المعرفة العلمية مع افتراضات موثقة؛ غير مخصص للتنبؤ السريري أو التشخيص الفردي."
        )
    ),
    LEVEL_C_ILLUSTRATIVE_ANIMATION(
        code = "LEVEL C",
        badgeLabel = LocalizedText(
            en = "Level C • Illustrative Animation",
            ar = "المستوى C • رسم توضيحي متحرك"
        ),
        explanation = LocalizedText(
            en = "Visual teaching aid demonstrating anatomical direction, sequence, and conceptual physiological behavior rather than quantitative physical measurement.",
            ar = "وسيلة تعليمية بصرية توضح الاتجاه والتسلسل والسلوك الفسيولوجي العام وليست قياساً فيزيائياً كمياً."
        )
    )
}

enum class PhysiologySystemId(
    val id: String,
    val title: LocalizedText,
    val shortLabel: LocalizedText,
    val linkedAnatomySystemId: String,
    val defaultAnatomyStructureId: String
) {
    CARDIOVASCULAR(
        id = "cardiovascular",
        title = LocalizedText("Cardiovascular Physiology", "فسيولوجيا القلب والأوعية"),
        shortLabel = LocalizedText("Heart", "القلب"),
        linkedAnatomySystemId = "cardiovascular",
        defaultAnatomyStructureId = "heart"
    ),
    RESPIRATORY(
        id = "respiratory",
        title = LocalizedText("Respiratory Mechanics & Gas Exchange", "ميكانيكا التنفس وتبادل الغازات"),
        shortLabel = LocalizedText("Lungs", "الرئتان"),
        linkedAnatomySystemId = "respiratory",
        defaultAnatomyStructureId = "heart"
    ),
    NEURAL(
        id = "neural",
        title = LocalizedText("Neural Signal Transmission", "انتقال الإشارات العصبية"),
        shortLabel = LocalizedText("Neuron", "العصبون"),
        linkedAnatomySystemId = "nervous",
        defaultAnatomyStructureId = "heart"
    ),
    RENAL(
        id = "renal",
        title = LocalizedText("Renal Filtration & Nephron", "الترشيح الكلوي والنفرون"),
        shortLabel = LocalizedText("Nephron", "النفرون"),
        linkedAnatomySystemId = "urinary",
        defaultAnatomyStructureId = "heart"
    ),
    DIGESTIVE(
        id = "digestive",
        title = LocalizedText("Gastrointestinal Function", "وظائف الجهاز الهضمي"),
        shortLabel = LocalizedText("Digestive", "الهضم"),
        linkedAnatomySystemId = "digestive",
        defaultAnatomyStructureId = "heart"
    )
}

enum class RenderQualityMode(val label: LocalizedText, val particleMultiplier: Float) {
    AUTO(LocalizedText("Auto", "تلقائي"), 1.0f),
    LOW(LocalizedText("Low", "منخفض"), 0.45f),
    MEDIUM(LocalizedText("Medium", "متوسط"), 0.75f),
    HIGH(LocalizedText("High", "عالي"), 1.0f)
}

enum class CirculationFilterMode(val label: LocalizedText) {
    BOTH(LocalizedText("Both Circuits", "الدورتان معاً")),
    PULMONARY_ONLY(LocalizedText("Pulmonary Circuit", "الدورة الرئوية")),
    SYSTEMIC_ONLY(LocalizedText("Systemic Circuit", "الدورة الجهازية"))
}

enum class ValveStatus(val label: LocalizedText) {
    OPEN(LocalizedText("Open", "مفتوح")),
    CLOSED(LocalizedText("Closed", "مغلق"))
}

data class SimulationParameterDef(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val unit: String,
    val minValue: Float,
    val maxValue: Float,
    val defaultValue: Float,
    val step: Float,
    val presets: List<Float> = emptyList(),
    val description: LocalizedText,
    val modelDependency: LocalizedText,
    val validityLevel: ScientificValidityLevel
)

data class SimulationPhaseDef(
    val id: String,
    val name: LocalizedText,
    val startNormalized: Float, // 0f..1f
    val endNormalized: Float,   // 0f..1f
    val summary: LocalizedText,
    val highlightedStructures: List<String>
)

data class PhysiologyExplanationRecord(
    val processId: String,
    val systemId: PhysiologySystemId,
    val processName: LocalizedText,
    val scientificDefinition: LocalizedText,
    val anatomicalStructures: LocalizedText,
    val sequenceOfEvents: List<LocalizedText>,
    val biologicalFunction: LocalizedText,
    val relatedConcepts: List<LocalizedText>,
    val clinicalRelevance: LocalizedText,
    val sources: List<ReferenceSource>
)

data class SimulationModelRecord(
    val id: String,
    val systemId: PhysiologySystemId,
    val titleEnglish: String,
    val titleArabic: String,
    val validityLevel: ScientificValidityLevel,
    val description: LocalizedText,
    val parameterDefs: List<SimulationParameterDef>,
    val phases: List<SimulationPhaseDef>,
    val equationNotes: LocalizedText,
    val assumptions: List<LocalizedText>,
    val limitations: List<LocalizedText>,
    val assetProvenanceNote: LocalizedText,
    val sources: List<ReferenceSource>,
    val reviewStatus: MedicalReviewStatus,
    val lastReviewedDate: String,
    val version: String
)

data class SimulationScenario(
    val scenarioId: String,
    val modelId: String,
    val systemId: PhysiologySystemId,
    val titleEnglish: String,
    val titleArabic: String,
    val descriptionEnglish: String,
    val descriptionArabic: String,
    val validityLevel: ScientificValidityLevel,
    val defaultParameters: Map<String, Float>,
    val expectedObservations: List<LocalizedText>,
    val educationalObjectives: List<LocalizedText>,
    val limitations: LocalizedText,
    val sources: List<ReferenceSource>,
    val viewModeTag: String = "default" // e.g. "cardiac_cycle", "pulmonary", "systemic", "conduction", "ventilation", "gas_exchange", "synapse"
)

data class CardiacValveStates(
    val tricuspid: ValveStatus,
    val mitral: ValveStatus,
    val pulmonary: ValveStatus,
    val aortic: ValveStatus
)

data class SimulationState(
    val activeSystem: PhysiologySystemId = PhysiologySystemId.CARDIOVASCULAR,
    val activeScenarioId: String = "cardio_normal_cycle",
    val simulationTimeSec: Float = 0f,
    val elapsedRealTimeSec: Float = 0f,
    val cycleProgress: Float = 0f, // 0f..1f within current physiological cycle
    val playbackSpeed: Float = 1.0f, // Viewing speed multiplier (0.25x..2.0x), strictly separate from physiological BPM
    val isRunning: Boolean = true,
    val currentPhaseId: String = "atrial_systole",
    val parameterValues: Map<String, Float> = mapOf("heart_rate_bpm" to 75f),
    val derivedOutputs: Map<String, String> = emptyMap(), // Strictly documented exact timing derivations only
    val selectedOrganPartId: String? = "heart",
    val circulationFilter: CirculationFilterMode = CirculationFilterMode.BOTH,
    val showConductionOverlay: Boolean = false,
    val showLabels: Boolean = true,
    val zoomSynapse: Boolean = false,
    val qualityMode: RenderQualityMode = RenderQualityMode.AUTO,
    val reducedMotion: Boolean = false,
    val isDisposed: Boolean = false
)
