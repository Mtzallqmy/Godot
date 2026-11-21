package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalizedText
import com.example.core.i18n.UiStrings
import com.example.core.model.AssetProvenanceType
import com.example.core.model.ModelAssetMetadata
import com.example.data.seed.MedicalReferencesSeed
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedGreen

@Composable
fun AssetProvenanceDialog(
    metadata: ModelAssetMetadata?,
    phaseTwoAssets: List<Pair<String, LocalizedText>>,
    language: AppLanguage,
    onImportGlbClick: () -> Unit,
    onRestoreDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = MedPanelNavy,
            border = BorderStroke(1.dp, MedBorderHairline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MedCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = UiStrings.toolProvenance.get(language),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MedTextSecondary)
                    }
                }

                if (metadata != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MedDeepNavy,
                        border = BorderStroke(1.dp, MedSchematicViolet.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = metadata.name.get(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${metadata.format}  •  ${metadata.vertexCount} verts  •  ${metadata.triangleCount} tris",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                color = MedCyan
                            )
                            Text(
                                text = metadata.accuracyNotice.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                            Text(
                                text = "${if (language == AppLanguage.ARABIC) "الترخيص" else "License"}: ${metadata.license}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MedTeal
                            )
                        }
                    }

                    // Validation Pipeline Checks
                    Text(
                        text = if (language == AppLanguage.ARABIC) {
                            "نتائج التحقق من النموذج"
                        } else {
                            "Model Validation Diagnostics"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    metadata.validationNotes.forEach { note ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MedVerifiedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Import GLB/GLTF & Restore Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                            onImportGlbClick()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MedCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = MedObsidianNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = UiStrings.toolImportGlb.get(language),
                            color = MedObsidianNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (metadata?.provenance == AssetProvenanceType.USER_IMPORTED_GLB_GLTF) {
                        OutlinedButton(
                            onClick = {
                                onRestoreDefault()
                                onDismiss()
                            },
                            border = BorderStroke(1.dp, MedBorderHairline)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                HorizontalDivider(color = MedBorderHairline)

                // Verified NIH 3D Scientific Source Link
                ReferenceSourceCard(source = MedicalReferencesSeed.nih3dPortal, language = language)

                // Phase 02 Required Clinical Mesh Checklist
                Text(
                    text = if (language == AppLanguage.ARABIC) {
                        "النماذج التشريحية المطلوبة للمرحلة 02"
                    } else {
                        "Documented Assets Required for Phase 02"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                phaseTwoAssets.forEach { (filename, desc) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MedDeepNavy,
                        border = BorderStroke(1.dp, MedBorderHairline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = filename,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                color = MedCyan
                            )
                            Text(
                                text = desc.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
