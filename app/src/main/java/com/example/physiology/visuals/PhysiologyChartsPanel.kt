package com.example.physiology.visuals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalizedText
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.SimulationModelRegistry
import com.example.physiology.engine.SimulationState
import com.example.physiology.engine.SimulationValidationService
import com.example.physiology.engine.ValveStatus
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

/**
 * Real-time educational charts synchronized with PhysiologySimulationEngine.
 * Explicitly labels schematic graphics (Level C / Level B) and never presents fabricated clinical data.
 */
@Composable
fun PhysiologyChartsPanel(
    simState: SimulationState,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val model = remember(simState.activeSystem) { SimulationModelRegistry.getModel(simState.activeSystem) }
    var selectedEcgWave by remember { mutableIntStateOf(0) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("physiology_charts_panel"),
        shape = RoundedCornerShape(18.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (simState.activeSystem) {
                        PhysiologySystemId.CARDIOVASCULAR -> if (language.isRtl) "تخطيط القلب التخطيطي وحالة الصمامات" else "Schematic ECG & Cardiac Valve Matrix"
                        PhysiologySystemId.RESPIRATORY -> if (language.isRtl) "موجة التهوية التخطيطية (شهيق / زفير)" else "Schematic Ventilation Phase Wave"
                        PhysiologySystemId.NEURAL -> if (language.isRtl) "منحنى جهد الغشاء التخطيطي (مستوى C)" else "Schematic Membrane Potential (Level C)"
                        PhysiologySystemId.RENAL -> if (language.isRtl) "مخطط مراحل النقل في النفرون" else "Nephron Transport Stage Sequence"
                        PhysiologySystemId.DIGESTIVE -> if (language.isRtl) "مخطط حركية وعبور القناة الهضمية" else "GI Motility & Transit Sequence"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedObsidianNavy,
                    border = BorderStroke(1.dp, MedSchematicViolet.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = if (language.isRtl) "رسم تعليمي تخطيطي" else "Schematic Graphic",
                        style = MaterialTheme.typography.labelSmall,
                        color = MedSchematicViolet,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Live Waveform / Phase Curve Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp)
                    .testTag("physiology_chart_canvas")
            ) {
                val w = size.width
                val h = size.height
                // Horizontal reference grid lines
                drawLine(MedBorderHairline, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
                drawLine(MedBorderHairline, Offset(0f, h * 0.55f), Offset(w, h * 0.55f), strokeWidth = 1f)
                drawLine(MedBorderHairline, Offset(0f, h * 0.85f), Offset(w, h * 0.85f), strokeWidth = 1f)

                val curvePath = Path()
                val samples = 120
                for (i in 0..samples) {
                    val t = i.toFloat() / samples
                    val x = t * w
                    val yVal = evaluateEducationalWaveform(simState.activeSystem, t)
                    val y = h * (0.82f - yVal * 0.64f)
                    if (i == 0) curvePath.moveTo(x, y) else curvePath.lineTo(x, y)
                }

                val strokeCol = when (simState.activeSystem) {
                    PhysiologySystemId.CARDIOVASCULAR -> MedVerifiedGreen
                    PhysiologySystemId.RESPIRATORY -> MedCyan
                    PhysiologySystemId.NEURAL -> MedPendingAmber
                    PhysiologySystemId.RENAL -> MedTeal
                    PhysiologySystemId.DIGESTIVE -> MedCyan
                }
                drawPath(curvePath, color = strokeCol, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                // Synchronized Live Playhead Cursor
                val cursorX = simState.cycleProgress * w
                val cursorY = h * (0.82f - evaluateEducationalWaveform(simState.activeSystem, simState.cycleProgress) * 0.64f)
                drawLine(MedCyan.copy(alpha = 0.6f), Offset(cursorX, 0f), Offset(cursorX, h), strokeWidth = 2f)
                drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(cursorX, cursorY))
            }

            // Axis & Non-Diagnostic Notice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language.isRtl) "المحور الأفقي: تقدم الدورة (0% – 100%)" else "X-Axis: Cycle Progress (0% – 100%)",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                    color = MedTextSecondary
                )
                Text(
                    text = "t = ${"%.2f".format(simState.simulationTimeSec)} s",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                    color = MedCyan
                )
            }

            // Cardiovascular-Specific: Live 4-Valve State Matrix + Interactive P / QRS / T Explainer
            if (simState.activeSystem == PhysiologySystemId.CARDIOVASCULAR) {
                val valves = SimulationValidationService.evaluateCardiacValves(simState.cycleProgress)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ValvePill(if (language.isRtl) "ثلاثي الشرفات" else "Tricuspid", valves.tricuspid, language, Modifier.weight(1f))
                    ValvePill(if (language.isRtl) "التاجي" else "Mitral", valves.mitral, language, Modifier.weight(1f))
                    ValvePill(if (language.isRtl) "الرئوي" else "Pulmonary", valves.pulmonary, language, Modifier.weight(1f))
                    ValvePill(if (language.isRtl) "الأبهري" else "Aortic", valves.aortic, language, Modifier.weight(1f))
                }

                // Interactive P Wave / QRS Complex / T Wave Educational Explainer
                val ecgItems = listOf(
                    "P Wave" to LocalizedText(
                        en = "P Wave: Represents atrial depolarization initiated by the SA node preceding atrial systole.",
                        ar = "موجة P: تمثل إزالة استقطاب الأذينين الصادرة من العقدة الجيبية قبيل انقباض الأذينين."
                    ),
                    "QRS Complex" to LocalizedText(
                        en = "QRS Complex: Represents rapid ventricular depolarization across the His-Purkinje system triggering ventricular systole.",
                        ar = "مركب QRS: يمثل إزالة استقطاب البطينين السريعة عبر حزمة هيس وألياف بركنجي لإطلاق الانقباض البطيني."
                    ),
                    "T Wave" to LocalizedText(
                        en = "T Wave: Represents ventricular repolarization as ventricles prepare for relaxation (diastole). Not a clinical patient ECG.",
                        ar = "موجة T: تمثل إعادة استقطاب البطينين استعداداً للانبساط. هذا المخطط تعليمي وليس تخطيط مريض."
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ecgItems.forEachIndexed { idx, (label, _) ->
                        val active = selectedEcgWave == idx
                        Surface(
                            modifier = Modifier.clickable { selectedEcgWave = idx },
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) MedCyanContainer else MedDeepNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (active) MedCyan else Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                Text(
                    text = ecgItems[selectedEcgWave].second.get(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MedTextSecondary
                )
            } else {
                // Phase Sequence Progress Strip for Respiratory, Neural, Renal, Digestive
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    model.phases.forEach { phase ->
                        val active = phase.id == simState.currentPhaseId
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) MedCyanContainer else MedDeepNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = phase.name.get(language).substringBefore(" ").plus(" ").plus(
                                    phase.name.get(language).substringAfter(" ").take(12)
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) MedCyan else MedTextSecondary,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ValvePill(
    name: String,
    status: ValveStatus,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isOpen = status == ValveStatus.OPEN
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isOpen) MedVerifiedContainer else MedDeepNavy,
        border = BorderStroke(1.dp, if (isOpen) MedVerifiedGreen else MedPendingAmber.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = status.label.get(language),
                style = MaterialTheme.typography.labelSmall,
                color = if (isOpen) MedVerifiedGreen else MedPendingAmber,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Generates normalized schematic educational waveforms (0f..1f) synchronized with simulation cycleProgress.
 */
private fun evaluateEducationalWaveform(systemId: PhysiologySystemId, t: Float): Float = when (systemId) {
    PhysiologySystemId.CARDIOVASCULAR -> {
        // Schematic ECG P-QRS-T envelope (illustrative educational waveform)
        val pWave = 0.16f * gaussian(t, 0.08f, 0.025f)
        val qDip = -0.10f * gaussian(t, 0.155f, 0.008f)
        val rPeak = 0.72f * gaussian(t, 0.175f, 0.011f)
        val sDip = -0.16f * gaussian(t, 0.198f, 0.010f)
        val tWave = 0.28f * gaussian(t, 0.44f, 0.045f)
        (0.26f + pWave + qDip + rPeak + sDip + tWave).coerceIn(0.04f, 0.98f)
    }
    PhysiologySystemId.RESPIRATORY -> {
        // Schematic inspiration / expiration thoracic volume contour
        val v = if (t < 0.46f) {
            sin((t / 0.46f) * (PI / 2.0)).toFloat()
        } else {
            sin(((1f - t) / 0.54f) * (PI / 2.0)).toFloat()
        }
        0.12f + 0.78f * v
    }
    PhysiologySystemId.NEURAL -> {
        // Schematic Action Potential (Resting -> Depolarization spike -> Undershoot -> Recovery)
        val spike = 0.86f * gaussian(t, 0.30f, 0.055f)
        val undershoot = -0.14f * gaussian(t, 0.48f, 0.07f)
        (0.24f + spike + undershoot).coerceIn(0.05f, 0.96f)
    }
    PhysiologySystemId.RENAL -> {
        // Schematic filtrate volume reduction across nephron (reabsorption illustration)
        (0.88f - 0.68f * t + 0.08f * sin(t * 10f)).coerceIn(0.12f, 0.94f)
    }
    PhysiologySystemId.DIGESTIVE -> {
        // Schematic rhythmic peristaltic wave envelope
        (0.45f + 0.32f * sin(t * 2f * PI.toFloat() * 3f)).coerceIn(0.10f, 0.90f)
    }
}

private fun gaussian(x: Float, mu: Float, sigma: Float): Float =
    exp(-0.5f * ((x - mu) / sigma).pow(2))
