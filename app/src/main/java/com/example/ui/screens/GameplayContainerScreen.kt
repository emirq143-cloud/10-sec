package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

@Composable
fun GameplayContainerScreen(
  session: GamePlaySession,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  Box(modifier = modifier.fillMaxSize()) {
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
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.STROOP_COLOR -> StroopColorGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.MEMORY_SYMBOLS -> SymbolMemoryGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.QUICK_MATH -> QuickMathGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.FIND_DIFFERENCE -> FindDifferenceGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.LOGIC_PATTERN -> LogicPatternGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.ESTIMATION_BAR -> EstimationBarGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
      GameType.WORKING_MEMORY -> WorkingMemoryGame(
        remainingSeconds = session.remainingSeconds,
        durationSeconds = session.durationSeconds,
        streak = session.currentStreak,
        isRiskMode = session.isRiskMode,
        onBack = onBack,
        onSuccess = onSuccess,
        onFail = onFail
      )
    }
  }
}
