package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalSapphireLight,
    onPrimary = Color.White,
    primaryContainer = DarkUserBubble,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = CyanAccent,
    onSecondary = Color(0xFF083344),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = ElectricVioletLight,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalSapphire,
    onPrimary = PureWhite,
    primaryContainer = RoyalSapphireBg,
    onPrimaryContainer = RoyalSapphireDark,
    secondary = ElectricViolet,
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF4338CA),
    tertiary = EmeraldGlow,
    onTertiary = PureWhite,
    background = SnowCanvas,
    onBackground = ObsidianText,
    surface = PureWhite,
    onSurface = ObsidianText,
    surfaceVariant = SoftMist,
    onSurfaceVariant = SlateText,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Pure Clean White Mode by default for professional luxury aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


