package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantRed
import java.util.Locale
import kotlin.math.max

@Composable
fun CircularTimer(
  remainingSeconds: Float,
  totalDurationSeconds: Float,
  modifier: Modifier = Modifier,
  size: Dp = 78.dp
) {
  val fraction = (remainingSeconds / max(0.1f, totalDurationSeconds)).coerceIn(0f, 1f)

  val isUrgent = remainingSeconds <= 3f
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isUrgent) 1.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  val activeColor by animateColorAsState(
    targetValue = when {
      remainingSeconds <= 2.5f -> VibrantRed
      remainingSeconds <= 4.5f -> NeonOrange
      else -> SkyBlueAccent
    },
    label = "timerColor"
  )

  Box(
    modifier = modifier
      .size(size)
      .scale(pulseScale)
      .shadow(12.dp, CircleShape, spotColor = activeColor.copy(alpha = 0.5f))
      .background(Color.Black.copy(alpha = 0.55f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size - 12.dp)) {
      // Background ring
      drawArc(
        color = Color(0x3338BDF8),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
      )

      // Active progress ring with cyan/sky glow
      drawArc(
        brush = Brush.sweepGradient(
          listOf(activeColor, CyanBright, activeColor)
        ),
        startAngle = -90f,
        sweepAngle = fraction * 360f,
        useCenter = false,
        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
      )
    }

    Text(
      text = String.format(Locale.US, "%.0fs", max(0f, remainingSeconds)),
      color = Color.White,
      fontSize = (size.value * 0.28f).sp,
      fontWeight = FontWeight.ExtraBold
    )
  }
}
