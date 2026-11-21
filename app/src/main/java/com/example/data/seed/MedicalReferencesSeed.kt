package com.example.data.seed

import com.example.core.i18n.LocalizedText
import com.example.core.model.ReferenceSource

object MedicalReferencesSeed {
    val ifaaFipatTa2 = ReferenceSource(
        id = "ref_ifaa_ta2",
        title = "Terminologia Anatomica (TA2) — International Anatomical Terminology",
        organization = "IFAA / FIPAT (Federative International Programme for Anatomical Terminology)",
        url = "https://ta2viewer.openanatomy.org/",
        licenseNote = LocalizedText(
            en = "Standardized international anatomical nomenclature referenced by attribution.",
            ar = "المعيار الدولي للمصطلحات التشريحية المعتمدة بالإشارة المرجعية."
        )
    )

    val nihNhlbiHeart = ReferenceSource(
        id = "ref_nih_nhlbi",
        title = "How the Heart Works — Cardiovascular Anatomy & Chambers",
        organization = "National Heart, Lung, and Blood Institute (NHLBI / NIH)",
        url = "https://www.nhlbi.nih.gov/health/heart",
        licenseNote = LocalizedText(
            en = "U.S. National Institutes of Health public educational resource (linked reference).",
            ar = "مصدر تعليمي عام من المعاهد الوطنية الأمريكية للصحة (مرجع مرتبط)."
        )
    )

    val medlinePlusHeart = ReferenceSource(
        id = "ref_medlineplus",
        title = "Heart Diseases & Anatomy Overview — MedlinePlus",
        organization = "U.S. National Library of Medicine (NLM / NIH)",
        url = "https://medlineplus.gov/heartdiseases.html",
        licenseNote = LocalizedText(
            en = "National Library of Medicine educational reference; linked without copying text.",
            ar = "مرجع تعليمي من المكتبة الوطنية الأمريكية للطب دون نسخ نصوص محمية."
        )
    )

    val msdManualsCardio = ReferenceSource(
        id = "ref_msd_cardio",
        title = "Biology of the Heart & Blood Vessels",
        organization = "MSD Manuals (Professional & Consumer Educational Reference)",
        url = "https://www.msdmanuals.com/home/heart-and-blood-vessel-disorders/biology-of-the-heart-and-blood-vessels",
        licenseNote = LocalizedText(
            en = "Cited educational reference family; link provided for deeper clinical reading.",
            ar = "عائلة مرجعية طبية موثوقة؛ الرابط مرفق للقراءة السريرية المتعمقة."
        )
    )

    val nih3dPortal = ReferenceSource(
        id = "ref_nih_3d",
        title = "NIH 3D — Open Biomedical 3D Model Repository",
        organization = "National Institute of Allergy and Infectious Diseases (NIAID / NIH)",
        url = "https://3d.nih.gov/",
        licenseNote = LocalizedText(
            en = "Recommended scientific source for verified GLB/GLTF anatomical models in Phase 02+.",
            ar = "المصدر العلمي الموصى به لاستيراد النماذج التشريحية ثلاثية الأبعاد الموثقة بصيغة GLB/GLTF."
        )
    )

    val defaultCardiovascularSources = listOf(ifaaFipatTa2, nihNhlbiHeart, medlinePlusHeart, msdManualsCardio)
}
