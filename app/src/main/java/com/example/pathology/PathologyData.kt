package com.example.pathology

import com.example.core.i18n.LocalizedText
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ReferenceSource
import com.example.data.seed.MedicalReferencesSeed
import com.example.physiology.engine.PhysiologyReferencesSeed
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.ScientificValidityLevel

enum class PathologyVisualType {
    AORTIC_STENOSIS,
    CORONARY_ATHEROSCLEROSIS,
    BRONCHIAL_ASTHMA,
    GLOMERULAR_INJURY,
    PEPTIC_ULCER_GERD,
    DEMYELINATION_MS
}

data class DiseaseRecord(
    val id: String,
    val englishName: String,
    val arabicName: String,
    val icdCategory: String,
    val systemId: String,
    val linkedPhysiologySystem: PhysiologySystemId,
    val linkedAnatomyStructureId: String,
    val linkedDrugClassIds: List<String>,
    val visualType: PathologyVisualType,
    val validityLevel: ScientificValidityLevel,
    val overview: LocalizedText,
    val normalStateSummary: LocalizedText,
    val diseasedStateSummary: LocalizedText,
    val pathophysiologySteps: List<LocalizedText>,
    val educationalInvestigations: List<LocalizedText>,
    val keyClinicalConcepts: LocalizedText,
    val reviewStatus: MedicalReviewStatus,
    val lastReviewedDate: String,
    val sources: List<ReferenceSource>
)

object PathologySeed {

    private val nihNhlbiValves = ReferenceSource(
        id = "ref_nih_nhlbi_valves",
        title = "NIH NHLBI — Heart Valve Diseases (Aortic Stenosis)",
        organization = "National Heart, Lung, and Blood Institute (NIH)",
        url = "https://www.nhlbi.nih.gov/health/heart-valve-diseases",
        licenseNote = LocalizedText(
            en = "Public domain U.S. government health education resource",
            ar = "مصدر تعليمي صحي عام من المعاهد الوطنية الأمريكية للصحة"
        )
    )

    private val nihNhlbiCad = ReferenceSource(
        id = "ref_nih_nhlbi_cad",
        title = "NIH NHLBI — Coronary Heart Disease & Atherosclerosis",
        organization = "National Heart, Lung, and Blood Institute (NIH)",
        url = "https://www.nhlbi.nih.gov/health/coronary-heart-disease",
        licenseNote = LocalizedText(
            en = "Public domain U.S. government health education resource",
            ar = "مصدر تعليمي صحي عام من المعاهد الوطنية الأمريكية للصحة"
        )
    )

    private val nihNhlbiAsthma = ReferenceSource(
        id = "ref_nih_nhlbi_asthma",
        title = "NIH NHLBI — What Is Asthma? Airway Inflammation & Narrowing",
        organization = "National Heart, Lung, and Blood Institute (NIH)",
        url = "https://www.nhlbi.nih.gov/health/asthma",
        licenseNote = LocalizedText(
            en = "Public domain U.S. government health education resource",
            ar = "مصدر تعليمي صحي عام من المعاهد الوطنية الأمريكية للصحة"
        )
    )

    private val nihNindsMs = ReferenceSource(
        id = "ref_nih_ninds_ms",
        title = "NIH NINDS — Multiple Sclerosis & Demyelination Education",
        organization = "National Institute of Neurological Disorders and Stroke",
        url = "https://www.ninds.nih.gov/health-information/disorders/multiple-sclerosis",
        licenseNote = LocalizedText(
            en = "Public domain U.S. government educational resource",
            ar = "مصدر تعليمي عام من المعهد الوطني للاضطرابات العصبية"
        )
    )

    val diseases: List<DiseaseRecord> = listOf(
        DiseaseRecord(
            id = "aortic_stenosis",
            englishName = "Aortic Valve Stenosis",
            arabicName = "تضيق الصمام الأبهري",
            icdCategory = "Valvular Heart Disease",
            systemId = "cardiovascular",
            linkedPhysiologySystem = PhysiologySystemId.CARDIOVASCULAR,
            linkedAnatomyStructureId = "aorta",
            linkedDrugClassIds = listOf("beta_blockers", "ace_inhibitors"),
            visualType = PathologyVisualType.AORTIC_STENOSIS,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Progressive narrowing of the aortic valve opening that obstructs systolic blood flow from the Left Ventricle into the Aorta, increasing ventricular afterload.",
                ar = "تضيق تدريجي في فتحة الصمام الأبهري يعيق خروج الدم الانقباضي من البطين الأيسر إلى الشريان الأبهر، مما يزيد الحمل التلوي على البطين."
            ),
            normalStateSummary = LocalizedText(
                en = "Three thin, pliable semilunar cusps open wide during ventricular ejection, permitting smooth low-resistance flow into the ascending aorta.",
                ar = "تفتح الشرفات الهلالية الثلاث المرنة بشكل واسع أثناء القذف البطيني، مما يسمح بتدفق سلس منخفض المقاومة نحو الأبهر."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Calcified, stiffened valve leaflets create a narrowed orifice, elevating left ventricular systolic pressure and driving concentric myocardial hypertrophy over time.",
                ar = "تؤدي الشرفات المتكلسة والمتيبسة إلى تضيق الفوهة، مما يرفع الضغط الانقباضي للبطين الأيسر ويحفز تضخماً تعويضياً في عضلة البطين."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Mechanical stress and lipid-mediated inflammation trigger valve leaflet fibrosis and calcification.", "1. يحفز الإجهاد الميكانيكي والالتهاب تليف وتكلس شرفات الصمام الأبهري."),
                LocalizedText("2. Reduced aortic orifice area increases resistance to left ventricular ejection.", "2. يقلص تضيق الفوهة مساحة العبور ويزيد مقاومة قذف البطين الأيسر."),
                LocalizedText("3. Left ventricular myocardium thickens (concentric hypertrophy) to maintain systemic outflow.", "3. تتضخم عضلة البطين الأيسر تعويضياً للحفاظ على الضخ الجهازي.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Transthoracic Echocardiography (conceptual educational reference for valve motion & gradient)", "تخطيط صدى القلب (مرجع تعليمي لتصوير حركة الصمام وتدرج الضغط)"),
                LocalizedText("12-Lead Electrocardiogram (educational concept of left ventricular strain patterns)", "تخطيط كهربائية القلب (مفهوم تعليمي لعلامات إجهاد البطين الأيسر)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Educational overview only. Clinical evaluation and management of valvular disease require qualified cardiology assessment.",
                ar = "عرض تعليمي فقط. يتطلب التقييم السريري لأمراض الصمامات استشارة أخصائي أمراض القلب."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(nihNhlbiValves, PhysiologyReferencesSeed.ahaHeartCycle)
        ),
        DiseaseRecord(
            id = "coronary_atherosclerosis",
            englishName = "Coronary Artery Atherosclerosis",
            arabicName = "تصلب الشرايين التاجية",
            icdCategory = "Ischemic Heart Disease",
            systemId = "cardiovascular",
            linkedPhysiologySystem = PhysiologySystemId.CARDIOVASCULAR,
            linkedAnatomyStructureId = "heart",
            linkedDrugClassIds = listOf("statins", "beta_blockers", "ace_inhibitors"),
            visualType = PathologyVisualType.CORONARY_ATHEROSCLEROSIS,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Accumulation of lipid-rich atherosclerotic plaque inside the coronary arterial wall narrows the lumen and limits oxygenated blood delivery to working myocardium.",
                ar = "تراكم لويحات عصيدية غنية بالدهون داخل جدار الشرايين التاجية يضيّق لمعة الوعاء ويحد من وصول الدم المؤكسج لعضلة القلب."
            ),
            normalStateSummary = LocalizedText(
                en = "Smooth intact vascular endothelium maintains an unobstructed lumen capable of dilating to match increased myocardial oxygen demand.",
                ar = "تحافظ البطانة الوعائية السليمة على لمعة واسعة قادرة على التوسع لتلبية احتياج عضلة القلب من الأكسجين."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Subendothelial plaque encroaches on the arterial lumen; plaque rupture can trigger thrombosis and acute myocardial ischemia.",
                ar = "تتمدد اللويحة العصيدية داخل اللمعة مسببة تضيقاً؛ وقد يؤدي تمزقها إلى تشكل خثرة ونقص تروية قلبي حاد."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Endothelial dysfunction allows LDL lipoprotein infiltration and macrophage foam-cell formation.", "1. يسمح خلل البطانة بتسلل دهون LDL وتكوين الخلايا الرغوية البلعمية."),
                LocalizedText("2. Fibrous cap and lipid core progressively narrow the coronary lumen.", "2. يتشكل غطاء ليفي ولب دهني يضيّقان لمعة الشريان التاجي تدريجياً."),
                LocalizedText("3. Myocardial oxygen demand exceeds supply during exertion, producing ischemic imbalance.", "3. يتجاوز احتياج العضلة للأكسجين مقدار التروية المتاحة مما يسبب نقص التروية.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Resting & Stress ECG (educational overview of ST-segment physiology)", "تخطيط القلب أثناء الراحة والجهد (مفهوم تعليمي لتغيرات قطعة ST)"),
                LocalizedText("Lipid Profile & Cardiac Biomarkers (general educational laboratory concepts)", "ملف الدهون والمؤشرات القلبية (مفاهيم مخبرية تعليمية عامة)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Urgent safety note: Acute chest pressure, radiating arm/jaw pain, or sudden dyspnea requires immediate local emergency medical care.",
                ar = "تنبيه سلامة عاجل: ألم الصدر الضاغط الحاد أو ضيق التنفس المفاجئ يستدعي الرعاية الإسعافية الفورية."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(nihNhlbiCad, MedicalReferencesSeed.nihNhlbiHeart)
        ),
        DiseaseRecord(
            id = "bronchial_asthma",
            englishName = "Bronchial Asthma (Airway Narrowing)",
            arabicName = "الربو القصبي وتضيق المسالك الهوائية",
            icdCategory = "Obstructive Airway Disease",
            systemId = "respiratory",
            linkedPhysiologySystem = PhysiologySystemId.RESPIRATORY,
            linkedAnatomyStructureId = "heart",
            linkedDrugClassIds = listOf("saba_bronchodilators"),
            visualType = PathologyVisualType.BRONCHIAL_ASTHMA,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Chronic inflammatory airway disorder characterized by bronchial smooth-muscle constriction, mucosal edema, and excess mucus that reversibly impede airflow.",
                ar = "اضطراب التهابي مزمن في المسالك الهوائية يتميز بتشنج العضلات الملساء القصبية ووذمة الغشاء المخاطي وزيادة المخاط مما يعيق تدفق الهواء بشكل قابل للعكس."
            ),
            normalStateSummary = LocalizedText(
                en = "Relaxed bronchial smooth muscle and thin mucosal lining preserve wide, low-resistance air conduction to alveoli.",
                ar = "عضلات قصبية ملساء مرتخية وبطانة مخاطية رقيقة تحافظ على ممر هوائي واسع منخفض المقاومة نحو الحويصلات."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Tightened smooth muscle rings, inflamed swollen mucosa, and mucus plugs sharply reduce airway diameter—especially impeding expiration.",
                ar = "انقباض العضلات الملساء وتورم البطانة وتراكم المخاط تقلص قطر القصيبات بشدة، خاصة أثناء الزفير."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Environmental or immune triggers activate airway mast cells and inflammatory mediators.", "1. تنشّط المحفزات البيئية أو المناعية الخلايا البدينة والوسائط الالتهابية في الشعب الهوائية."),
                LocalizedText("2. Bronchial smooth muscle contracts (bronchospasm) while airway walls swell.", "2. تنقبض العضلات الملساء القصبية (تشنج قصبي) وتتورم جدران المسالك."),
                LocalizedText("3. Narrowed lumens increase airflow resistance and prolong expiration.", "3. يزيد تضيق اللمعة من مقاومة تدفق الهواء ويطيل زمن الزفير.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Spirometry (conceptual study of FEV1/FVC airflow obstruction ratios)", "قياس التنفس (دراسة مفاهيمية لنسبة FEV1/FVC في انسداد المسالك)"),
                LocalizedText("Peak Expiratory Flow monitoring (educational self-monitoring concept)", "قياس ذروة التدفق الزفيري (مفهوم تعليمي لمتابعة تدفق الهواء)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Educational overview of airway mechanics; individualized asthma action plans must come from a licensed clinician.",
                ar = "عرض تعليمي لميكانيكا المسالك الهوائية؛ الخطط العلاجية الفردية للربو يضعها الطبيب المختص."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(nihNhlbiAsthma, PhysiologyReferencesSeed.medlinePlusLungs)
        ),
        DiseaseRecord(
            id = "glomerular_injury",
            englishName = "Glomerular Filtration Barrier Injury",
            arabicName = "إصابة حاجز الترشيح الكبيبي",
            icdCategory = "Renal / Nephron Pathology",
            systemId = "urinary",
            linkedPhysiologySystem = PhysiologySystemId.RENAL,
            linkedAnatomyStructureId = "heart",
            linkedDrugClassIds = listOf("ace_inhibitors", "loop_diuretics"),
            visualType = PathologyVisualType.GLOMERULAR_INJURY,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Structural damage to the glomerular basement membrane and podocytes (e.g., from chronic hyperglycemia or hypertension) disrupts selective filtration, allowing plasma proteins to leak into Bowman's space.",
                ar = "تلف بنيوي في الغشاء القاعدي الكبيبي والخلايا الرجلاء (مثل تأثير ارتفاع السكر أو الضغط المزمن) يخل بانتقائية الترشيح ويسمح بتسرب بروتينات البلازما إلى محفظة بومان."
            ),
            normalStateSummary = LocalizedText(
                en = "Intact fenestrated endothelium, negatively charged basement membrane, and podocyte foot processes filter water and small solutes while retaining albumin.",
                ar = "يحفظ الغشاء القاعدي السليم والخلايا الرجلاء ترشيح الماء والأملاح الصغيرة مع منع عبور بروتين الألبومين."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Thickened basement membrane, effaced podocytes, and elevated intraglomerular pressure impair filtration selectivity and reduce functional nephron reserve.",
                ar = "يؤدي تسمك الغشاء القاعدي وتضرر الخلايا الرجلاء وارتفاع الضغط الكبيبي إلى فقدان الانتقائية وتراجع كفاءة النفرونات."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Sustained metabolic or hemodynamic stress elevates glomerular capillary pressure.", "1. يرفع الإجهاد الأيضي أو الوعائي المزمن الضغط داخل الشعيرات الكبيبية."),
                LocalizedText("2. Podocyte architecture and filtration slits lose size/charge selectivity.", "2. تفقد الخلايا الرجلاء وشقوق الترشيح انتقائيتها للحجم والشحنة الكهربائية."),
                LocalizedText("3. Albumin leaks into filtrate (albuminuria) while progressive sclerosis lowers nephron filtration surface.", "3. يتسرب الألبومين إلى الرشيح بينما يقلص التصلب التدريجي مساحة الترشيح.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Urine Albumin-to-Creatinine Ratio (conceptual marker of glomerular barrier integrity)", "نسبة الألبومين إلى الكرياتينين في البول (مؤشر تعليمي لسلامة حاجز الكبيبة)"),
                LocalizedText("Serum Creatinine & eGFR concept (educational overview of renal filtration estimation)", "كرياتينين المصل ومفهوم eGFR (نظرة تعليمية لتقدير وظيفة الترشيح)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Conceptual nephron pathology teaching module; never substitutes for laboratory interpretation by a nephrologist or physician.",
                ar = "وحدة تعليمية مفاهيمية لأمراض النفرون ولا تغني عن تفسير التحاليل من قبل الطبيب المختص."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(PhysiologyReferencesSeed.nihNiddkKidney, PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        ),
        DiseaseRecord(
            id = "peptic_ulcer_gerd",
            englishName = "Gastric Mucosal Barrier Disruption & Peptic Ulcer",
            arabicName = "اختلال الحاجز المخاطي المعدي والقرحة الهضمية",
            icdCategory = "Gastrointestinal Pathology",
            systemId = "digestive",
            linkedPhysiologySystem = PhysiologySystemId.DIGESTIVE,
            linkedAnatomyStructureId = "heart",
            linkedDrugClassIds = listOf("proton_pump_inhibitors"),
            visualType = PathologyVisualType.PEPTIC_ULCER_GERD,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Imbalance between aggressive luminal factors (gastric hydrochloric acid and pepsin) and mucosal protective defenses (mucus-bicarbonate layer, blood flow, prostaglandins) creates a focal mucosal defect.",
                ar = "اختلال التوازن بين العوامل المخرشة (حمض المعدة والبيبسين) ووسائل الحماية المخاطية (المخاط والبيكربونات والتروية والبروستاغلاندين) يؤدي إلى تآكل موضعي في بطانة المعدة أو الإثني عشر."
            ),
            normalStateSummary = LocalizedText(
                en = "Thick bicarbonate-rich mucus gel and rapid epithelial renewal protect underlying gastric tissue from acidic gastric juice.",
                ar = "تحمي طبقة مخاطية غنية بالبيكربونات وتجدد ظهاري سريع أنسجة المعدة من العصارة الحمضية."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Deficient mucus-bicarbonate defense (e.g., H. pylori colonization or NSAID prostaglandin inhibition) allows acid-pepsin erosion into deeper mucosal layers.",
                ar = "يسمح ضعف الحاجز المخاطي (مثل عدوى الملوية البوابية أو تثبيط البروستاغلاندين) بتآكل الحمض والبيبسين لطبقات أعمق."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Protective mucus-bicarbonate barrier and mucosal prostaglandin defenses weaken.", "1. يضعف الحاجز المخاطي الغني بالبيكربونات ودفاعات البروستاغلاندين."),
                LocalizedText("2. Hydrogen ions back-diffuse into epithelial cells alongside pepsin proteolytic activity.", "2. ترتد أيونات الهيدروجين نحو الخلايا الظهارية بالتزامن مع نشاط إنزيم البيبسين."),
                LocalizedText("3. Localized tissue inflammation and focal ulceration develop in the stomach or duodenum.", "3. يتطور التهاب موضعي وتقرح بؤري في بطانة المعدة أو الإثني عشر.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Upper GI Endoscopy (educational concept of direct mucosal visualization)", "التنظير الهضمي العلوي (مفهوم تعليمي للفحص المباشر للبطانة المخاطية)"),
                LocalizedText("Non-invasive H. pylori educational tests (urea breath / stool antigen concepts)", "فحوصات الملوية البوابية التعليمية (مفاهيم اختبار التنفس ومستضد البراز)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Educational gastroenterology overview; gastrointestinal bleeding warning signs require immediate medical evaluation.",
                ar = "عرض تعليمي لأمراض الجهاز الهضمي؛ علامات النزف الهضمي تستوجب تقييماً طبياً فورياً."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(PhysiologyReferencesSeed.nihNiddkDigestive)
        ),
        DiseaseRecord(
            id = "demyelination_ms",
            englishName = "Focal CNS Demyelination (Multiple Sclerosis Concept)",
            arabicName = "زوال الميالين المركزي (مفهوم التصلب المتعدد)",
            icdCategory = "Neurological Demyelinating Disorder",
            systemId = "nervous",
            linkedPhysiologySystem = PhysiologySystemId.NEURAL,
            linkedAnatomyStructureId = "heart",
            linkedDrugClassIds = emptyList(),
            visualType = PathologyVisualType.DEMYELINATION_MS,
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            overview = LocalizedText(
                en = "Immune-mediated damage to central nervous system myelin sheaths disrupts electrical insulation along myelinated axons, slowing or blocking saltatory action potential conduction.",
                ar = "تلف مناعي لغمد الميالين في الجهاز العصبي المركزي يُفقد المحاور العصبية عزلها الكهربائي، مما يبطئ أو يعيق التوصيل القفزي لجهد الفعل."
            ),
            normalStateSummary = LocalizedText(
                en = "Continuous myelin internodes insulate the axonal membrane so action potentials regenerate rapidly from one Node of Ranvier to the next.",
                ar = "تعزل أغمدة الميالين السليمة غشاء المحور فيقفز جهد الفعل بسرعة وكفاءة من عقدة رانفييه إلى أخرى."
            ),
            diseasedStateSummary = LocalizedText(
                en = "Patchy loss of myelin exposes underlying axonal membrane, causing current leakage, slowed impulse velocity, or conduction block.",
                ar = "يكشف فقدان الميالين البؤري غشاء المحور مما يسبب تسرب التيار الأيوني وتباطؤ أو انقطاع الإشارة العصبية."
            ),
            pathophysiologySteps = listOf(
                LocalizedText("1. Immune cells cross the blood-brain barrier and target oligodendrocyte myelin membranes.", "1. تعبر خلايا مناعية الحاجز الدموي الدماغي وتستهدف أغمدة الميالين."),
                LocalizedText("2. Loss of myelin insulation increases membrane capacitance and ionic current leak.", "2. يؤدي فقدان العزل المياليني إلى تسرب التيار الأيوني عبر الغشاء."),
                LocalizedText("3. Saltatory conduction slows or fails across demyelinated axonal segments.", "3. يتباطأ التوصيل القفزي أو يتوقف عبر المقاطع منزوعة الميالين.")
            ),
            educationalInvestigations = listOf(
                LocalizedText("Brain & Spinal Cord MRI (educational overview of white-matter demyelination imaging)", "الرنين المغناطيسي للدماغ والحبل الشوكي (مفهوم تعليمي لتصوير المادة البيضاء)"),
                LocalizedText("Evoked Potentials (conceptual study of neural conduction latency)", "الجهود المحرّضة (دراسة مفاهيمية لزمن التوصيل العصبي)")
            ),
            keyClinicalConcepts = LocalizedText(
                en = "Educational neurobiology module illustrating why myelin insulation is essential for rapid nerve signaling.",
                ar = "وحدة تعليمية في بيولوجيا الأعصاب توضح أهمية عزل الميالين لسرعة الإشارات العصبية."
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            sources = listOf(nihNindsMs, PhysiologyReferencesSeed.nihNindsBrain)
        )
    )

    fun findById(id: String): DiseaseRecord? = diseases.firstOrNull { it.id == id }
}
