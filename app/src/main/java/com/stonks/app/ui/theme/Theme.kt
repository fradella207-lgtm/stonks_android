package com.stonks.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = StonksGreen,
    onPrimary = PitchBlackBackground,
    secondary = StonksCyan,
    onSecondary = PitchBlackBackground,
    tertiary = NotStonksRed,
    background = DarkBackground,
    surface = DarkCard,
    surfaceVariant = DarkCardSubtle,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = BorderSubtleDark
)

private val BlackColorScheme = darkColorScheme(
    primary = StonksGreen,
    onPrimary = PitchBlackBackground,
    secondary = StonksCyan,
    onSecondary = PitchBlackBackground,
    tertiary = NotStonksRed,
    background = PitchBlackBackground,
    surface = Color(0xFF080808),
    surfaceVariant = Color(0xFF121212),
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0x22FFFFFF),
    outlineVariant = Color(0x10FFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = StonksGreen,
    onPrimary = LightBackground,
    secondary = StonksCyan,
    onSecondary = LightBackground,
    tertiary = NotStonksRed,
    background = LightBackground,
    surface = LightCard,
    surfaceVariant = LightCardSubtle,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = BorderLight
)

enum class AppThemeMode {
    DARK,
    BLACK,
    LIGHT
}

@Composable
fun StonksTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.BLACK -> BlackColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val isLight = themeMode == AppThemeMode.LIGHT
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLight
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isLight
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
