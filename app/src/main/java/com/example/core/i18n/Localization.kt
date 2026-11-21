package com.example.core.i18n

enum class AppLanguage(
    val code: String,
    val nativeLabel: String,
    val shortBadge: String,
    val isRtl: Boolean
) {
    ENGLISH(code = "en", nativeLabel = "English", shortBadge = "EN", isRtl = false),
    ARABIC(code = "ar", nativeLabel = "العربية", shortBadge = "AR", isRtl = true);

    fun toggled(): AppLanguage = if (this == ENGLISH) ARABIC else ENGLISH

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}

data class LocalizedText(
    val en: String,
    val ar: String
) {
    fun get(language: AppLanguage): String = if (language == AppLanguage.ARABIC) ar else en
    fun secondary(language: AppLanguage): String = if (language == AppLanguage.ARABIC) en else ar
}

object UiStrings {
    const val DEVELOPER_CREDIT = "برمجة وتطوير معتز العلقمي – تعز – اليمن – 2026م"

    val brandTitle = LocalizedText(
        en = "MEDVERSE 3D",
        ar = "عالم الطب التفاعلي"
    )
    val brandSubtitle = LocalizedText(
        en = "Interactive Medical Universe",
        ar = "عالم الطب التفاعلي ثلاثي الأبعاد"
    )
    val phaseBadge = LocalizedText(
        en = "PHASE 06 • COMPLETE 3D MEDICAL EDUCATION PLATFORM",
        ar = "المرحلة 06 • المنصة الطبية التعليمية المتكاملة"
    )

    // Hero Section
    val heroHeading = LocalizedText(
        en = "Explore the Human Body Like Never Before",
        ar = "اكتشف أسرار جسم الإنسان بتجربة طبية ثلاثية الأبعاد"
    )
    val heroDescription = LocalizedText(
        en = "Discover interactive 3D anatomy, live organ physiology, pathology comparisons, drug mechanisms, and a verified AI Medical Tutor in Arabic & English.",
        ar = "استكشف التشريح ثلاثي الأبعاد، ومختبر وظائف الأعضاء، وأطلس علم الأمراض، وآليات الأدوية، والمعلم الطبي الذكي الموثق بالعربية والإنجليزية."
    )
    val launchExplorerCta = LocalizedText(
        en = "Explore 3D Anatomy",
        ar = "استكشف التشريح ثلاثي الأبعاد"
    )
    val openDictionaryCta = LocalizedText(
        en = "Medical Dictionary",
        ar = "القاموس الطبي"
    )
    val quickSearchPlaceholder = LocalizedText(
        en = "Search organs, physiology, diseases, drugs (EN / العربية)...",
        ar = "ابحث عن عضو أو وظيفة أو مرض أو دواء (عربي / EN)..."
    )

    // Navigation Labels (All 12 Platform Destinations)
    val navHome = LocalizedText(en = "Home", ar = "الرئيسية")
    val navAnatomy = LocalizedText(en = "3D Anatomy", ar = "التشريح 3D")
    val navPhysiology = LocalizedText(en = "Physiology", ar = "وظائف الأعضاء")
    val navPathology = LocalizedText(en = "Pathology Atlas", ar = "أطلس الأمراض")
    val navPharmacology = LocalizedText(en = "Pharmacology Lab", ar = "مختبر الأدوية")
    val navDictionary = LocalizedText(en = "Dictionary", ar = "القاموس الطبي")
    val navAiTutor = LocalizedText(en = "AI Tutor", ar = "المعلم الذكي")
    val navAcademy = LocalizedText(en = "Learning Academy", ar = "الأكاديمية الطبية")
    val navAssessments = LocalizedText(en = "Assessments", ar = "الاختبارات")
    val navMyLearning = LocalizedText(en = "My Learning", ar = "تعلّمي")
    val navLabs = LocalizedText(en = "Labs", ar = "المختبرات")
    val navExplore = LocalizedText(en = "Platform", ar = "المنصة")
    val navAbout = LocalizedText(en = "About", ar = "حول المنصة")
    val navAdmin = LocalizedText(en = "Administration", ar = "الإدارة وCMS")

    // Phase 03 & Phase 06 Integration CTAs
    val openPhysiologyCta = LocalizedText(
        en = "Physiology Lab",
        ar = "مختبر وظائف الأعضاء"
    )
    val openAiTutorCta = LocalizedText(
        en = "Ask AI Tutor",
        ar = "اسأل المعلم الذكي"
    )
    val exploreFunctionCta = LocalizedText(
        en = "Explore Function",
        ar = "استكشف الوظيفة"
    )
    val statusActivePhase3 = LocalizedText(
        en = "Live",
        ar = "نشط"
    )
    val statusPhase6Live = LocalizedText(
        en = "Phase 06 Live",
        ar = "نشط الآن"
    )

    // Extended Module Labels
    val modPhysiology = LocalizedText(en = "Physiology Lab", ar = "مختبر وظائف الأعضاء")
    val modPathology = LocalizedText(en = "Pathology Atlas", ar = "أطلس علم الأمراض")
    val modPharmacology = LocalizedText(en = "Pharmacology Lab", ar = "مختبر علم الأدوية")
    val modLearningCenter = LocalizedText(en = "Learning Academy", ar = "الأكاديمية الطبية")
    val modDictionaryFull = LocalizedText(en = "Medical Dictionary", ar = "القاموس الطبي")
    val modAiTutor = LocalizedText(en = "AI Medical Tutor", ar = "المعلم الطبي الذكي")
    val modAssessments = LocalizedText(en = "Assessments", ar = "الاختبارات التفاعلية")
    val modMyLearning = LocalizedText(en = "My Learning", ar = "لوحة تعلّمي")

    // Home Sections
    val sectionInteractivePreview = LocalizedText(
        en = "Live 3D Cardiovascular Preview",
        ar = "معاينة ثلاثية الأبعاد للجهاز القلبي الوعائي"
    )
    val sectionFeaturedSystems = LocalizedText(
        en = "Anatomical Body Systems",
        ar = "أجهزة جسم الإنسان التشريحية"
    )
    val sectionSubjectModules = LocalizedText(
        en = "Medical Science Modules",
        ar = "وحدات العلوم الطبية"
    )
    val sectionLearningCards = LocalizedText(
        en = "Guided Medical Learning Paths",
        ar = "مسارات التعلم الطبي الموجهة"
    )
    val sectionProjectIntro = LocalizedText(
        en = "Built for Scientific Precision & Bilingual Clarity",
        ar = "مصمم بدقة علمية ووضوح ثنائي اللغة"
    )
    val projectIntroBody = LocalizedText(
        en = "MedVerse 3D establishes a modular foundation combining interactive 3D inspection, verified Terminologia Anatomica (TA2) nomenclature, simultaneous Arabic-English terminology, and transparent provenance tracking for medical learners.",
        ar = "يؤسس عالم الطب التفاعلي بنية معيارية متقدمة تجمع بين الفحص ثلاثي الأبعاد التفاعلي، والمصطلحات التشريحية المعتمدة (TA2)، والعرض المتزامن بالعربية والإنجليزية، وتوثيق المصادر العلمية لطلبة الطب والعلوم الصحية."
    )

    // Status Badges
    val statusActivePhase1 = LocalizedText(en = "Phase 01 Active", ar = "نشط في المرحلة 01")
    val statusPhase2Roadmap = LocalizedText(en = "Phase 02 Roadmap", ar = "خارطة المرحلة 02")
    val statusArchitectureReady = LocalizedText(en = "Architecture Ready", ar = "البنية جاهزة")
    val statusVerified = LocalizedText(en = "Medically Reviewed", ar = "مراجع علمياً")
    val statusPendingReview = LocalizedText(en = "Pending Review", ar = "قيد المراجعة")
    val statusSchematic = LocalizedText(en = "Schematic Demonstration", ar = "نموذج تخطيطي تعليمي")
    val statusImportedGlb = LocalizedText(en = "Imported GLB/GLTF", ar = "نموذج GLB/GLTF مستورد")

    // 3D Viewer Controls
    val viewerControlsTitle = LocalizedText(en = "3D Viewport Controls", ar = "أدوات التحكم ثلاثية الأبعاد")
    val camReset = LocalizedText(en = "Reset", ar = "إعادة")
    val camFront = LocalizedText(en = "Front", ar = "أمامي")
    val camRear = LocalizedText(en = "Rear", ar = "خلفي")
    val camLeft = LocalizedText(en = "Left", ar = "أيسر")
    val camRight = LocalizedText(en = "Right", ar = "أيمن")
    val toolOrbit = LocalizedText(en = "Orbit", ar = "تدوير")
    val toolPan = LocalizedText(en = "Pan", ar = "تحريك")
    val toolIsolate = LocalizedText(en = "Isolate", ar = "عزل")
    val toolXray = LocalizedText(en = "X-Ray", ar = "شفافية")
    val toolLighting = LocalizedText(en = "Lighting", ar = "الإضاءة")
    val toolBackground = LocalizedText(en = "Backdrop", ar = "الخلفية")
    val toolFullscreen = LocalizedText(en = "Fullscreen", ar = "ملء الشاشة")
    val toolExitFullscreen = LocalizedText(en = "Exit Fullscreen", ar = "إنهاء ملء الشاشة")
    val toolImportGlb = LocalizedText(en = "Import GLB/GLTF", ar = "استيراد GLB/GLTF")
    val toolProvenance = LocalizedText(en = "Asset Provenance", ar = "مصدر النموذج")
    val gestureHint = LocalizedText(
        en = "Drag to orbit • Pinch to zoom • Tap structure to inspect",
        ar = "اسحب للتدوير • قرّب بإصبعين • انقر على البنية للفحص"
    )
    val schematicNotice = LocalizedText(
        en = "Schematic 3D Demonstration — Not a clinically scanned mesh. Use Import GLB/GLTF to load NIH 3D assets.",
        ar = "نموذج تخطيطي تعليمي ثلاثي الأبعاد — ليس مسحاً سريرياً. يمكنك استيراد نماذج NIH 3D بصيغة GLB/GLTF."
    )

    // Anatomy Explorer
    val systemsHeader = LocalizedText(en = "Body Systems (10)", ar = "أجهزة الجسم (10)")
    val structuresHeader = LocalizedText(en = "Cardiovascular Structures", ar = "البنى التشريحية للقلب والأوعية")
    val filterStructuresPlaceholder = LocalizedText(
        en = "Filter structures...",
        ar = "تصفية البنى التشريحية..."
    )
    val hierarchyLabel = LocalizedText(
        en = "System → Organ → Structure",
        ar = "الجهاز ← العضو ← البنية التشريحية"
    )
    val inspectDetailsCta = LocalizedText(en = "Inspect Medical Details", ar = "عرض التفاصيل الطبية")
    val openIn3DCta = LocalizedText(en = "View in 3D Atlas", ar = "عرض في الأطلس 3D")

    // Detail Panel Tabs
    val tabOverview = LocalizedText(en = "Overview", ar = "نظرة عامة")
    val tabAnatomy = LocalizedText(en = "Anatomy", ar = "التشريح")
    val tabFunction = LocalizedText(en = "Function", ar = "الوظيفة")
    val tabClinical = LocalizedText(en = "Clinical Notes", ar = "ملاحظات سريرية")
    val tabReferences = LocalizedText(en = "References", ar = "المصادر")

    // Detail Panel Labels
    val labelScientificTerm = LocalizedText(en = "Scientific Terminology (TA2)", ar = "المصطلح العلمي (TA2)")
    val labelLocation = LocalizedText(en = "Anatomical Location", ar = "الموقع التشريحي")
    val labelDescription = LocalizedText(en = "General Description", ar = "الوصف العام")
    val labelStructure = LocalizedText(en = "Anatomical Structure", ar = "التركيب التشريحي")
    val labelRelationships = LocalizedText(en = "Relationship to Other Structures", ar = "العلاقة بالبنى المجاورة")
    val labelFunction = LocalizedText(en = "Biological Function", ar = "الوظيفة الحيوية")
    val labelClinicalRelevance = LocalizedText(en = "Clinical Relevance", ar = "الأهمية السريرية")
    val labelRelatedTopics = LocalizedText(en = "Related Anatomical Structures", ar = "البنى التشريحية المرتبطة")
    val labelSources = LocalizedText(en = "Reference Sources & Attribution", ar = "المراجع العلمية والتوثيق")
    val labelReviewStatus = LocalizedText(en = "Verification & Review Status", ar = "حالة المراجعة العلمية")
    val labelMeshMapping = LocalizedText(en = "3D Node Coordinate Mapping", ar = "معرّف وإحداثيات العقدة ثلاثية الأبعاد")

    // Dictionary
    val dictTitle = LocalizedText(en = "Bilingual Medical Dictionary", ar = "القاموس الطبي ثنائي اللغة")
    val dictSubtitle = LocalizedText(
        en = "Curated Arabic–English anatomical and cardiovascular terminology",
        ar = "مصطلحات تشريحية وقلبية وعائية منتقاة ومراجعة بالعربية والإنجليزية"
    )
    val dictSearchHint = LocalizedText(
        en = "Search English or Arabic terms...",
        ar = "ابحث بالمصطلح العربي أو الإنجليزي..."
    )
    val dictBookmarksOnly = LocalizedText(en = "Saved", ar = "المحفوظات")
    val dictAllCategories = LocalizedText(en = "All", ar = "الكل")
    val dictSynonyms = LocalizedText(en = "Synonyms", ar = "المرادفات")
    val dictPronunciation = LocalizedText(en = "Pronunciation", ar = "النطق الصوتي")
    val dictRelatedAnatomy = LocalizedText(en = "Linked 3D Anatomy", ar = "البنى التشريحية المرتبطة")
    val dictNoResults = LocalizedText(
        en = "No matching medical terms found. Try another Arabic or English keyword.",
        ar = "لم يتم العثور على مصطلحات مطابقة. جرّب كلمة بحث أخرى بالعربية أو الإنجليزية."
    )

    // Safety Disclaimer
    val medicalDisclaimerTitle = LocalizedText(
        en = "Medical Education Platform Notice",
        ar = "تنويه منصة التعليم الطبي"
    )
    val medicalDisclaimerBody = LocalizedText(
        en = "MedVerse 3D is strictly an interactive educational reference. It is not a diagnostic medical device and does not provide individualized clinical diagnosis, treatment, or medication prescriptions.",
        ar = "عالم الطب التفاعلي منصة تعليمية طبية فقط، وليس جهازاً طبياً تشخيصياً، ولا يقدم تشخيصاً سريرياً فردياً أو وصفات علاجية."
    )
}
