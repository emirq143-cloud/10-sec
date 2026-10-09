package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.random.Random

val SYMBOL_POOL = listOf(
  "⭐", "🔺", "💖", "🟣", "🌙", "🟩", "🔷", "☀️",
  "🪐", "🚀", "💎", "⚡", "🍀", "🔮", "🧿", "👑",
  "🎯", "🍎", "🦄", "🌟", "🍕", "🎈", "🏆", "🔥"
)

data class SymbolRoundData(
  val initialSymbols: List<String>,
  val changedIndex: Int,
  val replacementSymbol: String
)

fun generateSymbolRound(gridSize: Int): SymbolRoundData {
  val selected = SYMBOL_POOL.shuffled().take(gridSize)
  val changedIndex = Random.nextInt(gridSize)
  val unused = SYMBOL_POOL.filter { it !in selected }
  val replacement = unused.random()
  return SymbolRoundData(selected, changedIndex, replacement)
}

@Composable
fun SymbolMemoryGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSetTimerPaused: (Boolean) -> Unit = {},
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {}
) {
  var round by remember { mutableIntStateOf(1) }
  val gridSize = if (round <= 2) 6 else 9
  var roundData by remember { mutableStateOf(generateSymbolRound(gridSize)) }

  var isMemorizePhase by remember { mutableStateOf(true) }
  var memorizeProgress by remember { mutableFloatStateOf(1f) }
  val currentDisplay = remember { mutableStateListOf<String>() }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  DisposableEffect(Unit) {
    onDispose { onSetTimerPaused(false) }
  }

  LaunchedEffect(round) {
    roundData = generateSymbolRound(if (round <= 2) 6 else 9)
    currentDisplay.clear()
    currentDisplay.addAll(roundData.initialSymbols)
    isMemorizePhase = true
    onSetTimerPaused(true)

    val totalMs = 2000L
    val step = 50L
    var elapsed = 0L

    while (elapsed < totalMs) {
      delay(step)
      elapsed += step
      memorizeProgress = 1f - (elapsed.toFloat() / totalMs)
    }

    currentDisplay[roundData.changedIndex] = roundData.replacementSymbol
    isMemorizePhase = false
    onSetTimerPaused(false)
  }

  fun onCardClick(index: Int) {
    if (isMemorizePhase) return
    if (index == roundData.changedIndex) {
      feedbackText = "✓ DOĞRU SEMBOL! (+120)"
      feedbackIsSuccess = true
      onRoundSuccess(120, 2.5f)
      round++
    } else {
      feedbackText = "✗ YANLIŞ KART!"
      feedbackIsSuccess = false
      onRoundMistake(2.0f, "Hatalı sembol seçtin!")
      if (!isRiskMode) {
        round++
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Sembol Hafıza",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Seviye ve Geri Bildirim
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.5f))
          .border(1.dp, SkyBlueAccent, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Tur $round",
          color = SkyBlueAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (feedbackText.isNotEmpty()) {
        Text(
          text = feedbackText,
          color = if (feedbackIsSuccess) VibrantGreen else VibrantRed,
          fontSize = 12.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Faz Göstergesi
    if (isMemorizePhase) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.6f))
          .border(1.5.dp, NeonGold, RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = com.example.util.LocalizationManager.string("inspect_symbols"),
          color = NeonGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { memorizeProgress },
        color = NeonGold,
        trackColor = Color.White.copy(alpha = 0.2f),
        modifier = Modifier
          .fillMaxWidth(0.55f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
      )
    } else {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0F172A))
          .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = com.example.util.LocalizationManager.string("tap_changed_symbol"),
          color = SkyBlueAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Semboller Tablosu
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(currentDisplay.size) { index ->
          val symbol = currentDisplay[index]
          Box(
            modifier = Modifier
              .size(80.dp)
              .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = SkyBlueAccent.copy(alpha = 0.4f))
              .clip(RoundedCornerShape(20.dp))
              .background(if (isMemorizePhase) Color.White else Color(0xFFF8FAFC))
              .border(
                width = 2.5.dp,
                color = if (!isMemorizePhase) SkyBlueAccent else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(20.dp)
              )
              .clickable(enabled = !isMemorizePhase) {
                onCardClick(index)
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = symbol,
              fontSize = 36.sp
            )
          }
        }
      }
    }

    // Alt Süre
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 74.dp
      )
    }
  }
}
