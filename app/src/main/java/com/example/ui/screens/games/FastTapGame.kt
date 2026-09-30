package com.example.ui.screens.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.random.Random

data class DotItem(
  val id: Int,
  val isGreen: Boolean,
  val isVisible: Boolean
)

@Composable
fun FastTapGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {}
) {
  var totalHits by remember { mutableIntStateOf(0) }
  var combo by remember { mutableIntStateOf(0) }

  val dots = remember {
    mutableStateListOf<DotItem>().apply {
      repeat(12) { i ->
        add(DotItem(id = i, isGreen = (i % 3 != 0), isVisible = true))
      }
    }
  }

  // Periodic random reshuffling of visible dots
  LaunchedEffect(Unit) {
    while (true) {
      delay(650)
      for (i in dots.indices) {
        val show = Random.nextFloat() > 0.25f
        val isGreen = Random.nextFloat() > 0.30f
        dots[i] = DotItem(id = i, isGreen = isGreen, isVisible = show)
      }
    }
  }

  fun onDotTapped(index: Int) {
    val dot = dots[index]
    if (!dot.isVisible) return

    if (dot.isGreen) {
      totalHits++
      combo++
      onRoundSuccess(50, 0.4f)
      // Instantly respawn in new state
      dots[index] = DotItem(id = index, isGreen = false, isVisible = false)
    } else {
      combo = 0
      onRoundMistake(2.0f, "Kırmızı noktaya dokundun!")
      dots[index] = DotItem(id = index, isGreen = false, isVisible = false)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Yıldırım Refleks",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Skor ve Kombo Sayacı
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
          .border(1.dp, VibrantGreen, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "🎯 İsabet: $totalHits",
          color = VibrantGreen,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (combo > 2) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NeonGold.copy(alpha = 0.2f))
            .border(1.dp, NeonGold, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "🔥 ${combo}X Seri!",
            color = NeonGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Kural Banner'ı
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.6f))
        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
        .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "🟢 YEŞİLE BAS!",
          color = Color(0xFF34D399),
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
        Text(text = "•", color = Color.White)
        Text(
          text = "🔴 KIRMIZIYA BASMA!",
          color = Color(0xFFF87171),
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Refleks Izgarası (3x4 Grid)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 28.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
      ) {
        items(dots.size) { index ->
          val dot = dots[index]

          if (dot.isVisible) {
            Box(
              modifier = Modifier
                .size(76.dp)
                .shadow(
                  12.dp,
                  CircleShape,
                  spotColor = if (dot.isGreen) VibrantGreen else VibrantRed
                )
                .clip(CircleShape)
                .background(if (dot.isGreen) VibrantGreen else VibrantRed)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onDotTapped(index) },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (dot.isGreen) "DOKUN" else "BASMA",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          } else {
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
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
