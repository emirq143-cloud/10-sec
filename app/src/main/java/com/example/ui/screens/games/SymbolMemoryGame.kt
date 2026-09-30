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
import com.example.ui.theme.TextWhiteTranslucent
import com.example.ui.theme.VibrantPink
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SymbolMemoryGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  val initialSymbols = remember {
    listOf("⭐", "🔺", "💖", "🟣", "🌙", "🟩", "🔷", "⭐", "🔺")
  }

  val displayList = remember {
    mutableStateListOf<String>().apply {
      addAll(initialSymbols)
    }
  }

  var isMemorizePhase by remember { mutableStateOf(true) }
  var memorizeProgress by remember { mutableFloatStateOf(1f) }
  var changedIndex by remember { mutableIntStateOf(-1) }
  val replacementSymbol = remember { "☀️" }

  LaunchedEffect(Unit) {
    val totalMemorizeMs = 2000L
    val step = 50L
    var elapsed = 0L

    while (elapsed < totalMemorizeMs) {
      delay(step)
      elapsed += step
      memorizeProgress = 1f - (elapsed.toFloat() / totalMemorizeMs)
    }

    changedIndex = Random.nextInt(displayList.size)
    displayList[changedIndex] = replacementSymbol
    isMemorizePhase = false
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Hafıza",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Phase indicator & Instruction Banner
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(if (isMemorizePhase) Color(0x66000000) else Color(0xDD0F172A))
        .border(1.5.dp, if (isMemorizePhase) NeonGold else SkyBlueAccent, RoundedCornerShape(16.dp))
        .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
      Text(
        text = if (isMemorizePhase) "👁️ 2 Saniye Boyunca İncele!" else "👇 DEĞİŞEN SEMBOLE DOKUN! 👇",
        color = if (isMemorizePhase) NeonGold else Color(0xFF38BDF8),
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.5.sp
      )
    }

    if (isMemorizePhase) {
      Spacer(modifier = Modifier.height(10.dp))
      LinearProgressIndicator(
        progress = { memorizeProgress },
        color = NeonGold,
        trackColor = Color.White.copy(alpha = 0.2f),
        modifier = Modifier
          .fillMaxWidth(0.55f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3x3 Grid of Symbol Tiles (High contrast, clearly selectable in phase 2)
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
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(displayList.size) { index ->
          val symbol = displayList[index]
          Box(
            modifier = Modifier
              .size(96.dp)
              .shadow(
                elevation = if (!isMemorizePhase) 10.dp else 4.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = if (!isMemorizePhase) SkyBlueAccent.copy(alpha = 0.6f) else Color(0x33000000)
              )
              .clip(RoundedCornerShape(22.dp))
              .background(if (!isMemorizePhase) Color.White else Color(0x44FFFFFF))
              .border(
                width = if (!isMemorizePhase) 2.5.dp else 1.5.dp,
                color = if (!isMemorizePhase) BluePrimary else Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(22.dp)
              )
              .clickable(enabled = !isMemorizePhase) {
                if (index == changedIndex) {
                  onSuccess(100, 100)
                } else {
                  onFail("Yanlış sembol! Değişen sembol: $replacementSymbol idi.")
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = symbol,
                fontSize = 40.sp
              )
              if (!isMemorizePhase) {
                Text(
                  text = "SEÇ",
                  color = BluePrimary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }
          }
        }
      }
    }

    // Bottom Circular Timer
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 78.dp
      )
    }
  }
}
