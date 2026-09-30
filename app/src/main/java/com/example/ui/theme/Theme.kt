package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGameColorScheme = lightColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = SkyBlueAccent.copy(alpha = 0.15f),
  onPrimaryContainer = BluePrimary,
  secondary = NeonOrange,
  onSecondary = Color.White,
  secondaryContainer = OrangeLight,
  onSecondaryContainer = NeonOrange,
  tertiary = VibrantPink,
  onTertiary = Color.White,
  background = LightBg,
  onBackground = TextDark,
  surface = CardWhite,
  onSurface = TextDark,
  surfaceVariant = LightBgGradientTop,
  onSurfaceVariant = TextDarkSecondary,
  outline = CardBorderLight,
  outlineVariant = CardBorderSubtle,
  error = VibrantRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = LightGameColorScheme,
    typography = Typography,
    content = content
  )
}
