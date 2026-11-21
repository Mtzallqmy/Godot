package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.MedicalReviewStatus
import com.example.core.model.ModuleAvailability
import com.example.core.model.ReferenceSource
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedPendingContainer
import com.example.ui.theme.MedSchematicContainer
import com.example.ui.theme.MedSchematicViolet
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedContainer
import com.example.ui.theme.MedVerifiedGreen

@Composable
fun LanguageToggleChip(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onToggle)
            .testTag("language_toggle_button"),
        shape = RoundedCornerShape(12.dp),
        color = MedCyanContainer,
        border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = if (language == AppLanguage.ARABIC) "تبديل اللغة" else "Switch Language",
                tint = MedCyan,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (language == AppLanguage.ENGLISH) "العربية" else "EN",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReviewStatusBadge(
    status: MedicalReviewStatus,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (bg, accent, text) = when (status) {
        MedicalReviewStatus.VERIFIED_EDUCATIONAL -> Triple(
            MedVerifiedContainer,
            MedVerifiedGreen,
            UiStrings.statusVerified.get(language)
        )
        MedicalReviewStatus.PENDING_MEDICAL_REVIEW -> Triple(
            MedPendingContainer,
            MedPendingAmber,
            UiStrings.statusPendingReview.get(language)
        )
        MedicalReviewStatus.SCHEMATIC_DEMONSTRATION -> Triple(
            MedSchematicContainer,
            MedSchematicViolet,
            UiStrings.statusSchematic.get(language)
        )
        MedicalReviewStatus.SCIENTIFIC_SIMULATION -> Triple(
            MedCyanContainer,
            MedCyan,
            if (language == AppLanguage.ARABIC) "محاكاة علمية" else "Scientific Simulation"
        )
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AvailabilityBadge(
    availability: ModuleAvailability,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isActive = availability == ModuleAvailability.ACTIVE_PHASE_1
    val accent = if (isActive) MedTeal else MedPendingAmber
    val bg = if (isActive) MedVerifiedContainer else MedPendingContainer
    val label = if (isActive) {
        UiStrings.statusActivePhase1.get(language)
    } else {
        UiStrings.statusPhase2Roadmap.get(language)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SafetyDisclaimerCard(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedPendingAmber.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = UiStrings.medicalDisclaimerTitle.get(language),
                tint = MedPendingAmber,
                modifier = Modifier.size(22.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = UiStrings.medicalDisclaimerTitle.get(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = MedPendingAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = UiStrings.medicalDisclaimerBody.get(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MedTextSecondary
                )
            }
        }
    }
}

@Composable
fun ReferenceSourceCard(
    source: ReferenceSource,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                runCatching {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                    context.startActivity(intent)
                }
            },
        shape = RoundedCornerShape(12.dp),
        color = MedDeepNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = source.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = source.organization,
                    style = MaterialTheme.typography.labelMedium,
                    color = MedCyan
                )
                Text(
                    text = source.licenseNote.get(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MedTextSecondary
                )
                Text(
                    text = source.url,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                    color = MedTeal
                )
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Open source link",
                tint = MedCyan,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Composable
fun MedVerseFooter(
    language: AppLanguage,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MedDeepNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = UiStrings.brandTitle.get(language),
                        style = MaterialTheme.typography.titleMedium,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = UiStrings.brandSubtitle.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MedTextSecondary
                    )
                }
                Surface(
                    modifier = Modifier.clickable(onClick = onOpenAbout),
                    shape = RoundedCornerShape(10.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline)
                ) {
                    Text(
                        text = UiStrings.navAbout.get(language),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
            HorizontalDivider(color = MedBorderHairline)
            Text(
                text = UiStrings.medicalDisclaimerBody.get(language),
                style = MaterialTheme.typography.bodySmall,
                color = MedTextSecondary
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MedObsidianNavy,
                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = UiStrings.DEVELOPER_CREDIT,
                    style = MaterialTheme.typography.labelLarge,
                    color = MedCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }
    }
}

fun systemIconForKey(key: String): ImageVector = when (key) {
    "favorite" -> Icons.Default.Favorite
    "accessibility" -> Icons.Default.AccessibilityNew
    "fitness_center" -> Icons.Default.FitnessCenter
    "psychology" -> Icons.Default.Psychology
    "air" -> Icons.Default.Air
    "restaurant" -> Icons.Default.Restaurant
    "water_drop" -> Icons.Default.WaterDrop
    "science" -> Icons.Default.Science
    "diversity_1" -> Icons.Default.Diversity1
    "layers" -> Icons.Default.Layers
    else -> Icons.Default.Science
}
