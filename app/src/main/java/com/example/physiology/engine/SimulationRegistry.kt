package com.example.physiology.engine

import com.example.core.i18n.LocalizedText
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ReferenceSource
import com.example.data.seed.MedicalReferencesSeed

object PhysiologyReferencesSeed {
    val ncbiBookshelfPhysiology = ReferenceSource(
        id = "ref_ncbi_bookshelf",
        title = "StatPearls & Physiology Chapters — NCBI Bookshelf",
        organization = "National Center for Biotechnology Information (NCBI / NIH)",
        url = "https://www.ncbi.nlm.nih.gov/books/",
        licenseNote = LocalizedText(
            en = "Openly accessible biomedical reference repository; cited by link without copying text.",
            ar = "مستودع مراجع طبية حيوية متاح للعموم؛ موثق بالرابط دون نسخ نصوص."
        )
    )

    val ahaHeartCycle = ReferenceSource(
        id = "ref_aha_heart",
        title = "How the Healthy Heart Works — Chambers, Valves & Electrical System",
        organization = "American Heart Association (AHA)",
        url = "https://www.heart.org/en/health-topics/high-blood-pressure/the-facts-about-high-blood-pressure/how-the-healthy-heart-works",
        licenseNote = LocalizedText(
            en = "Educational cardiology overview referenced for cardiac cycle sequence and valve timing.",
            ar = "مرجع تعليمي لجمعية القلب الأمريكية لتسلسل الدورة القلبية وحالة الصمامات."
        )
    )

    val medlinePlusLungs = ReferenceSource(
        id = "ref_medline_lungs",
        title = "Lungs and Breathing — MedlinePlus Medical Encyclopedia",
        organization = "U.S. National Library of Medicine (NLM / NIH)",
        url = "https://medlineplus.gov/lungsandbreathing.html",
        licenseNote = LocalizedText(
            en = "NIH/NLM educational reference for pulmonary ventilation and gas exchange.",
            ar = "مرجع تعليمي من المكتبة الوطنية للطب حول التهوية الرئوية وتبادل الغازات."
        )
    )

    val nihNindsBrain = ReferenceSource(
        id = "ref_ninds_neuron",
        title = "Brain Basics: The Life and Death of a Neuron",
        organization = "National Institute of Neurological Disorders and Stroke (NINDS / NIH)",
        url = "https://www.ninds.nih.gov/health-information/public-education/brain-basics/brain-basics-life-and-death-neuron",
        licenseNote = LocalizedText(
            en = "Public NIH neuroscience educational reference on action potentials and synapses.",
            ar = "مرجع تعليمي عام من معهد الاضطرابات العصبية (NIH) حول جهد الفعل والمشابك العصبية."
        )
    )

    val nihNiddkKidney = ReferenceSource(
        id = "ref_niddk_kidney",
        title = "Your Kidneys & How They Work — Nephron Filtration",
        organization = "National Institute of Diabetes and Digestive and Kidney Diseases (NIDDK / NIH)",
        url = "https://www.niddk.nih.gov/health-information/kidney-disease/kidneys-how-they-work",
        licenseNote = LocalizedText(
            en = "Public NIH educational reference for glomerular filtration and tubular transport.",
            ar = "مرجع تعليمي من المعاهد الوطنية للصحة حول الترشيح الكبيبي والنقل الأنبوبي."
        )
    )

    val nihNiddkDigestive = ReferenceSource(
        id = "ref_niddk_digestive",
        title = "Your Digestive System & How It Works",
        organization = "National Institute of Diabetes and Digestive and Kidney Diseases (NIDDK / NIH)",
        url = "https://www.niddk.nih.gov/health-information/digestive-diseases/digestive-system-how-it-works",
        licenseNote = LocalizedText(
            en = "Public NIH educational overview of peristalsis, secretions, and nutrient absorption.",
            ar = "مرجع تعليمي من NIH حول الحركة الدودية والإفرازات وامتصاص المغذيات."
        )
    )
}

/**
 * Centralized Phase 03 Simulation Model Registry & Scenario Manager (Sections 15 & 23).
 * Centralizes all 5 organ-system models, 15 scenarios, validity levels, assumptions, and bilingual explanations.
 */
object SimulationModelRegistry {

    val models: Map<PhysiologySystemId, SimulationModelRecord> = mapOf(
        PhysiologySystemId.CARDIOVASCULAR to SimulationModelRecord(
            id = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Cardiac Cycle, Valve Mechanics & Circulation Lab",
            titleArabic = "مختبر الدورة القلبية والصمامات والدوران الدموي",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            description = LocalizedText(
                en = "Synchronizes the five classical phases of the cardiac cycle, four valve states (Tricuspid, Mitral, Pulmonary, Aortic), pulmonary/systemic circulation pathways, and schematic electrical conduction.",
                ar = "يزامن المراحل الخمس الكلاسيكية للدورة القلبية وحالة الصمامات الأربعة ومساري الدورتين الرئوية والجهازية مع التوصيل الكهربائي التخطيطي."
            ),
            parameterDefs = listOf(
                SimulationParameterDef(
                    id = "heart_rate_bpm",
                    nameEnglish = "Educational Heart Rate",
                    nameArabic = "معدل ضربات القلب التعليمي",
                    unit = "BPM",
                    minValue = 50f,
                    maxValue = 120f,
                    defaultValue = 75f,
                    step = 5f,
                    presets = listOf(60f, 75f, 90f, 100f),
                    description = LocalizedText(
                        en = "Illustrative adult resting heart-rate preset controlling exact cardiac cycle period (T = 60 / BPM). Strictly separate from playback viewing speed.",
                        ar = "قيمة تعليمية لمعدل النبض تتحكم في زمن الدورة القلبية (T = 60 / BPM)، وهي مستقلة تماماً عن سرعة عرض الحركة."
                    ),
                    modelDependency = LocalizedText(
                        en = "Directly sets cycle period (seconds per beat); does NOT fabricate stroke volume or blood pressure.",
                        ar = "يحدد زمن النبضة بالثواني مباشرة ولا يختلق قيماً غير موثقة لحجم الضربة أو ضغط الدم."
                    ),
                    validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL
                )
            ),
            phases = listOf(
                SimulationPhaseDef(
                    id = "atrial_systole",
                    name = LocalizedText("1. Atrial Systole", "1. انقباض الأذينين"),
                    startNormalized = 0.00f,
                    endNormalized = 0.12f,
                    summary = LocalizedText(
                        en = "SA node triggers atrial depolarization (P wave); both atria contract, completing final ventricular filling across open Tricuspid and Mitral valves.",
                        ar = "تُطلق العقدة الجيبية إزالة استقطاب الأذينين (موجة P) فينقبض الأذينان لإتمام امتلاء البطينين عبر الصمامين ثلاثي الشرفات والتاجي المفتوحين."
                    ),
                    highlightedStructures = listOf("right_atrium", "left_atrium", "tricuspid_valve", "mitral_valve")
                ),
                SimulationPhaseDef(
                    id = "isovolumetric_contraction",
                    name = LocalizedText("2. Isovolumetric Ventricular Contraction", "2. الانقباض البطيني متساوي الحجم"),
                    startNormalized = 0.12f,
                    endNormalized = 0.20f,
                    summary = LocalizedText(
                        en = "Ventricular depolarization (QRS complex) initiates ventricular contraction. AV valves snap shut (S1 sound) while semilunar valves remain closed briefly.",
                        ar = "يبدأ انقباض البطينين مع مركب QRS، فينغلق الصمامان الأذينيان البطينيان (الصوت القلبي الأول S1) بينما تظل الصمامات الهلالية مغلقة لحظياً."
                    ),
                    highlightedStructures = listOf("right_ventricle", "left_ventricle", "interventricular_septum")
                ),
                SimulationPhaseDef(
                    id = "ventricular_ejection",
                    name = LocalizedText("3. Ventricular Ejection", "3. القذف البطيني"),
                    startNormalized = 0.20f,
                    endNormalized = 0.48f,
                    summary = LocalizedText(
                        en = "Ventricular pressure exceeds arterial pressure, opening Pulmonary and Aortic valves to eject blood into the Pulmonary Artery and Aorta.",
                        ar = "يتجاوز الضغط البطيني الضغط الشرياني فينفتح الصمامان الرئوي والأبهري لقذف الدم نحو الشريان الرئوي والشريان الأبهر."
                    ),
                    highlightedStructures = listOf("left_ventricle", "aorta", "right_ventricle", "pulmonary_artery", "aortic_valve", "pulmonary_valve")
                ),
                SimulationPhaseDef(
                    id = "isovolumetric_relaxation",
                    name = LocalizedText("4. Isovolumetric Ventricular Relaxation", "4. الانبساط البطيني متساوي الحجم"),
                    startNormalized = 0.48f,
                    endNormalized = 0.58f,
                    summary = LocalizedText(
                        en = "Following repolarization (T wave), ventricles relax and Pulmonary/Aortic valves close (S2 sound) preventing backflow; all four valves are briefly closed.",
                        ar = "عقب إعادة الاستقطاب (موجة T) يرتخي البطينان فينغلق الصمامان الأبهري والرئوي (الصوت القلبي الثاني S2) لمنع الارتجاع، وتغلق الصمامات الأربعة لحظياً."
                    ),
                    highlightedStructures = listOf("aortic_valve", "pulmonary_valve", "left_ventricle", "right_ventricle")
                ),
                SimulationPhaseDef(
                    id = "ventricular_filling",
                    name = LocalizedText("5. Passive Ventricular Filling (Diastole)", "5. الامتلاء البطيني السلبي (الانبساط)"),
                    startNormalized = 0.58f,
                    endNormalized = 1.00f,
                    summary = LocalizedText(
                        en = "Tricuspid and Mitral valves reopen as ventricular pressure falls below atrial pressure, allowing venous return from Venae Cavae and Pulmonary Veins to refill both ventricles.",
                        ar = "يُعاد فتح الصمامين ثلاثي الشرفات والتاجي مع انخفاض الضغط البطيني، ليتدفق الدم العائد من الوريدين الأجوفين والأوردة الرئوية لإعادة ملء البطينين."
                    ),
                    highlightedStructures = listOf("vena_cava", "pulmonary_veins", "right_atrium", "left_atrium", "tricuspid_valve", "mitral_valve")
                )
            ),
            equationNotes = LocalizedText(
                en = "Exact cycle period: T_cycle (s) = 60 / HR (BPM). Schematic ECG waveform and phase fractions are educational approximations, not patient recordings.",
                ar = "زمن الدورة الدقيق: T_cycle = 60 / HR بالثانية. مخطط ECG التخطيطي ونسب المراحل هي تقريبات تعليمية وليست تسجيلات مريض."
            ),
            assumptions = listOf(
                LocalizedText("Proportional phase fractions represent an idealized adult resting cycle.", "تمثل نسب المراحل دورة قلبية مثالية للبالغين في حالة الراحة."),
                LocalizedText("Blood is colored red/blue by educational convention; deoxygenated blood is dark crimson, not literally blue.", "يُستخدم اللونان الأحمر والأزرق اصطلاحياً للتعليم؛ فالدم غير المؤكسج أحمر داكن وليس أزرق فعلياً.")
            ),
            limitations = listOf(
                LocalizedText("Does not model diastolic shortening curves, stroke volume, ejection fraction, or blood pressure.", "لا يحسب حجم الضربة أو الكسر القذفي أو ضغط الدم السريري."),
                LocalizedText("Schematic ECG is purely illustrative and must never be used for clinical interpretation.", "مخطط ECG توضيحي فقط ولا يُستخدم للتفسير أو التشخيص السريري.")
            ),
            assetProvenanceNote = LocalizedText(
                en = "Reuses Phase 01 Cardiovascular 3D Schematic Geometry augmented with schematic valve indicators and guided vascular flow splines (Level B/C).",
                ar = "يعيد استخدام البنية التخطيطية ثلاثية الأبعاد للقلب من المرحلة 01 مع مؤشرات الصمامات ومسارات التدفق الموجهة."
            ),
            sources = listOf(
                PhysiologyReferencesSeed.ahaHeartCycle,
                MedicalReferencesSeed.nihNhlbiHeart,
                PhysiologyReferencesSeed.ncbiBookshelfPhysiology
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            version = "3.0.0"
        ),

        PhysiologySystemId.RESPIRATORY to SimulationModelRecord(
            id = "model_respiratory_mechanics_v1",
            systemId = PhysiologySystemId.RESPIRATORY,
            titleEnglish = "Respiratory Mechanics & Alveolar Gas Exchange Lab",
            titleArabic = "مختبر ميكانيكا التنفس وتبادل الغازات السنخي",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            description = LocalizedText(
                en = "Demonstrates thoracic cavity expansion, diaphragm contraction/relaxation, directional airflow across trachea and bronchi, and alveolar-capillary O₂/CO₂ diffusion gradients.",
                ar = "يوضح تمدد القفص الصدري وحركة الحجاب الحاجز واتجاه تدفق الهواء عبر القصبة والشعب الهوائية، إضافة إلى تبادل O₂ و CO₂ عبر الغشاء السنخي الشعيري."
            ),
            parameterDefs = listOf(
                SimulationParameterDef(
                    id = "breathing_rate_bpm",
                    nameEnglish = "Educational Breathing Rate",
                    nameArabic = "معدل التنفس التعليمي",
                    unit = "breaths/min",
                    minValue = 10f,
                    maxValue = 28f,
                    defaultValue = 14f,
                    step = 2f,
                    presets = listOf(12f, 14f, 18f, 24f),
                    description = LocalizedText(
                        en = "Educational respiratory rate setting exact breath cycle duration (T = 60 / Rate). Adult quiet breathing reference is typically 12–20 breaths/min.",
                        ar = "معدل التنفس التعليمي الذي يحدد مدة دورة التنفس (T = 60 / المعدل). المعدل الهادئ المرجعي للبالغين 12–20 نفس/دقيقة."
                    ),
                    modelDependency = LocalizedText(
                        en = "Controls inspiration/expiration timing period; does NOT fabricate tidal volume or SpO₂ values.",
                        ar = "يتحكم في توقيت الشهيق والزفير فقط دون اختلاق قيم حجم مدي أو تشبع أكسجين."
                    ),
                    validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL
                )
            ),
            phases = listOf(
                SimulationPhaseDef(
                    id = "inspiration",
                    name = LocalizedText("1. Inspiration (Active)", "1. الشهيق (عملية نشطة)"),
                    startNormalized = 0.00f,
                    endNormalized = 0.42f,
                    summary = LocalizedText(
                        en = "Diaphragm contracts and flattens downward while external intercostals elevate the rib cage, increasing thoracic volume and drawing air into the lungs.",
                        ar = "ينقبض الحجاب الحاجز ويهبط للأسفل مع ارتفاع الأضلاع، مما يزيد حجم التجويف الصدري ويسحب الهواء عبر القصبة نحو الحويصلات."
                    ),
                    highlightedStructures = listOf("diaphragm", "trachea", "right_lung", "left_lung")
                ),
                SimulationPhaseDef(
                    id = "end_inspiratory_exchange",
                    name = LocalizedText("2. Alveolar Gas Exchange Peak", "2. ذروة تبادل الغازات السنخي"),
                    startNormalized = 0.42f,
                    endNormalized = 0.54f,
                    summary = LocalizedText(
                        en = "Fresh alveolar gas maximizes partial pressure gradients: O₂ diffuses across the thin alveolar-capillary membrane into erythrocytes while CO₂ diffuses outward.",
                        ar = "يعزز الهواء السنخي المتجدد تدرج الضغوط الجزئية؛ فينتشر الأكسجين نحو الشعيرات وكريات الدم الحمراء بينما ينتشر ثاني أكسيد الكربون للسنخ."
                    ),
                    highlightedStructures = listOf("alveolar_region", "pulmonary_capillary")
                ),
                SimulationPhaseDef(
                    id = "expiration",
                    name = LocalizedText("3. Expiration (Passive Elastic Recoil)", "3. الزفير (ارتداد مرن سلبي)"),
                    startNormalized = 0.54f,
                    endNormalized = 1.00f,
                    summary = LocalizedText(
                        en = "Diaphragm relaxes upward into its dome shape and elastic lung tissue recoils, decreasing thoracic volume and expelling CO₂-rich air.",
                        ar = "يرتخي الحجاب الحاجز صعوداً لشكله القبي وترتد الرئتان بمرونتهما، مما يقلص حجم الصدر ويدفع الهواء المحمل بثاني أكسيد الكربون للخارج."
                    ),
                    highlightedStructures = listOf("diaphragm", "bronchial_tree", "trachea")
                )
            ),
            equationNotes = LocalizedText(
                en = "Breath period: T_breath (s) = 60 / Rate (breaths/min). Ventilation wave shows relative schematic excursion, not calibrated liters or cmH₂O.",
                ar = "زمن دورة التنفس: T_breath = 60 / المعدل بالثانية. الموجة تمثل حركة تخطيطية نسبية وليست مقاسة باللتر أو ضغط السنتيمتر المائي."
            ),
            assumptions = listOf(
                LocalizedText("Quiet tidal inspiration occupies ~42% of the cycle and passive expiration ~46% (illustrative I:E ratio).", "يشغل الشهيق نحو 42% والزفير السلبي نحو 46% في نموذج تعليمي مبسط."),
                LocalizedText("Gas exchange depends on ventilation-perfusion matching, membrane thickness, surface area, and partial pressure gradients.", "يعتمد تبادل الغازات على تدرج الضغوط الجزئية وسمك الغشاء ومساحة السطح وتوافق التهوية والتروية.")
            ),
            limitations = listOf(
                LocalizedText("Does not compute quantitative tidal volume, minute ventilation, arterial PaO₂, or SpO₂.", "لا يحسب الحجم المدي الكمي أو تشبع الأكسجين الشرياني."),
                LocalizedText("3D thoracic-lung representation is a labeled educational schematic until verified NIH 3D lung rigs are imported.", "الرسم الرئوي تخطيطي تعليمي موضح لحين دمج نماذج NIH 3D رئوية مجهزة بالحركة.")
            ),
            assetProvenanceNote = LocalizedText(
                en = "Schematic educational 3D respiratory assembly & alveolar-capillary interface diagram (Level B/C).",
                ar = "تجميع تنفسي تخطيطي ثلاثي الأبعاد ومخطط قريب للغشاء السنخي الشعيري."
            ),
            sources = listOf(
                PhysiologyReferencesSeed.medlinePlusLungs,
                PhysiologyReferencesSeed.ncbiBookshelfPhysiology
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            version = "3.0.0"
        ),

        PhysiologySystemId.NEURAL to SimulationModelRecord(
            id = "model_neural_transmission_v1",
            systemId = PhysiologySystemId.NEURAL,
            titleEnglish = "Neural Action Potential & Synaptic Transmission Lab",
            titleArabic = "مختبر جهد الفعل العصبي والانتقال المشبكي",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            description = LocalizedText(
                en = "Illustrates how dendritic stimuli trigger localized ionic depolarization along a myelinated axon (Nodes of Ranvier) leading to chemical synaptic transmission.",
                ar = "يوضح كيف تؤدي التنبيهات الشجيرية إلى إزالة استقطاب أيونية متتابعة على طول المحور المغمد بالميالين (عقد رانفييه) وصولاً للانتقال المشبكي الكيميائي."
            ),
            parameterDefs = listOf(
                SimulationParameterDef(
                    id = "neural_cycle_rate",
                    nameEnglish = "Educational Sequence Tempo",
                    nameArabic = "إيقاع التسلسل التعليمي",
                    unit = "cycles/min",
                    minValue = 8f,
                    maxValue = 30f,
                    defaultValue = 15f,
                    step = 2f,
                    presets = listOf(10f, 15f, 22f),
                    description = LocalizedText(
                        en = "Controls how frequently the slowed-down educational signal demonstration repeats (real action potentials last ~1–2 milliseconds).",
                        ar = "يتحكم في تكرار العرض التعليمي المبطّأ للتسلسل العصبي (علماً أن جهد الفعل الحقيقي يستغرق 1–2 مللي ثانية فقط)."
                    ),
                    modelDependency = LocalizedText(
                        en = "Time-dilated illustrative teaching visualization; not a real-time millisecond Hodgkin-Huxley solver.",
                        ar = "عرض تعليمي مبطّأ زمنياً للتوضيح البصري، وليس محاكياً فورياً لمعادلات هودجكن-هكسلي."
                    ),
                    validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION
                )
            ),
            phases = listOf(
                SimulationPhaseDef(
                    id = "stimulus_excitation",
                    name = LocalizedText("1. Dendritic Stimulus & Threshold", "1. التنبيه الشجيري وعتبة الإثارة"),
                    startNormalized = 0.00f,
                    endNormalized = 0.20f,
                    summary = LocalizedText(
                        en = "Incoming synaptic inputs summate at dendrites and soma; reaching threshold at the axon hillock opens voltage-gated Na⁺ channels.",
                        ar = "تتجمع الإشارات الواردة عند الزوائد الشجيرية وجسم الخلية؛ وعند بلوغ العتبة عند هضبة المحور تنفتح قنوات الصوديوم المبوبة بالفولطية."
                    ),
                    highlightedStructures = listOf("dendrites", "cell_body")
                ),
                SimulationPhaseDef(
                    id = "action_potential_propagation",
                    name = LocalizedText("2. Action Potential Along Nodes of Ranvier", "2. انتشار جهد الفعل عبر عقد رانفييه"),
                    startNormalized = 0.20f,
                    endNormalized = 0.62f,
                    summary = LocalizedText(
                        en = "Rapid local Na⁺ influx and K⁺ efflux regenerate membrane depolarization at each Node of Ranvier between insulating myelin sheaths (saltatory conduction—not fluid electricity in a pipe).",
                        ar = "يحدث تدفق أيوني موضعي للصوديوم ثم البوتاسيوم عند كل عقدة رانفييه بين أغمدة الميالين العازلة (توصيل قافز عبر تغير جهد الغشاء وليس تياراً سائلاً)."
                    ),
                    highlightedStructures = listOf("axon", "myelin_sheath", "nodes_of_ranvier")
                ),
                SimulationPhaseDef(
                    id = "axon_terminal_activation",
                    name = LocalizedText("3. Axon Terminal & Ca²⁺ Influx", "3. تنشيط النهاية المحورية ودخول الكالسيوم"),
                    startNormalized = 0.62f,
                    endNormalized = 0.78f,
                    summary = LocalizedText(
                        en = "Depolarization reaches the presynaptic axon terminal, opening voltage-gated Ca²⁺ channels that cue synaptic vesicles to dock with the membrane.",
                        ar = "تصل إزالة الاستقطاب للنهاية المحورية فتفتح قنوات الكالسيوم المبوبة بالفولطية لتحفيز التحام الحويصلات المشبكية بالغشاء."
                    ),
                    highlightedStructures = listOf("axon_terminal")
                ),
                SimulationPhaseDef(
                    id = "synaptic_transmission",
                    name = LocalizedText("4. Synaptic Transmission & Postsynaptic Response", "4. الانتقال المشبكي والاستجابة بعد المشبكية"),
                    startNormalized = 0.78f,
                    endNormalized = 1.00f,
                    summary = LocalizedText(
                        en = "Neurotransmitters diffuse across the synaptic cleft and bind postsynaptic receptors, altering postsynaptic membrane potential before clearance/reuptake.",
                        ar = "تنتشر النواقل العصبية عبر الشق المشبكي وترتبط بالمستقبلات بعد المشبكية محدثةً استجابة كهربائية جديدة قبل إزالتها أو إعادة امتصاصها."
                    ),
                    highlightedStructures = listOf("synapse")
                )
            ),
            equationNotes = LocalizedText(
                en = "Schematic time-dilated sequence. The displayed membrane potential curve is a conceptual teaching graphic (Level C), not computed from conductance differential equations.",
                ar = "تسلسل تخطيطي مبطّأ زمنياً. منحنى جهد الغشاء رسم تعليمي تخطيطي (المستوى C) وليس محسوباً من معادلات تفاضلية للأيونات."
            ),
            assumptions = listOf(
                LocalizedText("Represents a prototypical myelinated multipolar neuron for educational clarity.", "يمثل خلية عصبية نموذجية مغمدة بالميالين لتوضيح المبادئ الأساسية."),
                LocalizedText("Clarifies that action potentials are localized transmembrane ion-gradient changes, not literal electrical fluid flow.", "يؤكد أن جهد الفعل تغير أيوني موضعي عبر الغشاء وليس تدفقاً لسائل كهربائي.")
            ),
            limitations = listOf(
                LocalizedText("Time scale is dramatically slowed down so learners can follow each biological stage.", "المقياس الزمني مبطّأ بشكل كبير لتمكين المتعلم من متابعة كل مرحلة."),
                LocalizedText("Does not simulate individual ion-channel kinetics, refractory periods, or neurological disorders.", "لا يحاكي حركية القنوات الأيونية الفردية أو الأمراض العصبية.")
            ),
            assetProvenanceNote = LocalizedText(
                en = "Original interactive schematic 3D neuron and synaptic cleft educational view (Level C).",
                ar = "نموذج تخطيطي ثلاثي الأبعاد للخلية العصبية ومشهد مكبر للمشبك العصبي (المستوى C)."
            ),
            sources = listOf(
                PhysiologyReferencesSeed.nihNindsBrain,
                PhysiologyReferencesSeed.ncbiBookshelfPhysiology
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            version = "3.0.0"
        ),

        PhysiologySystemId.RENAL to SimulationModelRecord(
            id = "model_renal_nephron_v1",
            systemId = PhysiologySystemId.RENAL,
            titleEnglish = "Renal Filtration & Nephron Function Lab",
            titleArabic = "مختبر الترشيح الكلوي ووظيفة النفرون",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            description = LocalizedText(
                en = "Guides learners through the functional nephron unit: Renal Artery inflow, Glomerular Filtration in Bowman's Capsule, Tubular Reabsorption, Tubular Secretion, and Collecting Duct urine pathway.",
                ar = "يرشد المتعلم عبر وحدة النفرون الوظيفية: تدفق الشريان الكلوي، الترشيح الكبيبي في محفظة بومان، إعادة الامتصاص الأنبوبي، الإفراز الأنبوبي، والقناة الجامعة."
            ),
            parameterDefs = listOf(
                SimulationParameterDef(
                    id = "nephron_flow_tempo",
                    nameEnglish = "Educational Process Tempo",
                    nameArabic = "إيقاع العرض التعليمي للنفرون",
                    unit = "cycles/min",
                    minValue = 8f,
                    maxValue = 24f,
                    defaultValue = 12f,
                    step = 2f,
                    presets = listOf(8f, 12f, 18f),
                    description = LocalizedText(
                        en = "Adjusts the illustrative demonstration cycle speed along the nephron; visual markers are conceptual and do NOT represent measured GFR.",
                        ar = "يضبط سرعة العرض التوضيحي عبر النفرون؛ المؤشرات البصرية مفاهيمية ولا تمثل معدل ترشيح كبيبي مقاساً."
                    ),
                    modelDependency = LocalizedText(
                        en = "Illustrative sequence pacing only; never computes patient-specific eGFR.",
                        ar = "توقيت تعليمي فقط ولا يحسب eGFR أو توقعات كلوية سريرية."
                    ),
                    validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION
                )
            ),
            phases = listOf(
                SimulationPhaseDef(
                    id = "glomerular_filtration",
                    name = LocalizedText("1. Glomerular Filtration", "1. الترشيح الكبيبي"),
                    startNormalized = 0.00f,
                    endNormalized = 0.25f,
                    summary = LocalizedText(
                        en = "Blood enters the glomerulus from the renal artery; hydrostatic pressure drives water and small solutes across the filtration barrier into Bowman's capsule while retaining blood cells and large proteins.",
                        ar = "يدخل الدم الكبيبة من الشريان الكلوي، فيدفع الضغط الهيدروستاتيكي الماء والذائبات الصغيرة إلى محفظة بومان مع إبقاء خلايا الدم والبروتينات الكبيرة."
                    ),
                    highlightedStructures = listOf("renal_artery", "glomerulus", "bowmans_capsule")
                ),
                SimulationPhaseDef(
                    id = "tubular_reabsorption",
                    name = LocalizedText("2. Tubular Reabsorption", "2. إعادة الامتصاص الأنبوبي"),
                    startNormalized = 0.25f,
                    endNormalized = 0.60f,
                    summary = LocalizedText(
                        en = "Along the Proximal Tubule and Loop of Henle, essential water, glucose, amino acids, and electrolytes are reclaimed from filtrate back into peritubular capillaries and the Renal Vein.",
                        ar = "على طول الأنبوب القريب والتواء هنلي تُعاد معظم المياه والجلوكوز والأحماض الأمينية والأملاح الأساسية من الرشيح إلى الشعيرات حول الأنبوبية والوريد الكلوي."
                    ),
                    highlightedStructures = listOf("proximal_tubule", "loop_of_henle", "renal_vein")
                ),
                SimulationPhaseDef(
                    id = "tubular_secretion",
                    name = LocalizedText("3. Tubular Secretion", "3. الإفراز الأنبوبي"),
                    startNormalized = 0.60f,
                    endNormalized = 0.80f,
                    summary = LocalizedText(
                        en = "Selected ions (such as H⁺ and K⁺) and metabolic wastes are actively transported from peritubular capillaries into the Distal Tubule lumen to help maintain acid-base balance.",
                        ar = "تُفرز أيونات مختارة (مثل الهيدروجين والبوتاسيوم) وبعض الفضلات الأيضية من الدم إلى لمعة الأنبوب البعيد للمساعدة في تنظيم التوازن الحمضي القاعدي."
                    ),
                    highlightedStructures = listOf("distal_tubule")
                ),
                SimulationPhaseDef(
                    id = "urine_collection",
                    name = LocalizedText("4. Collecting Duct & Urine Formation", "4. القناة الجامعة وتكوين البول"),
                    startNormalized = 0.80f,
                    endNormalized = 1.00f,
                    summary = LocalizedText(
                        en = "Final water balance adjustments occur in the Collecting Duct under hormonal regulation before processed filtrate exits toward the renal pelvis and ureter.",
                        ar = "تتم التعديلات النهائية لتوازن الماء في القناة الجامعة بتنظيم هرموني قبل خروج البول المتكوّن نحو حوض الكلية والحالب."
                    ),
                    highlightedStructures = listOf("collecting_duct")
                )
            ),
            equationNotes = LocalizedText(
                en = "Conceptual educational transport diagram (Level C). Particle markers indicate transport direction only—never equate particle counts to mL/min filtration rates.",
                ar = "مخطط نقل تعليمي مفاهيمي (المستوى C). تشير الجسيمات للاتجاه فقط ولا تمثل معدل ترشيح حقيقي."
            ),
            assumptions = listOf(
                LocalizedText("Presents an unrolled schematic nephron structure to make spatial transport steps clearly inspectable.", "يعرض النفرون بشكل تخطيطي مبسط لتوضيح خطوات النقل المكاني بوضوح.")
            ),
            limitations = listOf(
                LocalizedText("Does not calculate eGFR, creatinine clearance, or clinical fluid/electrolyte prescriptions.", "لا يحسب eGFR أو تصفية الكرياتينين أو أي قرارات علاجية.")
            ),
            assetProvenanceNote = LocalizedText(
                en = "Interactive schematic nephron and renal vasculature educational scene (Level C).",
                ar = "مشهد تعليمي تخطيطي تفاعلي للنفرون والأوعية الكلوية (المستوى C)."
            ),
            sources = listOf(
                PhysiologyReferencesSeed.nihNiddkKidney,
                PhysiologyReferencesSeed.ncbiBookshelfPhysiology
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            version = "3.0.0"
        ),

        PhysiologySystemId.DIGESTIVE to SimulationModelRecord(
            id = "model_digestive_transit_v1",
            systemId = PhysiologySystemId.DIGESTIVE,
            titleEnglish = "Gastrointestinal Motility & Absorption Lab",
            titleArabic = "مختبر حركية الجهاز الهضمي والامتصاص",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            description = LocalizedText(
                en = "Visualizes the educational journey from swallowing and esophageal peristalsis through gastric mixing, hepatobiliary/pancreatic secretions in the duodenum, intestinal absorption, and colonic water uptake.",
                ar = "يعرض الرحلة التعليمية من البلع والحركة الدودية المريئية إلى المزج المعدي وإفرازات الكبد والمرارة والبنكرياس في الإثني عشر والامتصاص المعوي وامتصاص الماء في القولون."
            ),
            parameterDefs = listOf(
                SimulationParameterDef(
                    id = "gi_journey_tempo",
                    nameEnglish = "Educational Journey Tempo",
                    nameArabic = "إيقاع الرحلة التعليمية الهضمية",
                    unit = "cycles/min",
                    minValue = 6f,
                    maxValue = 20f,
                    defaultValue = 10f,
                    step = 2f,
                    presets = listOf(6f, 10f, 16f),
                    description = LocalizedText(
                        en = "Controls playback pacing of the time-compressed educational digestive sequence (real digestion occurs over hours).",
                        ar = "يتحكم في وتيرة العرض المضغوط زمنياً لمراحل الهضم (حيث تستغرق العملية الحقيقية ساعات)."
                    ),
                    modelDependency = LocalizedText(
                        en = "Illustrative sequence pacing only; does NOT represent measured gastric emptying time.",
                        ar = "توقيت توضيحي فقط ولا يمثل زمن إفراغ معدي مقاس سريرياً."
                    ),
                    validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION
                )
            ),
            phases = listOf(
                SimulationPhaseDef(
                    id = "swallowing_esophageal",
                    name = LocalizedText("1. Swallowing & Esophageal Transport", "1. البلع والنقل المريئي"),
                    startNormalized = 0.00f,
                    endNormalized = 0.20f,
                    summary = LocalizedText(
                        en = "Coordinated muscular waves (peristalsis) propel the food bolus down the Esophagus and through the lower esophageal sphincter into the Stomach.",
                        ar = "تدفع موجات انقباض عضلية منسقة (الحركة الدودية) اللقمة الغذائية عبر المريء والمصرة المريئية السفلية إلى المعدة."
                    ),
                    highlightedStructures = listOf("esophagus")
                ),
                SimulationPhaseDef(
                    id = "gastric_mixing",
                    name = LocalizedText("2. Gastric Mixing & Acid-Enzyme Breakdown", "2. المزج المعدي والتفكيك الإنزيمي"),
                    startNormalized = 0.20f,
                    endNormalized = 0.45f,
                    summary = LocalizedText(
                        en = "Rhythmic gastric contractions churn food with gastric secretions into semi-fluid chyme before regulated passage into the Duodenum.",
                        ar = "تمزج الانقباضات المعدية الإيقاعية الطعام بالإفرازات المعدية لتكوين الكيموس شبه السائل قبل عبوره المنظم إلى الإثني عشر."
                    ),
                    highlightedStructures = listOf("stomach")
                ),
                SimulationPhaseDef(
                    id = "duodenal_secretions",
                    name = LocalizedText("3. Duodenal Secretions (Liver, Gallbladder, Pancreas)", "3. إفرازات الإثني عشر (الكبد والمرارة والبنكرياس)"),
                    startNormalized = 0.45f,
                    endNormalized = 0.64f,
                    summary = LocalizedText(
                        en = "Bile from the Liver/Gallbladder emulsifies fats while Pancreatic enzymes and bicarbonate neutralize acid and chemically digest macronutrients in the Duodenum.",
                        ar = "تستحلب العصارة الصفراوية من الكبد والمرارة الدهون، بينما تعادل بيكربونات البنكرياس الحموضة وتهضم إنزيماته المغذيات في الإثني عشر."
                    ),
                    highlightedStructures = listOf("duodenum", "liver", "gallbladder", "pancreas")
                ),
                SimulationPhaseDef(
                    id = "intestinal_absorption",
                    name = LocalizedText("4. Small Intestinal Peristalsis & Nutrient Absorption", "4. الحركة الدودية وامتصاص المغذيات"),
                    startNormalized = 0.64f,
                    endNormalized = 0.84f,
                    summary = LocalizedText(
                        en = "Peristalsis and segmentation move chyme across the vast villous surface area of the Small Intestine where digested carbohydrates, amino acids, and lipids are absorbed.",
                        ar = "تحرّك الحركة الدودية والتجزؤ الكيموس عبر الزغابات الواسعة للأمعاء الدقيقة لامتصاص السكريات والأحماض الأمينية والدهون المهضومة."
                    ),
                    highlightedStructures = listOf("small_intestine")
                ),
                SimulationPhaseDef(
                    id = "colonic_water_absorption",
                    name = LocalizedText("5. Large Intestine & Colonic Water Absorption", "5. الأمعاء الغليظة وامتصاص الماء"),
                    startNormalized = 0.84f,
                    endNormalized = 1.00f,
                    summary = LocalizedText(
                        en = "The Large Intestine reclaims remaining water and electrolytes while commensal microbiota process residual fiber, forming solid waste.",
                        ar = "تستعيد الأمعاء الغليظة ما تبقى من الماء والأملاح بمشاركة البكتيريا النافعة لتشكيل الفضلات."
                    ),
                    highlightedStructures = listOf("large_intestine")
                )
            ),
            equationNotes = LocalizedText(
                en = "Time-compressed illustrative educational pathway (Level C). Real gastrointestinal transit spans many hours and varies widely across individuals.",
                ar = "مسار تعليمي توضيحي مضغوط زمنياً (المستوى C). العبور الهضمي الفعلي يستغرق ساعات ويتفاوت بين الأفراد."
            ),
            assumptions = listOf(
                LocalizedText("Sequential phase highlighting illustrates anatomical order from esophagus to colon.", "يعرض الترتيب التشريحي التتابعي من المريء إلى القولون.")
            ),
            limitations = listOf(
                LocalizedText("Does not predict individual gastric emptying times, enzymatic rates, or nutritional bioavailability.", "لا يتنبأ بزمن الإفراغ المعدي الفردي أو معدلات الهضم الخاصة بشخص معين.")
            ),
            assetProvenanceNote = LocalizedText(
                en = "Interactive schematic GI tract and accessory organ educational scene (Level C).",
                ar = "مشهد تعليمي تخطيطي تفاعلي للقناة الهضمية والأعضاء الملحقة (المستوى C)."
            ),
            sources = listOf(
                PhysiologyReferencesSeed.nihNiddkDigestive,
                PhysiologyReferencesSeed.ncbiBookshelfPhysiology
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            lastReviewedDate = "2026-11-20",
            version = "3.0.0"
        )
    )

    val scenarios: List<SimulationScenario> = listOf(
        // CARDIOVASCULAR (5 scenarios)
        SimulationScenario(
            scenarioId = "cardio_normal_cycle",
            modelId = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Normal Cardiac Cycle (75 BPM)",
            titleArabic = "الدورة القلبية الطبيعية (75 نبضة/دقيقة)",
            descriptionEnglish = "Explore synchronized chamber contraction, four valve states, and schematic ECG timing at an illustrative resting rate of 75 BPM.",
            descriptionArabic = "استكشف التزامن بين انقباض الحجرات وحالة الصمامات الأربعة ومخطط ECG التخطيطي عند معدل راحة تعليمي 75 نبضة/دقيقة.",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            defaultParameters = mapOf("heart_rate_bpm" to 75f),
            expectedObservations = listOf(
                LocalizedText("Cycle duration is exactly 0.80 seconds per beat (60 / 75).", "مدة الدورة 0.80 ثانية لكل نبضة (60 / 75)."),
                LocalizedText("AV valves (Tricuspid/Mitral) and Semilunar valves (Pulmonary/Aortic) alternate with brief all-closed isovolumetric phases.", "يتناوب فتح الصمامات الأذينية البطينية مع الصمامات الهلالية تتخللهما مرحلتا انقباض وانبساط متساويي الحجم.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Sequence the 5 cardiac cycle phases and correlate them with valve opening/closing.", "ترتيب مراحل الدورة القلبية الخمس وربطها بفتح وإغلاق الصمامات.")
            ),
            limitations = LocalizedText("Illustrative resting rate only; not a clinical cardiac assessment.", "معدل راحة توضيحي فقط وليس تقييماً سريرياً."),
            sources = listOf(PhysiologyReferencesSeed.ahaHeartCycle, MedicalReferencesSeed.nihNhlbiHeart),
            viewModeTag = "cardiac_cycle"
        ),
        SimulationScenario(
            scenarioId = "cardio_slower_cycle",
            modelId = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Slower Educational Heart Cycle (60 BPM)",
            titleArabic = "دورة قلبية تعليمية أبطأ (60 نبضة/دقيقة)",
            descriptionEnglish = "Observe a 1.00-second cycle period to inspect valve transitions and diastolic filling clearly.",
            descriptionArabic = "لاحظ دورة مدتها 1.00 ثانية لفحص انتقالات الصمامات والامتلاء الانبساطي بوضوح أكبر.",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            defaultParameters = mapOf("heart_rate_bpm" to 60f),
            expectedObservations = listOf(
                LocalizedText("Cycle period lengthens to 1.00 second per beat.", "تطول مدة الدورة لتصبح 1.00 ثانية لكل نبضة.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Distinguish physiological BPM rate changes from playback viewing speed.", "التمييز بين تغير معدل النبض الفسيولوجي وتغير سرعة عرض الفيديو.")
            ),
            limitations = LocalizedText("Educational timing demonstration; does not simulate athletic bradycardia physiology.", "عرض توقيت تعليمي ولا يحاكي حالات بطء القلب السريرية."),
            sources = listOf(PhysiologyReferencesSeed.ahaHeartCycle),
            viewModeTag = "cardiac_cycle"
        ),
        SimulationScenario(
            scenarioId = "cardio_faster_cycle",
            modelId = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Faster Educational Heart Cycle (100 BPM)",
            titleArabic = "دورة قلبية تعليمية أسرع (100 نبضة/دقيقة)",
            descriptionEnglish = "Compare cycle compression at 100 BPM (0.60 s/beat) alongside the cardiac electrical conduction pathway.",
            descriptionArabic = "قارن قصر مدة الدورة عند 100 نبضة/دقيقة (0.60 ثانية/نبضة) مع تفعيل مسار التوصيل الكهربائي القلبي.",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            defaultParameters = mapOf("heart_rate_bpm" to 100f),
            expectedObservations = listOf(
                LocalizedText("Cycle period shortens to 0.60 seconds per beat while keeping ordered SA→AV→His→Purkinje activation.", "تقصر مدة الدورة إلى 0.60 ثانية مع بقاء تسلسل التوصيل الكهربائي منتظماً.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Trace electrical excitation from SA Node through Purkinje Fibers.", "تتبع انتشار التنبيه الكهربائي من العقدة الجيبية حتى ألياف بركنجي.")
            ),
            limitations = LocalizedText("Upper normal resting educational preset; not an arrhythmia simulation.", "إعداد تعليمي ضمن الحد الأعلى للراحة وليس محاكاة لاضطراب نظم."),
            sources = listOf(PhysiologyReferencesSeed.ahaHeartCycle),
            viewModeTag = "conduction"
        ),
        SimulationScenario(
            scenarioId = "cardio_pulmonary_circuit",
            modelId = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Pulmonary Circulation Pathway",
            titleArabic = "مسار الدورة الدموية الرئوية",
            descriptionEnglish = "Isolate the right-heart pulmonary loop: Right Ventricle → Pulmonary Artery → Pulmonary Capillaries → Pulmonary Veins → Left Atrium.",
            descriptionArabic = "عزل مسار الدورة الرئوية: البطين الأيمن ← الشريان الرئوي ← الشعيرات الرئوية ← الأوردة الرئوية ← الأذين الأيسر.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("heart_rate_bpm" to 75f),
            expectedObservations = listOf(
                LocalizedText("Guided directional markers move from RV through Pulmonary Artery and return oxygenated via Pulmonary Veins to LA.", "تتحرك المؤشرات الموجهة من البطين الأيمن عبر الشريان الرئوي وتعود مؤكسجة عبر الأوردة الرئوية للأذين الأيسر.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Recognize that the Pulmonary Artery carries deoxygenated blood while Pulmonary Veins return oxygenated blood.", "تمييز أن الشريان الرئوي يحمل دماً غير مؤكسج بينما الأوردة الرئوية تعيد دماً مؤكسجاً.")
            ),
            limitations = LocalizedText("Directional educational pathway only; particle speed is not quantitative flow velocity.", "مسار اتجاهي تعليمي فقط وسرعة الجسيمات ليست قياساً لسرعة التدفق."),
            sources = listOf(MedicalReferencesSeed.nihNhlbiHeart),
            viewModeTag = "pulmonary"
        ),
        SimulationScenario(
            scenarioId = "cardio_systemic_circuit",
            modelId = "model_cardiovascular_cycle_v1",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            titleEnglish = "Systemic Circulation Pathway",
            titleArabic = "مسار الدورة الدموية الجهازية",
            descriptionEnglish = "Follow oxygenated systemic flow: Left Ventricle → Aorta → Systemic Capillaries → Venae Cavae → Right Atrium.",
            descriptionArabic = "تتبع الدورة الجهازية: البطين الأيسر ← الشريان الأبهر ← الشعيرات الجهازية ← الوريدين الأجوفين ← الأذين الأيمن.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("heart_rate_bpm" to 75f),
            expectedObservations = listOf(
                LocalizedText("Guided flow exits LV through the Aortic Valve into the Aorta and returns venous blood via Superior/Inferior Vena Cava to RA.", "يخرج التدفق من البطين الأيسر عبر الصمام الأبهري إلى الأبهر ويعود وريدياً عبر الوريدين الأجوفين إلى الأذين الأيمن.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Differentiate high-pressure systemic outflow from low-pressure venous return.", "التمييز بين الضخ الجهازي عالي الضغط والعود الوريدي منخفض الضغط.")
            ),
            limitations = LocalizedText("Educational color convention (red/blue) clarifies oxygenation; venous blood is dark red in vivo.", "الألوان اصطلاحية للتعليم؛ فالدم الوريدي أحمر داكن في الواقع."),
            sources = listOf(MedicalReferencesSeed.nihNhlbiHeart),
            viewModeTag = "systemic"
        ),

        // RESPIRATORY (3 scenarios)
        SimulationScenario(
            scenarioId = "resp_quiet_breathing",
            modelId = "model_respiratory_mechanics_v1",
            systemId = PhysiologySystemId.RESPIRATORY,
            titleEnglish = "Quiet Breathing (14 breaths/min)",
            titleArabic = "التنفس الهادئ (14 نفس/دقيقة)",
            descriptionEnglish = "Observe synchronized diaphragm descent during inspiration and passive elastic recoil during expiration.",
            descriptionArabic = "راقب هبوط الحجاب الحاجز المتزامن أثناء الشهيق وارتداده المرن السلبي أثناء الزفير.",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            defaultParameters = mapOf("breathing_rate_bpm" to 14f),
            expectedObservations = listOf(
                LocalizedText("Diaphragm flattens downward as thoracic volume expands and air enters via the trachea.", "ينبسط الحجاب الحاجز للأسفل مع توسع الصدر ودخول الهواء عبر القصبة الهوائية.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Explain how diaphragm motion changes thoracic volume to drive airflow.", "شرح كيف تغيّر حركة الحجاب الحاجز حجم التجويف الصدري لتوليد تدفق الهواء.")
            ),
            limitations = LocalizedText("Schematic mechanics without quantitative spirometry volumes.", "ميكانيكا تخطيطية دون حساب أحجام قياس التنفس الكمية."),
            sources = listOf(PhysiologyReferencesSeed.medlinePlusLungs),
            viewModeTag = "ventilation"
        ),
        SimulationScenario(
            scenarioId = "resp_faster_breathing",
            modelId = "model_respiratory_mechanics_v1",
            systemId = PhysiologySystemId.RESPIRATORY,
            titleEnglish = "Faster Educational Breathing (22 breaths/min)",
            titleArabic = "تنفس تعليمي أسرع (22 نفس/دقيقة)",
            descriptionEnglish = "Observe shortened breath cycle timing (~2.73 s/breath) while comparing inspiration and expiration phases.",
            descriptionArabic = "لاحظ قصر دورة التنفس (~2.73 ثانية/نفس) مع مقارنة مرحلتي الشهيق والزفير.",
            validityLevel = ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL,
            defaultParameters = mapOf("breathing_rate_bpm" to 22f),
            expectedObservations = listOf(
                LocalizedText("Respiratory cycle repeats more rapidly without altering physiological sequence.", "تتكرر دورة التنفس بوتيرة أسرع مع الحفاظ على التسلسل الفسيولوجي.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Relate breathing rate (breaths/min) to breath period in seconds.", "ربط معدل التنفس بزمن كل دورة تنفسية بالثواني.")
            ),
            limitations = LocalizedText("Does not model hyperventilation blood-gas chemistry.", "لا يحاكي تغيرات غازات الدم السريرية."),
            sources = listOf(PhysiologyReferencesSeed.medlinePlusLungs),
            viewModeTag = "ventilation"
        ),
        SimulationScenario(
            scenarioId = "resp_alveolar_exchange",
            modelId = "model_respiratory_mechanics_v1",
            systemId = PhysiologySystemId.RESPIRATORY,
            titleEnglish = "Alveolar Gas Exchange Close-Up",
            titleArabic = "تبادل الغازات عبر الغشاء السنخي الشعيري",
            descriptionEnglish = "Zoom into the alveolar-capillary interface to watch O₂ diffuse into pulmonary capillary red blood cells and CO₂ diffuse into the alveolus.",
            descriptionArabic = "مشهد قريب للغشاء السنخي الشعيري يوضح انتشار O₂ نحو كريات الدم الحمراء وانتشار CO₂ نحو الحويصلة الهوائية.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("breathing_rate_bpm" to 14f),
            expectedObservations = listOf(
                LocalizedText("O₂ moves down its partial pressure gradient from alveolus to capillary blood; CO₂ moves from capillary blood to alveolus.", "ينتشر الأكسجين وفق تدرج الضغط الجزئي من السنخ إلى الدم الشعيري، وينتقل ثاني أكسيد الكربون بالاتجاه المعاكس.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Identify the alveolus, alveolar-capillary membrane, red blood cells, and opposing O₂/CO₂ diffusion directions.", "تحديد السنخ والغشاء السنخي الشعيري وكريات الدم الحمراء واتجاهي انتشار O₂ و CO₂.")
            ),
            limitations = LocalizedText("Qualitative diffusion illustration; animation speed is not a measured diffusion coefficient.", "توضيح كيفي للانتشار وسرعة الحركة ليست معامل انتشار مقاساً."),
            sources = listOf(PhysiologyReferencesSeed.medlinePlusLungs, PhysiologyReferencesSeed.ncbiBookshelfPhysiology),
            viewModeTag = "gas_exchange"
        ),

        // NEURAL (2 scenarios)
        SimulationScenario(
            scenarioId = "neural_action_potential",
            modelId = "model_neural_transmission_v1",
            systemId = PhysiologySystemId.NEURAL,
            titleEnglish = "Basic Action Potential Propagation",
            titleArabic = "انتشار جهد الفعل العصبي الأساسي",
            descriptionEnglish = "Follow dendritic excitation, localized depolarization jumping between Nodes of Ranvier, and arrival at the axon terminal.",
            descriptionArabic = "تتبع التنبيه الشجيري وانتشار إزالة الاستقطاب الموضعية عبر عقد رانفييه حتى النهاية المحورية.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("neural_cycle_rate" to 15f),
            expectedObservations = listOf(
                LocalizedText("Depolarization regenerates at exposed Nodes of Ranvier between myelin segments and maps onto the schematic membrane potential curve.", "يتجدد جهد الفعل عند عقد رانفييه بين أغمدة الميالين بالتزامن مع منحنى جهد الغشاء التخطيطي.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Understand why neural signaling is ionic membrane depolarization rather than fluid flowing in a pipe.", "فهم أن الإشارة العصبية تغير أيوني غشائي موضعي وليست سرياناً لسائل.")
            ),
            limitations = LocalizedText("Time-dilated Level C illustrative model; schematic membrane curve.", "نموذج توضيحي مبطّأ من المستوى C."),
            sources = listOf(PhysiologyReferencesSeed.nihNindsBrain),
            viewModeTag = "neuron"
        ),
        SimulationScenario(
            scenarioId = "neural_synaptic_transmission",
            modelId = "model_neural_transmission_v1",
            systemId = PhysiologySystemId.NEURAL,
            titleEnglish = "Synaptic Transmission Close-Up",
            titleArabic = "الانتقال المشبكي الكيميائي (تكبير المشبك)",
            descriptionEnglish = "Zoom directly into the presynaptic axon terminal, synaptic cleft, vesicle release, and postsynaptic receptor binding.",
            descriptionArabic = "تكبير مباشر للنهاية المحورية قبل المشبكية، الشق المشبكي، تحرر النواقل العصبية وارتباطها بالمستقبلات بعد المشبكية.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("neural_cycle_rate" to 12f),
            expectedObservations = listOf(
                LocalizedText("Arrival of depolarization triggers vesicle fusion and neurotransmitter diffusion across the synaptic cleft.", "وصول إزالة الاستقطاب يحفز التحام الحويصلات وانتشار النواقل عبر الشق المشبكي.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Distinguish electrical propagation along the axon from chemical transmission across the synaptic cleft.", "التمييز بين الانتشار الكهربائي على المحور والانتقال الكيميائي عبر الشق المشبكي.")
            ),
            limitations = LocalizedText("Conceptual synaptic sequence without receptor kinetics equations.", "تسلسل مفاهيمي دون معادلات حركية للمستقبلات."),
            sources = listOf(PhysiologyReferencesSeed.nihNindsBrain),
            viewModeTag = "synapse"
        ),

        // RENAL (2 scenarios)
        SimulationScenario(
            scenarioId = "renal_filtration_pathway",
            modelId = "model_renal_nephron_v1",
            systemId = PhysiologySystemId.RENAL,
            titleEnglish = "Nephron Filtration Pathway",
            titleArabic = "مسار الترشيح الكبيبي في النفرون",
            descriptionEnglish = "Trace incoming renal arterial blood into the glomerulus, filtrate entry into Bowman's capsule, and progression through the tubular segments.",
            descriptionArabic = "تتبع دخول الدم من الشريان الكلوي إلى الكبيبة، تشكل الرشيح في محفظة بومان، ومسيره عبر أجزاء الأنبوب الكلوي.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("nephron_flow_tempo" to 12f),
            expectedObservations = listOf(
                LocalizedText("Filtrate moves sequentially: Glomerulus → Bowman's Capsule → Proximal Tubule → Loop of Henle → Distal Tubule → Collecting Duct.", "يسير الرشيح بتتابع: الكبيبة ← محفظة بومان ← الأنبوب القريب ← التواء هنلي ← الأنبوب البعيد ← القناة الجامعة.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Identify the anatomical segments of the nephron and direction of filtrate movement.", "تحديد أجزاء النفرون التشريحية واتجاه حركة الرشيح.")
            ),
            limitations = LocalizedText("Visual markers show direction only; never predicts clinical eGFR.", "المؤشرات توضح الاتجاه فقط ولا تحسب eGFR."),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkKidney),
            viewModeTag = "nephron_filtration"
        ),
        SimulationScenario(
            scenarioId = "renal_reabsorption_concepts",
            modelId = "model_renal_nephron_v1",
            systemId = PhysiologySystemId.RENAL,
            titleEnglish = "Tubular Reabsorption & Secretion Concepts",
            titleArabic = "مفاهيم إعادة الامتصاص والإفراز الأنبوبي",
            descriptionEnglish = "Highlight bidirectional exchange between the tubular lumen and peritubular capillaries returning reclaimed water/solutes to the Renal Vein.",
            descriptionArabic = "إبراز التبادل ثنائي الاتجاه بين لمعة الأنبوب والشعيرات حول الأنبوبية لإعادة الماء والأملاح إلى الوريد الكلوي وإفراز الفضلات.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("nephron_flow_tempo" to 10f),
            expectedObservations = listOf(
                LocalizedText("Reabsorption transfers water/nutrients from tubule back to blood; secretion transfers selected wastes from blood into the distal tubule.", "إعادة الامتصاص تنقل الماء والمغذيات من الأنبوب للدم؛ والإفراز ينقل فضلات مختارة من الدم إلى الأنبوب.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Contrast filtration, reabsorption, and secretion across nephron segments.", "المقارنة بين الترشيح وإعادة الامتصاص والإفراز عبر أجزاء النفرون.")
            ),
            limitations = LocalizedText("Conceptual educational diagram (Level C).", "مخطط تعليمي مفاهيمي (المستوى C)."),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkKidney),
            viewModeTag = "nephron_transport"
        ),

        // DIGESTIVE (3 scenarios)
        SimulationScenario(
            scenarioId = "gi_gastric_mixing",
            modelId = "model_digestive_transit_v1",
            systemId = PhysiologySystemId.DIGESTIVE,
            titleEnglish = "Swallowing & Gastric Mixing",
            titleArabic = "البلع والمزج المعدي",
            descriptionEnglish = "Follow bolus descent down the esophagus into rhythmic gastric mixing and chyme formation.",
            descriptionArabic = "تتبع نزول اللقمة عبر المريء إلى المزج المعدي الإيقاعي وتكوين الكيموس.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("gi_journey_tempo" to 10f),
            expectedObservations = listOf(
                LocalizedText("Esophageal peristalsis feeds the stomach, where mechanical churning blends food with gastric secretions.", "تنقل الحركة الدودية اللقمة للمعدة حيث يمزجها الانقباض الميكانيكي بالإفرازات المعدية.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Differentiate esophageal propulsion from gastric churning and mixing.", "التمييز بين الدفع المريئي والمزج المعدي.")
            ),
            limitations = LocalizedText("Illustrative pacing; not measured human gastric emptying time.", "إيقاع توضيحي وليس زمن إفراغ معدي مقاس."),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkDigestive),
            viewModeTag = "gastric"
        ),
        SimulationScenario(
            scenarioId = "gi_intestinal_peristalsis",
            modelId = "model_digestive_transit_v1",
            systemId = PhysiologySystemId.DIGESTIVE,
            titleEnglish = "Intestinal Peristalsis & Secretions",
            titleArabic = "الحركة الدودية والإفرازات الهضمية",
            descriptionEnglish = "Inspect coordinated biliary/pancreatic input into the duodenum and propulsive peristaltic waves along the small intestine.",
            descriptionArabic = "فحص دخول العصارة الصفراوية والبنكرياسية للإثني عشر وموجات الحركة الدودية الدافعة عبر الأمعاء الدقيقة.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("gi_journey_tempo" to 10f),
            expectedObservations = listOf(
                LocalizedText("Liver/Gallbladder bile and Pancreatic enzymes join chyme in the Duodenum before peristaltic propagation.", "تندمج العصارة الصفراوية وإنزيمات البنكرياس مع الكيموس في الإثني عشر قبل انتشار الحركة الدودية.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Identify roles of the Liver, Gallbladder, and Pancreas in duodenal digestion.", "تحديد أدوار الكبد والمرارة والبنكرياس في الهضم داخل الإثني عشر.")
            ),
            limitations = LocalizedText("Illustrative teaching sequence (Level C).", "تسلسل تعليمي توضيحي (المستوى C)."),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkDigestive),
            viewModeTag = "peristalsis"
        ),
        SimulationScenario(
            scenarioId = "gi_nutrient_absorption",
            modelId = "model_digestive_transit_v1",
            systemId = PhysiologySystemId.DIGESTIVE,
            titleEnglish = "Nutrient & Colonic Water Absorption",
            titleArabic = "امتصاص المغذيات وامتصاص الماء القولوني",
            descriptionEnglish = "Trace small intestinal nutrient uptake across mucosal villi followed by water and electrolyte reclamation in the large intestine.",
            descriptionArabic = "تتبع امتصاص المغذيات عبر زغابات الأمعاء الدقيقة يليه استعادة الماء والأملاح في الأمعاء الغليظة.",
            validityLevel = ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION,
            defaultParameters = mapOf("gi_journey_tempo" to 10f),
            expectedObservations = listOf(
                LocalizedText("Primary nutrient absorption occurs in the Small Intestine while the Large Intestine concentrates residue by reclaiming water.", "يحدث امتصاص المغذيات الأساسي في الأمعاء الدقيقة بينما تستعيد الأمعاء الغليظة الماء والأملاح.")
            ),
            educationalObjectives = listOf(
                LocalizedText("Contrast small intestinal nutrient absorption with colonic water absorption.", "المقارنة بين امتصاص المغذيات في الأمعاء الدقيقة وامتصاص الماء في القولون.")
            ),
            limitations = LocalizedText("Conceptual overview; does not compute individual absorption kinetics.", "عرض مفاهيمي لا يحسب حركية امتصاص فردية."),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkDigestive),
            viewModeTag = "absorption"
        )
    )

    val explanations: Map<PhysiologySystemId, PhysiologyExplanationRecord> = mapOf(
        PhysiologySystemId.CARDIOVASCULAR to PhysiologyExplanationRecord(
            processId = "proc_cardiac_cycle",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            processName = LocalizedText("Cardiac Systole & Diastole (Cardiac Cycle)", "الانقباض والانبساط القلبي (الدورة القلبية)"),
            scientificDefinition = LocalizedText(
                en = "The coordinated electrical and mechanical sequence of chamber contraction (systole) and relaxation (diastole) during a single heartbeat that maintains unidirectional pulmonary and systemic blood flow.",
                ar = "التسلسل الكهربائي والميكانيكي المنسق لانقباض حجرات القلب (الانقباض) وارتخائها (الانبساط) خلال نبضة واحدة لضمان تدفق الدم في اتجاه واحد عبر الدورتين الرئوية والجهازية."
            ),
            anatomicalStructures = LocalizedText(
                en = "Right & Left Atria, Right & Left Ventricles, Tricuspid, Mitral, Pulmonary, and Aortic Valves, Aorta, Pulmonary Artery, Pulmonary Veins, Superior & Inferior Vena Cava, SA/AV Nodes & Purkinje Fibers.",
                ar = "الأذينان الأيمن والأيسر، البطينان الأيمن والأيسر، الصمامات الأربعة (ثلاثي الشرفات، التاجي، الرئوي، الأبهري)، الأبهر، الشريان الرئوي، الأوردة الرئوية، الوريدان الأجوفان، ونظام التوصيل الكهربائي."
            ),
            sequenceOfEvents = listOf(
                LocalizedText("SA node depolarizes atria (P wave) → Atrial Systole tops off ventricles.", "تُفعّل العقدة الجيبية الأذينين (موجة P) فيكتمل امتلاء البطينين."),
                LocalizedText("QRS triggers ventricular contraction → AV valves close (S1) → Isovolumetric Contraction.", "يبدأ انقباض البطينين مع QRS فينغلق الصمامان الأذينيان البطينيان (S1)."),
                LocalizedText("Semilunar valves open → Ventricular Ejection into Aorta and Pulmonary Artery.", "ينفتح الصمامان الأبهري والرئوي ويُقذف الدم نحو الأبهر والشريان الرئوي."),
                LocalizedText("T wave marks ventricular repolarization → Semilunar valves close (S2) → Relaxation & Filling.", "تحدث إعادة الاستقطاب (موجة T) فينغلق الصمامان الهلاليان (S2) ويعود الامتلاء الانبساطي.")
            ),
            biologicalFunction = LocalizedText(
                en = "Continuously delivers oxygenated blood to systemic organs while routing venous blood to the lungs for gas exchange.",
                ar = "توصيل الدم المؤكسج باستمرار إلى أنسجة الجسم وتوجيه الدم الوريدي إلى الرئتين لتبادل الغازات."
            ),
            relatedConcepts = listOf(
                LocalizedText("Cardiac Conduction System", "نظام التوصيل الكهربائي القلبي"),
                LocalizedText("Pulmonary vs. Systemic Circuits", "الدورة الرئوية مقابل الجهازية"),
                LocalizedText("Heart Sounds (S1 / S2)", "أصوات القلب (S1 و S2)")
            ),
            clinicalRelevance = LocalizedText(
                en = "Understanding valve closure timing explains normal S1/S2 heart sounds, basic ECG wave correlation, and why unidirectional valves prevent backflow.",
                ar = "يفسر فهم توقيت إغلاق الصمامات أصل صوتي القلب الطبيعيين (S1/S2)، وترابط موجات تخطيط القلب، وأهمية الصمامات في منع الارتجاع."
            ),
            sources = listOf(PhysiologyReferencesSeed.ahaHeartCycle, MedicalReferencesSeed.nihNhlbiHeart)
        ),
        PhysiologySystemId.RESPIRATORY to PhysiologyExplanationRecord(
            processId = "proc_pulmonary_ventilation",
            systemId = PhysiologySystemId.RESPIRATORY,
            processName = LocalizedText("Pulmonary Ventilation & Alveolar Gas Exchange", "التهوية الرئوية وتبادل الغازات السنخي"),
            scientificDefinition = LocalizedText(
                en = "Ventilation is the cyclic movement of air into and out of the lungs driven by pressure-volume changes from respiratory muscles; gas exchange is passive diffusion of O₂ and CO₂ across the alveolar-capillary membrane.",
                ar = "التهوية الرئوية هي حركة الهواء الدورية دخولاً وخروجاً بفعل تغيرات حجم الصدر عبر عضلات التنفس؛ وتبادل الغازات هو الانتشار السلبي لـ O₂ و CO₂ عبر الغشاء السنخي الشعيري."
            ),
            anatomicalStructures = LocalizedText(
                en = "Trachea, Main Bronchi, Bronchial Tree, Right & Left Lungs, Alveoli, Alveolar-Capillary Membrane, Pulmonary Capillaries, Diaphragm, Thoracic Cavity.",
                ar = "القصبة الهوائية، الشعبتان الرئيسيتان، الشجرة القصبية، الرئتان اليمنى واليسرى، الحويصلات الهوائية، الغشاء السنخي الشعيري، الشعيرات الرئوية، الحجاب الحاجز، والتجويف الصدري."
            ),
            sequenceOfEvents = listOf(
                LocalizedText("Diaphragm contracts downward → thoracic volume rises → air enters airways (Inspiration).", "ينقبض الحجاب الحاجز للأسفل فيزداد حجم الصدر ويندفع الهواء للداخل (الشهيق)."),
                LocalizedText("O₂ diffuses down its partial pressure gradient from alveolus into capillary erythrocytes.", "ينتشر الأكسجين وفق تدرج الضغط الجزئي من السنخ إلى كريات الدم الحمراء."),
                LocalizedText("CO₂ diffuses from pulmonary capillary blood into alveolar air.", "ينتشر ثاني أكسيد الكربون من الدم الشعيري إلى هواء السنخ."),
                LocalizedText("Diaphragm relaxes upward and lungs recoil elastically → air exits (Expiration).", "يرتخي الحجاب الحاجز للأعلى وترتد الرئتان بمرونة فيخرج الهواء (الزفير).")
            ),
            biologicalFunction = LocalizedText(
                en = "Replenishes blood oxygen required for aerobic cellular metabolism and eliminates carbon dioxide to support acid-base homeostasis.",
                ar = "تزويد الدم بالأكسجين اللازم للأيض الخلوي الهوائي والتخلص من ثاني أكسيد الكربون للحفاظ على التوازن الحمضي القاعدي."
            ),
            relatedConcepts = listOf(
                LocalizedText("Partial Pressure Gradients", "تدرج الضغوط الجزئية"),
                LocalizedText("Ventilation-Perfusion Matching", "توافق التهوية والتروية"),
                LocalizedText("Passive Diffusion", "الانتشار السلبي")
            ),
            clinicalRelevance = LocalizedText(
                en = "Clarifies how airway patency, diaphragmatic mechanics, and thin alveolar membranes work together to sustain oxygenation.",
                ar = "يوضح تكامل سالكية المجاري الهوائية وميكانيكية الحجاب الحاجز ورقة الغشاء السنخي لضمان الأكسجة السليمة."
            ),
            sources = listOf(PhysiologyReferencesSeed.medlinePlusLungs, PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        ),
        PhysiologySystemId.NEURAL to PhysiologyExplanationRecord(
            processId = "proc_action_potential",
            systemId = PhysiologySystemId.NEURAL,
            processName = LocalizedText("Action Potential & Synaptic Transmission", "جهد الفعل العصبي والانتقال المشبكي"),
            scientificDefinition = LocalizedText(
                en = "A rapid, self-propagating reversal of transmembrane electrical potential driven by gated Na⁺ and K⁺ ion fluxes, culminating in chemical neurotransmitter signaling across a synapse.",
                ar = "انعكاس سريع ذاتي الانتشار في جهد الغشاء الخلوي تقوده قنوات الصوديوم والبوتاسيوم الأيونية، وينتهي بنقل إشارة كيميائية عبر المشبك العصبي."
            ),
            anatomicalStructures = LocalizedText(
                en = "Dendrites, Cell Body (Soma), Axon Hillock, Axon, Myelin Sheath, Nodes of Ranvier, Presynaptic Axon Terminal, Synaptic Cleft, Postsynaptic Membrane.",
                ar = "الزوائد الشجيرية، جسم الخلية العصبية، هضبة المحور، المحور العصبي، غمد الميالين، عقد رانفييه، النهاية المحورية، الشق المشبكي، والغشاء بعد المشبكي."
            ),
            sequenceOfEvents = listOf(
                LocalizedText("Dendritic inputs summate to reach threshold at the axon hillock.", "تتجمع التنبيهات الشجيرية لتبلغ عتبة الإثارة عند هضبة المحور."),
                LocalizedText("Voltage-gated Na⁺ influx causes rapid local depolarization; K⁺ efflux drives repolarization.", "يُحدث دخول الصوديوم إزالة استقطاب سريعة ويُعيد خروج البوتاسيوم الاستقطاب."),
                LocalizedText("Myelin insulates internodes so excitation regenerates rapidly at Nodes of Ranvier.", "يعزل الميالين المسافات البينية فيتجدد التنبيه بسرعة عند عقد رانفييه."),
                LocalizedText("Terminal Ca²⁺ entry releases neurotransmitters across the synaptic cleft to activate postsynaptic receptors.", "يؤدي دخول الكالسيوم في النهاية المحورية لتحرر النواقل العصبية وتنشيط المستقبلات بعد المشبكية.")
            ),
            biologicalFunction = LocalizedText(
                en = "Enables high-speed communication across sensory pathways, neural circuits, autonomic control, and neuromuscular junctions.",
                ar = "تتيح تواصلاً فائق السرعة عبر المسارات الحسية والدوائر العصبية والتنظيم الذاتي والوصل العصبي العضلي."
            ),
            relatedConcepts = listOf(
                LocalizedText("Saltatory Conduction", "التوصيل القافز"),
                LocalizedText("Chemical Synapse", "المشبك الكيميائي"),
                LocalizedText("Ion Channel Gating", "تبويب القنوات الأيونية")
            ),
            clinicalRelevance = LocalizedText(
                en = "Explains why myelin integrity accelerates signal conduction and how synaptic receptors mediate communication between neurons.",
                ar = "يفسر دور سلامة غمد الميالين في تسريع التوصيل العصبي ودور المستقبلات المشبكية في التواصل بين الخلايا العصبية."
            ),
            sources = listOf(PhysiologyReferencesSeed.nihNindsBrain, PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        ),
        PhysiologySystemId.RENAL to PhysiologyExplanationRecord(
            processId = "proc_glomerular_filtration",
            systemId = PhysiologySystemId.RENAL,
            processName = LocalizedText("Glomerular Filtration & Nephron Transport", "الترشيح الكبيبي والنقل الأنبوبي في النفرون"),
            scientificDefinition = LocalizedText(
                en = "The integrated four-stage renal process—glomerular filtration, tubular reabsorption, tubular secretion, and regulated water collection—that filters plasma and forms urine.",
                ar = "العملية الكلوية المتكاملة رباعية المراحل: الترشيح الكبيبي، إعادة الامتصاص الأنبوبي، الإفراز الأنبوبي، وتجميع البول لتنقية البلازما وتنظيم سوائل الجسم."
            ),
            anatomicalStructures = LocalizedText(
                en = "Kidney, Renal Artery, Renal Vein, Glomerulus, Bowman's Capsule, Proximal Convoluted Tubule, Loop of Henle, Distal Tubule, Collecting Duct.",
                ar = "الكلية، الشريان الكلوي، الوريد الكلوي، الكبيبة، محفظة بومان، الأنبوب القريب، التواء هنلي، الأنبوب البعيد، والقناة الجامعة."
            ),
            sequenceOfEvents = listOf(
                LocalizedText("Glomerular Filtration: water and small solutes filter into Bowman's capsule.", "الترشيح الكبيبي: يُرشح الماء والذائبات الصغيرة إلى محفظة بومان."),
                LocalizedText("Tubular Reabsorption: glucose, amino acids, ions, and most water return to blood.", "إعادة الامتصاص: يُعاد الجلوكوز والأحماض الأمينية ومعظم الماء والأملاح للدم."),
                LocalizedText("Tubular Secretion: excess H⁺, K⁺, and metabolic wastes move from blood into tubule.", "الإفراز الأنبوبي: تُنقل الأيونات الزائدة وبعض الفضلات من الدم للمعة الأنبوب."),
                LocalizedText("Collecting Duct adjusts final urine concentration before leaving the kidney.", "تضبط القناة الجامعة تركيز البول النهائي قبل مغادرة الكلية.")
            ),
            biologicalFunction = LocalizedText(
                en = "Excretes nitrogenous wastes while tightly regulating extracellular fluid volume, osmolarity, electrolytes, and blood pH.",
                ar = "إطراح الفضلات النيتروجينية مع التنظيم الدقيق لحجم السوائل والأسمولية والأملاح ودرجة الحموضة."
            ),
            relatedConcepts = listOf(
                LocalizedText("Nephron Functional Unit", "الوحدة الوظيفية للنفرون"),
                LocalizedText("Selective Membrane Barrier", "حاجز الترشيح الانتقائي"),
                LocalizedText("Fluid-Electrolyte Homeostasis", "توازن السوائل والأملاح")
            ),
            clinicalRelevance = LocalizedText(
                en = "Foundation for understanding why intact glomerular barriers retain plasma proteins and how distinct nephron segments handle electrolytes and water.",
                ar = "الأساس لفهم احتفاظ الحاجز الكبيبي السليم ببروتينات البلازما ودور كل جزء من النفرون في توازن الأملاح والماء."
            ),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkKidney, PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        ),
        PhysiologySystemId.DIGESTIVE to PhysiologyExplanationRecord(
            processId = "proc_peristalsis",
            systemId = PhysiologySystemId.DIGESTIVE,
            processName = LocalizedText("Peristalsis, Digestion & Nutrient Absorption", "الحركة الدودية والهضم وامتصاص المغذيات"),
            scientificDefinition = LocalizedText(
                en = "Coordinated smooth-muscle contractions (peristalsis and mixing) combined with enzymatic and biliary secretions that break macromolecules into absorbable nutrients and reclaim water.",
                ar = "انقباضات عضلية ملساء منسقة (الحركة الدودية والمزج) تتكامل مع الإفرازات الإنزيمية والصفراوية لتفكيك الغذاء إلى مغذيات قابلة للامتصاص واستعادة الماء."
            ),
            anatomicalStructures = LocalizedText(
                en = "Esophagus, Stomach, Duodenum, Small Intestine, Large Intestine, Liver, Gallbladder, and Pancreas.",
                ar = "المريء، المعدة، الإثني عشر، الأمعاء الدقيقة، الأمعاء الغليظة، الكبد، المرارة، والبنكرياس."
            ),
            sequenceOfEvents = listOf(
                LocalizedText("Swallowing & Esophageal Peristalsis transport food to the stomach.", "ينقل البلع والحركة الدودية المريئية الطعام إلى المعدة."),
                LocalizedText("Gastric Mixing mechanically churns food with acid and pepsin into chyme.", "يمزج الانقباض المعدي الطعام بالحمض والإنزيمات مكوناً الكيموس."),
                LocalizedText("Liver/Gallbladder bile and Pancreatic enzymes enter the Duodenum.", "تصب العصارة الصفراوية وإنزيمات البنكرياس في الإثني عشر."),
                LocalizedText("Small Intestine absorbs digested nutrients; Large Intestine absorbs water and electrolytes.", "تمتص الأمعاء الدقيقة المغذيات المهضومة وتستعيد الأمعاء الغليظة الماء والأملاح.")
            ),
            biologicalFunction = LocalizedText(
                en = "Converts ingested food into bioavailable substrates (glucose, amino acids, fatty acids, vitamins, water) fueling cellular metabolism.",
                ar = "تحويل الغذاء إلى عناصر قابلة للاستفادة الحيوية (جلوكوز، أحماض أمينية ودهنية، فيتامينات، ماء) لدعم الأيض الخلوي."
            ),
            relatedConcepts = listOf(
                LocalizedText("Peristalsis & Segmentation", "الحركة الدودية والتجزؤ"),
                LocalizedText("Biliary Emulsification", "الاستحلاب الصفراوي"),
                LocalizedText("Intestinal Villi Surface Area", "مساحة سطح الزغابات المعوية")
            ),
            clinicalRelevance = LocalizedText(
                en = "Clarifies how motility, accessory organ secretions, and specialized regional mucosa cooperate across the gastrointestinal tract.",
                ar = "يوضح التكامل بين الحركية الهضمية وإفرازات الأعضاء الملحقة وتخصص بطانة كل جزء من القناة الهضمية."
            ),
            sources = listOf(PhysiologyReferencesSeed.nihNiddkDigestive, PhysiologyReferencesSeed.ncbiBookshelfPhysiology)
        )
    )

    fun getModel(systemId: PhysiologySystemId): SimulationModelRecord =
        models.getValue(systemId)

    fun getScenariosForSystem(systemId: PhysiologySystemId): List<SimulationScenario> =
        scenarios.filter { it.systemId == systemId }

    fun findScenario(scenarioId: String): SimulationScenario =
        scenarios.firstOrNull { it.scenarioId == scenarioId } ?: scenarios.first()

    fun getExplanation(systemId: PhysiologySystemId): PhysiologyExplanationRecord =
        explanations.getValue(systemId)
}
