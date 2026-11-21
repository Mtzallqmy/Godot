package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.data.seed.MedicalReferencesSeed
import com.example.ui.components.ReferenceSourceCard
import com.example.ui.components.SafetyDisclaimerCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary

@Composable
fun AboutScreen(
    language: AppLanguage,
    onOpenProvenance: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Brand Identity & Developer Attribution Hero
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp),
                shape = RoundedCornerShape(22.dp),
                color = MedDeepNavy,
                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_medverse_logo_1795214354550),
                            contentDescription = stringResource(id = R.string.cd_app_logo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "MEDVERSE 3D",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Interactive Medical Universe • عالم الطب التفاعلي ثلاثي الأبعاد",
                                style = MaterialTheme.typography.bodySmall,
                                color = MedCyan
                            )
                        }
                    }

                    HorizontalDivider(color = MedBorderHairline)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MedObsidianNavy,
                        border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "حقوق التطوير والبرمجة" else "Official Development Credit",
                                style = MaterialTheme.typography.labelMedium,
                                color = MedTeal
                            )
                            Text(
                                text = UiStrings.DEVELOPER_CREDIT,
                                style = MaterialTheme.typography.titleMedium,
                                color = MedCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = UiStrings.projectIntroBody.get(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        // Medical Safety & Governance Charter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SafetyDisclaimerCard(language = language)

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MedCyan, modifier = Modifier.size(20.dp))
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "حوكمة الأصول ثلاثية الأبعاد والشفافية العلمية"
                                } else {
                                    "3D Asset Provenance & Scientific Transparency"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "يميز عالم الطب التفاعلي بوضوح بين المحتوى التعليمي المراجع، والنماذج التخطيطية التعليمية، والمحاكاة العلمية المستقبلية. لا يتم عرض أي نموذج تخطيطي كمسح سريري معتمد، ويدعم التطبيق استيراد نماذج GLB/GLTF 2.0 الموثقة محلياً."
                            } else {
                                "MedVerse 3D strictly distinguishes verified educational content, schematic 3D demonstrations, pending reviews, and future mathematical simulations. Local GLB/GLTF 2.0 import and schema validation are built in."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MedTextSecondary
                        )
                        Button(
                            onClick = onOpenProvenance,
                            colors = ButtonDefaults.buttonColors(containerColor = MedCyanContainer),
                            border = BorderStroke(1.dp, MedCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = UiStrings.toolProvenance.get(language),
                                color = MedCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Architecture & Phase Roadmap Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp),
                shape = RoundedCornerShape(18.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedBorderHairline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MedTeal, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "الهندسة المعيارية — المرحلة 01" else "Modular Architecture — Phase 01",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    val bullets = listOf(
                        "core/i18n • Full Arabic (RTL) & English (LTR) + DataStore persistence",
                        "engine3d • Interactive 3D renderer, ray-picking, presets, lighting & GLB/GLTF 2.0 parser",
                        "data/repository • Clean interfaces decoupled from local seed datasets",
                        "core/simulation • Typed Phase 02 contracts (Physiology, Pathology, Pharmacology, Timelines)"
                    )
                    bullets.forEach { b ->
                        Text(
                            text = "• $b",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                            color = MedTextSecondary
                        )
                    }
                }
            }
        }

        // Scientific Source Families
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = UiStrings.labelSources.get(language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                ReferenceSourceCard(MedicalReferencesSeed.ifaaFipatTa2, language)
                ReferenceSourceCard(MedicalReferencesSeed.nihNhlbiHeart, language)
                ReferenceSourceCard(MedicalReferencesSeed.medlinePlusHeart, language)
                ReferenceSourceCard(MedicalReferencesSeed.msdManualsCardio, language)
                ReferenceSourceCard(MedicalReferencesSeed.nih3dPortal, language)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
