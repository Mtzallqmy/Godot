package com.example.data.seed

import com.example.core.i18n.LocalizedText
import com.example.core.model.AnatomicalCoordinate
import com.example.core.model.AnatomicalStructure
import com.example.core.model.BodySystem
import com.example.core.model.LearningGuideItem
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ModuleAvailability

object CardiovascularAnatomySeed {

    val bodySystems: List<BodySystem> = listOf(
        BodySystem(
            id = "cardiovascular",
            englishName = "Cardiovascular System",
            arabicName = "الجهاز القلبي الوعائي",
            summary = LocalizedText(
                en = "Heart chambers, great vessels, and interventricular septum powering pulmonary and systemic circulation.",
                ar = "حجرات القلب والأوعية الدموية الكبرى والحاجز بين البطينين المسؤولة عن الدورتين الرئوية والجهازية."
            ),
            availability = ModuleAvailability.ACTIVE_PHASE_1,
            structureCount = 10,
            iconKey = "favorite"
        ),
        BodySystem(
            id = "skeletal",
            englishName = "Skeletal System",
            arabicName = "الجهاز الهيكلي",
            summary = LocalizedText(
                en = "Axial and appendicular bones, articulations, and protective thoracic cage architecture.",
                ar = "العظام المحورية والطرفية والمفاصل والقفص الصدري الواقي للأعضاء الحيوية."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "accessibility"
        ),
        BodySystem(
            id = "muscular",
            englishName = "Muscular System",
            arabicName = "الجهاز العضلي",
            summary = LocalizedText(
                en = "Skeletal, smooth, and cardiac musculature enabling posture, locomotion, and visceral contractility.",
                ar = "العضلات الهيكلية والملساء والقلبية المسؤولة عن الحركة والوضعية والانقباض."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "fitness_center"
        ),
        BodySystem(
            id = "nervous",
            englishName = "Nervous System",
            arabicName = "الجهاز العصبي",
            summary = LocalizedText(
                en = "Central and peripheral neural pathways coordinating sensation, cognition, and autonomic regulation.",
                ar = "الجهاز العصبي المركزي والطرفي المسؤول عن الإحساس والإدراك والتنظيم الذاتي."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "psychology"
        ),
        BodySystem(
            id = "respiratory",
            englishName = "Respiratory System",
            arabicName = "الجهاز التنفسي",
            summary = LocalizedText(
                en = "Conducting airways, bronchial tree, and pulmonary alveoli mediating gas exchange.",
                ar = "الممرات الهوائية والشجرة القصبية والحويصلات الرئوية المسؤولة عن تبادل الغازات."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "air"
        ),
        BodySystem(
            id = "digestive",
            englishName = "Digestive System",
            arabicName = "الجهاز الهضمي",
            summary = LocalizedText(
                en = "Gastrointestinal tract and hepatobiliary organs responsible for digestion and nutrient absorption.",
                ar = "القناة الهضمية والكبد والبنكرياس المسؤولة عن الهضم وامتصاص العناصر الغذائية."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "restaurant"
        ),
        BodySystem(
            id = "urinary",
            englishName = "Urinary System",
            arabicName = "الجهاز البولي",
            summary = LocalizedText(
                en = "Kidneys, ureters, bladder, and urethra maintaining fluid-electrolyte and acid-base balance.",
                ar = "الكليتان والحالبان والمثانة والإحليل للحفاظ على توازن السوائل والأملاح."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "water_drop"
        ),
        BodySystem(
            id = "endocrine",
            englishName = "Endocrine System",
            arabicName = "جهاز الغدد الصماء",
            summary = LocalizedText(
                en = "Hormone-secreting glands governing metabolism, growth, stress response, and homeostasis.",
                ar = "الغدد الصماء المفرزة للهرمونات المنظمة للأيض والنمو والاستتباب الداخلي."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "science"
        ),
        BodySystem(
            id = "reproductive",
            englishName = "Reproductive System",
            arabicName = "الجهاز التناسلي",
            summary = LocalizedText(
                en = "Gonadal and reproductive anatomy supporting gametogenesis and endocrine regulation.",
                ar = "الأعضاء التناسلية المسؤولة عن التكاثر والتنظيم الهرموني المرتبط بها."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "diversity_1"
        ),
        BodySystem(
            id = "integumentary",
            englishName = "Integumentary System",
            arabicName = "الجهاز الغطائي (الجلدي)",
            summary = LocalizedText(
                en = "Epidermis, dermis, and appendages providing barrier protection, sensory reception, and thermoregulation.",
                ar = "الجلد وملحقاته لتوفير الحماية الميكانيكية والإحساس وتنظيم حرارة الجسم."
            ),
            availability = ModuleAvailability.ARCHITECTURE_READY_PHASE_2,
            structureCount = 0,
            iconKey = "layers"
        )
    )

    val cardiovascularStructures: List<AnatomicalStructure> = listOf(
        AnatomicalStructure(
            id = "heart",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "heart_whole",
            englishName = "Heart",
            arabicName = "القلب",
            scientificTerminology = "Cor",
            taCode = "TA2: 3960",
            coordinate = AnatomicalCoordinate(0f, 0f, 0f, -15f, 10f, 4.4f),
            anatomicalLocation = LocalizedText(
                en = "Middle mediastinum of the thoracic cavity, positioned obliquely between the two lungs, posterior to the sternum and superior to the central tendon of the diaphragm.",
                ar = "المنصف الأوسط داخل التجويف الصدري، مائلاً بين الرئتين خلف عظم القص وفوق الوتر المركزي للحجاب الحاجز."
            ),
            generalDescription = LocalizedText(
                en = "A hollow, fibromuscular four-chambered pump enclosed within the pericardial sac that continuously propels blood through the pulmonary and systemic vascular circuits.",
                ar = "مضخة عضلية جوفاء رباعية الحجرات محاطة بغشاء التامور، تضخ الدم باستمرار عبر الدورتين الدمويتين الرئوية والجهازية."
            ),
            structureDetails = LocalizedText(
                en = "Composed of three tissue layers—endocardium (inner lining), myocardium (contractile cardiac muscle), and epicardium (visceral pericardium)—organized around a central fibrous skeleton supporting four unidirectional valves.",
                ar = "يتكون الجدار القلبي من ثلاث طبقات: الشغاف (البطانة الداخلية)، وعضلة القلب (الطبقة المنقبضة)، والنخاب (الطبقة الخارجية)، مدعومة بهيكل ليفي مركزي يحمل الصمامات الأربعة."
            ),
            biologicalFunction = LocalizedText(
                en = "Generates synchronized pressure gradients via its intrinsic electrical conduction system (SA node, AV node, His-Purkinje network) to oxygenate venous blood in the lungs and perfuse systemic organs.",
                ar = "يولّد تدرجات ضغط متزامنة عبر نظام التوصيل الكهربائي الذاتي (العقدة الجيبية والأذينية البطينية وألياف بركنجي) لأكسجة الدم في الرئتين وتروية أنسجة الجسم."
            ),
            relationships = LocalizedText(
                en = "Directly connected to the superior and inferior venae cavae, pulmonary trunk, pulmonary veins, and ascending aorta; flanked laterally by the pleural cavities and phrenic nerves.",
                ar = "يتصل مباشرة بالوريدين الأجوفين العلوي والسفلي، والجذع الرئوي، والأوردة الرئوية، والأبهر الصاعد؛ وتحده جانبياً الرئتان والعصبان الحجابيان."
            ),
            clinicalRelevance = LocalizedText(
                en = "Central focus of cardiology education, including auscultation landmarks, electrocardiography (ECG) axis interpretation, coronary perfusion territories, and structural heart assessment.",
                ar = "محور رئيسي في تعليم طب القلب، ويشمل نقاط التسمع القلبي، وفهم محور تخطيط القلب الكهربائي، وتروية الشرايين التاجية، وتقييم البنية القلبية."
            ),
            relatedStructureIds = listOf("right_atrium", "left_atrium", "right_ventricle", "left_ventricle", "interventricular_septum"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFC24658
        ),
        AnatomicalStructure(
            id = "right_atrium",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "right_atrium",
            englishName = "Right Atrium",
            arabicName = "الأذين الأيمن",
            scientificTerminology = "Atrium dextrum",
            taCode = "TA2: 4035",
            coordinate = AnatomicalCoordinate(-0.62f, 0.38f, 0.12f, -45f, 12f, 3.6f),
            anatomicalLocation = LocalizedText(
                en = "Forms the right superior border of the heart within the middle mediastinum, lying anterior and to the right of the left atrium.",
                ar = "يشكّل الحافة اليمنى العلوية للقلب في المنصف الأوسط، ويقع إلى الأمام وإلى يمين الأذين الأيسر."
            ),
            generalDescription = LocalizedText(
                en = "The thin-walled receiving chamber that collects deoxygenated venous blood returning from the systemic circulation and coronary venous system.",
                ar = "حجرة استقبال رقيقة الجدار تتلقى الدم الوريدي غير المؤكسج العائد من الدورة الجهازية والجيب التاجي للقلب."
            ),
            structureDetails = LocalizedText(
                en = "Contains a smooth posterior wall (sinus venarum) separated by the crista terminalis from an anterior rough-walled region lined with pectinate muscles and the right auricle. Houses the fossa ovalis on the interatrial septum.",
                ar = "يضم جداراً خلفياً أملس (الجيب الوريدي) يفصله العرف الانتهائي عن جزء أمامي يحتوي على العضلات المشطية والأذينة اليمنى، وتظهر الحفرة البيضية على الحاجز بين الأذينين."
            ),
            biologicalFunction = LocalizedText(
                en = "Receives venous return from the superior vena cava, inferior vena cava, and coronary sinus, then delivers blood across the tricuspid valve into the right ventricle. Houses the sinoatrial (SA) node.",
                ar = "يستقبل العود الوريدي من الوريدين الأجوفين العلوي والسفلي والجيب التاجي، ويدفعه عبر الصمام ثلاثي الشرفات نحو البطين الأيمن، كما يحتضن العقدة الجيبية الأذينية."
            ),
            relationships = LocalizedText(
                en = "Continuous superiorly with the superior vena cava, inferiorly with the inferior vena cava, anteroinferiorly with the right ventricle via the tricuspid orifice, and medially with the left atrium.",
                ar = "يتصل علوياً بالوريد الأجوف العلوي، وسفلياً بالوريد الأجوف السفلي، وأمامياً سفلياً بالبطين الأيمن عبر الصمام ثلاثي الشرفات، وإنسياً بالأذين الأيسر."
            ),
            clinicalRelevance = LocalizedText(
                en = "Key anatomical site for central venous catheter tip positioning (cavoatrial junction), SA node pacemaker physiology, and evaluation of right atrial pressure.",
                ar = "موقع تشريحي أساسي لتحديد موضع القسطرة الوريدية المركزية، وفهم منظم ضربات القلب الطبيعي، وتقييم الضغط الوريدي المركزي."
            ),
            relatedStructureIds = listOf("vena_cava", "right_ventricle", "left_atrium", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFF3B82F6
        ),
        AnatomicalStructure(
            id = "left_atrium",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "left_atrium",
            englishName = "Left Atrium",
            arabicName = "الأذين الأيسر",
            scientificTerminology = "Atrium sinistrum",
            taCode = "TA2: 4073",
            coordinate = AnatomicalCoordinate(0.56f, 0.42f, -0.32f, 140f, 15f, 3.6f),
            anatomicalLocation = LocalizedText(
                en = "Forms most of the posterior base of the heart, situated directly anterior to the esophagus and descending thoracic aorta.",
                ar = "يشكّل معظم القاعدة الخلفية للقلب، ويقع مباشرة أمام المريء والأبهر الصدري النازل."
            ),
            generalDescription = LocalizedText(
                en = "The oxygenated-blood receiving chamber linking the pulmonary venous return to the high-pressure left ventricular pumping chamber.",
                ar = "حجرة استقبال الدم المؤكسج القادمة من الرئتين، والتي تغذي البطين الأيسر بالدم الغني بالأكسجين."
            ),
            structureDetails = LocalizedText(
                en = "Features a predominantly smooth interior wall formed by incorporated pulmonary venous tissue, with pectinate muscles confined largely to the tubular left auricle (left atrial appendage).",
                ar = "يتميز بجدار داخلي أملس في معظمه ناتج عن اندماج أصول الأوردة الرئوية، بينما تنحصر العضلات المشطية غالباً داخل الأذينة اليسرى."
            ),
            biologicalFunction = LocalizedText(
                en = "Collects freshly oxygenated blood from the four pulmonary veins and propels it through the mitral (bicuspid) valve into the left ventricle, contributing the final atrial kick during late diastole.",
                ar = "يجمع الدم المؤكسج من الأوردة الرئوية الأربعة ويمرره عبر الصمام التاجي (ثنائي الشرفات) إلى البطين الأيسر معززاً الملء البطيني في نهاية الانبساط."
            ),
            relationships = LocalizedText(
                en = "Receives the right and left superior and inferior pulmonary veins posteriorly; lies adjacent to the esophagus posteriorly, the aortic root anteriorly, and the left ventricle inferiorly.",
                ar = "تصب فيه الأوردة الرئوية الأربعة خلفياً، ويجاور المريء من الخلف، وجذر الأبهر من الأمام، والبطين الأيسر من الأسفل."
            ),
            clinicalRelevance = LocalizedText(
                en = "Its close proximity to the esophagus enables high-resolution transesophageal echocardiography (TEE), and enlargement can mechanically compress adjacent posterior mediastinal structures.",
                ar = "يتيح قربه التشريحي من المريء إجراء تصوير القلب بالموجات فوق الصوتية عبر المريء بدقة عالية، كما أن تضخمه قد يؤثر على البنى الخلفية المجاورة."
            ),
            relatedStructureIds = listOf("pulmonary_veins", "left_ventricle", "right_atrium", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFD94668
        ),
        AnatomicalStructure(
            id = "right_ventricle",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "right_ventricle",
            englishName = "Right Ventricle",
            arabicName = "البطين الأيمن",
            scientificTerminology = "Ventriculus dexter",
            taCode = "TA2: 4052",
            coordinate = AnatomicalCoordinate(-0.34f, -0.35f, 0.34f, -20f, 8f, 3.6f),
            anatomicalLocation = LocalizedText(
                en = "Occupies the majority of the anterior (sternocostal) surface of the heart, lying immediately posterior to the sternum.",
                ar = "يشغل معظم السطح الأمامي (القصي الضلعي) للقلب، ويقع مباشرة خلف عظم القص."
            ),
            generalDescription = LocalizedText(
                en = "A crescent-shaped, moderate-pressure pumping chamber that drives deoxygenated venous blood into the pulmonary arterial circulation.",
                ar = "حجرة ضخ هلالية المقطع ذات ضغط متوسط، تضخ الدم غير المؤكسج نحو الدورة الدموية الرئوية."
            ),
            structureDetails = LocalizedText(
                en = "Has a thinner muscular wall (~3–5 mm) than the left ventricle. Its inflow tract displays coarse trabeculae carneae, three papillary muscles with chordae tendineae, and the moderator band (septomarginal trabecula); its smooth outflow tract is the conus arteriosus (infundibulum).",
                ar = "يتمتع بجدار عضلي أرق (نحو 3–5 ملم) مقارنة بالبطين الأيسر، ويحتوي على ترابيق لحمية وعضلات حليمية وحبال وترية والشريط المعدِّل، ويؤدي مخرجه الأملس (القمع الشرياني) إلى الصمام الرئوي."
            ),
            biologicalFunction = LocalizedText(
                en = "Ejects deoxygenated blood through the pulmonary semilunar valve into the pulmonary trunk against low pulmonary vascular resistance for gas exchange in the lungs.",
                ar = "يقذف الدم غير المؤكسج عبر الصمام الرئوي إلى الجذع الرئوي مقابل مقاومة وعائية رئوية منخفضة لتبادل الغازات في الرئتين."
            ),
            relationships = LocalizedText(
                en = "Separated from the right atrium by the tricuspid valve, from the left ventricle by the interventricular septum, and continuous superiorly with the pulmonary trunk.",
                ar = "يفصله الصمام ثلاثي الشرفات عن الأذين الأيمن، والحاجز بين البطينين عن البطين الأيسر، ويتصل علوياً بالجذع الرئوي."
            ),
            clinicalRelevance = LocalizedText(
                en = "Because it forms the anterior cardiac surface, the right ventricle is vulnerable in penetrating chest trauma and adapts sensitively to elevations in pulmonary arterial pressure.",
                ar = "نظراً لموقعه الأمامي المباشر خلف القص، يُعد البطين الأيمن الأكثر عرضة لإصابات الصدر الأمامية ويتأثر مباشرة بارتفاع ضغط الشريان الرئوي."
            ),
            relatedStructureIds = listOf("right_atrium", "pulmonary_artery", "interventricular_septum", "left_ventricle"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFF4F8FF7
        ),
        AnatomicalStructure(
            id = "left_ventricle",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "left_ventricle",
            englishName = "Left Ventricle",
            arabicName = "البطين الأيسر",
            scientificTerminology = "Ventriculus sinister",
            taCode = "TA2: 4083",
            coordinate = AnatomicalCoordinate(0.42f, -0.40f, 0.14f, 35f, 8f, 3.6f),
            anatomicalLocation = LocalizedText(
                en = "Forms the anatomical apex of the heart, the left pulmonary surface, and a substantial portion of the diaphragmatic surface.",
                ar = "يشكّل قمة القلب التشريحية، والسطح الأيسر، وجزءاً كبيراً من السطح الحجابي للقلب."
            ),
            generalDescription = LocalizedText(
                en = "The thickest, conical, high-pressure muscular chamber responsible for propelling oxygenated blood throughout the entire systemic circulation.",
                ar = "أسمك حجرات القلب جداراً وأعلاها ضغطاً، يتخذ شكلاً مخروطياً ويضخ الدم المؤكسج إلى جميع أنحاء الدورة الجهازية."
            ),
            structureDetails = LocalizedText(
                en = "Its circular cross-section and dense myocardium (~8–12 mm thick, roughly 2–3× thicker than the right ventricle) feature finer trabeculae carneae, two large papillary muscles (anterior and posterior), and a smooth aortic vestibule leading to the aortic valve.",
                ar = "يتميز بمقطع عرضي دائري وجدار عضلي سميك (8–12 ملم تقريباً، أي ضعفي إلى ثلاثة أضعاف سمك البطين الأيمن)، وعضلتين حليميتين كبيرتين، ودهليز أبهري أملس يؤدي إلى الصمام الأبهري."
            ),
            biologicalFunction = LocalizedText(
                en = "Generates high systemic systolic pressure to eject oxygenated blood across the aortic valve into the ascending aorta, sustaining arterial perfusion of the brain, heart, and peripheral organs.",
                ar = "يولّد الضغط الانقباضي الجهازي المرتفع لقذف الدم المؤكسج عبر الصمام الأبهري نحو الشريان الأبهر لضمان تروية الدماغ والقلب وجميع الأعضاء."
            ),
            relationships = LocalizedText(
                en = "Receives blood from the left atrium via the mitral valve, shares the interventricular septum medially with the right ventricle, and opens superiorly into the ascending aorta.",
                ar = "يستقبل الدم من الأذين الأيسر عبر الصمام التاجي، ويشترك إنسياً في الحاجز بين البطينين مع البطين الأيمن، ويفتح علوياً على الأبهر الصاعد."
            ),
            clinicalRelevance = LocalizedText(
                en = "Left ventricular ejection fraction (LVEF), wall thickness, and regional wall motion are core foundational concepts in cardiovascular physiology and echocardiography education.",
                ar = "يمثل الكسر القذفي للبطين الأيسر وسمك جداره وحركة جدرانه مفاهيم أساسية في تعليم فسيولوجيا القلب وتصوير القلب بالصدى."
            ),
            relatedStructureIds = listOf("left_atrium", "aorta", "interventricular_septum", "right_ventricle"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFE14B5A
        ),
        AnatomicalStructure(
            id = "aorta",
            systemId = "cardiovascular",
            organId = "organ_great_vessels",
            organName = LocalizedText("Great Vessels", "الأوعية الدموية الكبرى"),
            meshNodeId = "aorta",
            englishName = "Aorta",
            arabicName = "الشريان الأبهر",
            scientificTerminology = "Aorta",
            taCode = "TA2: 4125",
            coordinate = AnatomicalCoordinate(0.08f, 1.02f, -0.05f, 10f, 18f, 3.8f),
            anatomicalLocation = LocalizedText(
                en = "Originates at the aortic root of the left ventricle, ascends in the middle mediastinum, arches posteriorly and to the left over the left main bronchus in the superior mediastinum, and descends along the thoracic spine.",
                ar = "ينشأ من جذر الأبهر في البطين الأيسر، ويصعد في المنصف الأوسط، ثم يتقوس للخلف واليسار فوق القصبة الرئيسية اليسرى، وينزل بمحاذات العمود الفقري الصدري."
            ),
            generalDescription = LocalizedText(
                en = "The largest elastic artery in the human body, serving as the main conduit distributing oxygenated blood from the left ventricle to all systemic arterial branches.",
                ar = "أكبر شريان مرن في جسم الإنسان، يعمل كقناة رئيسية لتوزيع الدم المؤكسج من البطين الأيسر إلى كافة فروع الشرايين الجهازية."
            ),
            structureDetails = LocalizedText(
                en = "Divided proximally into the ascending aorta (giving off the right and left coronary arteries from the aortic sinuses), the aortic arch (giving rise to the brachiocephalic trunk, left common carotid, and left subclavian arteries), and the descending thoracic aorta. Rich in elastic lamellae within the tunica media.",
                ar = "ينقسم قريباً إلى الأبهر الصاعد (الذي يعطي الشريانين التاجيين الأيمن والأيسر)، وقوس الأبهر (الذي يعطي الجذع العضدي الرأسي والسباتي الأيسر وتحت الترقوة الأيسر)، والأبهر الصدري النازل؛ ويتميز جداره بوفرة الألياف المرنة."
            ),
            biologicalFunction = LocalizedText(
                en = "Expands during ventricular systole and recoils elastically during diastole (Windkessel effect), damping pulse pressure and maintaining continuous diastolic blood flow into systemic and coronary vessels.",
                ar = "يتمدد أثناء انقباض البطين ويرتد بمرونة أثناء الانبساط (تأثير فيندكيسل) لتنظيم ضغط النبض وضمان تدفق مستمر للدم في الشرايين الجهازية والتاجية."
            ),
            relationships = LocalizedText(
                en = "Arises posterior and to the right of the pulmonary trunk; its arch passes superior to the pulmonary artery bifurcation and connects to it via the ligamentum arteriosum.",
                ar = "ينشأ خلف وإلى يمين الجذع الرئوي، ويمر قوسه فوق تفرع الشريان الرئوي ويرتبط به عبر الرباط الشرياني."
            ),
            clinicalRelevance = LocalizedText(
                en = "Understanding the three aortic arch branches, aortic valve sinus origins of the coronary arteries, and thoracic mediastinal contours is essential in radiographic and vascular anatomy.",
                ar = "يعد فهم فروع قوس الأبهر الثلاثة، ومنشأ الشرايين التاجية من جيوب الأبهر، ومعالم الأبهر الشعاعية في المنصف أساساً في التشريح الوعائي."
            ),
            relatedStructureIds = listOf("left_ventricle", "pulmonary_artery", "vena_cava", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFEF4444
        ),
        AnatomicalStructure(
            id = "pulmonary_artery",
            systemId = "cardiovascular",
            organId = "organ_great_vessels",
            organName = LocalizedText("Great Vessels", "الأوعية الدموية الكبرى"),
            meshNodeId = "pulmonary_artery",
            englishName = "Pulmonary Artery",
            arabicName = "الشريان الرئوي",
            scientificTerminology = "Truncus pulmonalis / Arteriae pulmonales",
            taCode = "TA2: 4100",
            coordinate = AnatomicalCoordinate(-0.08f, 0.56f, 0.42f, -12f, 16f, 3.7f),
            anatomicalLocation = LocalizedText(
                en = "Emerges from the conus arteriosus of the right ventricle anterior to the ascending aorta, then ascends obliquely upward and leftward before bifurcating beneath the aortic arch.",
                ar = "يخرج من القمع الشرياني للبطين الأيمن أمام الأبهر الصاعد، ويصعد مائلاً نحو الأعلى واليسار ليتفرع أسفل قوس الأبهر."
            ),
            generalDescription = LocalizedText(
                en = "The great arterial trunk and its left and right branches that carry deoxygenated venous blood away from the right ventricle to the pulmonary capillary beds.",
                ar = "الجذع الشرياني الكبير وفرعاه الأيمن والأيسر اللذان ينقلان الدم غير المؤكسج من البطين الأيمن إلى الشعيرات الدموية في الرئتين."
            ),
            structureDetails = LocalizedText(
                en = "The short, wide pulmonary trunk (~5 cm) divides at the level of the sternal angle into a longer right pulmonary artery (passing posterior to the ascending aorta and SVC) and a shorter left pulmonary artery entering the left lung hilum.",
                ar = "ينقسم الجذع الرئوي القصير والواسع (نحو 5 سم) عند مستوى الزاوية القصية إلى شريان رئوي أيمن أطول يمر خلف الأبهر الصاعد والوريد الأجوف العلوي، وشريان رئوي أيسر أقصر يدخل سرة الرئة اليسرى."
            ),
            biologicalFunction = LocalizedText(
                en = "Delivers the entire cardiac output of the right ventricle to the lungs at low arterial pressure so carbon dioxide can be eliminated and hemoglobin oxygenated.",
                ar = "ينقل كامل النتاج القلبي للبطين الأيمن إلى الرئتين تحت ضغط شرياني منخفض للتخلص من ثاني أكسيد الكربون وأكسجة الدم."
            ),
            relationships = LocalizedText(
                en = "Initially lies anterior to the aortic root, then curves to its left side; connected superiorly to the inferior concave surface of the aortic arch by the ligamentum arteriosum.",
                ar = "يقع في بدايته أمام جذر الأبهر ثم ينحني إلى يساره، ويرتبط علوياً بالسطح السفلي لقوس الأبهر بواسطة الرباط الشرياني."
            ),
            clinicalRelevance = LocalizedText(
                en = "Uniquely illustrates the anatomical definition of an artery (carrying blood away from the heart) despite transporting deoxygenated blood, and forms a critical landmark in cardiopulmonary imaging.",
                ar = "يجسد القاعدة التشريحية لتعريف الشريان (الوعاء الذي ينقل الدم بعيداً عن القلب) رغم حمله دماً غير مؤكسج، ويمثل معلماً رئيسياً في تصوير القلب والرئتين."
            ),
            relatedStructureIds = listOf("right_ventricle", "aorta", "pulmonary_veins", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFF38BDF8
        ),
        AnatomicalStructure(
            id = "pulmonary_veins",
            systemId = "cardiovascular",
            organId = "organ_great_vessels",
            organName = LocalizedText("Great Vessels", "الأوعية الدموية الكبرى"),
            meshNodeId = "pulmonary_veins",
            englishName = "Pulmonary Veins",
            arabicName = "الأوردة الرئوية",
            scientificTerminology = "Venae pulmonales",
            taCode = "TA2: 4722",
            coordinate = AnatomicalCoordinate(0.74f, 0.44f, -0.44f, 155f, 12f, 3.8f),
            anatomicalLocation = LocalizedText(
                en = "Emerge bilaterally from the right and left pulmonary hila and course medially into the posterolateral walls of the left atrium.",
                ar = "تخرج ثنائياً من سرتي الرئتين اليمنى واليسرى وتتجه إنسياً لتصب في الجدار الخلفي الجانبي للأذين الأيسر."
            ),
            generalDescription = LocalizedText(
                en = "Typically four valveless vessels—right and left superior and inferior pulmonary veins—that return freshly oxygenated blood from the lungs to the heart.",
                ar = "أربعة أوعية عادةً دون صمامات (الأوردة الرئوية العلوية والسفلية اليمنى واليسرى) تعيد الدم المؤكسج من الرئتين إلى القلب."
            ),
            structureDetails = LocalizedText(
                en = "Each lung typically drains via one superior and one inferior pulmonary vein. Myocardial sleeves extend briefly from the left atrial wall onto the proximal cuffs of the pulmonary veins.",
                ar = "تصرف كل رئة دمها عادة عبر وريد رئوي علوي وآخر سفلي، وتمتد أكمام عضلية قلبية قصيرة من جدار الأذين الأيسر حول الفوهات القريبة للأوردة الرئوية."
            ),
            biologicalFunction = LocalizedText(
                en = "Complete the pulmonary circuit by channeling oxygen-rich blood directly from alveolar capillary venules into the left atrium for systemic distribution.",
                ar = "تُكمل الدورة الدموية الرئوية بنقل الدم الغني بالأكسجين مباشرة من الشعيرات السنخية إلى الأذين الأيسر تمهيداً لتوزيعه في الجسم."
            ),
            relationships = LocalizedText(
                en = "The right pulmonary veins pass posterior to the right atrium and superior vena cava; the left pulmonary veins pass anterior to the descending thoracic aorta.",
                ar = "تمر الأوردة الرئوية اليمنى خلف الأذين الأيمن والوريد الأجوف العلوي، بينما تمر اليسرى أمام الأبهر الصدري النازل."
            ),
            clinicalRelevance = LocalizedText(
                en = "Key anatomical landmarks in cardiac electrophysiology education because myocardial tissue sleeves at the pulmonary vein ostia are studied in atrial electrical conduction.",
                ar = "معالم تشريحية بارزة في تعليم الفسيولوجيا الكهربائية للقلب نظراً لدور الأكمام العضلية عند فوهات الأوردة الرئوية في التوصيل الكهربائي الأذيني."
            ),
            relatedStructureIds = listOf("left_atrium", "pulmonary_artery", "left_ventricle", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFF472B6
        ),
        AnatomicalStructure(
            id = "vena_cava",
            systemId = "cardiovascular",
            organId = "organ_great_vessels",
            organName = LocalizedText("Great Vessels", "الأوعية الدموية الكبرى"),
            meshNodeId = "vena_cava",
            englishName = "Vena Cava (Superior & Inferior)",
            arabicName = "الوريد الأجوف (العلوي والسفلي)",
            scientificTerminology = "Vena cava superior et Vena cava inferior",
            taCode = "TA2: 4735 / 4920",
            coordinate = AnatomicalCoordinate(-0.74f, 0.86f, -0.05f, -55f, 12f, 3.9f),
            anatomicalLocation = LocalizedText(
                en = "The superior vena cava descends on the right side of the superior and middle mediastinum; the inferior vena cava pierces the diaphragm at T8 to enter the inferoposterior right atrium.",
                ar = "ينزل الوريد الأجوف العلوي على الجانب الأيمن للمنصف العلوي والأوسط، بينما يخترق الوريد الأجوف السفلي الحجاب الحاجز عند مستوى الفقرة الصدرية الثامنة ليصب في أسفل وخلف الأذين الأيمن."
            ),
            generalDescription = LocalizedText(
                en = "The two largest systemic veins in the body, returning low-pressure deoxygenated blood from the upper and lower regions of the body to the right atrium.",
                ar = "أكبر وريدين جهازيين في الجسم، يعيدان الدم غير المؤكسج منخفض الضغط من النصفين العلوي والسفلي للجسم إلى الأذين الأيمن."
            ),
            structureDetails = LocalizedText(
                en = "The SVC forms by union of the right and left brachiocephalic veins and receives the azygos vein; the IVC forms at L5 from the common iliac veins and ascends retroperitoneally alongside the abdominal aorta before its brief intrathoracic segment.",
                ar = "يتشكل الوريد الأجوف العلوي من اتحاد الوريدين العضديين الرأسيين ويستقبل الوريد الفرد، أما السفلي فيتشكل عند الفقرة القطنية الخامسة ويصعد خلف الصفاق بجانب الأبهر البطني."
            ),
            biologicalFunction = LocalizedText(
                en = "Provide high-capacitance, low-resistance venous return pathways that regulate right ventricular preload in coordination with intrathoracic respiratory pressure changes.",
                ar = "يوفران مساراً وريدياً عالي السعة ومنخفض المقاومة للعود الوريدي، مما ينظم الحمل القبلي للبطين الأيمن بالتكامل مع تغيرات الضغط الصدري التنفسي."
            ),
            relationships = LocalizedText(
                en = "The SVC courses immediately right of the ascending aorta and anterior to the right pulmonary hilum; the IVC borders the posterior liver and enters the right atrium near the Eustachian ridge.",
                ar = "يسير الوريد الأجوف العلوي يمين الأبهر الصاعد وأمام سرة الرئة اليمنى، فيما يجاور السفلي السطح الخلفي للكبد ويصب في الأذين الأيمن."
            ),
            clinicalRelevance = LocalizedText(
                en = "Fundamental anatomical references for central venous access, venous return hemodynamics, and inferior vena cava diameter assessment in volume-status education.",
                ar = "مرجعان تشريحيان أساسيان في القسطرة الوريدية المركزية، وديناميكية العود الوريدي، وتقييم قطر الوريد الأجوف السفلي في التعليم الطبي."
            ),
            relatedStructureIds = listOf("right_atrium", "aorta", "right_ventricle", "heart"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFF2563EB
        ),
        AnatomicalStructure(
            id = "interventricular_septum",
            systemId = "cardiovascular",
            organId = "organ_heart",
            organName = LocalizedText("Heart", "القلب"),
            meshNodeId = "interventricular_septum",
            englishName = "Interventricular Septum",
            arabicName = "الحاجز بين البطينين",
            scientificTerminology = "Septum interventriculare",
            taCode = "TA2: 3983",
            coordinate = AnatomicalCoordinate(0.03f, -0.34f, 0.08f, 15f, 14f, 3.4f),
            anatomicalLocation = LocalizedText(
                en = "Internal oblique fibromuscular partition located centrally between the right and left ventricular cavities, bulging slightly into the right ventricle.",
                ar = "جدار ليفي عضلي داخلي مائل يفصل مركزياً بين تجويفي البطين الأيمن والبطين الأيسر، ويميل بتحدبه قليلاً نحو البطين الأيمن."
            ),
            generalDescription = LocalizedText(
                en = "The structural and electrical divider separating oxygenated systemic blood in the left ventricle from deoxygenated pulmonary blood in the right ventricle.",
                ar = "الفاصل البنيوي والكهربائي الذي يمنع اختلاط الدم المؤكسج في البطين الأيسر بالدم غير المؤكسج في البطين الأيمن."
            ),
            structureDetails = LocalizedText(
                en = "Consists of a large thick inferior muscular part (pars muscularis), matching left ventricular thickness, and a small thin superior membranous part (pars membranacea) continuous with the cardiac fibrous skeleton. Contains the bundle of His and right/left bundle branches.",
                ar = "يتألف من جزء عضلي سفلي سميك وكبير يضاهي سمك البطين الأيسر، وجزء غشائي علوي رقيق وصغير متصل بالهيكل الليفي للقلب، وتمر داخله حزمة هيس وفرعاها الأيمن والأيسر."
            ),
            biologicalFunction = LocalizedText(
                en = "Maintains complete separation of the high-pressure systemic and low-pressure pulmonary circuits, contributes mechanically to ventricular contraction, and conducts rapid electrical depolarization toward the cardiac apex.",
                ar = "يحافظ على الفصل التام بين الدورتين الجهازية والرئوية، ويسهم ميكانيكياً في الانقباض البطيني، وينقل إشارات إزالة الاستقطاب الكهربائي بسرعة نحو قمة القلب."
            ),
            relationships = LocalizedText(
                en = "Externally marked by the anterior and posterior interventricular sulci carrying major coronary vessels; superiorly joins the interatrial septum and aortic root.",
                ar = "يُستدل عليه خارجياً بالثلمين بين البطينين الأمامي والخلفي اللذين يحملان الشرايين التاجية، ويتصل علوياً بالحاجز بين الأذينين وجذر الأبهر."
            ),
            clinicalRelevance = LocalizedText(
                en = "Enable X-Ray mode in the 3D viewer to inspect its internal position. Crucial in understanding congenital septal defects (most commonly in the membranous region) and bundle branch conduction blocks.",
                ar = "فعّل وضع الشفافية (X-Ray) في العارض ثلاثي الأبعاد لفحص موقعه الداخلي. يُعد محورياً لفهم عيوب الحاجز الخلقية وحصار فروع الحزمة الكهربائية."
            ),
            relatedStructureIds = listOf("left_ventricle", "right_ventricle", "heart", "aorta"),
            sources = MedicalReferencesSeed.defaultCardiovascularSources,
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL,
            colorHexTag = 0xFFF59E0B
        )
    )

    val learningGuides: List<LearningGuideItem> = listOf(
        LearningGuideItem(
            id = "guide_pulmonary_circuit",
            title = LocalizedText(
                en = "Trace the Pulmonary Circulation Pathway",
                ar = "تتبّع مسار الدورة الدموية الرئوية"
            ),
            subtitle = LocalizedText(
                en = "Follow deoxygenated blood from the Vena Cava through the right heart to the lungs and back to the Left Atrium.",
                ar = "تتبع رحلة الدم غير المؤكسج من الوريد الأجوف عبر الجانب الأيمن للقلب إلى الرئتين وعودته للأذين الأيسر."
            ),
            targetAnatomyId = "right_atrium",
            durationMinutes = 6,
            steps = listOf(
                LocalizedText("1. Vena Cava delivers systemic venous blood into the Right Atrium.", "1. ينقل الوريد الأجوف الدم الوريدي الجهازي إلى الأذين الأيمن."),
                LocalizedText("2. Blood passes through the tricuspid valve into the Right Ventricle.", "2. يعبر الدم عبر الصمام ثلاثي الشرفات إلى البطين الأيمن."),
                LocalizedText("3. Right Ventricle ejects blood into the Pulmonary Artery toward the lungs.", "3. يضخ البطين الأيمن الدم عبر الشريان الرئوي نحو الرئتين."),
                LocalizedText("4. Four Pulmonary Veins return oxygenated blood to the Left Atrium.", "4. تعيد الأوردة الرئوية الأربعة الدم المؤكسج إلى الأذين الأيسر.")
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL
        ),
        LearningGuideItem(
            id = "guide_systemic_circuit",
            title = LocalizedText(
                en = "Systemic High-Pressure Output & Aortic Arch",
                ar = "الضخ الجهازي عالي الضغط وقوس الشريان الأبهر"
            ),
            subtitle = LocalizedText(
                en = "Inspect why the Left Ventricle wall is 2–3× thicker and how the Aorta sustains systemic perfusion.",
                ar = "تعرّف لماذا يبلغ سمك جدار البطين الأيسر ضعفي إلى ثلاثة أضعاف البطين الأيمن وكيف يديم الأبهر التروية."
            ),
            targetAnatomyId = "left_ventricle",
            durationMinutes = 5,
            steps = listOf(
                LocalizedText("1. Left Atrium fills the Left Ventricle across the mitral valve.", "1. يملأ الأذين الأيسر البطين الأيسر عبر الصمام التاجي."),
                LocalizedText("2. Thick left ventricular myocardium generates systemic systolic pressure.", "2. تولّد عضلة البطين الأيسر السميكة الضغط الانقباضي الجهازي."),
                LocalizedText("3. Oxygenated blood enters the ascending Aorta and elastic aortic arch.", "3. يندفع الدم المؤكسج إلى الأبهر الصاعد وقوس الأبهر المرن."),
                LocalizedText("4. Elastic recoil maintains continuous arterial perfusion during diastole.", "4. يحافظ الارتداد المرن للأبهر على استمرارية التدفق أثناء الانبساط.")
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL
        ),
        LearningGuideItem(
            id = "guide_septum_xray",
            title = LocalizedText(
                en = "Internal Septal Architecture in X-Ray Mode",
                ar = "البنية الداخلية للحاجز القلبي بوضع الشفافية"
            ),
            subtitle = LocalizedText(
                en = "Use shell transparency and isolation in the 3D viewer to examine the Interventricular Septum.",
                ar = "استخدم وضع الشفافية وعزل الأعضاء في العارض ثلاثي الأبعاد لفحص الحاجز بين البطينين."
            ),
            targetAnatomyId = "interventricular_septum",
            durationMinutes = 4,
            steps = listOf(
                LocalizedText("1. Select Interventricular Septum in the 3D Anatomy Explorer.", "1. اختر الحاجز بين البطينين من مستكشف التشريح ثلاثي الأبعاد."),
                LocalizedText("2. Toggle X-Ray mode to see through the anterior ventricular shell.", "2. فعّل وضع الشفافية للرؤية عبر الجدار البطيني الأمامي."),
                LocalizedText("3. Distinguish the thick muscular portion from the superior membranous part.", "3. ميّز بين الجزء العضلي السميك والجزء الغشائي العلوي الرقيق.")
            ),
            reviewStatus = MedicalReviewStatus.VERIFIED_EDUCATIONAL
        )
    )
}
