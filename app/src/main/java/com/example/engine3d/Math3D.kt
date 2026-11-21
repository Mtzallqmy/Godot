package com.example.engine3d

import androidx.compose.ui.graphics.Color
import com.example.core.i18n.LocalizedText
import com.example.core.model.ModelAssetMetadata
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)

    fun dot(o: Vec3): Float = x * o.x + y * o.y + z * o.z

    fun cross(o: Vec3): Vec3 = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalized(): Vec3 {
        val len = length()
        return if (len > 1e-6f) Vec3(x / len, y / len, z / len) else Vec3(0f, 1f, 0f)
    }
}

data class TriangleFace(
    val i0: Int,
    val i1: Int,
    val i2: Int,
    val faceNormal: Vec3
)

data class AnatomicalMeshPart(
    val nodeId: String,
    val displayName: LocalizedText,
    val vertices: List<Vec3>,
    val triangles: List<TriangleFace>,
    val baseColor: Color,
    val centroid: Vec3,
    val isInternal: Boolean = false
)

data class Scene3D(
    val parts: List<AnatomicalMeshPart>,
    val metadata: ModelAssetMetadata
)

enum class CameraPreset {
    RESET,
    FRONT,
    REAR,
    LEFT,
    RIGHT
}

enum class ViewportBackdrop(val label: LocalizedText, val isLight: Boolean) {
    OBSIDIAN_LAB(LocalizedText("Obsidian Lab", "مختبر داكن"), false),
    NAVY_GRID(LocalizedText("Spatial Grid", "شبكة إحداثيات"), false),
    CLINICAL_SLATE(LocalizedText("Clinical Slate", "رمادي سريري"), false),
    STUDIO_LIGHT(LocalizedText("Light Studio", "استوديو مضيء"), true)
}

data class Camera3DState(
    val yawDeg: Float = -18f,
    val pitchDeg: Float = 10f,
    val distance: Float = 4.3f,
    val panX: Float = 0f,
    val panY: Float = 0f
) {
    fun withPreset(preset: CameraPreset): Camera3DState = when (preset) {
        CameraPreset.RESET -> Camera3DState(yawDeg = -18f, pitchDeg = 10f, distance = 4.3f, panX = 0f, panY = 0f)
        CameraPreset.FRONT -> Camera3DState(yawDeg = 0f, pitchDeg = 4f, distance = 4.2f, panX = 0f, panY = 0f)
        CameraPreset.REAR -> Camera3DState(yawDeg = 180f, pitchDeg = 6f, distance = 4.2f, panX = 0f, panY = 0f)
        CameraPreset.LEFT -> Camera3DState(yawDeg = 90f, pitchDeg = 6f, distance = 4.2f, panX = 0f, panY = 0f)
        CameraPreset.RIGHT -> Camera3DState(yawDeg = -90f, pitchDeg = 6f, distance = 4.2f, panX = 0f, panY = 0f)
    }
}

data class LightingConfig(
    val keyIntensity: Float = 0.92f,
    val lightAzimuthDeg: Float = 38f,
    val ambientFill: Float = 0.28f
)

object Projection3D {
    fun rotatePoint(p: Vec3, yawRad: Float, pitchRad: Float): Vec3 {
        val cosY = cos(yawRad)
        val sinY = sin(yawRad)
        val x1 = p.x * cosY + p.z * sinY
        val z1 = -p.x * sinY + p.z * cosY

        val cosP = cos(pitchRad)
        val sinP = sin(pitchRad)
        val y2 = p.y * cosP - z1 * sinP
        val z2 = p.y * sinP + z1 * cosP
        return Vec3(x1, y2, z2)
    }

    fun projectToScreen(
        rotated: Vec3,
        cameraDistance: Float,
        panX: Float,
        panY: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ): Pair<Float, Float>? {
        val zDepth = cameraDistance - rotated.z
        if (zDepth < 0.35f) return null
        val focal = minOf(canvasWidth, canvasHeight) * 0.92f
        val scale = focal / zDepth
        val sx = canvasWidth * 0.5f + panX + rotated.x * scale
        val sy = canvasHeight * 0.5f + panY - rotated.y * scale
        return sx to sy
    }

    fun pointInTriangle2D(
        px: Float,
        py: Float,
        ax: Float,
        ay: Float,
        bx: Float,
        by: Float,
        cx: Float,
        cy: Float
    ): Boolean {
        val v0x = cx - ax
        val v0y = cy - ay
        val v1x = bx - ax
        val v1y = by - ay
        val v2x = px - ax
        val v2y = py - ay

        val dot00 = v0x * v0x + v0y * v0y
        val dot01 = v0x * v1x + v0y * v1y
        val dot02 = v0x * v2x + v0y * v2y
        val dot11 = v1x * v1x + v1y * v1y
        val dot12 = v1x * v2x + v1y * v2y

        val denom = dot00 * dot11 - dot01 * dot01
        if (kotlin.math.abs(denom) < 1e-6f) return false
        val invDenom = 1f / denom
        val u = (dot11 * dot02 - dot01 * dot12) * invDenom
        val v = (dot00 * dot12 - dot01 * dot02) * invDenom
        return u >= -0.02f && v >= -0.02f && (u + v) <= 1.02f
    }
}
