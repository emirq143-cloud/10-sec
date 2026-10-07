package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GameType
import com.example.ui.GamePlaySession
import com.example.ui.screens.games.EstimationBarGame
import com.example.ui.screens.games.FastTapGame
import com.example.ui.screens.games.FindDifferenceGame
import com.example.ui.screens.games.LogicPatternGame
import com.example.ui.screens.games.QuickMathGame
import com.example.ui.screens.games.StroopColorGame
import com.example.ui.screens.games.SymbolMemoryGame
import com.example.ui.screens.games.WorkingMemoryGame
import com.example.ui.theme.NeonGold
import kotlinx.coroutines.launch

@Composable
fun GameplayContainerScreen(
  session: GamePlaySession,
  onBack: () -> Unit,
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSetTimerPaused: (Boolean) -> Unit = {},
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {},
  onWatchAdRevive: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val flashAlpha = remember { Animatable(0f) }
  var flashColor by remember { mutableStateOf(Color.Transparent) }
  val scope = rememberCoroutineScope()

  fun triggerFlash(color: Color, durationMs: Int = 360) {
    flashColor = color
    scope.launch {
      flashAlpha.snapTo(1f)
      flashAlpha.animateTo(
        targetValue = 0f,
        animationSpec = tween(durationMillis = durationMs, easing = LinearOutSlowInEasing)
      )
    }
  }

  // Intercepted handlers that trigger visual feedback
  val handleRoundSuccess: (Int, Float) -> Unit = { scoreBonus, timeBonus ->
    triggerFlash(Color(0xFF10B981), durationMs = 380) // Vibrant Emerald Green
    onRoundSuccess(scoreBonus, timeBonus)
  }

  val handleRoundMistake: (Float, String) -> Unit = { timePenalty, reason ->
    triggerFlash(Color(0xFFEF4444), durationMs = 450) // Bright Crimson Red
    onRoundMistake(timePenalty, reason)
  }

  val handleSuccess: (Int, Int) -> Unit = { scoreBonus, accuracy ->
    triggerFlash(Color(0xFF10B981), durationMs = 450)
    onSuccess(scoreBonus, accuracy)
  }

  val handleFail: (String) -> Unit = { reason ->
    triggerFlash(Color(0xFFEF4444), durationMs = 500)
    onFail(reason)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .drawWithContent {
        drawContent()
        val alpha = flashAlpha.value
        if (alpha > 0.005f) {
          // 1. Subtle, translucent full-screen wash
          drawRect(
            color = flashColor.copy(alpha = alpha * 0.22f),
            size = size
          )
          // 2. High-visibility glowing neon border frame along screen edges
          drawRect(
            color = flashColor.copy(alpha = alpha * 0.90f),
            size = size,
            style = Stroke(width = 10.dp.toPx())
          )
          // 3. Top and bottom ambient glow
          drawRect(
            brush = Brush.verticalGradient(
              colors = listOf(
                flashColor.copy(alpha = alpha * 0.45f),
                Color.Transparent
              ),
              startY = 0f,
              endY = 140.dp.toPx()
            ),
            size = Size(size.width, 140.dp.toPx())
          )
          drawRect(
            brush = Brush.verticalGradient(
              colors = listOf(
                Color.Transparent,
                flashColor.copy(alpha = alpha * 0.45f)
              ),
              startY = size.height - 140.dp.toPx(),
              endY = size.height
            ),
            topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - 140.dp.toPx()),
            size = Size(size.width, 140.dp.toPx())
          )
        }
      }
  ) {
    // Beautiful atmospheric twilight mountain & sunset wallpaper
    Image(
      painter = painterResource(id = R.drawable.twilight_mountain_bg_1790506194088),
      contentDescription = "Twilight Mountain Landscape",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // Soft gradient overlay to ensure UI elements pop with high contrast
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0x991E284A),
              Color(0x44263560),
              Color(0x33F97316)
            )
          )
        )
    )

    when (session.gameType) {
      GameType.REFLEX_DOTS -> FastTapGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.STROOP_COLOR -> StroopColorGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.MEMORY_SYMBOLS -> SymbolMemoryGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSetTimerPaused = onSetTimerPaused,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.QUICK_MATH -> QuickMathGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.FIND_DIFFERENCE -> FindDifferenceGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.LOGIC_PATTERN -> LogicPatternGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.ESTIMATION_BAR -> EstimationBarGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
      GameType.WORKING_MEMORY -> WorkingMemoryGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSetTimerPaused = onSetTimerPaused,
        onRoundSuccess = handleRoundSuccess,
        onRoundMistake = handleRoundMistake,
        onSuccess = handleSuccess,
        onFail = handleFail
      )
    }

    // Quick Rewarded Ad Boost Button (visible when timer drops to 5s or less and revive hasn't been used yet)
    if (!session.hasUsedRevive && session.remainingSeconds in 0.1f..5.5f) {
      val infiniteTransition = rememberInfiniteTransition(label = "pulseAd")
      val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
          animation = tween(400),
          repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAdScale"
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 70.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
      ) {
        Box(
          modifier = Modifier
            .scale(pulseScale)
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF10B981))
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF10B981), Color(0xFF059669))
              )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .clickable { onWatchAdRevive() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(text = "🎬", fontSize = 14.sp)
            Text(
              text = "+5s Ek Süre",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}
