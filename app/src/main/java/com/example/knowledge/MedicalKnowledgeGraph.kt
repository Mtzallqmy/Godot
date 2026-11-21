package com.example.knowledge

import com.example.academy.LearningAcademySeed
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalizedText
import com.example.core.model.ReferenceSource
import com.example.data.repository.LocalTerminologyRepository
import com.example.data.seed.CardiovascularAnatomySeed
import com.example.data.seed.MedicalDictionarySeed
import com.example.pathology.PathologySeed
import com.example.pharmacology.PharmacologySeed
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.SimulationModelRegistry

enum class KnowledgeEntityType(val label: LocalizedText) {
    ANATOMY(LocalizedText("3D Anatomy", "التشريح 3D")),
    PHYSIOLOGY(LocalizedText("Physiology", "وظائف الأعضاء")),
    PATHOLOGY(LocalizedText("Pathology", "علم الأمراض")),
    PHARMACOLOGY(LocalizedText("Pharmacology", "علم الأدوية")),
    TERMINOLOGY(LocalizedText("Dictionary", "القاموس الطبي")),
    ACADEMY_LESSON(LocalizedText("Academy Lesson", "درس تعليمي"))
}

data class UnifiedSearchResult(
    val id: String,
    val entityType: KnowledgeEntityType,
    val titleEn: String,
    val titleAr: String,
    val subtitle: LocalizedText,
    val matchReason: LocalizedText,
    val targetStructureId: String? = null,
    val targetPhysiologySystem: PhysiologySystemId? = null,
    val targetDiseaseId: String? = null,
    val targetDrugClassId: String? = null,
    val targetTermId: String? = null,
    val targetCourseId: String? = null,
    val sources: List<ReferenceSource> = emptyList()
)

data class KnowledgeGraphContext(
    val topicId: String,
    val entityType: KnowledgeEntityType,
    val titleEn: String,
    val titleAr: String,
    val verifiedSummaryEn: String,
    val verifiedSummaryAr: String,
    val relatedAnatomyIds: List<String>,
    val relatedPhysiologySystems: List<PhysiologySystemId>,
    val relatedDiseaseIds: List<String>,
    val relatedDrugClassIds: List<String>,
    val sources: List<ReferenceSource>
)

/**
 * Unified relational Medical Knowledge Graph & Smart Bilingual Search Engine.
 * Links Anatomy <-> Physiology <-> Pathology <-> Pharmacology <-> Terminology <-> Academy
 * strictly through explicitly stored, scientifically verified records.
 */
object MedicalKnowledgeGraph {

    // Controlled transliteration aliases mapping common Arabic/English learner queries to canonical concepts
    private val transliterationAliases: Map<String, List<String>> = mapOf(
        "aorta" to listOf("اورطى", "الاورطى", "ابهر", "الشريان الابهر", "aortic", "aortic valve", "صمام ابهر", "صمام اورطي", "aortic stenosis"),
        "left_atrium" to listOf("مترالي", "تاجي", "المترالي", "الصمام التاجي", "bicuspid", "mitral", "mitral valve"),
        "heart" to listOf("قلب", "القلب", "cardiac", "myocardium", "cor", "coronary", "الشريان التاجي", "شريان تاجي"),
        "beta_blockers" to listOf("ميتوبرولول", "بيتا", "حاصرات بيتا", "metoprolol", "beta blocker"),
        "ace_inhibitors" to listOf("ليزينوبريل", "انجيوتنسين", "lisinopril", "acei"),
        "saba_bronchodilators" to listOf("فنتولين", "سالبوتامول", "البوتيرول", "salbutamol", "albuterol", "asthma", "ربو"),
        "loop_diuretics" to listOf("لازيكس", "فوروسيميد", "مدر", "furosemide", "nephron", "نفرون"),
        "proton_pump_inhibitors" to listOf("اوميبرازول", "مضخة البروتون", "قرحة", "omeprazole", "ppi", "ulcer"),
        "statins" to listOf("اتورفاستاتين", "ستاتين", "كوليسترول", "atorvastatin", "cholesterol")
    )

    fun searchAll(
        rawQuery: String,
        filterType: KnowledgeEntityType? = null
    ): List<UnifiedSearchResult> {
        val norm = LocalTerminologyRepository.normalizeArabicAndLatin(rawQuery)
        if (norm.isBlank()) return emptyList()

        val results = mutableListOf<UnifiedSearchResult>()

        fun matchesAlias(entityId: String): Boolean {
            val aliases = transliterationAliases[entityId] ?: return false
            return aliases.any {
                val a = LocalTerminologyRepository.normalizeArabicAndLatin(it)
                a.contains(norm) || norm.contains(a)
            }
        }

        // 1. Anatomical Structures
        if (filterType == null || filterType == KnowledgeEntityType.ANATOMY) {
            CardiovascularAnatomySeed.cardiovascularStructures.forEach { s ->
                val reason = when {
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.englishName).contains(norm) ->
                        LocalizedText("Matched English anatomical name", "تطابق مع الاسم التشريحي بالإنجليزية")
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.arabicName).contains(norm) ->
                        LocalizedText("Matched Arabic anatomical name", "تطابق مع الاسم التشريحي بالعربية")
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.scientificTerminology).contains(norm) ->
                        LocalizedText("Matched TA2 Latin nomenclature", "تطابق مع التسمية اللاتينية TA2")
                    matchesAlias(s.id) ->
                        LocalizedText("Matched controlled bilingual alias", "تطابق مع مرادف طبي معتمد")
                    LocalTerminologyRepository.normalizeArabicAndLatin(s.generalDescription.en + " " + s.generalDescription.ar).contains(norm) ->
                        LocalizedText("Matched anatomical description", "تطابق داخل الوصف التشريحي")
                    else -> null
                }
                if (reason != null) {
                    results.add(
                        UnifiedSearchResult(
                            id = "anat_${s.id}",
                            entityType = KnowledgeEntityType.ANATOMY,
                            titleEn = s.englishName,
                            titleAr = s.arabicName,
                            subtitle = LocalizedText(s.scientificTerminology, s.organName.ar),
                            matchReason = reason,
                            targetStructureId = s.id,
                            sources = s.sources
                        )
                    )
                }
            }
        }

        // 2. Physiology Processes & Systems
        if (filterType == null || filterType == KnowledgeEntityType.PHYSIOLOGY) {
            PhysiologySystemId.entries.forEach { sys ->
                val model = SimulationModelRegistry.getModel(sys)
                val exp = SimulationModelRegistry.getExplanation(sys)
                val haystack = LocalTerminologyRepository.normalizeArabicAndLatin(
                    "${model.titleEnglish} ${model.titleArabic} ${exp.scientificDefinition.en} ${exp.scientificDefinition.ar}"
                )
                if (haystack.contains(norm)) {
                    results.add(
                        UnifiedSearchResult(
                            id = "phys_${sys.id}",
                            entityType = KnowledgeEntityType.PHYSIOLOGY,
                            titleEn = model.titleEnglish,
                            titleAr = model.titleArabic,
                            subtitle = model.validityLevel.badgeLabel,
                            matchReason = LocalizedText("Matched physiology simulation model", "تطابق مع نموذج المحاكاة الفسيولوجية"),
                            targetPhysiologySystem = sys,
                            sources = model.sources
                        )
                    )
                }
            }
        }

        // 3. Pathology / Diseases
        if (filterType == null || filterType == KnowledgeEntityType.PATHOLOGY) {
            PathologySeed.diseases.forEach { d ->
                val haystack = LocalTerminologyRepository.normalizeArabicAndLatin(
                    "${d.englishName} ${d.arabicName} ${d.icdCategory} ${d.overview.en} ${d.overview.ar}"
                )
                if (haystack.contains(norm) || matchesAlias(d.id)) {
                    results.add(
                        UnifiedSearchResult(
                            id = "path_${d.id}",
                            entityType = KnowledgeEntityType.PATHOLOGY,
                            titleEn = d.englishName,
                            titleAr = d.arabicName,
                            subtitle = LocalizedText(d.icdCategory, d.overview.ar.take(70)),
                            matchReason = LocalizedText("Matched pathology atlas record", "تطابق مع سجل أطلس علم الأمراض"),
                            targetDiseaseId = d.id,
                            sources = d.sources
                        )
                    )
                }
            }
        }

        // 4. Pharmacology / Drug Classes
        if (filterType == null || filterType == KnowledgeEntityType.PHARMACOLOGY) {
            PharmacologySeed.drugClasses.forEach { drug ->
                val haystack = LocalTerminologyRepository.normalizeArabicAndLatin(
                    "${drug.englishName} ${drug.arabicName} ${drug.prototypeGenericEn} ${drug.prototypeGenericAr} ${drug.molecularTarget.en} ${drug.mechanismOfAction.en} ${drug.mechanismOfAction.ar}"
                )
                if (haystack.contains(norm) || matchesAlias(drug.id)) {
                    results.add(
                        UnifiedSearchResult(
                            id = "pharm_${drug.id}",
                            entityType = KnowledgeEntityType.PHARMACOLOGY,
                            titleEn = drug.englishName,
                            titleAr = drug.arabicName,
                            subtitle = LocalizedText(drug.prototypeGenericEn, drug.prototypeGenericAr),
                            matchReason = LocalizedText("Matched drug class / molecular target", "تطابق مع فئة دوائية أو هدف جزيئي"),
                            targetDrugClassId = drug.id,
                            sources = drug.sources
                        )
                    )
                }
            }
        }

        // 5. Medical Dictionary Terminology
        if (filterType == null || filterType == KnowledgeEntityType.TERMINOLOGY) {
            MedicalDictionarySeed.curatedTerms.forEach { term ->
                val haystack = LocalTerminologyRepository.normalizeArabicAndLatin(
                    "${term.englishName} ${term.arabicName} ${term.synonymsEn.joinToString(" ")} ${term.synonymsAr.joinToString(" ")} ${term.definitionEnglish} ${term.definitionArabic}"
                )
                if (haystack.contains(norm)) {
                    results.add(
                        UnifiedSearchResult(
                            id = "term_${term.id}",
                            entityType = KnowledgeEntityType.TERMINOLOGY,
                            titleEn = term.englishName,
                            titleAr = term.arabicName,
                            subtitle = LocalizedText(term.definitionEnglish, term.definitionArabic),
                            matchReason = LocalizedText("Matched bilingual medical dictionary", "تطابق مع القاموس الطبي ثنائي اللغة"),
                            targetTermId = term.id,
                            targetStructureId = term.relatedAnatomyIds.firstOrNull(),
                            sources = term.sources
                        )
                    )
                }
            }
        }

        // 6. Learning Academy Lessons
        if (filterType == null || filterType == KnowledgeEntityType.ACADEMY_LESSON) {
            LearningAcademySeed.courses.forEach { course ->
                course.lessons.forEach { lesson ->
                    val haystack = LocalTerminologyRepository.normalizeArabicAndLatin(
                        "${course.title.en} ${course.title.ar} ${lesson.title.en} ${lesson.title.ar} ${lesson.readingSummary.en} ${lesson.readingSummary.ar}"
                    )
                    if (haystack.contains(norm)) {
                        results.add(
                            UnifiedSearchResult(
                                id = "lesson_${lesson.id}",
                                entityType = KnowledgeEntityType.ACADEMY_LESSON,
                                titleEn = lesson.title.en,
                                titleAr = lesson.title.ar,
                                subtitle = course.title,
                                matchReason = LocalizedText("Matched Learning Academy lesson", "تطابق مع درس في الأكاديمية الطبية"),
                                targetCourseId = course.id,
                                sources = course.sources
                            )
                        )
                    }
                }
            }
        }

        return results.take(30)
    }

    /**
     * Resolves verified, allowlisted anatomical structure ID from a natural-language request
     * (e.g., "Show me the mitral valve" / "أرني الصمام التاجي") for safe AI tutor tool actions.
     */
    fun resolveValidatedStructureId(text: String): String? {
        val norm = LocalTerminologyRepository.normalizeArabicAndLatin(text)
        if (norm.isBlank()) return null
        CardiovascularAnatomySeed.cardiovascularStructures.forEach { s ->
            val en = LocalTerminologyRepository.normalizeArabicAndLatin(s.englishName)
            val ar = LocalTerminologyRepository.normalizeArabicAndLatin(s.arabicName)
            val latin = LocalTerminologyRepository.normalizeArabicAndLatin(s.scientificTerminology)
            if (norm.contains(en) || norm.contains(ar) || norm.contains(latin)) {
                return s.id
            }
            val aliases = transliterationAliases[s.id].orEmpty()
            if (aliases.any { norm.contains(LocalTerminologyRepository.normalizeArabicAndLatin(it)) }) {
                return s.id
            }
        }
        return null
    }

    /**
     * Retrieves verified internal educational context entries relevant to a student's question
     * plus the currently active screen/topic context for RAG grounding.
     */
    fun retrieveGroundedContext(
        question: String,
        activeStructureId: String?,
        activePhysiologySystem: PhysiologySystemId?,
        activeDiseaseId: String?,
        activeDrugClassId: String?
    ): List<KnowledgeGraphContext> {
        val retrieved = linkedMapOf<String, KnowledgeGraphContext>()

        // 1. Always inject active application context first so the student does not need to re-type it
        activeStructureId?.let { id ->
            CardiovascularAnatomySeed.cardiovascularStructures.firstOrNull { it.id == id }?.let { s ->
                val relDiseases = PathologySeed.diseases.filter { it.linkedAnatomyStructureId == s.id || it.systemId == s.systemId }.map { it.id }
                val relDrugs = PharmacologySeed.drugClasses.filter { it.systemId == s.systemId }.map { it.id }
                retrieved["anat_${s.id}"] = KnowledgeGraphContext(
                    topicId = s.id,
                    entityType = KnowledgeEntityType.ANATOMY,
                    titleEn = s.englishName,
                    titleAr = s.arabicName,
                    verifiedSummaryEn = "${s.generalDescription.en} Function: ${s.biologicalFunction.en}",
                    verifiedSummaryAr = "${s.generalDescription.ar} الوظيفة: ${s.biologicalFunction.ar}",
                    relatedAnatomyIds = s.relatedStructureIds,
                    relatedPhysiologySystems = listOf(PhysiologySystemId.CARDIOVASCULAR),
                    relatedDiseaseIds = relDiseases,
                    relatedDrugClassIds = relDrugs,
                    sources = s.sources
                )
            }
        }

        activePhysiologySystem?.let { sys ->
            val model = SimulationModelRegistry.getModel(sys)
            val exp = SimulationModelRegistry.getExplanation(sys)
            retrieved["phys_${sys.id}"] = KnowledgeGraphContext(
                topicId = sys.id,
                entityType = KnowledgeEntityType.PHYSIOLOGY,
                titleEn = model.titleEnglish,
                titleAr = model.titleArabic,
                verifiedSummaryEn = "${exp.scientificDefinition.en} Biological function: ${exp.biologicalFunction.en} Validity: ${model.validityLevel.code}.",
                verifiedSummaryAr = "${exp.scientificDefinition.ar} الوظيفة الحيوية: ${exp.biologicalFunction.ar}",
                relatedAnatomyIds = listOf(sys.defaultAnatomyStructureId),
                relatedPhysiologySystems = listOf(sys),
                relatedDiseaseIds = PathologySeed.diseases.filter { it.linkedPhysiologySystem == sys }.map { it.id },
                relatedDrugClassIds = PharmacologySeed.drugClasses.filter { it.linkedPhysiologySystem == sys }.map { it.id },
                sources = exp.sources
            )
        }

        activeDiseaseId?.let { id ->
            PathologySeed.findById(id)?.let { d ->
                retrieved["path_${d.id}"] = KnowledgeGraphContext(
                    topicId = d.id,
                    entityType = KnowledgeEntityType.PATHOLOGY,
                    titleEn = d.englishName,
                    titleAr = d.arabicName,
                    verifiedSummaryEn = "${d.overview.en} Normal: ${d.normalStateSummary.en} Diseased: ${d.diseasedStateSummary.en}",
                    verifiedSummaryAr = "${d.overview.ar} السليم: ${d.normalStateSummary.ar} المريض: ${d.diseasedStateSummary.ar}",
                    relatedAnatomyIds = listOf(d.linkedAnatomyStructureId),
                    relatedPhysiologySystems = listOf(d.linkedPhysiologySystem),
                    relatedDiseaseIds = listOf(d.id),
                    relatedDrugClassIds = d.linkedDrugClassIds,
                    sources = d.sources
                )
            }
        }

        activeDrugClassId?.let { id ->
            PharmacologySeed.findById(id)?.let { drug ->
                retrieved["pharm_${drug.id}"] = KnowledgeGraphContext(
                    topicId = drug.id,
                    entityType = KnowledgeEntityType.PHARMACOLOGY,
                    titleEn = drug.englishName,
                    titleAr = drug.arabicName,
                    verifiedSummaryEn = "Prototype: ${drug.prototypeGenericEn}. Target: ${drug.molecularTarget.en}. MOA: ${drug.mechanismOfAction.en} Safety: ${drug.safetyAndContraindications.en}",
                    verifiedSummaryAr = "المثال: ${drug.prototypeGenericAr}. الهدف: ${drug.molecularTarget.ar}. الآلية: ${drug.mechanismOfAction.ar} السلامة: ${drug.safetyAndContraindications.ar}",
                    relatedAnatomyIds = emptyList(),
                    relatedPhysiologySystems = listOf(drug.linkedPhysiologySystem),
                    relatedDiseaseIds = drug.linkedDiseaseIds,
                    relatedDrugClassIds = listOf(drug.id),
                    sources = drug.sources
                )
            }
        }

        // 2. Keyword/token retrieval across all verified records
        val tokens = LocalTerminologyRepository.normalizeArabicAndLatin(question)
            .split(Regex("\\s+"))
            .filter { it.length >= 3 }
            .distinct()
            .take(6)

        tokens.forEach { token ->
            searchAll(token).take(3).forEach { hit ->
                if (retrieved.size >= 5) return@forEach
                when (hit.entityType) {
                    KnowledgeEntityType.ANATOMY -> hit.targetStructureId?.let { id ->
                        CardiovascularAnatomySeed.cardiovascularStructures.firstOrNull { it.id == id }?.let { s ->
                            retrieved.putIfAbsent("anat_${s.id}", KnowledgeGraphContext(
                                topicId = s.id,
                                entityType = KnowledgeEntityType.ANATOMY,
                                titleEn = s.englishName,
                                titleAr = s.arabicName,
                                verifiedSummaryEn = "${s.generalDescription.en} Function: ${s.biologicalFunction.en}",
                                verifiedSummaryAr = "${s.generalDescription.ar} الوظيفة: ${s.biologicalFunction.ar}",
                                relatedAnatomyIds = s.relatedStructureIds,
                                relatedPhysiologySystems = listOf(PhysiologySystemId.CARDIOVASCULAR),
                                relatedDiseaseIds = emptyList(),
                                relatedDrugClassIds = emptyList(),
                                sources = s.sources
                            ))
                        }
                    }
                    KnowledgeEntityType.PATHOLOGY -> hit.targetDiseaseId?.let { id ->
                        PathologySeed.findById(id)?.let { d ->
                            retrieved.putIfAbsent("path_${d.id}", KnowledgeGraphContext(
                                topicId = d.id,
                                entityType = KnowledgeEntityType.PATHOLOGY,
                                titleEn = d.englishName,
                                titleAr = d.arabicName,
                                verifiedSummaryEn = "${d.overview.en} Diseased state: ${d.diseasedStateSummary.en}",
                                verifiedSummaryAr = "${d.overview.ar} الحالة المرضية: ${d.diseasedStateSummary.ar}",
                                relatedAnatomyIds = listOf(d.linkedAnatomyStructureId),
                                relatedPhysiologySystems = listOf(d.linkedPhysiologySystem),
                                relatedDiseaseIds = listOf(d.id),
                                relatedDrugClassIds = d.linkedDrugClassIds,
                                sources = d.sources
                            ))
                        }
                    }
                    KnowledgeEntityType.PHARMACOLOGY -> hit.targetDrugClassId?.let { id ->
                        PharmacologySeed.findById(id)?.let { drug ->
                            retrieved.putIfAbsent("pharm_${drug.id}", KnowledgeGraphContext(
                                topicId = drug.id,
                                entityType = KnowledgeEntityType.PHARMACOLOGY,
                                titleEn = drug.englishName,
                                titleAr = drug.arabicName,
                                verifiedSummaryEn = "Target: ${drug.molecularTarget.en}. MOA: ${drug.mechanismOfAction.en} Safety: ${drug.safetyAndContraindications.en}",
                                verifiedSummaryAr = "الهدف: ${drug.molecularTarget.ar}. الآلية: ${drug.mechanismOfAction.ar} السلامة: ${drug.safetyAndContraindications.ar}",
                                relatedAnatomyIds = emptyList(),
                                relatedPhysiologySystems = listOf(drug.linkedPhysiologySystem),
                                relatedDiseaseIds = drug.linkedDiseaseIds,
                                relatedDrugClassIds = listOf(drug.id),
                                sources = drug.sources
                            ))
                        }
                    }
                    KnowledgeEntityType.PHYSIOLOGY -> hit.targetPhysiologySystem?.let { sys ->
                        val model = SimulationModelRegistry.getModel(sys)
                        val exp = SimulationModelRegistry.getExplanation(sys)
                        retrieved.putIfAbsent("phys_${sys.id}", KnowledgeGraphContext(
                            topicId = sys.id,
                            entityType = KnowledgeEntityType.PHYSIOLOGY,
                            titleEn = model.titleEnglish,
                            titleAr = model.titleArabic,
                            verifiedSummaryEn = exp.scientificDefinition.en,
                            verifiedSummaryAr = exp.scientificDefinition.ar,
                            relatedAnatomyIds = listOf(sys.defaultAnatomyStructureId),
                            relatedPhysiologySystems = listOf(sys),
                            relatedDiseaseIds = emptyList(),
                            relatedDrugClassIds = emptyList(),
                            sources = exp.sources
                        ))
                    }
                    KnowledgeEntityType.TERMINOLOGY -> hit.targetTermId?.let { id ->
                        MedicalDictionarySeed.curatedTerms.firstOrNull { it.id == id }?.let { t ->
                            retrieved.putIfAbsent("term_${t.id}", KnowledgeGraphContext(
                                topicId = t.id,
                                entityType = KnowledgeEntityType.TERMINOLOGY,
                                titleEn = t.englishName,
                                titleAr = t.arabicName,
                                verifiedSummaryEn = t.definitionEnglish,
                                verifiedSummaryAr = t.definitionArabic,
                                relatedAnatomyIds = t.relatedAnatomyIds,
                                relatedPhysiologySystems = emptyList(),
                                relatedDiseaseIds = emptyList(),
                                relatedDrugClassIds = emptyList(),
                                sources = t.sources
                            ))
                        }
                    }
                    KnowledgeEntityType.ACADEMY_LESSON -> Unit
                }
            }
        }

        return retrieved.values.toList()
    }

    fun formatMatchSnippet(result: UnifiedSearchResult, language: AppLanguage): String {
        val primary = if (language == AppLanguage.ARABIC) result.titleAr else result.titleEn
        val secondary = if (language == AppLanguage.ARABIC) result.titleEn else result.titleAr
        return "$primary ($secondary)"
    }
}
