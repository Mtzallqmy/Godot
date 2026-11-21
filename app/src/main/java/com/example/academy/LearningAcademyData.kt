package com.example.academy

import com.example.core.i18n.LocalizedText
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ReferenceSource
import com.example.data.seed.MedicalReferencesSeed
import com.example.physiology.engine.PhysiologyReferencesSeed
import com.example.physiology.engine.PhysiologySystemId

enum class DifficultyTier(val label: LocalizedText) {
    BEGINNER(LocalizedText("Beginner", "مبتدئ")),
    INTERMEDIATE(LocalizedText("Intermediate", "متوسط")),
    ADVANCED(LocalizedText("Advanced", "متقدم"))
}

enum class AcademyDiscipline(val label: LocalizedText) {
    HUMAN_ANATOMY(LocalizedText("Human Anatomy", "التشريح البشري")),
    HUMAN_PHYSIOLOGY(LocalizedText("Human Physiology", "علم وظائف الأعضاء")),
    PATHOLOGY(LocalizedText("Pathology", "علم الأمراض")),
    PHARMACOLOGY(LocalizedText("Pharmacology", "علم الأدوية")),
    MEDICAL_TERMINOLOGY(LocalizedText("Medical Terminology", "المصطلحات الطبية"))
}

data class AcademyLesson(
    val id: String,
    val title: LocalizedText,
    val durationMinutes: Int,
    val readingSummary: LocalizedText,
    val keyTakeaway: LocalizedText,
    val linkedAnatomyStructureId: String? = null,
    val linkedPhysiologySystem: PhysiologySystemId? = null,
    val linkedDiseaseId: String? = null,
    val linkedDrugClassId: String? = null
)

data class AcademyCourse(
    val id: String,
    val discipline: AcademyDiscipline,
    val difficulty: DifficultyTier,
    val title: LocalizedText,
    val description: LocalizedText,
    val learningObjectives: List<LocalizedText>,
    val lessons: List<AcademyLesson>,
    val reviewStatus: MedicalReviewStatus,
    val sources: List<ReferenceSource>
)

enum class AssessmentQuestionType(val label: LocalizedText) {
    MULTIPLE_CHOICE(LocalizedText("Multiple Choice", "اختيار من متعدد")),
    MULTIPLE_SELECT(LocalizedText("Multiple Select", "اختيار متعدد الإجابات")),
    TRUE_FALSE(LocalizedText("True or False", "صح أم خطأ")),
    TERMINOLOGY_MATCHING(LocalizedText("Terminology Matching", "مطابقة المصطلحات")),
    STRUCTURE_IDENTIFICATION(LocalizedText("Structure Identification", "تحديد البنية التشريحية")),
    PHYSIOLOGY_ORDERING(LocalizedText("Physiology Ordering", "ترتيب التسلسل الفسيولوجي")),
    DRUG_MECHANISM_MATCH(LocalizedText("Drug Mechanism Matching", "مطابقة آلية الدواء")),
    EDUCATIONAL_CASE(LocalizedText("Educational Case", "حالة تعليمية تطبيقية"))
}

data class AssessmentQuestion(
    val id: String,
    val subject: AcademyDiscipline,
    val difficulty: DifficultyTier,
    val type: AssessmentQuestionType,
    val prompt: LocalizedText,
    val options: List<LocalizedText> = emptyList(),
    val correctIndices: Set<Int> = setOf(0),
    val orderedItems: List<LocalizedText> = emptyList(),
    val matchPairs: List<Pair<String, String>> = emptyList(),
    val explanation: LocalizedText,
    val source: ReferenceSource
)

object LearningAcademySeed {

    val courses: List<AcademyCourse> = listOf(
        AcademyCourse(
            id = "course_cardio_anatomy",
            discipline = AcademyDiscipline.HUMAN_ANATOMY,
            difficulty = DifficultyTier.BEGINNER,
            title = LocalizedText("Foundations of Cardiovascular 3D Anatomy", "أساسيات تشريح القلب والأوعية ثلاثي الأبعاد"),
            description = LocalizedText(
                "Master the four cardiac chambers, great vessels, and valve orientation using interactive 3D inspection and TA2 nomenclature.",
                "أتقن حجرات القلب الأربع والأوعية الكبرى ومواقع الصمامات عبر الفحص ثلاثي الأبعاد التفاعلي ومصطلحات TA2."
            ),
            learningObjectives = listOf(
                LocalizedText("Differentiate oxygenated left-heart chambers from deoxygenated right-heart chambers.", "التمييز بين حجرات الجانب الأيسر المؤكسجة وحجرات الجانب الأيمن غير المؤكسجة."),
                LocalizedText("Trace blood outflow through the Aorta and Pulmonary Artery.", "تتبع خروج الدم عبر الشريان الأبهر والشريان الرئوي.")
            ),
            lessons = listOf(
                AcademyLesson(
                    id = "acad_cardio_chambers",
                    title = LocalizedText("1. Four Chambers & Interventricular Septum", "1. الحجرات الأربع والحاجز بين البطينين"),
                    durationMinutes = 8,
                    readingSummary = LocalizedText(
                        "The Left Ventricle features the thickest myocardium because it pumps blood across the entire systemic circulation, separated from the Right Ventricle by the muscular interventricular septum.",
                        "يمتاز البطين الأيسر بأسمك عضلة قلبية لضخ الدم نحو الدورة الجهازية بأكملها، ويفصله عن البطين الأيمن الحاجز العضلي بين البطينين."
                    ),
                    keyTakeaway = LocalizedText(
                        "Inspect Left Ventricle & Interventricular Septum using X-Ray mode in 3D Anatomy.",
                        "افحص البطين الأيسر والحاجز بين البطينين باستخدام وضع الشفافية في التشريح ثلاثي الأبعاد."
                    ),
                    linkedAnatomyStructureId = "left_ventricle"
                ),
                AcademyLesson(
                    id = "acad_great_vessels",
                    title = LocalizedText("2. Great Vessels: Aorta, Pulmonary Artery & Venae Cavae", "2. الأوعية الكبرى: الأبهر والشريان الرئوي والأجوفان"),
                    durationMinutes = 7,
                    readingSummary = LocalizedText(
                        "Arteries always carry blood away from the heart regardless of oxygenation: the Pulmonary Artery carries deoxygenated blood to the lungs while the Aorta delivers oxygenated blood systemically.",
                        "تحمل الشرايين الدم دائماً بعيداً عن القلب بغض النظر عن الأكسجة: يحمل الشريان الرئوي دماً غير مؤكسج للرئتين بينما يوزع الأبهر الدم المؤكسج للجسم."
                    ),
                    keyTakeaway = LocalizedText(
                        "Isolate the Aorta and Pulmonary Artery in the 3D viewport to compare their origins.",
                        "اعزل الشريان الأبهر والشريان الرئوي في العارض ثلاثي الأبعاد لمقارنة منشئهما."
                    ),
                    linkedAnatomyStructureId = "aorta"
                )
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(MedicalReferencesSeed.ifaaFipatTa2, MedicalReferencesSeed.nihNhlbiHeart)
        ),
        AcademyCourse(
            id = "course_organ_physiology",
            discipline = AcademyDiscipline.HUMAN_PHYSIOLOGY,
            difficulty = DifficultyTier.INTERMEDIATE,
            title = LocalizedText("Integrated Organ Physiology Simulations", "محاكيات فسيولوجيا الأعضاء المتكاملة"),
            description = LocalizedText(
                "Connect mechanical cardiac cycles, alveolar diffusion, saltatory nerve conduction, and nephron filtration in real time.",
                "اربط بين الدورة القلبية الميكانيكية وتبادل الغازات السنخي والتوصيل العصبي القفزي والترشيح الكلوي بصورة تفاعلية."
            ),
            learningObjectives = listOf(
                LocalizedText("Correlate P-QRS-T schematic waveforms with mechanical valve transitions.", "ربط موجات ECG التخطيطية بفتح وإغلاق صمامات القلب الميكانيكية."),
                LocalizedText("Explain partial pressure gradients driving alveolar O₂ and CO₂ diffusion.", "شرح تدرجات الضغط الجزئي الموجهة لانتشار O₂ و CO₂ في الحويصلات.")
            ),
            lessons = listOf(
                AcademyLesson(
                    id = "acad_phys_cardiac",
                    title = LocalizedText("1. Cardiac Cycle Mechanics & Electrical Conduction", "1. ميكانيكا الدورة القلبية والتوصيل الكهربائي"),
                    durationMinutes = 10,
                    readingSummary = LocalizedText(
                        "Electrical depolarization originating at the SA Node initiates atrial systole, followed by brief AV nodal delay and rapid ventricular excitation through the Bundle of His and Purkinje fibers.",
                        "يبدأ التنبيه الكهربائي في العقدة الجيبية مسبباً انقباض الأذينين، يليه تأخير قصير في العقدة الأذينية البطينية ثم تنبيه بطيني سريع عبر حزمة هيس وألياف بركنجي."
                    ),
                    keyTakeaway = LocalizedText(
                        "Launch the Cardiovascular Physiology Lab and toggle the Conduction overlay.",
                        "افتح مختبر فسيولوجيا القلب وفعّل طبقة التوصيل الكهربائي."
                    ),
                    linkedPhysiologySystem = PhysiologySystemId.CARDIOVASCULAR
                ),
                AcademyLesson(
                    id = "acad_phys_respiratory",
                    title = LocalizedText("2. Diaphragm Mechanics & Alveolar Gas Exchange", "2. ميكانيكا الحجاب الحاجز وتبادل الغازات السنخي"),
                    durationMinutes = 9,
                    readingSummary = LocalizedText(
                        "Active contraction of the diaphragm increases thoracic volume, drawing fresh air downward; across the alveolar-capillary membrane, O₂ diffuses into blood while CO₂ exits into alveolar air.",
                        "يزيد انقباض الحجاب الحاجز حجم الصدر ساحباً الهواء للداخل، وعبر الغشاء السنخي الشعيري ينتشر O₂ للدم بينما يخرج CO₂ للهواء السنخي."
                    ),
                    keyTakeaway = LocalizedText(
                        "Compare Quiet Breathing vs. Alveolar Gas Exchange Close-Up in the Respiratory Lab.",
                        "قارن بين التنفس الهادئ ومشهد تبادل الغازات السنخي في مختبر التنفس."
                    ),
                    linkedPhysiologySystem = PhysiologySystemId.RESPIRATORY
                )
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(PhysiologyReferencesSeed.ahaHeartCycle, PhysiologyReferencesSeed.medlinePlusLungs)
        ),
        AcademyCourse(
            id = "course_systemic_pathology",
            discipline = AcademyDiscipline.PATHOLOGY,
            difficulty = DifficultyTier.INTERMEDIATE,
            title = LocalizedText("Core Mechanisms of Systemic Pathology", "الآليات الأساسية لعلم الأمراض الجهازي"),
            description = LocalizedText(
                "Compare healthy anatomical structures against valvular stenosis, coronary atherosclerosis, airway obstruction, and glomerular injury.",
                "قارن بين البنية السليمة وحالات تضيق الصمام الأبهري وتصلب الشرايين التاجية والربو وإصابة الكبيبة الكلوية."
            ),
            learningObjectives = listOf(
                LocalizedText("Explain how structural lumen narrowing alters physiological flow resistance.", "شرح كيف يغير تضيق اللمعة مقاومة التدفق الفسيولوجي."),
                LocalizedText("Identify normal vs. pathological barriers in cardiac, pulmonary, and renal organs.", "تحديد الفروق بين الحواجز السليمة والمريضة في القلب والرئة والكلية.")
            ),
            lessons = listOf(
                AcademyLesson(
                    id = "acad_path_aortic",
                    title = LocalizedText("1. Valvular Stenosis & Pressure Overload", "1. تضيق الصمامات وفرط حمل الضغط"),
                    durationMinutes = 10,
                    readingSummary = LocalizedText(
                        "Calcific aortic valve stenosis narrows the left ventricular outflow tract, driving concentric myocardial hypertrophy as an adaptive mechanical response.",
                        "يضيّق تضيق الصمام الأبهري التكلسي مخرج البطين الأيسر، محفزاً تضخماً عضلياً مركزياً كاستجابة ميكانيكية تعويضية."
                    ),
                    keyTakeaway = LocalizedText(
                        "Inspect Normal vs. Diseased Aortic Valve side-by-side in the Pathology Atlas.",
                        "قارن الصمام الأبهري السليم والمتضيق جنباً إلى جنب في أطلس علم الأمراض."
                    ),
                    linkedDiseaseId = "aortic_stenosis"
                ),
                AcademyLesson(
                    id = "acad_path_asthma",
                    title = LocalizedText("2. Reversible Airway Narrowing in Bronchial Asthma", "2. تضيق المسالك الهوائية القابل للعكس في الربو"),
                    durationMinutes = 8,
                    readingSummary = LocalizedText(
                        "Smooth muscle constriction, mucosal edema, and excess mucus sharply elevate bronchial airflow resistance—making expiration markedly harder.",
                        "يرفع تشنج العضلات الملساء ووذمة البطانة والمخاط مقاومة المسالك الهوائية بشدة، مما يصعّب عملية الزفير."
                    ),
                    keyTakeaway = LocalizedText(
                        "Review the Bronchial Asthma normal-vs-narrowed cross-section in Pathology Atlas.",
                        "راجع المقطع العرضي المقارن للربو القصبي في أطلس علم الأمراض."
                    ),
                    linkedDiseaseId = "bronchial_asthma"
                )
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(PhysiologyReferencesSeed.ncbiBookshelfPhysiology, MedicalReferencesSeed.nihNhlbiHeart)
        ),
        AcademyCourse(
            id = "course_pharmacology_mechanisms",
            discipline = AcademyDiscipline.PHARMACOLOGY,
            difficulty = DifficultyTier.ADVANCED,
            title = LocalizedText("Receptor Pharmacology & Drug Mechanisms", "علم الأدوية الجزيئي وآليات المستقبلات"),
            description = LocalizedText(
                "Explore how major drug classes interact with β₁/β₂ receptors, ACE enzymes, NKCC2 transporters, and gastric proton pumps.",
                "استكشف كيف تتفاعل الفئات الدوائية الكبرى مع مستقبلات بيتا وإنزيم ACE وناقل NKCC2 ومضخة البروتون المعدية."
            ),
            learningObjectives = listOf(
                LocalizedText("Differentiate receptor agonists from competitive antagonists using cellular signaling pathways.", "التمييز بين الناهضات والحاصرات التنافسية عبر مسارات الإشارة الخلوية."),
                LocalizedText("Connect molecular drug targets to educational safety considerations.", "ربط الأهداف الجزيئية للأدوية باعتبارات السلامة التعليمية.")
            ),
            lessons = listOf(
                AcademyLesson(
                    id = "acad_pharm_beta",
                    title = LocalizedText("1. Beta-1 Blockers vs. Beta-2 Agonists", "1. المقارنة بين حاصرات بيتا-1 وناهضات بيتا-2"),
                    durationMinutes = 11,
                    readingSummary = LocalizedText(
                        "While β₁ antagonists reduce SA/AV nodal cAMP to calm cardiac demand, β₂ agonists raise bronchial smooth-muscle cAMP to dilate constricted airways.",
                        "بينما تخفض حاصرات β₁ مستوى cAMP في العقد القلبية لتقليل العبء القلبي، ترفع ناهضات β₂ مستوى cAMP في القصبات لتوسيع المسالك الهوائية."
                    ),
                    keyTakeaway = LocalizedText(
                        "Explore both receptor visualizations interactively in the Pharmacology Lab.",
                        "استكشف كلا مخططي المستقبلات التفاعليين في مختبر علم الأدوية."
                    ),
                    linkedDrugClassId = "beta_blockers"
                ),
                AcademyLesson(
                    id = "acad_pharm_renal_gi",
                    title = LocalizedText("2. Transporter & Pump Inhibitors: Loop Diuretics & PPIs", "2. مثبطات النواقل والمضخات: مدرات العروة ومثبطات مضخة البروتون"),
                    durationMinutes = 10,
                    readingSummary = LocalizedText(
                        "Loop diuretics inhibit NKCC2 in the Thick Ascending Limb of Henle, whereas PPIs covalently block H⁺/K⁺-ATPase on gastric parietal cells.",
                        "تثبط مدرات العروة ناقل NKCC2 في الطرف الصاعد السميك لالتواء هنلي، بينما تثبط PPIs مضخة H⁺/K⁺-ATPase تساهمياً في الخلايا الجدارية."
                    ),
                    keyTakeaway = LocalizedText(
                        "Compare Loop Diuretics and PPIs in the Pharmacology Lab with verified DailyMed references.",
                        "قارن بين مدرات العروة ومثبطات مضخة البروتون في مختبر الأدوية مع مراجع DailyMed."
                    ),
                    linkedDrugClassId = "loop_diuretics"
                )
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        )
    )

    val assessmentQuestions: List<AssessmentQuestion> = listOf(
        AssessmentQuestion(
            id = "aq_mc_01",
            subject = AcademyDiscipline.HUMAN_ANATOMY,
            difficulty = DifficultyTier.BEGINNER,
            type = AssessmentQuestionType.MULTIPLE_CHOICE,
            prompt = LocalizedText(
                "Which heart chamber generates the highest systolic pressure to propel oxygenated blood into the Aorta?",
                "أي حجرة قلبية تولّد أعلى ضغط انقباضي لدفع الدم المؤكسج إلى الشريان الأبهر؟"
            ),
            options = listOf(
                LocalizedText("Right Atrium", "الأذين الأيمن"),
                LocalizedText("Right Ventricle", "البطين الأيمن"),
                LocalizedText("Left Atrium", "الأذين الأيسر"),
                LocalizedText("Left Ventricle", "البطين الأيسر")
            ),
            correctIndices = setOf(3),
            explanation = LocalizedText(
                "The Left Ventricle has the thickest muscular wall because it overcomes systemic vascular resistance across the entire body.",
                "يمتلك البطين الأيسر أسمك جدار عضلي لتجاوز مقاومة الأوعية الجهازية في كامل الجسم."
            ),
            source = MedicalReferencesSeed.nihNhlbiHeart
        ),
        AssessmentQuestion(
            id = "aq_ms_02",
            subject = AcademyDiscipline.HUMAN_PHYSIOLOGY,
            difficulty = DifficultyTier.INTERMEDIATE,
            type = AssessmentQuestionType.MULTIPLE_SELECT,
            prompt = LocalizedText(
                "Select ALL cardiac valves that are OPEN during Ventricular Ejection (Systole):",
                "اختر جميع صمامات القلب التي تكون مفتوحة أثناء مرحلة القذف البطيني (الانقباض):"
            ),
            options = listOf(
                LocalizedText("Tricuspid Valve", "الصمام ثلاثي الشرفات"),
                LocalizedText("Pulmonary Valve", "الصمام الرئوي"),
                LocalizedText("Mitral (Bicuspid) Valve", "الصمام التاجي"),
                LocalizedText("Aortic Valve", "الصمام الأبهري")
            ),
            correctIndices = setOf(1, 3),
            explanation = LocalizedText(
                "During ventricular ejection, the semilunar valves (Pulmonary and Aortic) open while both atrioventricular valves (Tricuspid and Mitral) remain tightly closed.",
                "أثناء القذف البطيني ينفتح الصمامان الهلاليان (الرئوي والأبهري) بينما يبقى الصمامان الأذينيان البطينيان مغلقين بإحكام."
            ),
            source = PhysiologyReferencesSeed.ahaHeartCycle
        ),
        AssessmentQuestion(
            id = "aq_tf_03",
            subject = AcademyDiscipline.HUMAN_PHYSIOLOGY,
            difficulty = DifficultyTier.BEGINNER,
            type = AssessmentQuestionType.TRUE_FALSE,
            prompt = LocalizedText(
                "True or False: Deoxygenated venous blood inside the human body is literally blue in color.",
                "صح أم خطأ: الدم الوريدي غير المؤكسج داخل جسم الإنسان لونه أزرق فعلياً."
            ),
            options = listOf(
                LocalizedText("True", "صحيح"),
                LocalizedText("False", "خطأ")
            ),
            correctIndices = setOf(1),
            explanation = LocalizedText(
                "False. Human venous blood is dark red/crimson. Blue coloration is strictly an educational diagram convention to contrast deoxygenated and oxygenated pathways.",
                "خطأ. الدم الوريدي أحمر داكن في الواقع، ويُستخدم اللون الأزرق اصطلاحياً فقط في المخططات التعليمية للتمييز عن الدم المؤكسج."
            ),
            source = MedicalReferencesSeed.nihNhlbiHeart
        ),
        AssessmentQuestion(
            id = "aq_id_04",
            subject = AcademyDiscipline.PATHOLOGY,
            difficulty = DifficultyTier.INTERMEDIATE,
            type = AssessmentQuestionType.STRUCTURE_IDENTIFICATION,
            prompt = LocalizedText(
                "In Glomerular Filtration Barrier Injury, which nephron filtration structure normally prevents plasma albumin from entering Bowman's space?",
                "في إصابة حاجز الترشيح الكبيبي، أي بنية في النفرون تمنع عادةً تسرب الألبومين إلى محفظة بومان؟"
            ),
            options = listOf(
                LocalizedText("Collecting Duct principal cells", "خلايا القناة الجامعة"),
                LocalizedText("Glomerular basement membrane & podocytes", "الغشاء القاعدي الكبيبي والخلايا الرجلاء"),
                LocalizedText("Thick ascending limb NKCC2 transporter", "ناقل NKCC2 في الطرف الصاعد السميك"),
                LocalizedText("Renal pelvis urothelium", "ظهارة حويضة الكلية")
            ),
            correctIndices = setOf(1),
            explanation = LocalizedText(
                "The fenestrated glomerular endothelium, negatively charged basement membrane, and podocyte filtration slits form the selective filtration barrier.",
                "يشكل الغشاء القاعدي الكبيبي سالب الشحنة والخلايا الرجلاء والبطانة المثقبة حاجز الترشيح الانتقائي."
            ),
            source = PhysiologyReferencesSeed.nihNiddkKidney
        ),
        AssessmentQuestion(
            id = "aq_ord_05",
            subject = AcademyDiscipline.HUMAN_PHYSIOLOGY,
            difficulty = DifficultyTier.INTERMEDIATE,
            type = AssessmentQuestionType.PHYSIOLOGY_ORDERING,
            prompt = LocalizedText(
                "Order the canonical Cardiac Electrical Conduction pathway from pacemaker origin to ventricular myocardium:",
                "رتّب مسار التوصيل الكهربائي القلبي الصحيح من منشأ الناظمة حتى عضلة البطينين:"
            ),
            orderedItems = listOf(
                LocalizedText("Sinoatrial (SA) Node", "العقدة الجيبية الأذينية (SA)"),
                LocalizedText("Atrioventricular (AV) Node", "العقدة الأذينية البطينية (AV)"),
                LocalizedText("Bundle of His & Bundle Branches", "حزمة هيس وفروعها"),
                LocalizedText("Purkinje Fibers", "ألياف بركنجي")
            ),
            explanation = LocalizedText(
                "Normal excitation starts at the SA Node, slows briefly at the AV Node to allow ventricular filling, then conducts rapidly via Bundle of His, Bundle Branches, and Purkinje Fibers.",
                "يبدأ التنبيه في العقدة الجيبية، يتباطأ لحظياً في العقدة الأذينية البطينية، ثم ينتقل بسرعة عبر حزمة هيس وفروعها وألياف بركنجي."
            ),
            source = PhysiologyReferencesSeed.ahaHeartCycle
        ),
        AssessmentQuestion(
            id = "aq_match_06",
            subject = AcademyDiscipline.MEDICAL_TERMINOLOGY,
            difficulty = DifficultyTier.BEGINNER,
            type = AssessmentQuestionType.TERMINOLOGY_MATCHING,
            prompt = LocalizedText(
                "Match each English anatomical/physiological term with its exact Arabic medical equivalent:",
                "طابق كل مصطلح طبي إنجليزي مع مقابله العربي العلمي الدقيق:"
            ),
            matchPairs = listOf(
                "Aortic Valve" to "الصمام الأبهري",
                "Diaphragm" to "الحجاب الحاجز",
                "Nephron" to "النفرون (الوحدة الكلوية)"
            ),
            explanation = LocalizedText(
                "Consistent Arabic–English medical terminology mapping reinforces bilingual scientific precision.",
                "يعزز الربط الدقيق بين المصطلحات الطبية العربية والإنجليزية الدقة العلمية ثنائية اللغة."
            ),
            source = MedicalReferencesSeed.ifaaFipatTa2
        ),
        AssessmentQuestion(
            id = "aq_drug_07",
            subject = AcademyDiscipline.PHARMACOLOGY,
            difficulty = DifficultyTier.ADVANCED,
            type = AssessmentQuestionType.DRUG_MECHANISM_MATCH,
            prompt = LocalizedText(
                "Match each drug class with its primary molecular target:",
                "طابق كل فئة دوائية مع هدفها الجزيئي الرئيسي:"
            ),
            matchPairs = listOf(
                "Loop Diuretics (Furosemide)" to "NKCC2 Co-transporter (Loop of Henle)",
                "Proton Pump Inhibitors (Omeprazole)" to "Gastric Parietal H⁺/K⁺-ATPase",
                "SABA (Albuterol)" to "Bronchial Smooth-Muscle β₂ Receptor"
            ),
            explanation = LocalizedText(
                "Loop diuretics inhibit NKCC2 in the thick ascending limb, PPIs inhibit parietal H⁺/K⁺-ATPase, and SABA agents stimulate airway β₂ receptors.",
                "تثبط مدرات العروة ناقل NKCC2، وتثبط PPIs مضخة H⁺/K⁺-ATPase الجدارية، بينما تنشط ناهضات β₂ استرخاء القصبات."
            ),
            source = PhysiologyReferencesSeed.ncbiBookshelfPhysiology
        ),
        AssessmentQuestion(
            id = "aq_case_08",
            subject = AcademyDiscipline.PHARMACOLOGY,
            difficulty = DifficultyTier.ADVANCED,
            type = AssessmentQuestionType.EDUCATIONAL_CASE,
            prompt = LocalizedText(
                "Educational Concept Case: A physiology student studies how blocking Angiotensin-Converting Enzyme lowers intraglomerular pressure while occasionally causing a persistent dry cough. Which mediator accumulation explains this educational mechanism?",
                "حالة مفاهيمية تعليمية: يدرس طالب طب كيف يخفض تثبيط إنزيم ACE الضغط الكبيبي وقد يسبب أحياناً سعالاً جافاً. تراكم أي وسيط يفسر هذه الآلية التعليمية؟"
            ),
            options = listOf(
                LocalizedText("Aldosterone", "الألدوستيرون"),
                LocalizedText("Bradykinin", "البراديكينين"),
                LocalizedText("Mevalonate", "الميفالونات"),
                LocalizedText("Pepsinogen", "مولد البيبسين")
            ),
            correctIndices = setOf(1),
            explanation = LocalizedText(
                "ACE also degrades bradykinin; inhibiting ACE elevates bradykinin levels, contributing both to vasodilation and to the classic educational dry-cough side effect.",
                "يقوم إنزيم ACE أيضاً بتكسير البراديكينين؛ وتثبيطه يرفع البراديكينين مما يساهم في التوسيع الوعائي والسعال الجاف المعروف تعليمياً."
            ),
            source = PhysiologyReferencesSeed.ncbiBookshelfPhysiology
        )
    )
}
