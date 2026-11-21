package com.example

import com.example.core.model.DictionaryCategory
import com.example.data.repository.LocalAnatomyRepository
import com.example.data.repository.LocalAssetProvenanceRepository
import com.example.data.repository.LocalTerminologyRepository
import com.example.engine3d.CardiovascularSchematicBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MedVerseDomainTest {

    @Test
    fun anatomyRepository_exposesAllTenBodySystemsAndTenCardiovascularStructures() {
        val repo = LocalAnatomyRepository()
        assertEquals(10, repo.getBodySystems().size)
        val cardio = repo.getStructuresBySystem("cardiovascular")
        assertEquals(10, cardio.size)
        val expectedIds = setOf(
            "heart", "right_atrium", "left_atrium", "right_ventricle", "left_ventricle",
            "aorta", "pulmonary_artery", "pulmonary_veins", "vena_cava", "interventricular_septum"
        )
        assertEquals(expectedIds, cardio.map { it.id }.toSet())
    }

    @Test
    fun terminologyRepository_supportsBilingualArabicAndEnglishSearch() {
        val repo = LocalTerminologyRepository()
        val enMatches = repo.searchTerms("Aorta", DictionaryCategory.ALL, false, emptySet())
        assertTrue(enMatches.any { it.englishName == "Aorta" })

        val arMatches = repo.searchTerms("الأبهر", DictionaryCategory.ALL, false, emptySet())
        assertTrue(arMatches.any { it.englishName == "Aorta" })

        // Verify Arabic Alef normalization ("الابهر" matches "الشريان الأبهر")
        val arNormMatches = repo.searchTerms("الابهر", DictionaryCategory.ALL, false, emptySet())
        assertTrue(arNormMatches.any { it.englishName == "Aorta" })
    }

    @Test
    fun schematicBuilder_generatesValidShadedGeometryMappedToAnatomyEntries() {
        val anatomyRepo = LocalAnatomyRepository()
        val provenanceRepo = LocalAssetProvenanceRepository()
        val scene = CardiovascularSchematicBuilder.buildDefaultScene(provenanceRepo)

        assertTrue(scene.parts.size >= 9)
        assertTrue(scene.metadata.vertexCount > 500)
        assertTrue(scene.metadata.triangleCount > 500)
        scene.parts.forEach { part ->
            assertNotNull("Node ${part.nodeId} must map to anatomy repository", anatomyRepo.findStructureByMeshNodeId(part.nodeId))
            assertTrue(part.vertices.isNotEmpty())
            assertTrue(part.triangles.isNotEmpty())
        }
    }
}
