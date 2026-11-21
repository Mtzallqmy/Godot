package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage

private val MedVerseDarkColorScheme = darkColorScheme(
    primary = MedCyan,
    onPrimary = MedOnCyan,
    primaryContainer = MedCyanContainer,
    onPrimaryContainer = MedCyanBright,
    secondary = MedTeal,
    onSecondary = MedOnTeal,
    secondaryContainer = MedTealContainer,
    onSecondaryContainer = MedTealLight,
    tertiary = MedElectricBlue,
    onTertiary = MedTextPrimary,
    tertiaryContainer = MedElectricBlueContainer,
    onTertiaryContainer = MedElectricBlueSoft,
    background = MedObsidianNavy,
    onBackground = MedTextPrimary,
    surface = MedDeepNavy,
    onSurface = MedTextPrimary,
    surfaceVariant = MedPanelNavy,
    onSurfaceVariant = MedTextSecondary,
    surfaceContainerLowest = MedObsidianNavy,
    surfaceContainerLow = MedDeepNavy,
    surfaceContainer = MedPanelNavy,
    surfaceContainerHigh = MedElevatedNavy,
    surfaceContainerHighest = MedSurfaceBright,
    outline = MedBorderHairline,
    outlineVariant = MedBorderSubtle,
    error = MedAlertRed,
    onError = MedTextPrimary,
    errorContainer = MedAlertContainer,
    onErrorContainer = MedTextPrimary
)

private val MedVerseShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MedVerseTheme(
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit
) {
    val typography = remember(language) { buildMedVerseTypography(language) }
    val layoutDirection = if (language == AppLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = MedVerseDarkColorScheme,
            typography = typography,
            shapes = MedVerseShapes,
            content = content
        )
    }
}
