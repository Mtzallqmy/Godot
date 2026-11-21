package com.example.engine3d

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterTiltShift
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.AssetProvenanceType
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedVerifiedGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private data class ProjectedTriangle(
    val nodeId: String,
    val ax: Float,
    val ay: Float,
    val bx: Float,
    val by: Float,
    val cx: Float,
    val cy: Float,
    val depth: Float,
    val shadedColor: Color,
    val strokeColor: Color?,
    val isSelected: Boolean
)

@Composable
fun Interactive3DViewport(
    scene: Scene3D?,
    language: AppLanguage,
    selectedNodeId: String?,
    cameraState: Camera3DState,
    lightingConfig: LightingConfig,
    backdrop: ViewportBackdrop,
    isolateSelected: Boolean,
    xrayMode: Boolean,
    panMode: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    isFullscreen: Boolean,
    compactPreview: Boolean = false,
    onCameraChange: (Camera3DState) -> Unit,
    onPresetSelected: (CameraPreset) -> Unit,
    onNodePicked: (String) -> Unit,
    onTogglePanMode: () -> Unit,
    onToggleIsolate: () -> Unit,
    onToggleXray: () -> Unit,
    onLightingChange: (LightingConfig) -> Unit,
    onCycleBackdrop: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onImportGlbClick: () -> Unit,
    onOpenProvenance: () -> Unit,
    onResetDefaultModel: () -> Unit,
    onInspectSelectedDetails: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var viewportSize by remember { mutableStateOf(IntSize(1, 1)) }
    var showLightingPopover by remember { mutableStateOf(false) }

    // Smooth camera interpolation for presets and focus transitions
    val animYaw by animateFloatAsState(
        targetValue = cameraState.yawDeg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "yaw"
    )
    val animPitch by animateFloatAsState(
        targetValue = cameraState.pitchDeg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pitch"
    )
    val animDistance by animateFloatAsState(
        targetValue = cameraState.distance,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "dist"
    )
    val animPanX by animateFloatAsState(
        targetValue = cameraState.panX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "panX"
    )
    val animPanY by animateFloatAsState(
        targetValue = cameraState.panY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "panY"
    )

    val effectiveCamera = Camera3DState(
        yawDeg = animYaw,
        pitchDeg = animPitch,
        distance = animDistance,
        panX = animPanX,
        panY = animPanY
    )

    // Keep latest projected triangles for accurate tap-to-pick hit testing
    val lastProjected = remember { mutableStateOf<List<ProjectedTriangle>>(emptyList()) }
    val selectedPart = remember(scene, selectedNodeId) {
        scene?.parts?.firstOrNull { it.nodeId == selectedNodeId }
    }

    val backdropColors = when (backdrop) {
        ViewportBackdrop.OBSIDIAN_LAB -> listOf(Color(0xFF040914), Color(0xFF0B1B36))
        ViewportBackdrop.NAVY_GRID -> listOf(Color(0xFF061124), Color(0xFF0F2547))
        ViewportBackdrop.CLINICAL_SLATE -> listOf(Color(0xFF141E2E), Color(0xFF213148))
        ViewportBackdrop.STUDIO_LIGHT -> listOf(Color(0xFFE6EEF6), Color(0xFFD2E0EE))
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 20.dp))
            .testTag("viewport_3d_container"),
        shape = RoundedCornerShape(if (isFullscreen) 0.dp else 20.dp),
        color = backdropColors.first(),
        border = if (isFullscreen) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(colors = backdropColors.reversed()))
                .onSizeChanged { viewportSize = it }
        ) {
            // 3D Interactive Rendering Canvas
            if (scene != null && errorMessage == null) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("viewport_3d_canvas")
                        .pointerInput(panMode, cameraState) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val nextDistance = (cameraState.distance / zoom).coerceIn(2.1f, 8.2f)
                                if (panMode || zoom != 1f) {
                                    onCameraChange(
                                        cameraState.copy(
                                            distance = nextDistance,
                                            panX = (cameraState.panX + pan.x).coerceIn(-260f, 260f),
                                            panY = (cameraState.panY + pan.y).coerceIn(-260f, 260f)
                                        )
                                    )
                                } else {
                                    onCameraChange(
                                        cameraState.copy(
                                            yawDeg = cameraState.yawDeg + pan.x * 0.45f,
                                            pitchDeg = (cameraState.pitchDeg + pan.y * 0.45f).coerceIn(-82f, 82f),
                                            distance = nextDistance
                                        )
                                    )
                                }
                            }
                        }
                        .pointerInput(scene, selectedNodeId, isolateSelected) {
                            detectTapGestures { offset ->
                                val hitList = lastProjected.value
                                // Traverse front-to-back (highest index drawn last = front-most)
                                for (i in hitList.lastIndex downTo 0) {
                                    val tri = hitList[i]
                                    if (Projection3D.pointInTriangle2D(
                                            offset.x, offset.y,
                                            tri.ax, tri.ay, tri.bx, tri.by, tri.cx, tri.cy
                                        )
                                    ) {
                                        onNodePicked(tri.nodeId)
                                        break
                                    }
                                }
                            }
                        }
                ) {
                    if (backdrop == ViewportBackdrop.NAVY_GRID) {
                        drawSpatialGrid(isLight = false)
                    } else if (backdrop == ViewportBackdrop.STUDIO_LIGHT) {
                        drawSpatialGrid(isLight = true)
                    }

                    val triangles = projectSceneTriangles(
                        scene = scene,
                        camera = effectiveCamera,
                        lighting = lightingConfig,
                        selectedNodeId = selectedNodeId,
                        isolateSelected = isolateSelected,
                        xrayMode = xrayMode,
                        width = size.width,
                        height = size.height
                    )
                    lastProjected.value = triangles

                    val reusePath = Path()
                    for (tri in triangles) {
                        reusePath.reset()
                        reusePath.moveTo(tri.ax, tri.ay)
                        reusePath.lineTo(tri.bx, tri.by)
                        reusePath.lineTo(tri.cx, tri.cy)
                        reusePath.close()
                        drawPath(path = reusePath, color = tri.shadedColor)
                        if (tri.strokeColor != null) {
                            drawPath(
                                path = reusePath,
                                color = tri.strokeColor,
                                style = Stroke(width = if (tri.isSelected) 1.15f else 0.6f)
                            )
                        }
                    }

                    // Draw live anchor reticle for selected anatomical structure
                    if (selectedPart != null) {
                        val yawRad = (effectiveCamera.yawDeg * PI / 180.0).toFloat()
                        val pitchRad = (effectiveCamera.pitchDeg * PI / 180.0).toFloat()
                        val rotCenter = Projection3D.rotatePoint(selectedPart.centroid, yawRad, pitchRad)
                        val pt = Projection3D.projectToScreen(
                            rotCenter,
                            effectiveCamera.distance,
                            effectiveCamera.panX,
                            effectiveCamera.panY,
                            size.width,
                            size.height
                        )
                        if (pt != null) {
                            drawCircle(
                                color = MedCyan.copy(alpha = 0.25f),
                                radius = 16.dp.toPx(),
                                center = Offset(pt.first, pt.second)
                            )
                            drawCircle(
                                color = MedCyan,
                                radius = 8.dp.toPx(),
                                center = Offset(pt.first, pt.second),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = Offset(pt.first, pt.second)
                            )
                        }
                    }

                    // Draw live 3D anatomical orientation axes triad in corner
                    drawOrientationTriad(effectiveCamera, isLight = backdrop.isLight)
                }
            }

            // Top Overlay Bar: Asset Provenance Pill + Fullscreen / Import Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Provenance Status Badge (Clickable to inspect license & validation)
                val provenance = scene?.metadata?.provenance ?: AssetProvenanceType.SCHEMATIC_EDUCATIONAL_DEMO
                val badgeAccent = when (provenance) {
                    AssetProvenanceType.VERIFIED_NIH_3D_ASSET -> MedVerifiedGreen
                    AssetProvenanceType.USER_IMPORTED_GLB_GLTF -> MedTeal
                    AssetProvenanceType.SCHEMATIC_EDUCATIONAL_DEMO -> MedSchematicViolet
                }
                val badgeLabel = when (provenance) {
                    AssetProvenanceType.VERIFIED_NIH_3D_ASSET -> UiStrings.statusVerified.get(language)
                    AssetProvenanceType.USER_IMPORTED_GLB_GLTF -> UiStrings.statusImportedGlb.get(language)
                    AssetProvenanceType.SCHEMATIC_EDUCATIONAL_DEMO -> UiStrings.statusSchematic.get(language)
                }
                Surface(
                    modifier = Modifier
                        .clickable(onClick = onOpenProvenance)
                        .testTag("viewport_provenance_chip"),
                    shape = RoundedCornerShape(50),
                    color = MedObsidianNavy.copy(alpha = 0.84f),
                    border = BorderStroke(1.dp, badgeAccent.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = UiStrings.toolProvenance.get(language),
                            tint = badgeAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right-side Quick Actions (Import GLB + Fullscreen)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!compactPreview) {
                        Surface(
                            modifier = Modifier
                                .clickable(onClick = onImportGlbClick)
                                .testTag("import_glb_button"),
                            shape = RoundedCornerShape(50),
                            color = MedPanelNavy.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.55f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = UiStrings.toolImportGlb.get(language),
                                    tint = MedCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "GLB",
                                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedCyan
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("viewport_fullscreen_button"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedObsidianNavy.copy(alpha = 0.84f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.CloseFullscreen else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) {
                                UiStrings.toolExitFullscreen.get(language)
                            } else {
                                UiStrings.toolFullscreen.get(language)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Floating Selected Structure Bilingual Callout Card
            if (selectedPart != null && errorMessage == null && !isLoading) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 10.dp, top = 54.dp, end = 64.dp)
                        .clickable(enabled = onInspectSelectedDetails != null) {
                            onInspectSelectedDetails?.invoke()
                        }
                        .testTag("selected_structure_hud"),
                    shape = RoundedCornerShape(12.dp),
                    color = MedObsidianNavy.copy(alpha = 0.86f),
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(selectedPart.baseColor)
                        )
                        Column {
                            Text(
                                text = selectedPart.displayName.get(language),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = selectedPart.displayName.secondary(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (onInspectSelectedDetails != null) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = UiStrings.inspectDetailsCta.get(language),
                                tint = MedCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Zoom +/- Floating Rail on Trailing Edge
            if (!compactPreview && errorMessage == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            onCameraChange(
                                cameraState.copy(distance = (cameraState.distance - 0.5f).coerceAtLeast(2.1f))
                            )
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("viewport_zoom_in"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedObsidianNavy.copy(alpha = 0.84f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom in", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = {
                            onCameraChange(
                                cameraState.copy(distance = (cameraState.distance + 0.5f).coerceAtMost(8.2f))
                            )
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("viewport_zoom_out"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MedObsidianNavy.copy(alpha = 0.84f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom out", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Adjustable Lighting Control Popover
            AnimatedVisibility(
                visible = showLightingPopover && !compactPreview,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 62.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MedDeepNavy.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = UiStrings.toolLighting.get(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "شدة الإضاءة" else "Key Light Intensity",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "${(lightingConfig.keyIntensity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan
                            )
                        }
                        Slider(
                            value = lightingConfig.keyIntensity,
                            onValueChange = { onLightingChange(lightingConfig.copy(keyIntensity = it)) },
                            valueRange = 0.35f..1.35f,
                            colors = SliderDefaults.colors(thumbColor = MedCyan, activeTrackColor = MedCyan)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "زاوية الضوء" else "Light Azimuth",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "${lightingConfig.lightAzimuthDeg.toInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                color = MedCyan
                            )
                        }
                        Slider(
                            value = lightingConfig.lightAzimuthDeg,
                            onValueChange = { onLightingChange(lightingConfig.copy(lightAzimuthDeg = it)) },
                            valueRange = -180f..180f,
                            colors = SliderDefaults.colors(thumbColor = MedTeal, activeTrackColor = MedTeal)
                        )
                    }
                }
            }

            // Bottom Control Dock: Camera Presets + Mode Toggles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (compactPreview) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ViewportControlChip(
                            label = UiStrings.camReset.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.RESET) },
                            tag = "preview_cam_reset"
                        )
                        ViewportControlChip(
                            label = UiStrings.camFront.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.FRONT) },
                            tag = "preview_cam_front"
                        )
                        ViewportControlChip(
                            label = UiStrings.camRear.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.REAR) },
                            tag = "preview_cam_rear"
                        )
                        ViewportControlChip(
                            label = UiStrings.toolXray.get(language),
                            selected = xrayMode,
                            onClick = onToggleXray,
                            tag = "preview_xray_toggle"
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ViewportControlChip(
                            label = UiStrings.camReset.get(language),
                            selected = false,
                            icon = Icons.Default.Refresh,
                            onClick = { onPresetSelected(CameraPreset.RESET) },
                            tag = "cam_preset_reset"
                        )
                        ViewportControlChip(
                            label = UiStrings.camFront.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.FRONT) },
                            tag = "cam_preset_front"
                        )
                        ViewportControlChip(
                            label = UiStrings.camRear.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.REAR) },
                            tag = "cam_preset_rear"
                        )
                        ViewportControlChip(
                            label = UiStrings.camLeft.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.LEFT) },
                            tag = "cam_preset_left"
                        )
                        ViewportControlChip(
                            label = UiStrings.camRight.get(language),
                            selected = false,
                            onClick = { onPresetSelected(CameraPreset.RIGHT) },
                            tag = "cam_preset_right"
                        )
                        ViewportControlChip(
                            label = if (panMode) UiStrings.toolPan.get(language) else UiStrings.toolOrbit.get(language),
                            selected = panMode,
                            icon = if (panMode) Icons.Default.OpenWith else Icons.Default.ViewInAr,
                            onClick = onTogglePanMode,
                            tag = "viewport_pan_orbit_toggle"
                        )
                        ViewportControlChip(
                            label = UiStrings.toolIsolate.get(language),
                            selected = isolateSelected,
                            icon = Icons.Default.CenterFocusStrong,
                            onClick = onToggleIsolate,
                            tag = "viewport_isolate_toggle"
                        )
                        ViewportControlChip(
                            label = UiStrings.toolXray.get(language),
                            selected = xrayMode,
                            icon = Icons.Default.Visibility,
                            onClick = onToggleXray,
                            tag = "viewport_xray_toggle"
                        )
                        ViewportControlChip(
                            label = UiStrings.toolLighting.get(language),
                            selected = showLightingPopover,
                            icon = Icons.Default.LightMode,
                            onClick = { showLightingPopover = !showLightingPopover },
                            tag = "viewport_lighting_toggle"
                        )
                        ViewportControlChip(
                            label = backdrop.label.get(language),
                            selected = false,
                            icon = Icons.Default.Palette,
                            onClick = onCycleBackdrop,
                            tag = "viewport_backdrop_toggle"
                        )
                    }
                }
            }

            // Loading Progress Indicator Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MedObsidianNavy.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = MedCyan, strokeWidth = 3.dp)
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "جارٍ تحميل والتحقق من النموذج ثلاثي الأبعاد..."
                            } else {
                                "Loading & validating 3D anatomical mesh..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }

            // Graceful Rendering / GLB Parse Error Boundary Overlay
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MedObsidianNavy.copy(alpha = 0.92f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MedPanelNavy,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "3D Model Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "تعذّر تحميل النموذج ثلاثي الأبعاد"
                                } else {
                                    "3D Model Validation Error"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = onResetDefaultModel,
                                colors = ButtonDefaults.buttonColors(containerColor = MedCyan)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.ARABIC) {
                                        "استعادة النموذج القلبي الافتراضي"
                                    } else {
                                        "Restore Default Cardiovascular Scene"
                                    },
                                    color = MedObsidianNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewportControlChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MedCyan else MedObsidianNavy.copy(alpha = 0.85f),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MedCyan else Color(0xFF284775)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) MedObsidianNavy else MedCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MedObsidianNavy else Color.White,
                maxLines = 1
            )
        }
    }
}

private fun DrawScope.drawSpatialGrid(isLight: Boolean) {
    val lineColor = if (isLight) {
        Color(0xFF335577).copy(alpha = 0.14f)
    } else {
        MedCyan.copy(alpha = 0.09f)
    }
    val step = 36.dp.toPx()
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

private fun DrawScope.drawOrientationTriad(camera: Camera3DState, isLight: Boolean) {
    val origin = Offset(30.dp.toPx(), size.height - 64.dp.toPx())
    val axisLen = 17.dp.toPx()
    val yawRad = (camera.yawDeg * PI / 180.0).toFloat()
    val pitchRad = (camera.pitchDeg * PI / 180.0).toFloat()

    val axes = listOf(
        Vec3(1f, 0f, 0f) to Color(0xFFEF4444), // X - Left/Right
        Vec3(0f, 1f, 0f) to Color(0xFF10B981), // Y - Superior/Inferior
        Vec3(0f, 0f, 1f) to Color(0xFF38BDF8)  // Z - Anterior/Posterior
    )

    drawCircle(
        color = if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f),
        radius = axisLen + 4.dp.toPx(),
        center = origin
    )
    for ((vec, col) in axes) {
        val rot = Projection3D.rotatePoint(vec, yawRad, pitchRad)
        val end = Offset(origin.x + rot.x * axisLen, origin.y - rot.y * axisLen)
        drawLine(color = col, start = origin, end = end, strokeWidth = 2.2f)
        drawCircle(color = col, radius = 2.5f, center = end)
    }
}

private fun projectSceneTriangles(
    scene: Scene3D,
    camera: Camera3DState,
    lighting: LightingConfig,
    selectedNodeId: String?,
    isolateSelected: Boolean,
    xrayMode: Boolean,
    width: Float,
    height: Float
): List<ProjectedTriangle> {
    val yawRad = (camera.yawDeg * PI / 180.0).toFloat()
    val pitchRad = (camera.pitchDeg * PI / 180.0).toFloat()
    val lightAzRad = (lighting.lightAzimuthDeg * PI / 180.0).toFloat()
    val lightDir = Vec3(sin(lightAzRad) * 0.85f, 0.55f, cos(lightAzRad) * 0.85f).normalized()
    val rimLightDir = Vec3(-0.6f, -0.25f, -0.75f).normalized()
    val viewDir = Vec3(0f, 0f, 1f)

    val out = ArrayList<ProjectedTriangle>(2400)
    val hasActiveFilter = isolateSelected && selectedNodeId != null && selectedNodeId != "heart_whole"

    for (part in scene.parts) {
        val isSelected = selectedNodeId == part.nodeId || selectedNodeId == "heart_whole"
        if (hasActiveFilter && !isSelected) continue

        // Transform all vertices once per part
        val rotatedVerts = Array(part.vertices.size) { idx ->
            Projection3D.rotatePoint(part.vertices[idx], yawRad, pitchRad)
        }
        val screenVerts = Array(part.vertices.size) { idx ->
            Projection3D.projectToScreen(
                rotatedVerts[idx],
                camera.distance,
                camera.panX,
                camera.panY,
                width,
                height
            )
        }

        // Alpha policy: X-Ray renders outer shells translucent so internal septum shines clearly
        val baseAlpha = when {
            part.isInternal && (xrayMode || isSelected) -> 0.96f
            xrayMode && !isSelected -> 0.22f
            xrayMode && isSelected && !part.isInternal -> 0.55f
            selectedNodeId != null && selectedNodeId != "heart_whole" && !isSelected -> 0.42f
            else -> 0.98f
        }

        for (tri in part.triangles) {
            val s0 = screenVerts[tri.i0] ?: continue
            val s1 = screenVerts[tri.i1] ?: continue
            val s2 = screenVerts[tri.i2] ?: continue

            // Screen-space signed area for back-face culling (keep double-sided in X-Ray mode)
            val area2 = (s1.first - s0.first) * (s2.second - s0.second) -
                (s1.second - s0.second) * (s2.first - s0.first)
            if (!xrayMode && area2 >= 0f) continue

            val r0 = rotatedVerts[tri.i0]
            val r1 = rotatedVerts[tri.i1]
            val r2 = rotatedVerts[tri.i2]
            val rotNormal = Projection3D.rotatePoint(tri.faceNormal, yawRad, pitchRad).normalized()

            // Lambertian diffuse + Blinn-Phong specular + subtle scientific cyan rim highlight
            val nDotL = max(0f, rotNormal.dot(lightDir))
            val halfVec = (lightDir + viewDir).normalized()
            val spec = max(0f, rotNormal.dot(halfVec)).pow(22f) * 0.36f * lighting.keyIntensity
            val rim = (1f - max(0f, rotNormal.dot(viewDir))).pow(2.4f) * 0.25f
            val backFill = max(0f, rotNormal.dot(rimLightDir)) * 0.16f

            val diffuse = lighting.ambientFill + nDotL * 0.78f * lighting.keyIntensity + backFill
            val highlightBoost = if (isSelected && selectedNodeId != "heart_whole") 1.18f else 1.0f

            val base = part.baseColor
            val cyanTint = if (isSelected && selectedNodeId != "heart_whole") 0.24f else 0.05f
            val r = min(1f, base.red * diffuse * highlightBoost + spec * 0.85f + rim * 0.08f)
            val g = min(1f, base.green * diffuse * highlightBoost + spec * 0.92f + rim * 0.72f + cyanTint * 0.22f)
            val b = min(1f, base.blue * diffuse * highlightBoost + spec + rim * 0.95f + cyanTint * 0.30f)

            val depth = (r0.z + r1.z + r2.z) * 0.3333f
            val stroke = when {
                isSelected && selectedNodeId != "heart_whole" -> MedCyan.copy(alpha = 0.26f)
                xrayMode -> base.copy(alpha = 0.22f)
                else -> null
            }

            out += ProjectedTriangle(
                nodeId = part.nodeId,
                ax = s0.first,
                ay = s0.second,
                bx = s1.first,
                by = s1.second,
                cx = s2.first,
                cy = s2.second,
                depth = depth,
                shadedColor = Color(r, g, b, baseAlpha),
                strokeColor = stroke,
                isSelected = isSelected && selectedNodeId != "heart_whole"
            )
        }
    }

    // Painter's algorithm: sort back-to-front (smaller Z drawn first)
    out.sortBy { it.depth }
    return out
}
