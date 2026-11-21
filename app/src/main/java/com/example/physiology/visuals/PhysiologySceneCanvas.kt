package com.example.physiology.visuals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.physiology.engine.CirculationFilterMode
import com.example.physiology.engine.PhysiologySystemId
import com.example.physiology.engine.ScientificValidityLevel
import com.example.physiology.engine.SimulationModelRegistry
import com.example.physiology.engine.SimulationState
import com.example.physiology.engine.SimulationValidationService
import com.example.physiology.engine.ValveStatus
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedArterialRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedVenousBlue
import com.example.ui.theme.MedVerifiedGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PhysiologySceneCanvas(
    simState: SimulationState,
    language: AppLanguage,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onToggleLabels: () -> Unit,
    onToggleConduction: () -> Unit,
    onToggleZoomSynapse: () -> Unit,
    onSelectPart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val model = remember(simState.activeSystem) { SimulationModelRegistry.getModel(simState.activeSystem) }
    val scenario = remember(simState.activeScenarioId) { SimulationModelRegistry.findScenario(simState.activeScenarioId) }
    val currentPhase = remember(model, simState.currentPhaseId) {
        model.phases.firstOrNull { it.id == simState.currentPhaseId } ?: model.phases.first()
    }

    var rotYaw by remember(simState.activeSystem) { mutableFloatStateOf(0f) }
    var rotPitch by remember(simState.activeSystem) { mutableFloatStateOf(0f) }
    var zoomScale by remember(simState.activeSystem) { mutableFloatStateOf(1f) }

    val validityAccent = when (scenario.validityLevel) {
        ScientificValidityLevel.LEVEL_A_SCIENTIFICALLY_VALIDATED -> MedVerifiedGreen
        ScientificValidityLevel.LEVEL_B_SCIENTIFICALLY_INFORMED_EDUCATIONAL -> MedCyan
        ScientificValidityLevel.LEVEL_C_ILLUSTRATIVE_ANIMATION -> MedSchematicViolet
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 20.dp))
            .testTag("physiology_scene_viewport"),
        shape = RoundedCornerShape(if (isFullscreen) 0.dp else 20.dp),
        color = Color(0xFF040A17),
        border = if (isFullscreen) null else BorderStroke(1.dp, MedBorderHairline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF0E2344), Color(0xFF040915))
                    )
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("physiology_canvas_surface")
                    .pointerInput(simState.activeSystem) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            rotYaw = (rotYaw + pan.x * 0.25f).coerceIn(-38f, 38f)
                            rotPitch = (rotPitch + pan.y * 0.2f).coerceIn(-25f, 25f)
                            zoomScale = (zoomScale * zoom).coerceIn(0.75f, 1.85f)
                        }
                    }
            ) {
                drawLabCoordinateGrid()

                when (simState.activeSystem) {
                    PhysiologySystemId.CARDIOVASCULAR -> drawCardiovascularPhysiology(
                        simState = simState,
                        yawOffset = rotYaw,
                        pitchOffset = rotPitch,
                        zoom = zoomScale
                    )
                    PhysiologySystemId.RESPIRATORY -> {
                        if (scenario.viewModeTag == "gas_exchange") {
                            drawAlveolarGasExchange(simState = simState, zoom = zoomScale)
                        } else {
                            drawRespiratoryMechanics(simState = simState, zoom = zoomScale)
                        }
                    }
                    PhysiologySystemId.NEURAL -> {
                        if (simState.zoomSynapse || scenario.viewModeTag == "synapse") {
                            drawSynapseCloseUp(simState = simState, zoom = zoomScale)
                        } else {
                            drawNeuronActionPotential(simState = simState, yawOffset = rotYaw, zoom = zoomScale)
                        }
                    }
                    PhysiologySystemId.RENAL -> drawRenalNephronScene(
                        simState = simState,
                        zoom = zoomScale
                    )
                    PhysiologySystemId.DIGESTIVE -> drawDigestiveTractScene(
                        simState = simState,
                        zoom = zoomScale
                    )
                }
            }

            // Top HUD: Scientific Validity Badge + Action Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MedObsidianNavy.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, validityAccent.copy(alpha = 0.7f)),
                    modifier = Modifier.testTag("physiology_validity_badge")
                ) {
                    Text(
                        text = scenario.validityLevel.badgeLabel.get(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = validityAccent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (simState.activeSystem == PhysiologySystemId.CARDIOVASCULAR) {
                        IconButton(
                            onClick = onToggleConduction,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("toggle_conduction_btn"),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (simState.showConductionOverlay) MedPendingAmber else MedObsidianNavy.copy(alpha = 0.85f),
                                contentColor = if (simState.showConductionOverlay) MedObsidianNavy else MedPendingAmber
                            )
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Toggle Electrical Conduction", modifier = Modifier.size(18.dp))
                        }
                    }
                    if (simState.activeSystem == PhysiologySystemId.NEURAL) {
                        IconButton(
                            onClick = onToggleZoomSynapse,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("toggle_synapse_zoom_btn"),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (simState.zoomSynapse) MedCyan else MedObsidianNavy.copy(alpha = 0.85f),
                                contentColor = if (simState.zoomSynapse) MedObsidianNavy else MedCyan
                            )
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom into Synapse", modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(
                        onClick = {
                            rotYaw = 0f
                            rotPitch = 0f
                            zoomScale = 1f
                        },
                        modifier = Modifier.size(34.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedObsidianNavy.copy(alpha = 0.85f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset view", modifier = Modifier.size(17.dp))
                    }
                    IconButton(
                        onClick = onToggleLabels,
                        modifier = Modifier.size(34.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (simState.showLabels) MedCyan.copy(alpha = 0.25f) else MedObsidianNavy.copy(alpha = 0.85f),
                            contentColor = if (simState.showLabels) MedCyan else Color.White
                        )
                    ) {
                        Icon(Icons.Default.Label, contentDescription = "Toggle Labels", modifier = Modifier.size(17.dp))
                    }
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("phys_fullscreen_btn"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedObsidianNavy.copy(alpha = 0.85f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.CloseFullscreen else Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Floating Live Phase & Bilingual Callout Labels
            if (simState.showLabels) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MedObsidianNavy.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = currentPhase.name.get(language),
                            style = MaterialTheme.typography.labelLarge,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentPhase.name.secondary(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Educational Color / Concept Legend on Bottom End
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MedObsidianNavy.copy(alpha = 0.84f),
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (simState.activeSystem) {
                            PhysiologySystemId.CARDIOVASCULAR -> {
                                LegendDot(MedArterialRed, if (language.isRtl) "مؤكسج" else "O₂-Rich")
                                LegendDot(MedVenousBlue, if (language.isRtl) "وريدي*" else "Venous*")
                            }
                            PhysiologySystemId.RESPIRATORY -> {
                                LegendDot(MedCyan, "O₂ →")
                                LegendDot(MedPendingAmber, "← CO₂")
                            }
                            PhysiologySystemId.NEURAL -> {
                                LegendDot(MedPendingAmber, if (language.isRtl) "جهد الفعل" else "Depolarization")
                                LegendDot(MedTeal, if (language.isRtl) "ميالين" else "Myelin")
                            }
                            PhysiologySystemId.RENAL -> {
                                LegendDot(MedArterialRed, if (language.isRtl) "دم" else "Blood")
                                LegendDot(MedPendingAmber, if (language.isRtl) "رشيح" else "Filtrate")
                            }
                            PhysiologySystemId.DIGESTIVE -> {
                                LegendDot(MedTeal, if (language.isRtl) "كيموس" else "Chyme")
                                LegendDot(MedPendingAmber, if (language.isRtl) "إفرازات" else "Bile/Enzymes")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
            color = Color.White
        )
    }
}

private fun DrawScope.drawLabCoordinateGrid() {
    val lineColor = MedCyan.copy(alpha = 0.07f)
    val step = 32.dp.toPx()
    var x = step
    while (x < size.width) {
        drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
        x += step
    }
    var y = step
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += step
    }
}

private fun DrawScope.drawCardiovascularPhysiology(
    simState: SimulationState,
    yawOffset: Float,
    pitchOffset: Float,
    zoom: Float
) {
    val cx = size.width * 0.5f + yawOffset * 1.4f
    val cy = size.height * 0.52f + pitchOffset * 1.1f
    val r = minOf(size.width, size.height) * 0.34f * zoom
    val p = simState.cycleProgress
    val valves = SimulationValidationService.evaluateCardiacValves(p)

    // Subtle, non-exaggerated educational phase contraction cues (Level B/C)
    val atrialPulse = if (p < 0.14f) 1f - 0.06f * sin((p / 0.14f) * PI).toFloat() else 1f
    val ventPulse = if (p in 0.16f..0.50f) 1f - 0.07f * sin(((p - 0.16f) / 0.34f) * PI).toFloat() else 1f

    val showPulmonary = simState.circulationFilter != CirculationFilterMode.SYSTEMIC_ONLY
    val showSystemic = simState.circulationFilter != CirculationFilterMode.PULMONARY_ONLY

    // Great Vessels & Vascular Pathways
    val svcStart = Offset(cx - r * 0.58f, cy - r * 1.18f)
    val raCenter = Offset(cx - r * 0.44f, cy - r * 0.28f)
    val ivcStart = Offset(cx - r * 0.58f, cy + r * 0.98f)
    val rvCenter = Offset(cx - r * 0.24f, cy + r * 0.40f)
    val lvCenter = Offset(cx + r * 0.30f, cy + r * 0.38f)
    val laCenter = Offset(cx + r * 0.46f, cy - r * 0.28f)
    val aortaPeak = Offset(cx + r * 0.04f, cy - r * 1.10f)
    val aortaOut = Offset(cx + r * 0.96f, cy - r * 0.85f)
    val paFork = Offset(cx - r * 0.04f, cy - r * 0.72f)
    val paLeft = Offset(cx - r * 1.05f, cy - r * 0.62f)
    val pvEntry = Offset(cx + r * 1.08f, cy - r * 0.26f)

    // Systemic Vessels (SVC, IVC, Aorta)
    val sysAlpha = if (showSystemic) 0.92f else 0.20f
    drawLine(MedVenousBlue.copy(alpha = sysAlpha), svcStart, raCenter, strokeWidth = r * 0.22f, cap = StrokeCap.Round)
    drawLine(MedVenousBlue.copy(alpha = sysAlpha), ivcStart, raCenter, strokeWidth = r * 0.22f, cap = StrokeCap.Round)

    val aortaPath = Path().apply {
        moveTo(lvCenter.x - r * 0.05f, lvCenter.y - r * 0.25f)
        cubicTo(cx + r * 0.02f, cy - r * 0.85f, aortaPeak.x, aortaPeak.y, aortaOut.x, aortaOut.y)
    }
    drawPath(aortaPath, color = MedArterialRed.copy(alpha = sysAlpha), style = Stroke(width = r * 0.24f, cap = StrokeCap.Round))

    // Pulmonary Vessels (Pulmonary Artery & Pulmonary Veins)
    val pulAlpha = if (showPulmonary) 0.92f else 0.20f
    val paPath = Path().apply {
        moveTo(rvCenter.x + r * 0.04f, rvCenter.y - r * 0.20f)
        quadraticTo(paFork.x, paFork.y, paLeft.x, paLeft.y)
    }
    drawPath(paPath, color = Color(0xFF38BDF8).copy(alpha = pulAlpha), style = Stroke(width = r * 0.20f, cap = StrokeCap.Round))
    drawLine(Color(0xFFF472B6).copy(alpha = pulAlpha), pvEntry, laCenter, strokeWidth = r * 0.17f, cap = StrokeCap.Round)

    // Outer Pericardial Glow Contour
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x33C24658), Color.Transparent),
            center = Offset(cx, cy + r * 0.06f),
            radius = r * 1.25f
        ),
        radius = r * 1.25f,
        center = Offset(cx, cy + r * 0.06f)
    )

    // Four Heart Chambers with Depth Gradient
    val raHighlight = p < 0.12f || p >= 0.58f
    val laHighlight = p < 0.12f || p >= 0.58f
    val ventHighlight = p in 0.12f..0.48f

    drawChamberOrb(raCenter, r * 0.37f * atrialPulse, MedVenousBlue, raHighlight, sysAlpha)
    drawChamberOrb(laCenter, r * 0.36f * atrialPulse, Color(0xFFD94668), laHighlight, pulAlpha)
    drawChamberOrb(rvCenter, r * 0.45f * ventPulse, Color(0xFF3B82F6), ventHighlight, pulAlpha)
    drawChamberOrb(lvCenter, r * 0.50f * ventPulse, MedArterialRed, ventHighlight, sysAlpha)

    // Interventricular Septum
    drawLine(
        color = MedPendingAmber.copy(alpha = 0.85f),
        start = Offset(cx + r * 0.02f, cy - r * 0.06f),
        end = Offset(cx + r * 0.03f, cy + r * 0.78f),
        strokeWidth = r * 0.10f,
        cap = StrokeCap.Round
    )

    // Four Mechanical Valves (Tricuspid, Mitral, Pulmonary, Aortic)
    drawValveIndicator(Offset(cx - r * 0.34f, cy + r * 0.06f), valves.tricuspid, r * 0.11f)
    drawValveIndicator(Offset(cx + r * 0.38f, cy + r * 0.06f), valves.mitral, r * 0.11f)
    drawValveIndicator(Offset(cx - r * 0.14f, cy - r * 0.08f), valves.pulmonary, r * 0.10f)
    drawValveIndicator(Offset(cx + r * 0.16f, cy - r * 0.10f), valves.aortic, r * 0.10f)

    // Guided Directional Blood-Flow Particles along Defined Vascular Paths
    val particleCount = (6 * simState.qualityMode.particleMultiplier).toInt().coerceAtLeast(3)
    if (showSystemic) {
        for (i in 0 until particleCount) {
            val t = (p + i.toFloat() / particleCount) % 1f
            // Systemic Venous Return (SVC -> RA)
            val venousPt = lerpOffset(svcStart, raCenter, t)
            drawCircle(Color(0xFF93C5FD), radius = 4.dp.toPx(), center = venousPt)
            // Systemic Arterial Ejection (LV -> Aorta Arch -> Out)
            val artPt = quadBezier(lvCenter, aortaPeak, aortaOut, t)
            drawCircle(Color(0xFFFFB4BC), radius = 4.5.dp.toPx(), center = artPt)
        }
    }
    if (showPulmonary) {
        for (i in 0 until particleCount) {
            val t = (p + i.toFloat() / particleCount) % 1f
            val paPt = quadBezier(rvCenter, paFork, paLeft, t)
            drawCircle(Color(0xFF7DD3FC), radius = 4.dp.toPx(), center = paPt)
            val pvPt = lerpOffset(pvEntry, laCenter, t)
            drawCircle(Color(0xFFFBCFE8), radius = 4.dp.toPx(), center = pvPt)
        }
    }

    // Optional Cardiac Electrical Conduction Pathway Overlay (SA -> AV -> His -> Branches -> Purkinje)
    if (simState.showConductionOverlay) {
        val saNode = Offset(raCenter.x - r * 0.18f, raCenter.y - r * 0.16f)
        val avNode = Offset(cx - r * 0.06f, cy + r * 0.02f)
        val hisBundle = Offset(cx + r * 0.02f, cy + r * 0.24f)
        val apex = Offset(cx + r * 0.04f, cy + r * 0.72f)
        val leftPurkinje = Offset(lvCenter.x + r * 0.34f, lvCenter.y + r * 0.10f)
        val rightPurkinje = Offset(rvCenter.x - r * 0.32f, rvCenter.y + r * 0.08f)

        val condColor = MedPendingAmber.copy(alpha = 0.75f)
        drawLine(condColor, saNode, avNode, strokeWidth = 3f)
        drawLine(condColor, avNode, hisBundle, strokeWidth = 3.5f)
        drawLine(condColor, hisBundle, apex, strokeWidth = 3f)
        drawLine(condColor, apex, leftPurkinje, strokeWidth = 2.5f)
        drawLine(condColor, apex, rightPurkinje, strokeWidth = 2.5f)

        // Traveling depolarization impulse pulse synchronized with P -> QRS
        val impulsePos = when {
            p < 0.10f -> lerpOffset(saNode, avNode, p / 0.10f)
            p < 0.16f -> lerpOffset(avNode, hisBundle, (p - 0.10f) / 0.06f)
            p < 0.26f -> lerpOffset(hisBundle, apex, (p - 0.16f) / 0.10f)
            p < 0.36f -> lerpOffset(apex, leftPurkinje, (p - 0.26f) / 0.10f)
            else -> null
        }
        drawCircle(MedPendingAmber, radius = 6.dp.toPx(), center = saNode)
        drawCircle(MedPendingAmber, radius = 5.dp.toPx(), center = avNode)
        if (impulsePos != null) {
            drawCircle(Color(0x66FBBF24), radius = 16.dp.toPx(), center = impulsePos)
            drawCircle(Color.White, radius = 5.5.dp.toPx(), center = impulsePos)
        }
    }
}

private fun DrawScope.drawChamberOrb(
    center: Offset,
    radius: Float,
    color: Color,
    isActivePhase: Boolean,
    alphaFactor: Float
) {
    val activeGlow = if (isActivePhase) 1f else 0.78f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alphaFactor * activeGlow),
                color.copy(alpha = alphaFactor * 0.45f)
            ),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius * 1.2f
        ),
        radius = radius,
        center = center
    )
    drawCircle(
        color = if (isActivePhase) MedCyan.copy(alpha = 0.85f * alphaFactor) else Color.White.copy(alpha = 0.22f * alphaFactor),
        radius = radius,
        center = center,
        style = Stroke(width = if (isActivePhase) 3f else 1.5f)
    )
}

private fun DrawScope.drawValveIndicator(center: Offset, status: ValveStatus, radius: Float) {
    val open = status == ValveStatus.OPEN
    drawCircle(
        color = MedObsidianNavy,
        radius = radius * 1.15f,
        center = center
    )
    drawCircle(
        color = if (open) MedVerifiedGreen else MedPendingAmber,
        radius = radius,
        center = center,
        style = Stroke(width = 3f)
    )
    if (open) {
        drawCircle(color = MedVerifiedGreen.copy(alpha = 0.45f), radius = radius * 0.55f, center = center)
    } else {
        drawLine(
            color = MedPendingAmber,
            start = Offset(center.x - radius * 0.65f, center.y),
            end = Offset(center.x + radius * 0.65f, center.y),
            strokeWidth = 3f
        )
    }
}

private fun DrawScope.drawRespiratoryMechanics(simState: SimulationState, zoom: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.48f
    val scale = minOf(size.width, size.height) * 0.36f * zoom
    val p = simState.cycleProgress

    // Smooth educational inspiration (0..0.46) / expiration (0.46..1.0) factor 0f..1f
    val inflation = when {
        p < 0.46f -> sin((p / 0.46f) * (PI / 2.0)).toFloat()
        else -> cos(((p - 0.46f) / 0.54f) * (PI / 2.0)).toFloat()
    }
    val isInspiring = p < 0.46f

    // Thoracic Cage Rib Arc Contour
    val ribWidth = scale * (1.72f + 0.14f * inflation)
    val ribHeight = scale * (1.42f + 0.08f * inflation)
    drawRoundRect(
        color = MedCyan.copy(alpha = 0.16f),
        topLeft = Offset(cx - ribWidth * 0.5f, cy - ribHeight * 0.5f),
        size = Size(ribWidth, ribHeight),
        cornerRadius = CornerRadius(scale * 0.45f, scale * 0.45f),
        style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))
    )

    // Trachea & Main Bronchi
    val tracheaTop = Offset(cx, cy - scale * 0.95f)
    val carina = Offset(cx, cy - scale * 0.32f)
    drawLine(Color(0xFF94A3B8), tracheaTop, carina, strokeWidth = scale * 0.14f, cap = StrokeCap.Round)
    drawLine(Color(0xFF94A3B8), carina, Offset(cx - scale * 0.36f, cy - scale * 0.05f), strokeWidth = scale * 0.09f, cap = StrokeCap.Round)
    drawLine(Color(0xFF94A3B8), carina, Offset(cx + scale * 0.36f, cy - scale * 0.05f), strokeWidth = scale * 0.09f, cap = StrokeCap.Round)

    // Right & Left Lungs (expand gently with inspiration)
    val lungRx = scale * (0.36f + 0.07f * inflation)
    val lungRy = scale * (0.58f + 0.09f * inflation)
    val leftLungCenter = Offset(cx + scale * 0.44f, cy + scale * 0.08f)
    val rightLungCenter = Offset(cx - scale * 0.44f, cy + scale * 0.08f)

    for (lungCenter in listOf(rightLungCenter, leftLungCenter)) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xCC14B8A6), Color(0x550F4C5C)),
                center = lungCenter,
                radius = lungRy * 1.2f
            ),
            topLeft = Offset(lungCenter.x - lungRx, lungCenter.y - lungRy),
            size = Size(lungRx * 2f, lungRy * 2f)
        )
        drawOval(
            color = if (isInspiring) MedCyan else MedTeal.copy(alpha = 0.6f),
            topLeft = Offset(lungCenter.x - lungRx, lungCenter.y - lungRy),
            size = Size(lungRx * 2f, lungRy * 2f),
            style = Stroke(width = 2.5f)
        )
    }

    // Animated Diaphragm Dome (flattens downward during Inspiration, arches upward during Expiration)
    val diaphragmY = cy + scale * (0.76f + 0.16f * inflation)
    val domeLift = scale * (0.28f - 0.17f * inflation)
    val diaphragmPath = Path().apply {
        moveTo(cx - scale * 0.88f, diaphragmY)
        quadraticTo(cx, diaphragmY - domeLift, cx + scale * 0.88f, diaphragmY)
    }
    drawPath(diaphragmPath, color = MedPendingAmber, style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round))

    // Directional Airflow Particles inside Trachea -> Bronchi
    val count = (5 * simState.qualityMode.particleMultiplier).toInt().coerceAtLeast(3)
    for (i in 0 until count) {
        val tRaw = (p * 2.2f + i.toFloat() / count) % 1f
        val t = if (isInspiring) tRaw else 1f - tRaw
        val pt = lerpOffset(tracheaTop, carina, t)
        drawCircle(
            color = if (isInspiring) MedCyan else MedPendingAmber,
            radius = 4.dp.toPx(),
            center = pt
        )
    }
}

private fun DrawScope.drawAlveolarGasExchange(simState: SimulationState, zoom: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.48f
    val r = minOf(size.width, size.height) * 0.28f * zoom
    val p = simState.cycleProgress

    // Alveolar Air Sac (Upper Sphere)
    val alveolusCenter = Offset(cx, cy - r * 0.42f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x5500D1E8), Color(0x220A3747)),
            center = alveolusCenter,
            radius = r * 1.1f
        ),
        radius = r * 0.96f,
        center = alveolusCenter
    )
    drawCircle(MedCyan, radius = r * 0.96f, center = alveolusCenter, style = Stroke(width = 3.dp.toPx()))

    // Thin Alveolar-Capillary Membrane
    val membraneY = cy + r * 0.56f
    drawLine(
        color = Color(0xFF94A3B8),
        start = Offset(cx - r * 1.6f, membraneY),
        end = Offset(cx + r * 1.6f, membraneY),
        strokeWidth = 3.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
    )

    // Pulmonary Capillary Vessel (Lower Channel)
    val capTop = membraneY + 8.dp.toPx()
    val capHeight = r * 0.58f
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(MedVenousBlue.copy(alpha = 0.45f), MedArterialRed.copy(alpha = 0.55f))
        ),
        topLeft = Offset(cx - r * 1.65f, capTop),
        size = Size(r * 3.3f, capHeight),
        cornerRadius = CornerRadius(capHeight * 0.5f, capHeight * 0.5f)
    )

    // Red Blood Cells Flowing Left -> Right Transitioning Venous to Oxygenated
    val rbcCount = (5 * simState.qualityMode.particleMultiplier).toInt().coerceAtLeast(3)
    for (i in 0 until rbcCount) {
        val t = (p + i.toFloat() / rbcCount) % 1f
        val x = cx - r * 1.45f + t * (r * 2.9f)
        val y = capTop + capHeight * 0.5f
        val col = lerpColor(MedVenousBlue, MedArterialRed, t)
        drawOval(
            color = col,
            topLeft = Offset(x - 12.dp.toPx(), y - 7.dp.toPx()),
            size = Size(24.dp.toPx(), 14.dp.toPx())
        )
    }

    // O2 Diffusion Downward (Alveolus -> Capillary) & CO2 Diffusion Upward (Capillary -> Alveolus)
    for (i in 0..2) {
        val t = (p * 1.5f + i * 0.33f) % 1f
        val o2X = cx - r * 0.45f + i * r * 0.36f
        val o2Y = (cy - r * 0.05f) + t * (capTop + capHeight * 0.45f - (cy - r * 0.05f))
        drawCircle(MedCyan, radius = 5.dp.toPx(), center = Offset(o2X, o2Y))

        val co2X = cx - r * 0.25f + i * r * 0.36f
        val co2Y = (capTop + capHeight * 0.45f) - t * (capTop + capHeight * 0.45f - (cy - r * 0.20f))
        drawCircle(MedPendingAmber, radius = 4.5.dp.toPx(), center = Offset(co2X, co2Y))
    }
}

private fun DrawScope.drawNeuronActionPotential(
    simState: SimulationState,
    yawOffset: Float,
    zoom: Float
) {
    val cy = size.height * 0.50f
    val margin = size.width * 0.12f
    val span = (size.width - margin * 2f) * zoom.coerceAtMost(1.25f)
    val startX = size.width * 0.5f - span * 0.5f + yawOffset * 0.8f
    val p = simState.cycleProgress

    val soma = Offset(startX + span * 0.15f, cy)
    val axonEnd = Offset(startX + span * 0.86f, cy)

    // Dendritic Branches
    val dendriteOffsets = listOf(
        Offset(-48f, -52f), Offset(-62f, -14f), Offset(-58f, 28f), Offset(-42f, 56f), Offset(-18f, -64f)
    )
    for (d in dendriteOffsets) {
        drawLine(
            color = MedCyan.copy(alpha = 0.72f),
            start = soma,
            end = Offset(soma.x + d.x, soma.y + d.y),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
    }

    // Cell Body (Soma) & Nucleus
    val somaActive = p < 0.22f
    drawCircle(
        color = if (somaActive) MedPendingAmber.copy(alpha = 0.9f) else MedCyan.copy(alpha = 0.65f),
        radius = 24.dp.toPx(),
        center = soma
    )
    drawCircle(MedObsidianNavy, radius = 9.dp.toPx(), center = soma)

    // Axon Fiber
    drawLine(Color(0xFF94A3B8), soma, axonEnd, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)

    // 4 Insulating Myelin Sheath Segments with Exposed Nodes of Ranvier
    val nodePositions = mutableListOf<Offset>()
    val segmentCount = 4
    val axonStartX = soma.x + 28.dp.toPx()
    val axonLen = (axonEnd.x - axonStartX).coerceAtLeast(60f)
    val segStep = axonLen / segmentCount

    for (i in 0 until segmentCount) {
        val segLeft = axonStartX + i * segStep + 6.dp.toPx()
        val segW = (segStep - 16.dp.toPx()).coerceAtLeast(16f)
        drawRoundRect(
            color = MedTeal.copy(alpha = 0.65f),
            topLeft = Offset(segLeft, cy - 11.dp.toPx()),
            size = Size(segW, 22.dp.toPx()),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        nodePositions += Offset(segLeft + segW + 5.dp.toPx(), cy)
    }

    // Presynaptic Axon Terminal & Postsynaptic Target
    drawCircle(
        color = if (p > 0.64f) MedPendingAmber else MedElectricBlueColor,
        radius = 14.dp.toPx(),
        center = axonEnd
    )
    drawArc(
        color = if (p > 0.80f) MedVerifiedGreen else MedCyan.copy(alpha = 0.6f),
        startAngle = 120f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(axonEnd.x + 14.dp.toPx(), cy - 22.dp.toPx()),
        size = Size(32.dp.toPx(), 44.dp.toPx()),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )

    // Localized Ionic Depolarization Pulse Hopping Across Nodes of Ranvier (Saltatory Conduction)
    if (p in 0.18f..0.72f) {
        val norm = ((p - 0.18f) / 0.54f).coerceIn(0f, 0.999f)
        val nodeIdx = (norm * nodePositions.size).toInt().coerceIn(0, nodePositions.lastIndex)
        val activeNode = nodePositions[nodeIdx]
        drawCircle(MedPendingAmber.copy(alpha = 0.35f), radius = 22.dp.toPx(), center = activeNode)
        drawCircle(Color.White, radius = 6.dp.toPx(), center = activeNode)
    }
}

private fun DrawScope.drawSynapseCloseUp(simState: SimulationState, zoom: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.50f
    val r = minOf(size.width, size.height) * 0.30f * zoom
    val p = simState.cycleProgress

    // Presynaptic Terminal Bulb (Top)
    val preCenter = Offset(cx, cy - r * 0.62f)
    drawCircle(Color(0xFF1D4ED8).copy(alpha = 0.55f), radius = r * 0.88f, center = preCenter)
    drawCircle(MedCyan, radius = r * 0.88f, center = preCenter, style = Stroke(width = 2.5.dp.toPx()))

    // Synaptic Vesicles inside Presynaptic Bulb
    for (i in -1..1) {
        val vOffset = Offset(cx + i * r * 0.38f, preCenter.y + r * 0.32f + (if (p > 0.5f) r * 0.14f else 0f))
        drawCircle(MedPendingAmber.copy(alpha = 0.85f), radius = 10.dp.toPx(), center = vOffset)
    }

    // Postsynaptic Membrane & Receptors (Bottom)
    val postY = cy + r * 0.68f
    drawLine(MedTeal, Offset(cx - r * 1.25f, postY), Offset(cx + r * 1.25f, postY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
    for (i in -2..2) {
        val rx = cx + i * r * 0.44f
        val activated = p > 0.72f
        drawCircle(
            color = if (activated) MedVerifiedGreen else MedCyan,
            radius = 6.dp.toPx(),
            center = Offset(rx, postY - 5.dp.toPx())
        )
    }

    // Neurotransmitter Diffusion Across Synaptic Cleft
    for (i in 0..5) {
        val t = (p * 1.4f + i * 0.16f) % 1f
        val x = cx - r * 0.75f + i * r * 0.30f
        val y = (preCenter.y + r * 0.75f) + t * (postY - 10.dp.toPx() - (preCenter.y + r * 0.75f))
        drawCircle(MedPendingAmber, radius = 3.5.dp.toPx(), center = Offset(x, y))
    }
}

private fun DrawScope.drawRenalNephronScene(simState: SimulationState, zoom: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.50f
    val w = size.width * 0.76f * zoom.coerceAtMost(1.25f)
    val h = size.height * 0.62f * zoom.coerceAtMost(1.25f)
    val left = cx - w * 0.5f
    val top = cy - h * 0.5f
    val p = simState.cycleProgress

    // Key Nephron Landmarks
    val arteryStart = Offset(left, top + h * 0.24f)
    val glomerulus = Offset(left + w * 0.22f, top + h * 0.26f)
    val pctMid = Offset(left + w * 0.44f, top + h * 0.18f)
    val henleBottom = Offset(left + w * 0.52f, top + h * 0.90f)
    val dctMid = Offset(left + w * 0.68f, top + h * 0.24f)
    val collectingTop = Offset(left + w * 0.86f, top + h * 0.18f)
    val collectingBottom = Offset(left + w * 0.86f, top + h * 0.92f)

    // Renal Artery -> Glomerulus
    drawLine(MedArterialRed, arteryStart, glomerulus, strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
    drawCircle(MedArterialRed.copy(alpha = 0.8f), radius = 16.dp.toPx(), center = glomerulus)
    // Bowman's Capsule C-cup
    drawArc(
        color = MedPendingAmber,
        startAngle = -75f,
        sweepAngle = 210f,
        useCenter = false,
        topLeft = Offset(glomerulus.x - 24.dp.toPx(), glomerulus.y - 24.dp.toPx()),
        size = Size(48.dp.toPx(), 48.dp.toPx()),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )

    // Nephron Tubule Path (PCT -> Loop of Henle -> DCT -> Collecting Duct)
    val tubulePath = Path().apply {
        moveTo(glomerulus.x + 22.dp.toPx(), glomerulus.y)
        lineTo(pctMid.x, pctMid.y)
        lineTo(henleBottom.x - 14.dp.toPx(), henleBottom.y)
        quadraticTo(henleBottom.x, henleBottom.y + 18.dp.toPx(), henleBottom.x + 14.dp.toPx(), henleBottom.y)
        lineTo(dctMid.x, dctMid.y)
        lineTo(collectingTop.x, collectingTop.y + 12.dp.toPx())
    }
    drawPath(tubulePath, color = MedPendingAmber.copy(alpha = 0.78f), style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
    drawLine(Color(0xFFFBBF24), collectingTop, collectingBottom, strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)

    // Parallel Peritubular Capillary / Renal Vein Return
    drawLine(
        color = MedVenousBlue.copy(alpha = 0.55f),
        start = Offset(left + w * 0.36f, top + h * 0.58f),
        end = Offset(left + w * 0.74f, top + h * 0.58f),
        strokeWidth = 4.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
    )

    // Moving Filtrate Progress Marker along Nephron
    val waypoints = listOf(glomerulus, pctMid, henleBottom, dctMid, collectingTop, collectingBottom)
    val segPos = p * (waypoints.size - 1)
    val idx = segPos.toInt().coerceIn(0, waypoints.size - 2)
    val frac = segPos - idx
    val marker = lerpOffset(waypoints[idx], waypoints[idx + 1], frac)
    drawCircle(MedCyan.copy(alpha = 0.35f), radius = 14.dp.toPx(), center = marker)
    drawCircle(Color.White, radius = 5.dp.toPx(), center = marker)
}

private fun DrawScope.drawDigestiveTractScene(simState: SimulationState, zoom: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.50f
    val scale = minOf(size.width, size.height) * 0.36f * zoom.coerceAtMost(1.25f)
    val p = simState.cycleProgress

    val esoTop = Offset(cx - scale * 0.08f, cy - scale * 1.05f)
    val stomach = Offset(cx + scale * 0.30f, cy - scale * 0.35f)
    val duodenum = Offset(cx - scale * 0.12f, cy + scale * 0.02f)
    val smallInt = Offset(cx + scale * 0.06f, cy + scale * 0.42f)
    val largeInt = Offset(cx - scale * 0.48f, cy + scale * 0.62f)
    val liverPos = Offset(cx - scale * 0.52f, cy - scale * 0.45f)
    val pancreasPos = Offset(cx + scale * 0.42f, cy + scale * 0.04f)

    // Accessory Organs (Liver, Gallbladder, Pancreas)
    drawOval(
        color = Color(0xAA9A3412),
        topLeft = Offset(liverPos.x - scale * 0.34f, liverPos.y - scale * 0.18f),
        size = Size(scale * 0.68f, scale * 0.36f)
    )
    drawCircle(MedVerifiedGreen.copy(alpha = 0.85f), radius = 8.dp.toPx(), center = Offset(liverPos.x + scale * 0.12f, liverPos.y + scale * 0.18f))
    drawRoundRect(
        color = MedPendingAmber.copy(alpha = 0.55f),
        topLeft = Offset(pancreasPos.x - scale * 0.30f, pancreasPos.y - 8.dp.toPx()),
        size = Size(scale * 0.60f, 16.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )

    // Esophagus -> Stomach -> Duodenum -> Small Intestine -> Large Intestine
    drawLine(Color(0xFF94A3B8), esoTop, Offset(stomach.x - scale * 0.15f, stomach.y - scale * 0.18f), strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)

    // Stomach Pouch (gentle mixing motion during gastric phase)
    val gastricMix = if (p in 0.20f..0.45f) sin((p - 0.20f) * 35f) * 4f else 0f
    drawOval(
        color = Color(0xCCC24658),
        topLeft = Offset(stomach.x - scale * 0.32f + gastricMix, stomach.y - scale * 0.24f),
        size = Size(scale * 0.64f, scale * 0.46f)
    )

    // Coiled Small Intestine & Framing Large Intestine
    val gutPath = Path().apply {
        moveTo(stomach.x - scale * 0.12f, stomach.y + scale * 0.16f)
        lineTo(duodenum.x, duodenum.y)
        cubicTo(cx + scale * 0.55f, cy + scale * 0.26f, cx - scale * 0.45f, cy + scale * 0.48f, smallInt.x, smallInt.y)
        lineTo(largeInt.x, largeInt.y)
    }
    drawPath(gutPath, color = MedTeal.copy(alpha = 0.82f), style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round))

    // Bolus / Chyme Progress Indicator along GI Tract
    val points = listOf(esoTop, stomach, duodenum, smallInt, largeInt)
    val seg = p * (points.size - 1)
    val idx = seg.toInt().coerceIn(0, points.size - 2)
    val pos = lerpOffset(points[idx], points[idx + 1], seg - idx)
    drawCircle(MedCyan.copy(alpha = 0.35f), radius = 15.dp.toPx(), center = pos)
    drawCircle(Color.White, radius = 6.dp.toPx(), center = pos)
}

private val MedElectricBlueColor = Color(0xFF3B82F6)

private fun lerpOffset(a: Offset, b: Offset, t: Float): Offset =
    Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)

private fun quadBezier(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val u = 1f - t
    return Offset(
        u * u * p0.x + 2f * u * t * p1.x + t * t * p2.x,
        u * u * p0.y + 2f * u * t * p1.y + t * t * p2.y
    )
}

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f
)
