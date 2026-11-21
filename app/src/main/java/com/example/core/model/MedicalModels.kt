package com.example.core.model

import com.example.core.i18n.LocalizedText

enum class MedicalReviewStatus {
    VERIFIED_EDUCATIONAL,
    PENDING_MEDICAL_REVIEW,
    SCHEMATIC_DEMONSTRATION,
    SCIENTIFIC_SIMULATION
}

enum class AssetProvenanceType {
    SCHEMATIC_EDUCATIONAL_DEMO,
    VERIFIED_NIH_3D_ASSET,
    USER_IMPORTED_GLB_GLTF
}

enum class ModuleAvailability {
    ACTIVE_PHASE_1,
    ARCHITECTURE_READY_PHASE_2
}

data class ReferenceSource(
    val id: String,
    val title: String,
    val organization: String,
    val url: String,
    val licenseNote: LocalizedText
)

data class BodySystem(
    val id: String,
    val englishName: String,
    val arabicName: String,
    val summary: LocalizedText,
    val availability: ModuleAvailability,
    val structureCount: Int,
    val iconKey: String
)

data class AnatomicalCoordinate(
    val x: Float,
    val y: Float,
    val z: Float,
    val cameraYawDeg: Float,
    val cameraPitchDeg: Float,
    val cameraDistance: Float
)

data class AnatomicalStructure(
    val id: String,
    val systemId: String,
    val organId: String,
    val organName: LocalizedText,
    val meshNodeId: String,
    val englishName: String,
    val arabicName: String,
    val scientificTerminology: String, // Terminologia Anatomica (TA2) Latin name
    val taCode: String,
    val coordinate: AnatomicalCoordinate,
    val anatomicalLocation: LocalizedText,
    val generalDescription: LocalizedText,
    val structureDetails: LocalizedText,
    val biologicalFunction: LocalizedText,
    val relationships: LocalizedText,
    val clinicalRelevance: LocalizedText,
    val relatedStructureIds: List<String>,
    val sources: List<ReferenceSource>,
    val reviewStatus: MedicalReviewStatus,
    val colorHexTag: Long
)

enum class DictionaryCategory(val label: LocalizedText) {
    ALL(LocalizedText("All", "الكل")),
    CARDIOVASCULAR_CORE(LocalizedText("Cardiac Core", "أساسيات القلب")),
    CHAMBERS(LocalizedText("Heart Chambers", "حجرات القلب")),
    GREAT_VESSELS(LocalizedText("Great Vessels", "الأوعية الكبرى")),
    SEPTUM_VALVES(LocalizedText("Septum & Valves", "الحواجز والصمامات")),
    DIRECTIONAL_GENERAL(LocalizedText("Anatomical Terms", "مصطلحات تشريحية"))
}

data class MedicalTerm(
    val id: String,
    val englishName: String,
    val arabicName: String,
    val pronunciationIpa: String,
    val pronunciationPhonetic: String,
    val synonymsEn: List<String>,
    val synonymsAr: List<String>,
    val category: DictionaryCategory,
    val definitionEnglish: String,
    val definitionArabic: String,
    val relatedAnatomyIds: List<String>,
    val sources: List<ReferenceSource>,
    val reviewStatus: MedicalReviewStatus
)

data class ModelAssetMetadata(
    val id: String,
    val name: LocalizedText,
    val format: String, // "PROCEDURAL_SCHEMATIC", "GLB", "GLTF"
    val provenance: AssetProvenanceType,
    val sourceOrganization: String,
    val sourceUrl: String,
    val license: String,
    val attribution: String,
    val accuracyNotice: LocalizedText,
    val vertexCount: Int,
    val triangleCount: Int,
    val mappedNodeIds: List<String>,
    val validationPassed: Boolean,
    val validationNotes: List<String>
)

data class LearningGuideItem(
    val id: String,
    val title: LocalizedText,
    val subtitle: LocalizedText,
    val targetAnatomyId: String,
    val durationMinutes: Int,
    val steps: List<LocalizedText>,
    val reviewStatus: MedicalReviewStatus
)
