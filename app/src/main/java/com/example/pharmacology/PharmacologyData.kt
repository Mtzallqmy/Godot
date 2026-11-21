package com.example.pharmacology

import com.example.core.i18n.LocalizedText
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ReferenceSource
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.ScientificValidityLevel

enum class ReceptorVisualType {
    BETA1_ANTAGONIST,
    ACE_INHIBITION,
    BETA2_AGONIST,
    NKCC2_BLOCKER,
    PROTON_PUMP_BLOCKER,
    HMG_COA_INHIBITOR
}

data class DrugClassRecord(
    val id: String,
    val englishName: String,
    val arabicName: String,
    val prototypeGenericEn: String,
    val prototypeGenericAr: String,
    val rxNormReferenceConcept: String,
    val systemId: String,
    val linkedPhysiologySystem: PhysiologySystemId,
    val linkedDiseaseIds: List<String>,
    val visualType: ReceptorVisualType,
    val validityLevel: ScientificValidityLevel,
    val molecularTarget: LocalizedText,
    val mechanismOfAction: LocalizedText,
    val mechanismSteps: List<LocalizedText>,
    val pharmacodynamicEffects: LocalizedText,
    val safetyAndContraindications: LocalizedText,
    val labelReferenceDate: String,
    val reviewStatus: MedicalReviewStatus,
    val sources: List<ReferenceSource>
)

object PharmacologySeed {

    private val dailyMedRef = ReferenceSource(
        id = "ref_nih_dailymed",
        title = "NIH National Library of Medicine — DailyMed FDA Structured Product Labeling",
        organization = "National Library of Medicine (NIH)",
        url = "https://dailymed.nlm.nih.gov/dailymed/",
        licenseNote = LocalizedText(
            en = "Public U.S. government drug labeling database",
            ar = "قاعدة بيانات نشرات الأدوية المعتمدة من المكتبة الوطنية للطب"
        )
    )

    private val rxNormRef = ReferenceSource(
        id = "ref_nih_rxnorm",
        title = "NIH NLM — RxNorm Standardized Clinical Drug Nomenclature",
        organization = "National Library of Medicine (NIH)",
        url = "https://www.nlm.nih.gov/research/umls/rxnorm/",
        licenseNote = LocalizedText(
            en = "Standardized nonproprietary drug terminology reference",
            ar = "مرجع التسمية الدوائية العلمية الموحدة RxNorm"
        )
    )

    private val medlinePlusDrugs = ReferenceSource(
        id = "ref_medlineplus_drugs",
        title = "MedlinePlus — Drug Information & Mechanism Education",
        organization = "National Library of Medicine (NIH)",
        url = "https://medlineplus.gov/druginformation.html",
        licenseNote = LocalizedText(
            en = "Public domain patient & student pharmacology education",
            ar = "مصدر تعليمي عام للمعلومات الدوائية من MedlinePlus"
        )
    )

    val drugClasses: List<DrugClassRecord> = listOf(
        DrugClassRecord(
            id = "beta_blockers",
            englishName = "Cardioselective Beta-1 Adrenergic Antagonists",
            arabicName = "حاصرات مستقبلات بيتا-1 القلبية الانتقائية",
            prototypeGenericEn = "Metoprolol (prototype example)",
            prototypeGenericAr = "ميتوبرولول (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Metoprolol (6918)",
            systemId = "cardiovascular",
            linkedPhysiologySystem = PhysiologySystemId.CARDIOVASCULAR,
            linkedDiseaseIds = listOf("coronary_atherosclerosis", "aortic_stenosis"),
            visualType = ReceptorVisualType.BETA1_ANTAGONIST,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Cardiac Beta-1 (β₁) G-protein-coupled adrenergic receptors on SA node, AV node, and ventricular myocardium",
                ar = "مستقبلات بيتا-1 الأدرينالية (β₁) على العقدة الجيبية والأذينية البطينية وعضلة القلب"
            ),
            mechanismOfAction = LocalizedText(
                en = "Competitively blocks endogenous epinephrine and norepinephrine binding at cardiac β₁ receptors, attenuating Gs-protein cAMP signaling.",
                ar = "تحجب تنافسياً ارتباط الأدرينالين والنورأدرينالين بمستقبلات β₁ القلبية، مما يخفض إشارات cAMP داخل الخلية."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Antagonist occupies β₁ receptor binding pocket without activating Gs-adenylyl cyclase.", "1. يشغل الحاصر موقع الارتباط على مستقبل β₁ دون تنشيط مسار cAMP."),
                LocalizedText("2. Reduced intracellular cAMP lowers SA nodal pacemaker slope and AV nodal conduction velocity.", "2. يخفض تراجع cAMP وتيرة العقدة الجيبية ويبطئ التوصيل عبر العقدة الأذينية البطينية."),
                LocalizedText("3. Myocardial oxygen demand decreases through moderated heart rate and contractility.", "3. ينخفض استهلاك عضلة القلب للأكسجين بتنظيم معدل النبض وقوة الانقباض.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Negative chronotropy (lower heart rate) and negative inotropy (moderated contractility) — reducing cardiac workload.",
                ar = "تأثير مخفّض لمعدل ضربات القلب ومعتدل لقوة الانقباض، مما يقلل العبء الأكسجيني على القلب."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Avoid abrupt discontinuation; caution in severe bradycardia, high-grade AV block, decompensated heart failure, or reactive bronchospasm. Never self-prescribe or adjust doses without a clinician.",
                ar = "نظرة تعليمية للسلامة: يُحذر من الإيقاف المفاجئ؛ ويُراعى الحذر في بطء القلب الشديد وإحصار القلب العالي والربو التشنجي. لا يُستخدم للوصف أو تعديل الجرعات الذاتية."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        ),
        DrugClassRecord(
            id = "ace_inhibitors",
            englishName = "ACE Inhibitors (Angiotensin-Converting Enzyme Inhibitors)",
            arabicName = "مثبطات الإنزيم المحول للأنجيوتنسين (ACE)",
            prototypeGenericEn = "Lisinopril (prototype example)",
            prototypeGenericAr = "ليزينوبريل (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Lisinopril (29046)",
            systemId = "cardiovascular",
            linkedPhysiologySystem = PhysiologySystemId.RENAL,
            linkedDiseaseIds = listOf("glomerular_injury", "coronary_atherosclerosis", "aortic_stenosis"),
            visualType = ReceptorVisualType.ACE_INHIBITION,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Angiotensin-Converting Enzyme (ACE) on pulmonary and systemic vascular endothelium",
                ar = "الإنزيم المحول للأنجيوتنسين (ACE) على البطانة الوعائية الرئوية والجهازية"
            ),
            mechanismOfAction = LocalizedText(
                en = "Inhibits enzymatic conversion of Angiotensin I to the potent vasoconstrictor Angiotensin II and slows bradykinin breakdown, lowering systemic vascular resistance and efferent glomerular arteriolar tone.",
                ar = "يثبط تحويل الأنجيوتنسين الأول إلى الثاني القابض للأوعية ويبطئ تكسير البراديكينين، مما يخفض المقاومة الوعائية والضغط داخل الكبيبة الكلوية."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Drug binds the catalytic zinc active site of Angiotensin-Converting Enzyme.", "1. يرتبط الدواء بالموقع التحفيزي للإنزيم المحول للأنجيوتنسين."),
                LocalizedText("2. Circulating Angiotensin II and aldosterone secretion decrease while bradykinin rises.", "2. ينخفض الأنجيوتنسين الثاني والألدوستيرون بينما يرتفع البراديكينين الموسع للأوعية."),
                LocalizedText("3. Arteriolar vasodilation reduces systemic afterload and glomerular efferent pressure.", "3. يخفض توسع الشرينات الحمل التلوي الجهازي والضغط الترشيحي الكبيبي.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Balanced arterial/venous vasodilation, reduced sodium retention, and lowered intraglomerular filtration pressure.",
                ar = "توسيع وعائي متوازن، وتقليل احتباس الصوديوم، وخفض الضغط داخل الكبيبات الكلوية."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Contraindicated in pregnancy (fetal toxicity warning) and history of angioedema; monitor serum potassium and renal function clinically; dry cough may occur due to bradykinin accumulation.",
                ar = "نظرة تعليمية للسلامة: يمنع استخدامه أثناء الحمل (تحذير سمية جنينية) أو مع سوابق وذمة وعائية؛ يتطلب مراقبة البوتاسيوم ووظائف الكلى سريرياً؛ وقد يسبب سعالاً جافاً."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        ),
        DrugClassRecord(
            id = "saba_bronchodilators",
            englishName = "Short-Acting Beta-2 Adrenergic Agonists (SABA)",
            arabicName = "ناهضات مستقبلات بيتا-2 قصيرة المفعول (موسعات القصبات)",
            prototypeGenericEn = "Albuterol / Salbutamol (prototype example)",
            prototypeGenericAr = "ألبوتيرول / سالبوتامول (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Albuterol (435)",
            systemId = "respiratory",
            linkedPhysiologySystem = PhysiologySystemId.RESPIRATORY,
            linkedDiseaseIds = listOf("bronchial_asthma"),
            visualType = ReceptorVisualType.BETA2_AGONIST,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Bronchial smooth-muscle Beta-2 (β₂) adrenergic receptors",
                ar = "مستقبلات بيتا-2 الأدرينالية (β₂) في العضلات الملساء للشعب الهوائية"
            ),
            mechanismOfAction = LocalizedText(
                en = "Activates β₂ Gs-coupled receptors on airway smooth muscle, elevating intracellular cAMP to inhibit myosin light-chain kinase and rapidly relax constricted bronchioles.",
                ar = "ينشط مستقبلات β₂ في العضلات الملساء للقصبات، مما يرفع cAMP داخل الخلية ويرخي التشنج القصبي سريعاً."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Inhaled β₂ agonist binds directly to bronchial smooth-muscle β₂ receptors.", "1. يرتبط الناهض المستنشق مباشرة بمستقبلات β₂ على العضلات الملساء القصبية."),
                LocalizedText("2. Adenylyl cyclase elevates cAMP and activates Protein Kinase A (PKA).", "2. يرفع إنزيم الأدينيليل سيكلاز مستوى cAMP وينشط كيناز البروتين A."),
                LocalizedText("3. Smooth muscle relaxes, widening airway lumen and lowering airflow resistance.", "3. ترتخي العضلات الملساء فتتسع المسالك الهوائية وتنخفض مقاومة تدفق الهواء.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Rapid-onset bronchodilation improving expiratory airflow during acute reversible bronchospasm.",
                ar = "توسيع سريع للشعب الهوائية يحسن تدفق الهواء الزفيري في حالات التشنج القصبي القابل للعكس."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Potential side effects include tremor, palpitations, mild tachycardia, and transient potassium shifts. Frequent rescue reliance warrants physician review of underlying airway inflammation.",
                ar = "نظرة تعليمية للسلامة: قد يسبب رجفة خفيفة أو خفقاناً أو تسرعاً عابراً بالنبض؛ الاعتماد المتكرر عليه يستدعي مراجعة الطبيب لتقييم التهاب المسالك."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        ),
        DrugClassRecord(
            id = "loop_diuretics",
            englishName = "Loop Diuretics (NKCC2 Co-transporter Inhibitors)",
            arabicName = "مدرات العروة (مثبطات الناقل NKCC2)",
            prototypeGenericEn = "Furosemide (prototype example)",
            prototypeGenericAr = "فوروسيميد (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Furosemide (4603)",
            systemId = "urinary",
            linkedPhysiologySystem = PhysiologySystemId.RENAL,
            linkedDiseaseIds = listOf("glomerular_injury"),
            visualType = ReceptorVisualType.NKCC2_BLOCKER,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Apical Na⁺-K⁺-2Cl⁻ (NKCC2) co-transporter in the Thick Ascending Limb of the Loop of Henle",
                ar = "الناقل المشترك Na⁺-K⁺-2Cl⁻ (NKCC2) في الطرف الصاعد السميك لالتواء هنلي"
            ),
            mechanismOfAction = LocalizedText(
                en = "Reversibly blocks NKCC2 in the thick ascending limb, inhibiting sodium and chloride reabsorption and diminishing the medullary osmotic gradient, increasing water and electrolyte excretion.",
                ar = "يثبط عكسياً ناقل NKCC2 في الطرف الصاعد السميك لالتواء هنلي، مما يقلل إعادة امتصاص الصوديوم والكلوريد ويزيد إطراح الماء والأملاح."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Drug enters tubular filtrate via proximal tubular organic acid secretion.", "1. يصل الدواء إلى لمعة الأنبوب عبر الإفراز النشط في الأنبوب القريب."),
                LocalizedText("2. Blocks luminal NKCC2 co-transporter in the Thick Ascending Limb of Loop of Henle.", "2. يحجب ناقل NKCC2 في الطرف الصاعد السميك لالتواء هنلي."),
                LocalizedText("3. Unreabsorbed NaCl retains water in the lumen, increasing urine output (natriuresis & diuresis).", "3. يحتفظ الصوديوم والكلوريد غير الممتصين بالماء داخل اللمعة فيزداد إدرار البول.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Potent natriuresis and diuresis reducing extracellular fluid volume overload.",
                ar = "إدرار قوي للصوديوم والماء يخفض فرط حجم السوائل خارج الخلوية."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Risk of volume depletion, hypotension, hypokalemia, hypomagnesemia, and ototoxicity at excessive doses; requires clinical electrolyte and renal monitoring.",
                ar = "نظرة تعليمية للسلامة: قد يسبب نقص الحجم وهبوط الضغط ونقص البوتاسيوم والمغنيسيوم وسمية سمعية عند الجرعات العالية؛ يتطلب مراقبة سريرية للأملاح ووظائف الكلى."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        ),
        DrugClassRecord(
            id = "proton_pump_inhibitors",
            englishName = "Proton Pump Inhibitors (PPIs)",
            arabicName = "مثبطات مضخة البروتون (PPIs)",
            prototypeGenericEn = "Omeprazole (prototype example)",
            prototypeGenericAr = "أوميبرازول (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Omeprazole (7646)",
            systemId = "digestive",
            linkedPhysiologySystem = PhysiologySystemId.DIGESTIVE,
            linkedDiseaseIds = listOf("peptic_ulcer_gerd"),
            visualType = ReceptorVisualType.PROTON_PUMP_BLOCKER,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Gastric parietal cell H⁺/K⁺-ATPase (proton pump) in secretory canaliculi",
                ar = "إنزيم H⁺/K⁺-ATPase (مضخة البروتون) في الخلايا الجدارية للمعدة"
            ),
            mechanismOfAction = LocalizedText(
                en = "Acid-activated prodrug that covalently and irreversibly inhibits parietal cell H⁺/K⁺-ATPase, blocking the final common step of gastric hydrochloric acid secretion.",
                ar = "دواء طليعي يتنشط في الوسط الحمضي ويرتبط تساهمياً بمضخة البروتون H⁺/K⁺-ATPase ليوقف الخطوة النهائية المشتركة لإفراز حمض المعدة."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Enteric-coated prodrug is absorbed in the small intestine and reaches gastric parietal cells.", "1. يُمتص الدواء المغلف معوياً في الأمعاء الدقيقة ويصل للخلايا الجدارية المعدية."),
                LocalizedText("2. Acidic canalicular pH converts it into an active sulfenamide intermediate.", "2. يحوله الوسط الحمضي إلى مركب نشط داخل القنيات الإفرازية."),
                LocalizedText("3. Covalently inhibits H⁺/K⁺-ATPase, raising gastric pH until new pump synthesis occurs.", "3. يثبط مضخة البروتون تساهمياً فيرتفع الأس الهيدروجيني للمعدة ويُتاح للبطانة الالتئام.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Profound, sustained suppression of basal and meal-stimulated gastric acid secretion.",
                ar = "تثبيط عميق ومستدام لإفراز الحمض المعدي القاعدي والمحفّز بالوجبات."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Long-term unsupervised use is associated with nutrient absorption concerns (e.g., magnesium, vitamin B12), enteric infection risk, and drug interactions (e.g., CYP2C19).",
                ar = "نظرة تعليمية للسلامة: يرتبط الاستخدام طويل الأمد دون إشراف باحتمال نقص المغنيسيوم وفيتامين B12 وزيادة خطر بعض الالتهابات المعوية والتداخلات الدوائية."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        ),
        DrugClassRecord(
            id = "statins",
            englishName = "HMG-CoA Reductase Inhibitors (Statins)",
            arabicName = "مثبطات إنزيم HMG-CoA ريدكتاز (الستاتينات)",
            prototypeGenericEn = "Atorvastatin (prototype example)",
            prototypeGenericAr = "أتورفاستاتين (مثال نموذجي)",
            rxNormReferenceConcept = "RxNorm Concept: Atorvastatin (83367)",
            systemId = "cardiovascular",
            linkedPhysiologySystem = PhysiologySystemId.CARDIOVASCULAR,
            linkedDiseaseIds = listOf("coronary_atherosclerosis"),
            visualType = ReceptorVisualType.HMG_COA_INHIBITOR,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            molecularTarget = LocalizedText(
                en = "Hepatic HMG-CoA Reductase (rate-limiting enzyme of intracellular cholesterol biosynthesis)",
                ar = "إنزيم HMG-CoA ريدكتاز الكبدي (الإنزيم المحدد لسرعة تصنيع الكوليسترول)"
            ),
            mechanismOfAction = LocalizedText(
                en = "Competitively inhibits hepatic HMG-CoA reductase, decreasing hepatocyte cholesterol pools and triggering SREBP-mediated upregulation of surface LDL receptors to clear circulating LDL cholesterol.",
                ar = "يثبط تنافسياً إنزيم HMG-CoA ريدكتاز في الكبد، فيحفز زيادة مستقبلات LDL على سطح خلايا الكبد لتسريع تنقية الدم من كوليسترول LDL."
            ),
            mechanismSteps = listOf(
                LocalizedText("1. Competitively blocks HMG-CoA conversion to mevalonate inside hepatocytes.", "1. يثبط تنافسياً تحول HMG-CoA إلى الميفالونات داخل خلايا الكبد."),
                LocalizedText("2. Lower intracellular sterols activate hepatocyte LDL-receptor transcription.", "2. يحفز انخفاض الكوليسترول الخلوي زيادة تصنيع مستقبلات LDL الكبدية."),
                LocalizedText("3. Increased hepatic receptor uptake reduces circulating plasma LDL particles.", "3. يخفض الالتقاط الكبدي المتزايد مستويات جزيئات LDL في مجرى الدم.")
            ),
            pharmacodynamicEffects = LocalizedText(
                en = "Substantial lowering of plasma LDL-cholesterol concentrations and stabilization of vascular endothelial environment.",
                ar = "خفض ملموس لتركيز كوليسترول LDL في البلازما ودعم استقرار البطانة الوعائية."
            ),
            safetyAndContraindications = LocalizedText(
                en = "Educational safety overview: Monitor for unexplained muscle pain/weakness (myopathy) and liver enzyme elevation; contraindicated in active decompensated liver disease and pregnancy.",
                ar = "نظرة تعليمية للسلامة: يُنصح بالانتباه لألم أو ضعف العضلات غير المفسر وارتفاع إنزيمات الكبد؛ ويُمنع في أمراض الكبد النشطة وأثناء الحمل."
            ),
            labelReferenceDate = "DailyMed Reference Verified 2026-11",
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            sources = listOf(dailyMedRef, rxNormRef, medlinePlusDrugs)
        )
    )

    fun findById(id: String): DrugClassRecord? = drugClasses.firstOrNull { it.id == id }
}
