package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.AtmosphericCyanGlow
import com.example.ui.theme.AtmosphericPeachGlow
import com.example.ui.theme.AtmosphericPurpleGlow

/**
 * Atmospheric background with twilight mountain landscape,
 * dynamic aurora lighting effects, and glowing particles.
 * Directly addresses the user request: "arkaya bi efekt, sadece beyaz çok kötü"
 */
@Composable
fun AppBackgroundEffect(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val transition = rememberInfiniteTransition(label = "ambientGlow")
  val pulse by transition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(modifier = modifier.fillMaxSize()) {
    // 1. Scenic Twilight Mountain Landscape (Matching user mockup photo)
    Image(
      painter = painterResource(id = R.drawable.twilight_mountain_bg_1790506194088),
      contentDescription = "Twilight Mountain Artwork",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // 2. Rich atmospheric twilight overlay (neither pitch-black nor plain white)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xCC111827), // deep twilight blue top
              Color(0x991E293B), // mid twilight
              Color(0x66F97316), // sunset warm glow horizon
              Color(0x880F172A)  // bottom dusk
            )
          )
        )
    )

    // 3. Ambient animated glow spots & star particles
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Top-left cyan celestial aurora glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(AtmosphericCyanGlow, Color.Transparent),
          center = Offset(w * 0.2f, h * 0.15f),
          radius = (w * 0.45f) * pulse
        ),
        radius = (w * 0.45f) * pulse,
        center = Offset(w * 0.2f, h * 0.15f)
      )

      // Top-right purple aurora glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(AtmosphericPurpleGlow, Color.Transparent),
          center = Offset(w * 0.8f, h * 0.25f),
          radius = (w * 0.4f) * (2f - pulse)
        ),
        radius = (w * 0.4f) * (2f - pulse),
        center = Offset(w * 0.8f, h * 0.25f)
      )

      // Bottom-center warm sunset horizon glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(AtmosphericPeachGlow, Color.Transparent),
          center = Offset(w * 0.5f, h * 0.85f),
          radius = (w * 0.55f) * pulse
        ),
        radius = (w * 0.55f) * pulse,
        center = Offset(w * 0.5f, h * 0.85f)
      )
    }

    // 4. Content rendered above the atmospheric scene
    content()
  }
}
