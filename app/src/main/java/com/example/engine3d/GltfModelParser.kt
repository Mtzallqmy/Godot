package com.example.engine3d

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.Color
import com.example.core.i18n.LocalizedText
import com.example.core.model.AssetProvenanceType
import com.example.core.model.ModelAssetMetadata
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max

/**
 * Native GLB (Binary glTF 2.0) and GLTF JSON parser & validator.
 * Reads user-selected files via Android Storage Access Framework (zero storage permissions),
 * validates glTF 2.0 schema headers, extracts mesh geometry, normalizes bounding volume,
 * and constructs provenance & validation reports.
 */
object GltfModelParser {

    private const val GLB_MAGIC = 0x46546C67 // "glTF" little-endian
    private const val CHUNK_TYPE_JSON = 0x4E4F534A // "JSON"
    private const val CHUNK_TYPE_BIN = 0x004E4942 // "BIN\0"
    private const val MAX_RENDER_TRIANGLES_PER_MESH = 5000

    private val importedPalette = listOf(
        Color(0xFF00B8D4),
        Color(0xFF14B8A6),
        Color(0xFF3B82F6),
        Color(0xFFE14B5A),
        Color(0xFFF59E0B),
        Color(0xFF8B5CF6)
    )

    fun loadFromUri(context: Context, uri: Uri): Result<Scene3D> {
        return runCatching {
            val fileName = resolveDisplayName(context, uri) ?: "imported_model.glb"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Unable to open selected model stream.")

            if (bytes.size < 12) {
                error("Selected file is too small to be a valid GLB or GLTF 2.0 model.")
            }

            val headerBuf = ByteBuffer.wrap(bytes, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
            val magic = headerBuf.int
            if (magic == GLB_MAGIC) {
                parseGlb(bytes, fileName)
            } else {
                val text = bytes.toString(Charsets.UTF_8).trimStart()
                if (text.startsWith("{")) {
                    parseGltfJson(text, fileName)
                } else {
                    error("Unsupported file format. Please choose a valid .glb or .gltf 2.0 medical asset.")
                }
            }
        }
    }

    private fun parseGlb(bytes: ByteArray, fileName: String): Scene3D {
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        buf.int // magic verified
        val version = buf.int
        if (version != 2) {
            error("Unsupported glTF version ($version). MedVerse 3D requires glTF 2.0.")
        }
        val declaredLength = buf.int
        val validationNotes = mutableListOf<String>()
        validationNotes += "GLB 2.0 magic header verified (0x46546C67), total size ${bytes.size} bytes."
        if (declaredLength != bytes.size) {
            validationNotes += "Header length ($declaredLength) differs slightly from file size (${bytes.size})."
        }

        var jsonText: String? = null
        var binData: ByteArray? = null

        var offset = 12
        while (offset + 8 <= bytes.size) {
            buf.position(offset)
            val chunkLen = buf.int
            val chunkType = buf.int
            val dataStart = offset + 8
            val dataEnd = minOf(dataStart + chunkLen, bytes.size)
            when (chunkType) {
                CHUNK_TYPE_JSON -> {
                    jsonText = String(bytes, dataStart, dataEnd - dataStart, Charsets.UTF_8)
                }
                CHUNK_TYPE_BIN -> {
                    binData = bytes.copyOfRange(dataStart, dataEnd)
                }
            }
            offset = dataStart + chunkLen
        }

        val root = JSONObject(jsonText ?: error("GLB file is missing its required JSON chunk."))
        val assetObj = root.optJSONObject("asset")
        val copyright = assetObj?.optString("copyright").orEmpty().ifBlank {
            "User-imported asset — verify license before clinical/academic redistribution."
        }
        val generator = assetObj?.optString("generator").orEmpty().ifBlank { "glTF 2.0 Exporter" }

        val parts = extractMeshesFromGltf(root, binData, validationNotes)
        if (parts.isEmpty()) {
            error("GLB parsed successfully, but no renderable triangle primitives were found in BIN buffer.")
        }

        val normalizedParts = normalizePartsToUnitViewport(parts)
        val totalVerts = normalizedParts.sumOf { it.vertices.size }
        val totalTris = normalizedParts.sumOf { it.triangles.size }

        val metadata = ModelAssetMetadata(
            id = "imported_${fileName.lowercase().replace(Regex("[^a-z0-9]"), "_")}",
            name = LocalizedText(fileName, fileName),
            format = "GLB 2.0",
            provenance = AssetProvenanceType.USER_IMPORTED_GLB_GLTF,
            sourceOrganization = generator,
            sourceUrl = "https://3d.nih.gov/",
            license = copyright,
            attribution = "Imported locally via Android Storage Access Framework ($fileName).",
            accuracyNotice = LocalizedText(
                en = "User-imported GLB 2.0 mesh. Verify scientific provenance and license metadata from the originating repository (e.g., NIH 3D).",
                ar = "نموذج GLB 2.0 مستورد محلياً. يرجى التحقق من مصدره العلمي ورخصته من المستودع الأصلي مثل NIH 3D."
            ),
            vertexCount = totalVerts,
            triangleCount = totalTris,
            mappedNodeIds = normalizedParts.map { it.nodeId },
            validationPassed = true,
            validationNotes = validationNotes
        )
        return Scene3D(parts = normalizedParts, metadata = metadata)
    }

    private fun parseGltfJson(jsonText: String, fileName: String): Scene3D {
        val root = JSONObject(jsonText)
        val assetObj = root.optJSONObject("asset")
            ?: error("Invalid .gltf file: missing top-level 'asset' object.")
        val version = assetObj.optString("version")
        if (!version.startsWith("2")) {
            error("Unsupported glTF version '$version'. Expected glTF 2.0.")
        }

        val buffers = root.optJSONArray("buffers")
        var embeddedBin: ByteArray? = null
        val validationNotes = mutableListOf("Valid glTF $version JSON manifest detected.")
        if (buffers != null && buffers.length() > 0) {
            val uri = buffers.getJSONObject(0).optString("uri")
            if (uri.startsWith("data:") && uri.contains("base64,")) {
                val base64 = uri.substringAfter("base64,")
                embeddedBin = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                validationNotes += "Decoded embedded Base64 data URI buffer (${embeddedBin.size} bytes)."
            } else {
                error("External .bin buffer ('$uri') cannot be resolved from a single picked .gltf file. Please export as self-contained .glb or embedded .gltf.")
            }
        }

        val parts = extractMeshesFromGltf(root, embeddedBin, validationNotes)
        if (parts.isEmpty()) {
            error("glTF JSON contains no renderable triangle primitives.")
        }
        val normalizedParts = normalizePartsToUnitViewport(parts)
        val totalVerts = normalizedParts.sumOf { it.vertices.size }
        val totalTris = normalizedParts.sumOf { it.triangles.size }

        val metadata = ModelAssetMetadata(
            id = "imported_${fileName.lowercase().replace(Regex("[^a-z0-9]"), "_")}",
            name = LocalizedText(fileName, fileName),
            format = "GLTF 2.0 (Embedded)",
            provenance = AssetProvenanceType.USER_IMPORTED_GLB_GLTF,
            sourceOrganization = assetObj.optString("generator").ifBlank { "glTF 2.0" },
            sourceUrl = "https://3d.nih.gov/",
            license = assetObj.optString("copyright").ifBlank { "Verify originating model license" },
            attribution = "Imported locally ($fileName).",
            accuracyNotice = LocalizedText(
                en = "Embedded glTF 2.0 asset validated and loaded locally.",
                ar = "تم التحقق من نموذج glTF 2.0 المضمّن وتحميله محلياً."
            ),
            vertexCount = totalVerts,
            triangleCount = totalTris,
            mappedNodeIds = normalizedParts.map { it.nodeId },
            validationPassed = true,
            validationNotes = validationNotes
        )
        return Scene3D(parts = normalizedParts, metadata = metadata)
    }

    private fun extractMeshesFromGltf(
        root: JSONObject,
        binData: ByteArray?,
        validationNotes: MutableList<String>
    ): List<AnatomicalMeshPart> {
        if (binData == null) return emptyList()
        val meshes = root.optJSONArray("meshes") ?: return emptyList()
        val accessors = root.optJSONArray("accessors") ?: return emptyList()
        val bufferViews = root.optJSONArray("bufferViews") ?: return emptyList()

        val result = mutableListOf<AnatomicalMeshPart>()
        val binBuf = ByteBuffer.wrap(binData).order(ByteOrder.LITTLE_ENDIAN)

        for (m in 0 until meshes.length()) {
            val meshObj = meshes.getJSONObject(m)
            val meshName = meshObj.optString("name").ifBlank { "mesh_node_$m" }
            val primitives = meshObj.optJSONArray("primitives") ?: continue

            val vertices = mutableListOf<Vec3>()
            val triangles = mutableListOf<TriangleFace>()

            for (p in 0 until primitives.length()) {
                val prim = primitives.getJSONObject(p)
                val mode = prim.optInt("mode", 4) // 4 = TRIANGLES
                if (mode != 4) continue
                val attrs = prim.optJSONObject("attributes") ?: continue
                if (!attrs.has("POSITION")) continue

                val posAccessorIdx = attrs.getInt("POSITION")
                val posPositions = readVec3Accessor(accessors, bufferViews, binBuf, posAccessorIdx)
                val vertBase = vertices.size
                vertices.addAll(posPositions)

                val indices = if (prim.has("indices")) {
                    readIndexAccessor(accessors, bufferViews, binBuf, prim.getInt("indices"))
                } else {
                    IntArray(posPositions.size) { it }
                }

                val totalPrimTris = indices.size / 3
                val stride = max(1, totalPrimTris / MAX_RENDER_TRIANGLES_PER_MESH)
                if (stride > 1) {
                    validationNotes += "Node '$meshName' decimated by ${stride}x for smooth mobile 60fps rendering."
                }
                var t = 0
                while (t + 2 < indices.size) {
                    val i0 = vertBase + indices[t]
                    val i1 = vertBase + indices[t + 1]
                    val i2 = vertBase + indices[t + 2]
                    if (i0 < vertices.size && i1 < vertices.size && i2 < vertices.size) {
                        val n = (vertices[i1] - vertices[i0]).cross(vertices[i2] - vertices[i0])
                        if (n.length() > 1e-6f) {
                            triangles += TriangleFace(i0, i1, i2, n.normalized())
                        }
                    }
                    t += 3 * stride
                }
            }

            if (vertices.isNotEmpty() && triangles.isNotEmpty()) {
                val color = importedPalette[m % importedPalette.size]
                val cx = vertices.map { it.x }.average().toFloat()
                val cy = vertices.map { it.y }.average().toFloat()
                val cz = vertices.map { it.z }.average().toFloat()
                result += AnatomicalMeshPart(
                    nodeId = meshName.lowercase().replace(' ', '_'),
                    displayName = LocalizedText(meshName, meshName),
                    vertices = vertices,
                    triangles = triangles,
                    baseColor = color,
                    centroid = Vec3(cx, cy, cz)
                )
            }
        }
        return result
    }

    private fun readVec3Accessor(
        accessors: org.json.JSONArray,
        bufferViews: org.json.JSONArray,
        binBuf: ByteBuffer,
        accessorIdx: Int
    ): List<Vec3> {
        val acc = accessors.getJSONObject(accessorIdx)
        val view = bufferViews.getJSONObject(acc.getInt("bufferView"))
        val count = acc.getInt("count")
        val baseOffset = view.optInt("byteOffset", 0) + acc.optInt("byteOffset", 0)
        val stride = view.optInt("byteStride", 12).let { if (it <= 0) 12 else it }
        val out = ArrayList<Vec3>(count)
        for (i in 0 until count) {
            val pos = baseOffset + i * stride
            if (pos + 12 > binBuf.limit()) break
            val x = binBuf.getFloat(pos)
            val y = binBuf.getFloat(pos + 4)
            val z = binBuf.getFloat(pos + 8)
            out += Vec3(x, y, z)
        }
        return out
    }

    private fun readIndexAccessor(
        accessors: org.json.JSONArray,
        bufferViews: org.json.JSONArray,
        binBuf: ByteBuffer,
        accessorIdx: Int
    ): IntArray {
        val acc = accessors.getJSONObject(accessorIdx)
        val view = bufferViews.getJSONObject(acc.getInt("bufferView"))
        val count = acc.getInt("count")
        val componentType = acc.getInt("componentType")
        val baseOffset = view.optInt("byteOffset", 0) + acc.optInt("byteOffset", 0)
        val out = IntArray(count)
        for (i in 0 until count) {
            out[i] = when (componentType) {
                5121 -> binBuf.get(baseOffset + i).toInt() and 0xFF // UNSIGNED_BYTE
                5123 -> binBuf.getShort(baseOffset + i * 2).toInt() and 0xFFFF // UNSIGNED_SHORT
                5125 -> binBuf.getInt(baseOffset + i * 4) // UNSIGNED_INT
                else -> 0
            }
        }
        return out
    }

    private fun normalizePartsToUnitViewport(parts: List<AnatomicalMeshPart>): List<AnatomicalMeshPart> {
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        for (part in parts) {
            for (v in part.vertices) {
                if (v.x < minX) minX = v.x
                if (v.y < minY) minY = v.y
                if (v.z < minZ) minZ = v.z
                if (v.x > maxX) maxX = v.x
                if (v.y > maxY) maxY = v.y
                if (v.z > maxZ) maxZ = v.z
            }
        }

        val cx = (minX + maxX) * 0.5f
        val cy = (minY + maxY) * 0.5f
        val cz = (minZ + maxZ) * 0.5f
        val span = maxOf(maxX - minX, maxY - minY, maxZ - minZ).coerceAtLeast(1e-4f)
        val scale = 2.4f / span

        return parts.map { part ->
            val normVerts = part.vertices.map { v ->
                Vec3((v.x - cx) * scale, (v.y - cy) * scale, (v.z - cz) * scale)
            }
            val normCentroid = Vec3(
                (part.centroid.x - cx) * scale,
                (part.centroid.y - cy) * scale,
                (part.centroid.z - cz) * scale
            )
            part.copy(vertices = normVerts, centroid = normCentroid)
        }
    }

    private fun resolveDisplayName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
    }
}
