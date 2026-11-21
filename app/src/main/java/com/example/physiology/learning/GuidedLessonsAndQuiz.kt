package com.example.physiology.learning

import com.example.core.i18n.LocalizedText
import com.example.core.model.ReferenceSource
import com.example.data.seed.MedicalReferencesSeed
import com.example.physiology.engine.PhysiologyReferencesSeed
import com.example.physiology.engine.PhysiologySystemId

data class GuidedLessonStep(
    val stepNumber: Int,
    val title: LocalizedText,
    val explanation: LocalizedText,
    val scenarioId: String,
    val targetProgress: Float? = null,
    val pauseOnEnter: Boolean = false,
    val highlightPartId: String? = null
)

data class GuidedPhysiologyLesson(
    val lessonId: String,
    val systemId: PhysiologySystemId,
    val title: LocalizedText,
    val subtitle: LocalizedText,
    val steps: List<GuidedLessonStep>,
    val linkedQuizQuestionId: String
)

enum class QuizQuestionFormat(val label: LocalizedText) {
    SINGLE_CHOICE(LocalizedText("Single Choice", "اختيار من متعدد")),
    IDENTIFY_STRUCTURE(LocalizedText("Identify Structure", "تحديد البنية التشريحية")),
    ORDER_EVENTS(LocalizedText("Order Events", "ترتيب التسلسل الفسيولوجي")),
    MATCH_BILINGUAL_TERMS(LocalizedText("Match EN ↔ AR", "مطابقة إنجليزي ↔ عربي")),
    INTERPRET_DIAGRAM(LocalizedText("Interpret Diagram", "تفسير المخطط التعليمي"))
}

data class PhysiologyQuizQuestion(
    val id: String,
    val systemId: PhysiologySystemId,
    val format: QuizQuestionFormat,
    val prompt: LocalizedText,
    val diagramHint: LocalizedText? = null,
    val options: List<LocalizedText> = emptyList(),
    val correctOptionIndex: Int = 0,
    // For ORDER_EVENTS: items presented shuffled and expected canonical indices
    val sequenceItems: List<LocalizedText> = emptyList(),
    // For MATCH_BILINGUAL_TERMS: pairs of (English, Arabic)
    val matchPairs: List<Pair<String, String>> = emptyList(),
    val explanation: LocalizedText,
    val source: ReferenceSource
)

object PhysiologyLearningSeed {

    val guidedLessons: List<GuidedPhysiologyLesson> = listOf(
        GuidedPhysiologyLesson(
            lessonId = "lesson_cardiac_cycle",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            title = LocalizedText("Understanding the Cardiac Cycle", "فهم الدورة القلبية الكاملة"),
            subtitle = LocalizedText(
                "7 interactive steps covering chambers, systole/diastole, valves, and both circulations.",
                "7 خطوات تفاعلية تغطي الحجرات والانقباض والانبساط والصمامات والدورتين الرئوية والجهازية."
            ),
            steps = listOf(
                GuidedLessonStep(
                    stepNumber = 1,
                    title = LocalizedText("Step 01: Explore the Four Cardiac Chambers", "الخطوة 01: استكشف حجرات القلب الأربع"),
                    explanation = LocalizedText(
                        "Inspect the Right & Left Atria (receiving chambers) above the Right & Left Ventricles (pumping chambers).",
                        "افحص الأذينين الأيمن والأيسر (حجرتي الاستقبال) أعلى البطينين الأيمن والأيسر (حجرتي الضخ)."
                    ),
                    scenarioId = "cardio_normal_cycle",
                    targetProgress = 0.02f,
                    pauseOnEnter = true,
                    highlightPartId = "heart"
                ),
                GuidedLessonStep(
                    stepNumber = 2,
                    title = LocalizedText("Step 02: Observe Atrial Contraction", "الخطوة 02: لاحظ انقباض الأذينين"),
                    explanation = LocalizedText(
                        "During Atrial Systole (P wave), Tricuspid and Mitral valves stay open to complete ventricular filling.",
                        "أثناء انقباض الأذينين (موجة P) يظل الصمامان ثلاثي الشرفات والتاجي مفتوحين لإتمام ملء البطينين."
                    ),
                    scenarioId = "cardio_normal_cycle",
                    targetProgress = 0.07f,
                    pauseOnEnter = false,
                    highlightPartId = "right_atrium"
                ),
                GuidedLessonStep(
                    stepNumber = 3,
                    title = LocalizedText("Step 03: Observe Ventricular Contraction", "الخطوة 03: لاحظ انقباض البطينين"),
                    explanation = LocalizedText(
                        "Following the QRS complex, ventricles contract forcefully, closing AV valves (S1) and ejecting blood into the great arteries.",
                        "بعد مركب QRS ينقبض البطينان بقوة، فيغلقان الصمامين الأذينيين البطينيين (S1) ويقذفان الدم للشرايين الكبرى."
                    ),
                    scenarioId = "cardio_normal_cycle",
                    targetProgress = 0.30f,
                    pauseOnEnter = false,
                    highlightPartId = "left_ventricle"
                ),
                GuidedLessonStep(
                    stepNumber = 4,
                    title = LocalizedText("Step 04: Inspect Valve Behavior", "الخطوة 04: افحص سلوك الصمامات"),
                    explanation = LocalizedText(
                        "Watch the Valve Matrix below the chart: Semilunar valves open only during Ventricular Ejection and snap shut (S2) at relaxation.",
                        "راقب مصفوفة الصمامات أسفل الرسم: الصمامان الأبهري والرئوي ينفتحان أثناء القذف فقط وينغلقان (S2) عند الارتخاء."
                    ),
                    scenarioId = "cardio_slower_cycle",
                    targetProgress = 0.52f,
                    pauseOnEnter = true,
                    highlightPartId = "aorta"
                ),
                GuidedLessonStep(
                    stepNumber = 5,
                    title = LocalizedText("Step 05: Follow Pulmonary Circulation", "الخطوة 05: تتبّع الدورة الدموية الرئوية"),
                    explanation = LocalizedText(
                        "Right Ventricle → Pulmonary Artery → Pulmonary Capillaries → Pulmonary Veins → Left Atrium.",
                        "البطين الأيمن ← الشريان الرئوي ← الشعيرات الرئوية ← الأوردة الرئوية ← الأذين الأيسر."
                    ),
                    scenarioId = "cardio_pulmonary_circuit",
                    pauseOnEnter = false,
                    highlightPartId = "pulmonary_artery"
                ),
                GuidedLessonStep(
                    stepNumber = 6,
                    title = LocalizedText("Step 06: Follow Systemic Circulation", "الخطوة 06: تتبّع الدورة الدموية الجهازية"),
                    explanation = LocalizedText(
                        "Left Ventricle → Aorta → Systemic Capillaries → Venae Cavae → Right Atrium. Remember: blue is an educational convention, not literal blood color.",
                        "البطين الأيسر ← الأبهر ← الشعيرات الجهازية ← الوريدين الأجوفين ← الأذين الأيمن. تذكّر: اللون الأزرق اصطلاح تعليمي فقط."
                    ),
                    scenarioId = "cardio_systemic_circuit",
                    pauseOnEnter = false,
                    highlightPartId = "aorta"
                ),
                GuidedLessonStep(
                    stepNumber = 7,
                    title = LocalizedText("Step 07: Complete the Knowledge Check", "الخطوة 07: أجب عن سؤال التحقق المعرفي"),
                    explanation = LocalizedText(
                        "Mark this lesson complete and test your understanding of valve timing and cardiac conduction in the Quiz tab.",
                        "أتمّ هذا الدرس واختبر فهمك لتوقيت الصمامات والتوصيل الكهربائي القلبي في قسم الاختبارات."
                    ),
                    scenarioId = "cardio_faster_cycle",
                    pauseOnEnter = false
                )
            ),
            linkedQuizQuestionId = "q_cardio_order"
        ),
        GuidedPhysiologyLesson(
            lessonId = "lesson_respiratory_exchange",
            systemId = PhysiologySystemId.RESPIRATORY,
            title = LocalizedText("Mechanics of Breathing & Alveolar Diffusion", "ميكانيكا التنفس والانتشار السنخي"),
            subtitle = LocalizedText(
                "3 steps connecting diaphragm descent to O₂/CO₂ partial pressure gradients.",
                "3 خطوات تربط هبوط الحجاب الحاجز بتدرجات الضغوط الجزئية لـ O₂ و CO₂."
            ),
            steps = listOf(
                GuidedLessonStep(
                    stepNumber = 1,
                    title = LocalizedText("Step 01: Inspiration & Diaphragm Descent", "الخطوة 01: الشهيق وهبوط الحجاب الحاجز"),
                    explanation = LocalizedText(
                        "Observe the amber diaphragm flattening downward to expand thoracic volume and draw air through the trachea.",
                        "لاحظ انبساط الحجاب الحاجز للأسفل لزيادة حجم التجويف الصدري وسحب الهواء عبر القصبة الهوائية."
                    ),
                    scenarioId = "resp_quiet_breathing",
                    targetProgress = 0.22f,
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 2,
                    title = LocalizedText("Step 02: Alveolar-Capillary Gas Exchange", "الخطوة 02: تبادل الغازات السنخي الشعيري"),
                    explanation = LocalizedText(
                        "Inspect opposing passive diffusion: O₂ moves from alveolus into red blood cells while CO₂ moves into the alveolus.",
                        "افحص الانتشار السلبي المتعاكس: الأكسجين يعبر من السنخ إلى كريات الدم الحمراء بينما يخرج ثاني أكسيد الكربون نحو السنخ."
                    ),
                    scenarioId = "resp_alveolar_exchange",
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 3,
                    title = LocalizedText("Step 03: Passive Expiration", "الخطوة 03: الزفير السلبي"),
                    explanation = LocalizedText(
                        "Elastic lung recoil and upward diaphragm relaxation reduce thoracic volume and expel CO₂-rich air.",
                        "يقلص الارتداد المرن للرئتين وصعود الحجاب الحاجز حجم الصدر لدفع الهواء المحمل بـ CO₂ للخارج."
                    ),
                    scenarioId = "resp_quiet_breathing",
                    targetProgress = 0.72f,
                    pauseOnEnter = false
                )
            ),
            linkedQuizQuestionId = "q_resp_gas_exchange"
        ),
        GuidedPhysiologyLesson(
            lessonId = "lesson_neural_signaling",
            systemId = PhysiologySystemId.NEURAL,
            title = LocalizedText("Action Potential & Synaptic Cleft", "جهد الفعل والشق المشبكي"),
            subtitle = LocalizedText(
                "3 steps covering Nodes of Ranvier depolarization and chemical neurotransmission.",
                "3 خطوات تغطي إزالة الاستقطاب في عقد رانفييه والانتقال الكيميائي المشبكي."
            ),
            steps = listOf(
                GuidedLessonStep(
                    stepNumber = 1,
                    title = LocalizedText("Step 01: Dendrites & Soma Threshold", "الخطوة 01: الزوائد الشجيرية وعتبة الإثارة"),
                    explanation = LocalizedText(
                        "Incoming signals integrate at the soma before triggering voltage-gated Na⁺ channels at the axon hillock.",
                        "تتجمع التنبيهات عند جسم الخلية لتفعيل قنوات الصوديوم عند هضبة المحور."
                    ),
                    scenarioId = "neural_action_potential",
                    targetProgress = 0.08f,
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 2,
                    title = LocalizedText("Step 02: Saltatory Conduction at Nodes of Ranvier", "الخطوة 02: التوصيل القافز عند عقد رانفييه"),
                    explanation = LocalizedText(
                        "Action potentials regenerate ionically at exposed Nodes of Ranvier—not as fluid flowing down a pipe.",
                        "يتجدد جهد الفعل أيونياً عند عقد رانفييه المكشوفة بين أغمدة الميالين."
                    ),
                    scenarioId = "neural_action_potential",
                    targetProgress = 0.42f,
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 3,
                    title = LocalizedText("Step 03: Synapse Close-Up", "الخطوة 03: تكبير المشبك العصبي"),
                    explanation = LocalizedText(
                        "Presynaptic vesicles release neurotransmitters across the synaptic cleft to bind postsynaptic receptors.",
                        "تحرر الحويصلات قبل المشبكية النواقل العصبية عبر الشق المشبكي لترتبط بالمستقبلات بعد المشبكية."
                    ),
                    scenarioId = "neural_synaptic_transmission",
                    pauseOnEnter = false
                )
            ),
            linkedQuizQuestionId = "q_neural_myelin"
        ),
        GuidedPhysiologyLesson(
            lessonId = "lesson_renal_nephron",
            systemId = PhysiologySystemId.RENAL,
            title = LocalizedText("Four Processes of the Nephron", "العمليات الأربع لوظيفة النفرون"),
            subtitle = LocalizedText(
                "2 steps contrasting Glomerular Filtration with Tubular Reabsorption & Secretion.",
                "خطوتان للمقارنة بين الترشيح الكبيبي وإعادة الامتصاص والإفراز الأنبوبي."
            ),
            steps = listOf(
                GuidedLessonStep(
                    stepNumber = 1,
                    title = LocalizedText("Step 01: Glomerular Filtration into Bowman's Capsule", "الخطوة 01: الترشيح الكبيبي في محفظة بومان"),
                    explanation = LocalizedText(
                        "Water and small solutes filter across the glomerular barrier while blood cells and large proteins stay in blood.",
                        "يرشح الماء والذائبات الصغيرة عبر الحاجز الكبيبي بينما تبقى خلايا الدم والبروتينات الكبيرة في الدم."
                    ),
                    scenarioId = "renal_filtration_pathway",
                    targetProgress = 0.12f,
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 2,
                    title = LocalizedText("Step 02: Reabsorption, Secretion & Collecting Duct", "الخطوة 02: إعادة الامتصاص والإفراز والقناة الجامعة"),
                    explanation = LocalizedText(
                        "Essential water and nutrients return to peritubular capillaries while selected wastes are secreted prior to urine collection.",
                        "تُعاد المياه والمغذيات الأساسية للدم بينما تُفرز فضلات مختارة قبل تجميع البول النهائي."
                    ),
                    scenarioId = "renal_reabsorption_concepts",
                    targetProgress = 0.55f,
                    pauseOnEnter = false
                )
            ),
            linkedQuizQuestionId = "q_renal_filtration"
        ),
        GuidedPhysiologyLesson(
            lessonId = "lesson_digestive_transit",
            systemId = PhysiologySystemId.DIGESTIVE,
            title = LocalizedText("Digestive Journey: Motility to Absorption", "الرحلة الهضمية من الحركة الدودية إلى الامتصاص"),
            subtitle = LocalizedText(
                "2 steps following chyme through stomach mixing, duodenal secretions, and intestinal uptake.",
                "خطوتان لمتابعة الكيموس من المزج المعدي وإفرازات الإثني عشر إلى الامتصاص المعوي."
            ),
            steps = listOf(
                GuidedLessonStep(
                    stepNumber = 1,
                    title = LocalizedText("Step 01: Gastric Mixing & Duodenal Secretions", "الخطوة 01: المزج المعدي وإفرازات الإثني عشر"),
                    explanation = LocalizedText(
                        "Stomach contractions churn food into chyme, which receives liver/gallbladder bile and pancreatic enzymes in the duodenum.",
                        "يمزج الانقباض المعدي الطعام مكوناً الكيموس، ثم تضاف الصفراء وإنزيمات البنكرياس في الإثني عشر."
                    ),
                    scenarioId = "gi_gastric_mixing",
                    targetProgress = 0.35f,
                    pauseOnEnter = false
                ),
                GuidedLessonStep(
                    stepNumber = 2,
                    title = LocalizedText("Step 02: Small vs. Large Intestinal Absorption", "الخطوة 02: الامتصاص في الأمعاء الدقيقة والغليظة"),
                    explanation = LocalizedText(
                        "Small intestine villi absorb digested macronutrients; the large intestine reclaims residual water and electrolytes.",
                        "تمتص زغابات الأمعاء الدقيقة المغذيات المهضومة بينما تستعيد الأمعاء الغليظة الماء والأملاح."
                    ),
                    scenarioId = "gi_nutrient_absorption",
                    targetProgress = 0.78f,
                    pauseOnEnter = false
                )
            ),
            linkedQuizQuestionId = "q_gi_bilingual_match"
        )
    )

    val quizQuestions: List<PhysiologyQuizQuestion> = listOf(
        // 1. SINGLE_CHOICE
        PhysiologyQuizQuestion(
            id = "q_cardio_valves",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            format = QuizQuestionFormat.SINGLE_CHOICE,
            prompt = LocalizedText(
                en = "During Ventricular Ejection, which heart valves are OPEN to allow blood into the Aorta and Pulmonary Artery?",
                ar = "أثناء مرحلة القذف البطيني، أيّ صمامات القلب تكون مفتوحة للسماح بمرور الدم نحو الأبهر والشريان الرئوي؟"
            ),
            options = listOf(
                LocalizedText("Tricuspid & Mitral Valves (AV valves)", "الصمام ثلاثي الشرفات والتاجي (الصمامات الأذينية البطينية)"),
                LocalizedText("Aortic & Pulmonary Valves (Semilunar valves)", "الصمام الأبهري والصمام الرئوي (الصمامات الهلالية)"),
                LocalizedText("All four heart valves simultaneously", "جميع صمامات القلب الأربعة معاً"),
                LocalizedText("Mitral & Aortic Valves only", "الصمام التاجي والأبهري فقط")
            ),
            correctOptionIndex = 1,
            explanation = LocalizedText(
                en = "Ventricular pressure exceeds arterial pressure during Ventricular Ejection, opening the Aortic and Pulmonary (semilunar) valves while Tricuspid and Mitral valves stay tightly closed.",
                ar = "يتجاوز الضغط البطيني الضغط الشرياني أثناء القذف البطيني فينفتح الصمامان الأبهري والرئوي (الهلاليان) بينما يظل الصمامان ثلاثي الشرفات والتاجي مغلقين."
            ),
            source = PhysiologyReferencesSeed.ahaHeartCycle
        ),

        // 2. IDENTIFY_STRUCTURE
        PhysiologyQuizQuestion(
            id = "q_identify_pacemaker",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            format = QuizQuestionFormat.IDENTIFY_STRUCTURE,
            prompt = LocalizedText(
                en = "Identify the natural pacemaker structure in the Right Atrium that initiates each normal electrical cardiac cycle:",
                ar = "حدّد البنية التي تعمل كمنظم ضربات طبيعي في الأذين الأيمن وتبدأ كل دورة كهربائية قلبية طبيعية:"
            ),
            options = listOf(
                LocalizedText("Sinoatrial (SA) Node", "العقدة الجيبية الأذينية (SA Node)"),
                LocalizedText("Purkinje Fibers", "ألياف بركنجي"),
                LocalizedText("Pulmonary Valve", "الصمام الرئوي"),
                LocalizedText("Interventricular Septum", "الحاجز بين البطينين")
            ),
            correctOptionIndex = 0,
            explanation = LocalizedText(
                en = "The Sinoatrial (SA) Node in the upper right atrium spontaneously depolarizes first, spreading activation across both atria (P wave) before reaching the AV node.",
                ar = "تطلق العقدة الجيبية الأذينية في أعلى الأذين الأيمن الإشارة الكهربائية الأولى فتنتشر عبر الأذينين (موجة P) قبل وصولها للعقدة الأذينية البطينية."
            ),
            source = MedicalReferencesSeed.nihNhlbiHeart
        ),

        // 3. ORDER_EVENTS
        PhysiologyQuizQuestion(
            id = "q_cardio_order",
            systemId = PhysiologySystemId.CARDIOVASCULAR,
            format = QuizQuestionFormat.ORDER_EVENTS,
            prompt = LocalizedText(
                en = "Tap the events in the correct physiological order of the Pulmonary Circulation starting from the Right Ventricle:",
                ar = "انقر على المراحل بالترتيب الفسيولوجي الصحيح للدورة الدموية الرئوية بدءاً من البطين الأيمن:"
            ),
            sequenceItems = listOf(
                LocalizedText("Right Ventricle", "البطين الأيمن"),
                LocalizedText("Pulmonary Artery", "الشريان الرئوي"),
                LocalizedText("Pulmonary Capillaries (Gas Exchange)", "الشعيرات الرئوية (تبادل الغازات)"),
                LocalizedText("Pulmonary Veins → Left Atrium", "الأوردة الرئوية ← الأذين الأيسر")
            ),
            explanation = LocalizedText(
                en = "Deoxygenated blood leaves the Right Ventricle via the Pulmonary Artery, gets oxygenated in Pulmonary Capillaries, and returns through the Pulmonary Veins to the Left Atrium.",
                ar = "يغادر الدم غير المؤكسج البطين الأيمن عبر الشريان الرئوي ليُؤكسج في الشعيرات الرئوية ويعود عبر الأوردة الرئوية إلى الأذين الأيسر."
            ),
            source = MedicalReferencesSeed.nihNhlbiHeart
        ),

        // 4. INTERPRET_DIAGRAM
        PhysiologyQuizQuestion(
            id = "q_resp_gas_exchange",
            systemId = PhysiologySystemId.RESPIRATORY,
            format = QuizQuestionFormat.INTERPRET_DIAGRAM,
            prompt = LocalizedText(
                en = "Interpret the Alveolar Gas Exchange interface: in which directions do O₂ and CO₂ passively diffuse across the alveolar-capillary membrane?",
                ar = "فسّر مخطط تبادل الغازات السنخي: في أي اتجاه ينتشر كل من O₂ و CO₂ سلبياً عبر الغشاء السنخي الشعيري؟"
            ),
            diagramHint = LocalizedText(
                en = "[ Alveolus (High PO₂, Low PCO₂) ]  ⇄  Membrane  ⇄  [ Pulmonary Capillary Blood ]",
                ar = "[ الحويصلة الهوائية (PO₂ مرتفع، PCO₂ منخفض) ] ⇄ الغشاء ⇄ [ دم الشعيرات الرئوية ]"
            ),
            options = listOf(
                LocalizedText("O₂ diffuses Alveolus → Capillary; CO₂ diffuses Capillary → Alveolus", "ينتشر O₂ من السنخ إلى الشعيرة؛ وينتشر CO₂ من الشعيرة إلى السنخ"),
                LocalizedText("Both O₂ and CO₂ diffuse from Alveolus into the Capillary", "ينتشر كل من O₂ و CO₂ من السنخ إلى الشعيرة"),
                LocalizedText("O₂ diffuses Capillary → Alveolus; CO₂ diffuses Alveolus → Capillary", "ينتشر O₂ من الشعيرة إلى السنخ؛ و CO₂ بالعكس"),
                LocalizedText("Gas exchange requires active ATP pumps rather than diffusion", "يعتمد تبادل الغازات على مضخات ATP نشطة بدل الانتشار")
            ),
            correctOptionIndex = 0,
            explanation = LocalizedText(
                en = "Each gas moves passively down its own partial pressure gradient: oxygen enters pulmonary capillary blood while carbon dioxide exits into alveolar air.",
                ar = "يتحرك كل غاز سلبياً وفق تدرج ضغطه الجزئي؛ فيدخل الأكسجين إلى الدم الشعيري ويخرج ثاني أكسيد الكربون نحو هواء الحويصلة."
            ),
            source = PhysiologyReferencesSeed.medlinePlusLungs
        ),

        // 5. SINGLE_CHOICE (Neural)
        PhysiologyQuizQuestion(
            id = "q_neural_myelin",
            systemId = PhysiologySystemId.NEURAL,
            format = QuizQuestionFormat.SINGLE_CHOICE,
            prompt = LocalizedText(
                en = "What is the primary physiological role of the Myelin Sheath and Nodes of Ranvier along a myelinated axon?",
                ar = "ما الوظيفة الفسيولوجية الأساسية لغمد الميالين وعقد رانفييه على طول المحور العصبي؟"
            ),
            options = listOf(
                LocalizedText("Allows rapid regeneration of ionic action potentials at Nodes of Ranvier (saltatory conduction)", "يتيح تجدد جهد الفعل الأيوني بسرعة عند عقد رانفييه (التوصيل القافز)"),
                LocalizedText("Pumps liquid electrical current through a hollow tube", "يضخ تياراً كهربائياً سائلاً داخل أنبوب مجوف"),
                LocalizedText("Stores digestive enzymes for neurotransmitter breakdown", "يخزّن إنزيمات هاضمة لتفكيك النواقل العصبية"),
                LocalizedText("Blocks all communication with postsynaptic receptors", "يمنع التواصل الكامل مع المستقبلات بعد المشبكية")
            ),
            correctOptionIndex = 0,
            explanation = LocalizedText(
                en = "Myelin insulates axonal segments so voltage-gated ion channels regenerate the action potential rapidly from one Node of Ranvier to the next.",
                ar = "يعزل الميالين المقاطع المحورية فتتجدد إزالة الاستقطاب الأيونية بسرعة من عقدة رانفييه إلى التي تليها."
            ),
            source = PhysiologyReferencesSeed.nihNindsBrain
        ),

        // 6. IDENTIFY_STRUCTURE (Renal)
        PhysiologyQuizQuestion(
            id = "q_renal_filtration",
            systemId = PhysiologySystemId.RENAL,
            format = QuizQuestionFormat.IDENTIFY_STRUCTURE,
            prompt = LocalizedText(
                en = "Which nephron structure receives the initial plasma filtrate directly from the Glomerulus?",
                ar = "أيّ بنية في النفرون تستقبل الرشيح البلازمي الأولي مباشرة من الكبيبة؟"
            ),
            options = listOf(
                LocalizedText("Bowman's Capsule", "محفظة بومان"),
                LocalizedText("Collecting Duct", "القناة الجامعة"),
                LocalizedText("Renal Vein", "الوريد الكلوي"),
                LocalizedText("Loop of Henle", "التواء هنلي")
            ),
            correctOptionIndex = 0,
            explanation = LocalizedText(
                en = "Bowman's Capsule surrounds the Glomerulus and captures filtered water and small solutes before directing filtrate into the Proximal Tubule.",
                ar = "تحيط محفظة بومان بالكبيبة وتستقبل الماء والذائبات المرشحة لتوجيهها نحو الأنبوب الملتوي القريب."
            ),
            source = PhysiologyReferencesSeed.nihNiddkKidney
        ),

        // 7. MATCH_BILINGUAL_TERMS (Multi-system EN <-> AR matching)
        PhysiologyQuizQuestion(
            id = "q_gi_bilingual_match",
            systemId = PhysiologySystemId.DIGESTIVE,
            format = QuizQuestionFormat.MATCH_BILINGUAL_TERMS,
            prompt = LocalizedText(
                en = "Match each core English physiological process with its established Arabic medical equivalent:",
                ar = "طابق كل عملية فسيولوجية بالإنجليزية مع مقابلها الطبي العربي المعتمد:"
            ),
            matchPairs = listOf(
                "Cardiac Systole" to "الانقباض القلبي",
                "Pulmonary Ventilation" to "التهوية الرئوية",
                "Glomerular Filtration" to "الترشيح الكبيبي",
                "Peristalsis" to "الحركة الدودية"
            ),
            explanation = LocalizedText(
                en = "Accurate bilingual scientific nomenclature ensures clear communication across Arabic and English medical curricula.",
                ar = "تضمن المصطلحات العلمية الدقيقة ثنائية اللغة تواصلاً واضحاً بين المناهج الطبية العربية والإنجليزية."
            ),
            source = PhysiologyReferencesSeed.ncbiBookshelfPhysiology
        )
    )
}
