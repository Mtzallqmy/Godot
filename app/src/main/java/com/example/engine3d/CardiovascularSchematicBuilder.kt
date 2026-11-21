package com.example.engine3d

import androidx.compose.ui.graphics.Color
import com.example.core.i18n.LocalizedText
import com.example.data.repository.LocalAssetProvenanceRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generates a clearly labeled, anatomically positioned 3D Cardiovascular Schematic Assembly
 * for Phase 01 educational demonstration. Every mesh node maps 1:1 to CardiovascularAnatomySeed.
 */
object CardiovascularSchematicBuilder {

    fun buildDefaultScene(provenanceRepository: LocalAssetProvenanceRepository): Scene3D {
        val parts = mutableListOf<AnatomicalMeshPart>()

        // 1. Left Ventricle (thick conical apex-forming left lower chamber)
        parts += buildTaperedChamber(
            nodeId = "left_ventricle",
            displayName = LocalizedText("Left Ventricle", "البطين الأيسر"),
            center = Vec3(0.36f, -0.36f, 0.06f),
            rx = 0.44f,
            ry = 0.64f,
            rz = 0.44f,
            tiltZDeg = -18f,
            taperBottom = 0.42f,
            color = Color(0xFFD84355)
        )

        // 2. Right Ventricle (anterior-right crescent-tapered lower chamber)
        parts += buildTaperedChamber(
            nodeId = "right_ventricle",
            displayName = LocalizedText("Right Ventricle", "البطين الأيمن"),
            center = Vec3(-0.28f, -0.28f, 0.24f),
            rx = 0.42f,
            ry = 0.56f,
            rz = 0.36f,
            tiltZDeg = -14f,
            taperBottom = 0.52f,
            color = Color(0xFF427BE0)
        )

        // 3. Interventricular Septum (internal oblique partition between LV & RV)
        parts += buildRoundedPlate(
            nodeId = "interventricular_septum",
            displayName = LocalizedText("Interventricular Septum", "الحاجز بين البطينين"),
            center = Vec3(0.04f, -0.32f, 0.12f),
            rx = 0.11f,
            ry = 0.56f,
            rz = 0.36f,
            tiltZDeg = -16f,
            color = Color(0xFFF59E0B),
            isInternal = true
        )

        // 4. Right Atrium (superior-right receiving chamber)
        parts += buildTaperedChamber(
            nodeId = "right_atrium",
            displayName = LocalizedText("Right Atrium", "الأذين الأيمن"),
            center = Vec3(-0.62f, 0.34f, 0.08f),
            rx = 0.36f,
            ry = 0.38f,
            rz = 0.34f,
            tiltZDeg = 8f,
            taperBottom = 0.88f,
            color = Color(0xFF2F6EDB)
        )

        // 5. Left Atrium (posterior superior-left oxygenated chamber)
        parts += buildTaperedChamber(
            nodeId = "left_atrium",
            displayName = LocalizedText("Left Atrium", "الأذين الأيسر"),
            center = Vec3(0.46f, 0.38f, -0.26f),
            rx = 0.38f,
            ry = 0.33f,
            rz = 0.32f,
            tiltZDeg = -6f,
            taperBottom = 0.90f,
            color = Color(0xFFC94468)
        )

        // 6. Aorta (ascending, arch, descending thoracic + 3 superior arch branches)
        parts += buildCombinedTubes(
            nodeId = "aorta",
            displayName = LocalizedText("Aorta", "الشريان الأبهر"),
            tubes = listOf(
                TubeSpec(
                    points = listOf(
                        Vec3(0.10f, 0.12f, 0.02f),
                        Vec3(0.02f, 0.58f, 0.06f),
                        Vec3(-0.08f, 0.96f, 0.04f),
                        Vec3(0.14f, 1.18f, -0.10f),
                        Vec3(0.36f, 1.04f, -0.32f),
                        Vec3(0.30f, 0.45f, -0.46f),
                        Vec3(0.24f, -0.35f, -0.48f),
                        Vec3(0.20f, -1.05f, -0.46f)
                    ),
                    radius = 0.165f,
                    radialSegments = 10
                ),
                // Brachiocephalic trunk
                TubeSpec(
                    points = listOf(
                        Vec3(-0.04f, 1.06f, 0.00f),
                        Vec3(-0.18f, 1.46f, 0.02f)
                    ),
                    radius = 0.072f,
                    radialSegments = 8
                ),
                // Left common carotid artery
                TubeSpec(
                    points = listOf(
                        Vec3(0.10f, 1.16f, -0.08f),
                        Vec3(0.08f, 1.54f, -0.09f)
                    ),
                    radius = 0.058f,
                    radialSegments = 8
                ),
                // Left subclavian artery
                TubeSpec(
                    points = listOf(
                        Vec3(0.25f, 1.12f, -0.20f),
                        Vec3(0.34f, 1.48f, -0.22f)
                    ),
                    radius = 0.062f,
                    radialSegments = 8
                )
            ),
            color = Color(0xFFEF4444)
        )

        // 7. Pulmonary Artery (trunk emerging anteriorly from RV + left & right branches)
        parts += buildCombinedTubes(
            nodeId = "pulmonary_artery",
            displayName = LocalizedText("Pulmonary Artery", "الشريان الرئوي"),
            tubes = listOf(
                // Pulmonary trunk
                TubeSpec(
                    points = listOf(
                        Vec3(-0.22f, 0.08f, 0.36f),
                        Vec3(-0.12f, 0.44f, 0.38f),
                        Vec3(0.08f, 0.72f, 0.24f)
                    ),
                    radius = 0.145f,
                    radialSegments = 10
                ),
                // Left pulmonary artery
                TubeSpec(
                    points = listOf(
                        Vec3(0.06f, 0.72f, 0.22f),
                        Vec3(0.54f, 0.74f, 0.02f),
                        Vec3(0.95f, 0.72f, -0.10f)
                    ),
                    radius = 0.105f,
                    radialSegments = 8
                ),
                // Right pulmonary artery passing posterior to ascending aorta
                TubeSpec(
                    points = listOf(
                        Vec3(0.02f, 0.70f, 0.18f),
                        Vec3(-0.42f, 0.68f, -0.08f),
                        Vec3(-0.92f, 0.66f, -0.14f)
                    ),
                    radius = 0.105f,
                    radialSegments = 8
                )
            ),
            color = Color(0xFF38BDF8)
        )

        // 8. Pulmonary Veins (4 vessels entering posterior Left Atrium)
        parts += buildCombinedTubes(
            nodeId = "pulmonary_veins",
            displayName = LocalizedText("Pulmonary Veins", "الأوردة الرئوية"),
            tubes = listOf(
                // Left superior PV
                TubeSpec(
                    points = listOf(Vec3(0.68f, 0.48f, -0.28f), Vec3(1.16f, 0.56f, -0.32f)),
                    radius = 0.072f,
                    radialSegments = 8
                ),
                // Left inferior PV
                TubeSpec(
                    points = listOf(Vec3(0.66f, 0.26f, -0.30f), Vec3(1.14f, 0.20f, -0.36f)),
                    radius = 0.070f,
                    radialSegments = 8
                ),
                // Right superior PV
                TubeSpec(
                    points = listOf(Vec3(0.24f, 0.44f, -0.34f), Vec3(-0.46f, 0.46f, -0.40f), Vec3(-0.98f, 0.48f, -0.36f)),
                    radius = 0.068f,
                    radialSegments = 8
                ),
                // Right inferior PV
                TubeSpec(
                    points = listOf(Vec3(0.24f, 0.24f, -0.34f), Vec3(-0.46f, 0.22f, -0.40f), Vec3(-0.96f, 0.18f, -0.36f)),
                    radius = 0.068f,
                    radialSegments = 8
                )
            ),
            color = Color(0xFFF472B6)
        )

        // 9. Vena Cava (Superior Vena Cava + Inferior Vena Cava into Right Atrium)
        parts += buildCombinedTubes(
            nodeId = "vena_cava",
            displayName = LocalizedText("Vena Cava", "الوريد الأجوف"),
            tubes = listOf(
                // Superior Vena Cava
                TubeSpec(
                    points = listOf(
                        Vec3(-0.64f, 0.56f, 0.02f),
                        Vec3(-0.66f, 1.02f, -0.02f),
                        Vec3(-0.68f, 1.44f, -0.04f)
                    ),
                    radius = 0.135f,
                    radialSegments = 10
                ),
                // Inferior Vena Cava
                TubeSpec(
                    points = listOf(
                        Vec3(-0.60f, 0.12f, -0.10f),
                        Vec3(-0.62f, -0.52f, -0.18f),
                        Vec3(-0.62f, -1.06f, -0.22f)
                    ),
                    radius = 0.145f,
                    radialSegments = 10
                )
            ),
            color = Color(0xFF2563EB)
        )

        val totalVerts = parts.sumOf { it.vertices.size }
        val totalTris = parts.sumOf { it.triangles.size }
        val metadata = provenanceRepository.getDefaultSchematicMetadata(totalVerts, totalTris)
        return Scene3D(parts = parts, metadata = metadata)
    }

    private data class TubeSpec(
        val points: List<Vec3>,
        val radius: Float,
        val radialSegments: Int
    )

    private fun buildTaperedChamber(
        nodeId: String,
        displayName: LocalizedText,
        center: Vec3,
        rx: Float,
        ry: Float,
        rz: Float,
        tiltZDeg: Float,
        taperBottom: Float,
        color: Color,
        latSteps: Int = 10,
        lonSteps: Int = 14
    ): AnatomicalMeshPart {
        val vertices = mutableListOf<Vec3>()
        val triangles = mutableListOf<TriangleFace>()
        val tiltRad = (tiltZDeg * PI / 180.0).toFloat()
        val cosT = cos(tiltRad)
        val sinT = sin(tiltRad)

        for (lat in 0..latSteps) {
            val v = lat.toFloat() / latSteps
            val theta = v * PI.toFloat() // 0 (top) .. PI (bottom)
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            // Taper lower half toward cardiac apex
            val taper = if (cosTheta < 0f) 1f - (1f - taperBottom) * (-cosTheta) else 1f
            for (lon in 0 until lonSteps) {
                val phi = (lon.toFloat() / lonSteps) * (2.0 * PI).toFloat()
                val lx = rx * taper * sinTheta * cos(phi)
                val ly = ry * cosTheta
                val lz = rz * taper * sinTheta * sin(phi)
                val rotX = lx * cosT - ly * sinT
                val rotY = lx * sinT + ly * cosT
                vertices += Vec3(center.x + rotX, center.y + rotY, center.z + lz)
            }
        }

        for (lat in 0 until latSteps) {
            for (lon in 0 until lonSteps) {
                val nextLon = (lon + 1) % lonSteps
                val a = lat * lonSteps + lon
                val b = lat * lonSteps + nextLon
                val c = (lat + 1) * lonSteps + lon
                val d = (lat + 1) * lonSteps + nextLon
                addTriangleIfValid(vertices, triangles, a, c, b)
                addTriangleIfValid(vertices, triangles, b, c, d)
            }
        }

        return AnatomicalMeshPart(
            nodeId = nodeId,
            displayName = displayName,
            vertices = vertices,
            triangles = triangles,
            baseColor = color,
            centroid = center
        )
    }

    private fun buildRoundedPlate(
        nodeId: String,
        displayName: LocalizedText,
        center: Vec3,
        rx: Float,
        ry: Float,
        rz: Float,
        tiltZDeg: Float,
        color: Color,
        isInternal: Boolean
    ): AnatomicalMeshPart {
        val chamber = buildTaperedChamber(
            nodeId = nodeId,
            displayName = displayName,
            center = center,
            rx = rx,
            ry = ry,
            rz = rz,
            tiltZDeg = tiltZDeg,
            taperBottom = 0.65f,
            color = color,
            latSteps = 8,
            lonSteps = 12
        )
        return chamber.copy(isInternal = isInternal)
    }

    private fun buildCombinedTubes(
        nodeId: String,
        displayName: LocalizedText,
        tubes: List<TubeSpec>,
        color: Color
    ): AnatomicalMeshPart {
        val vertices = mutableListOf<Vec3>()
        val triangles = mutableListOf<TriangleFace>()

        for (tube in tubes) {
            val spine = subdivideSpine(tube.points, subdivisionsPerSegment = 2)
            if (spine.size < 2) continue
            val baseIndex = vertices.size
            val segs = tube.radialSegments

            for (i in spine.indices) {
                val p = spine[i]
                val tangent = when (i) {
                    0 -> (spine[1] - spine[0]).normalized()
                    spine.lastIndex -> (spine[i] - spine[i - 1]).normalized()
                    else -> (spine[i + 1] - spine[i - 1]).normalized()
                }
                val refUp = if (kotlin.math.abs(tangent.y) < 0.9f) Vec3(0f, 1f, 0f) else Vec3(1f, 0f, 0f)
                val right = tangent.cross(refUp).normalized()
                val up = right.cross(tangent).normalized()

                for (s in 0 until segs) {
                    val angle = (s.toFloat() / segs) * (2.0 * PI).toFloat()
                    val offset = (right * cos(angle) + up * sin(angle)) * tube.radius
                    vertices += p + offset
                }
            }

            for (i in 0 until spine.lastIndex) {
                val ringA = baseIndex + i * segs
                val ringB = baseIndex + (i + 1) * segs
                for (s in 0 until segs) {
                    val nextS = (s + 1) % segs
                    val a = ringA + s
                    val b = ringA + nextS
                    val c = ringB + s
                    val d = ringB + nextS
                    addTriangleIfValid(vertices, triangles, a, b, c)
                    addTriangleIfValid(vertices, triangles, b, d, c)
                }
            }

            // Cap start and end of vessel cleanly
            val startCenterIdx = vertices.size
            vertices += spine.first()
            val endCenterIdx = vertices.size
            vertices += spine.last()
            val firstRing = baseIndex
            val lastRing = baseIndex + spine.lastIndex * segs
            for (s in 0 until segs) {
                val nextS = (s + 1) % segs
                addTriangleIfValid(vertices, triangles, startCenterIdx, firstRing + nextS, firstRing + s)
                addTriangleIfValid(vertices, triangles, endCenterIdx, lastRing + s, lastRing + nextS)
            }
        }

        val centroid = if (vertices.isEmpty()) {
            Vec3(0f, 0f, 0f)
        } else {
            var sx = 0f
            var sy = 0f
            var sz = 0f
            for (v in vertices) {
                sx += v.x
                sy += v.y
                sz += v.z
            }
            val n = vertices.size.toFloat()
            Vec3(sx / n, sy / n, sz / n)
        }

        return AnatomicalMeshPart(
            nodeId = nodeId,
            displayName = displayName,
            vertices = vertices,
            triangles = triangles,
            baseColor = color,
            centroid = centroid
        )
    }

    private fun subdivideSpine(points: List<Vec3>, subdivisionsPerSegment: Int): List<Vec3> {
        if (points.size < 2) return points
        val out = mutableListOf<Vec3>()
        for (i in 0 until points.lastIndex) {
            val p0 = points[maxOf(0, i - 1)]
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = points[minOf(points.lastIndex, i + 2)]
            for (step in 0 until subdivisionsPerSegment) {
                val t = step.toFloat() / subdivisionsPerSegment
                out += catmullRom(p0, p1, p2, p3, t)
            }
        }
        out += points.last()
        return out
    }

    private fun catmullRom(p0: Vec3, p1: Vec3, p2: Vec3, p3: Vec3, t: Float): Vec3 {
        val t2 = t * t
        val t3 = t2 * t
        fun eval(a: Float, b: Float, c: Float, d: Float): Float =
            0.5f * ((2f * b) + (-a + c) * t + (2f * a - 5f * b + 4f * c - d) * t2 + (-a + 3f * b - 3f * c + d) * t3)
        return Vec3(
            eval(p0.x, p1.x, p2.x, p3.x),
            eval(p0.y, p1.y, p2.y, p3.y),
            eval(p0.z, p1.z, p2.z, p3.z)
        )
    }

    private fun addTriangleIfValid(
        vertices: List<Vec3>,
        triangles: MutableList<TriangleFace>,
        i0: Int,
        i1: Int,
        i2: Int
    ) {
        val v0 = vertices[i0]
        val v1 = vertices[i1]
        val v2 = vertices[i2]
        val edge1 = v1 - v0
        val edge2 = v2 - v0
        val cross = edge1.cross(edge2)
        if (cross.length() > 1e-6f) {
            triangles += TriangleFace(i0, i1, i2, cross.normalized())
        }
    }
}
