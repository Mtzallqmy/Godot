package com.example.core.simulation

import com.example.core.i18n.LocalizedText

/**
 * Distinguishes scientifically validated mathematical physiological models from
 * illustrative educational animations. Phase 01 establishes strict contracts
 * without fabricating arbitrary animated clinical numbers.
 */
enum class SimulationFidelityTier {
    SCIENTIFICALLY_VALIDATED_MODEL,
    ILLUSTRATIVE_EDUCATIONAL_ANIMATION,
    ARCHITECTURAL_CONTRACT_ONLY
}

data class SimulationParameterSpec(
    val id: String,
    val label: LocalizedText,
    val unit: String,
    val minValue: Float,
    val maxValue: Float,
    val defaultValue: Float,
    val clinicalReferenceRange: String,
    val fidelityTier: SimulationFidelityTier
)

data class SimulationStateSnapshot(
    val engineId: String,
    val timestampSeconds: Float,
    val fidelityTier: SimulationFidelityTier,
    val outputs: Map<String, Float>,
    val disclaimers: List<LocalizedText>,
    val isExecutableInCurrentPhase: Boolean = false
)

/**
 * Extensible contract for Phase 02+ Physiology Simulation Engine
 * (e.g., Windkessel hemodynamics, Wiggers cardiac cycle diagrams, Starling curves).
 */
interface PhysiologySimulationEngine {
    val engineId: String
    val domainName: LocalizedText
    val fidelityTier: SimulationFidelityTier
    val parameters: List<SimulationParameterSpec>
    val citationSources: List<String>
    fun evaluate(parameterValues: Map<String, Float>, timeSeconds: Float): SimulationStateSnapshot
}

/**
 * Extensible contract for Phase 02+ Pathology Comparison Engine
 * comparing normal anatomical/structural geometry vs. pathological states.
 */
data class PathologyComparisonSpec(
    val conditionId: String,
    val conditionName: LocalizedText,
    val targetAnatomyId: String,
    val normalReferenceSummary: LocalizedText,
    val pathologicalChangeSummary: LocalizedText,
    val requiredAssetVariants: List<String>,
    val fidelityTier: SimulationFidelityTier
)

interface PathologyComparisonEngine {
    val engineId: String
    fun getRegisteredComparisons(): List<PathologyComparisonSpec>
}

/**
 * Extensible contract for Phase 02+ Drug Mechanism Visualization Engine
 * mapping pharmacodynamic receptor targets and hemodynamic pathways.
 */
data class DrugMechanismSpec(
    val mechanismId: String,
    val drugClass: LocalizedText,
    val targetOrganSystemId: String,
    val molecularTarget: String,
    val mechanismSummary: LocalizedText,
    val educationalWarning: LocalizedText,
    val fidelityTier: SimulationFidelityTier
)

interface DrugMechanismVisualizationEngine {
    val engineId: String
    fun getRegisteredMechanisms(): List<DrugMechanismSpec>
}

/**
 * Extensible contract for Phase 02+ Educational Animation Timeline & Parameter Controls.
 */
data class AnimationKeyframe(
    val id: String,
    val normalizedTime: Float, // 0f..1f
    val label: LocalizedText,
    val highlightedNodeIds: List<String>
)

interface EducationalAnimationTimeline {
    val timelineId: String
    val title: LocalizedText
    val durationSeconds: Float
    val fidelityTier: SimulationFidelityTier
    val keyframes: List<AnimationKeyframe>
}

/**
 * Phase 01 Registry providing transparent architectural blueprints without fake telemetry.
 */
object PhaseTwoArchitectureRegistry {
    val plannedPhysiologySpecs = listOf(
        SimulationParameterSpec(
            id = "heart_rate_bpm",
            label = LocalizedText("Heart Rate (Planned Input)", "معدل ضربات القلب (مدخل مخطط)"),
            unit = "bpm",
            minValue = 40f,
            maxValue = 180f,
            defaultValue = 72f,
            clinicalReferenceRange = "60–100 bpm (Adult Resting Reference)",
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        ),
        SimulationParameterSpec(
            id = "stroke_volume_ml",
            label = LocalizedText("Stroke Volume (Planned Input)", "حجم الضربة (مدخل مخطط)"),
            unit = "mL",
            minValue = 35f,
            maxValue = 120f,
            defaultValue = 70f,
            clinicalReferenceRange = "60–100 mL (Adult Resting Reference)",
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        ),
        SimulationParameterSpec(
            id = "svr_wood_units",
            label = LocalizedText("Systemic Vascular Resistance", "المقاومة الوعائية الجهازية"),
            unit = "dyn·s/cm⁵",
            minValue = 600f,
            maxValue = 1800f,
            defaultValue = 1100f,
            clinicalReferenceRange = "800–1200 dyn·s/cm⁵",
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        )
    )

    val plannedPathologyComparisons = listOf(
        PathologyComparisonSpec(
            conditionId = "lvh_comparison",
            conditionName = LocalizedText(
                "Left Ventricular Hypertrophy (LVH) vs. Normal Myocardium",
                "تضخم البطين الأيسر مقابل عضلة القلب الطبيعية"
            ),
            targetAnatomyId = "left_ventricle",
            normalReferenceSummary = LocalizedText(
                "Normal adult left ventricular wall thickness is approximately 8–11 mm in end-diastole.",
                "يبلغ سمك جدار البطين الأيسر الطبيعي لدى البالغين نحو 8–11 ملم في نهاية الانبساط."
            ),
            pathologicalChangeSummary = LocalizedText(
                "Chronic pressure overload increases myocardial wall thickness and reduces chamber compliance. Phase 02 will load paired cross-sectional GLB meshes.",
                "يؤدي فرط الحمل الضغطي المزمن إلى زيادة سمك الجدار العضلي وتقليل مطاوعة الحجرة. ستدعم المرحلة 02 مقارنة مقاطع ثلاثية الأبعاد."
            ),
            requiredAssetVariants = listOf("heart_normal_cross_section.glb", "heart_lvh_cross_section.glb"),
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        ),
        PathologyComparisonSpec(
            conditionId = "vsd_comparison",
            conditionName = LocalizedText(
                "Ventricular Septal Defect (VSD) vs. Intact Septum",
                "عيب الحاجز البطيني مقابل الحاجز السليم"
            ),
            targetAnatomyId = "interventricular_septum",
            normalReferenceSummary = LocalizedText(
                "Intact muscular and membranous interventricular septum separates oxygenated LV blood from deoxygenated RV blood.",
                "يفصل الحاجز بين البطينين السليم الدم المؤكسج في البطين الأيسر عن غير المؤكسج في البطين الأيمن."
            ),
            pathologicalChangeSummary = LocalizedText(
                "An opening in the septum permits interventricular shunting. Phase 02 will visualize hemodynamic directionality with validated labels.",
                "يسمح وجود فتحة في الحاجز بتحويلة دموية بين البطينين. ستعرض المرحلة 02 اتجاه التدفق بوضوح تعليمي."
            ),
            requiredAssetVariants = listOf("septum_intact.glb", "septum_vsd_schematic.glb"),
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        ),
        PathologyComparisonSpec(
            conditionId = "aortic_coarctation",
            conditionName = LocalizedText(
                "Aortic Arch Narrowing vs. Normal Aorta",
                "تضيق قوس الأبهر مقابل الشريان الأبهر الطبيعي"
            ),
            targetAnatomyId = "aorta",
            normalReferenceSummary = LocalizedText(
                "Smooth elastic aortic arch delivering systemic output without significant pressure gradient.",
                "قوس أبهر مرن ومنتظم ينقل الدم الجهازي دون تدرج ضغطي معيق."
            ),
            pathologicalChangeSummary = LocalizedText(
                "Localized luminal narrowing elevates proximal pressure and reduces distal perfusion. Scheduled for Phase 02 comparative mesh overlay.",
                "تضيق موضعي في اللمعة يرفع الضغط القريب ويقلل التروية البعيدة. مجدول للمرحلة 02."
            ),
            requiredAssetVariants = listOf("aorta_normal.glb", "aorta_coarctation.glb"),
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        )
    )

    val plannedPharmacologySpecs = listOf(
        DrugMechanismSpec(
            mechanismId = "beta_blockers_cardiac",
            drugClass = LocalizedText("Beta-1 Adrenergic Antagonists", "حاصرات مستقبلات بيتا-1 الأدرينالية"),
            targetOrganSystemId = "cardiovascular",
            molecularTarget = "Cardiac β1-Adrenoceptors (SA Node, AV Node, Myocardium)",
            mechanismSummary = LocalizedText(
                "Educational focus for Phase 02: visualizing how competitive antagonism of cardiac β1 receptors reduces chronotropy and inotropy, decreasing myocardial oxygen demand.",
                "التركيز التعليمي للمرحلة 02: توضيح آلية التثبيط التنافسي لمستقبلات بيتا-1 القلبية في خفض سرعة وقوة الانقباض وتقليل استهلاك عضلة القلب للأكسجين."
            ),
            educationalWarning = LocalizedText(
                "Educational mechanism overview only — never for clinical dosing or prescribing.",
                "عرض تعليمي للآلية الدوائية فقط — لا يُستخدم للجرعات أو الوصفات السريرية."
            ),
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        ),
        DrugMechanismSpec(
            mechanismId = "ace_inhibitors_vascular",
            drugClass = LocalizedText("ACE Inhibitors (RAAS Pathway)", "مثبطات الإنزيم المحول للأنجيوتنسين"),
            targetOrganSystemId = "cardiovascular",
            molecularTarget = "Pulmonary Vascular Endothelial ACE (Angiotensin I → II)",
            mechanismSummary = LocalizedText(
                "Educational focus for Phase 02: illustrating pulmonary-vascular conversion blockade, vasodilation, and reduced left ventricular afterload.",
                "التركيز التعليمي للمرحلة 02: توضيح تثبيط التحويل الوعائي الرئوي، وتوسيع الأوعية، وتقليل الحمل البعدي للبطين الأيسر."
            ),
            educationalWarning = LocalizedText(
                "Educational mechanism overview only — consult licensed clinicians and official formularies.",
                "محتوى تعليمي فقط — استشر الأطباء المرخصين والمراجع الدوائية المعتمدة."
            ),
            fidelityTier = SimulationFidelityTier.ARCHITECTURAL_CONTRACT_ONLY
        )
    )

    val plannedCardiacCycleTimeline = listOf(
        AnimationKeyframe(
            id = "kf_atrial_systole",
            normalizedTime = 0.0f,
            label = LocalizedText("Atrial Systole (Late Ventricular Filling)", "انقباض الأذينين (الملء البطيني المتأخر)"),
            highlightedNodeIds = listOf("right_atrium", "left_atrium")
        ),
        AnimationKeyframe(
            id = "kf_isovolumetric_contraction",
            normalizedTime = 0.2f,
            label = LocalizedText("Isovolumetric Ventricular Contraction", "الانقباض البطيني متساوي الحجم"),
            highlightedNodeIds = listOf("right_ventricle", "left_ventricle", "interventricular_septum")
        ),
        AnimationKeyframe(
            id = "kf_ventricular_ejection",
            normalizedTime = 0.45f,
            label = LocalizedText("Ventricular Ejection into Aorta & Pulmonary Artery", "القذف البطيني نحو الأبهر والشريان الرئوي"),
            highlightedNodeIds = listOf("left_ventricle", "aorta", "right_ventricle", "pulmonary_artery")
        ),
        AnimationKeyframe(
            id = "kf_ventricular_filling",
            normalizedTime = 0.75f,
            label = LocalizedText("Ventricular Diastole & Venous Return", "انبساط البطينين والعود الوريدي"),
            highlightedNodeIds = listOf("vena_cava", "pulmonary_veins", "right_atrium", "left_atrium")
        )
    )
}
