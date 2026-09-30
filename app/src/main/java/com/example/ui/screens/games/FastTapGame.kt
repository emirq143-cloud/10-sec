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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextWhiteTranslucent
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.random.Random

data class DotItem(
  val id: Int,
  val isGreen: Boolean,
  val isVisible: Boolean = true
)

@Composable
fun FastTapGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  val targetHits = if (isRiskMode) 14 else 10
  var currentHits by remember { mutableIntStateOf(0) }
  var mistakes by remember { mutableIntStateOf(0) }

  val dots = remember {
    mutableStateListOf<DotItem>().apply {
      repeat(12) { i ->
        add(DotItem(id = i, isGreen = (i % 3 != 0), isVisible = true))
      }
    }
  }

  LaunchedEffect(Unit) {
    while (true) {
      delay(700)
      for (i in dots.indices) {
        val show = Random.nextFloat() > 0.30f
        val isGreen = Random.nextFloat() > 0.25f
        dots[i] = DotItem(id = i, isGreen = isGreen, isVisible = show)
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Hızlı Dokun",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Prominent clear instruction rule banner
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.55f))
        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "🟢 YEŞİLE DOKUN!",
          color = Color(0xFF34D399),
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold
        )
        Text(text = "•", color = Color.White)
        Text(
          text = "🔴 KIRMIZIYA BASMA!",
          color = Color(0xFFF87171),
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Grid of glowing green & red dots
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(dots.size) { index ->
          val item = dots[index]
          Box(
            modifier = Modifier
              .size(82.dp)
              .clip(CircleShape)
              .background(
                if (!item.isVisible) Color.Transparent
                else if (item.isGreen) VibrantGreen
                else VibrantRed
              )
              .border(
                width = if (item.isVisible) 3.5.dp else 0.dp,
                color = if (!item.isVisible) Color.Transparent else Color.White,
                shape = CircleShape
              )
              .shadow(
                elevation = if (item.isVisible) 14.dp else 0.dp,
                shape = CircleShape,
                spotColor = if (item.isGreen) VibrantGreen.copy(alpha = 0.8f) else VibrantRed.copy(alpha = 0.8f)
              )
              .clickable(enabled = item.isVisible) {
                if (item.isGreen) {
                  currentHits++
                  dots[index] = item.copy(isVisible = false)
                  if (currentHits >= targetHits) {
                    val accuracy = if (mistakes == 0) 100 else 85
                    onSuccess(currentHits * 20, accuracy)
                  }
                } else {
                  mistakes++
                  dots[index] = item.copy(isVisible = false)
                  if (isRiskMode) {
                    onFail("Kırmızıya bastın! Risk modunda tek hata eler!")
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            if (item.isVisible) {
              if (item.isGreen) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = "DOKUN",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                  )
                  Text(text = "✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
              } else {
                Text(
                  text = "✕",
                  color = Color.White,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }
      }
    }

    // Bottom Stats & Circular Timer (Matching column 3 in mockup)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 36.dp, vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 78.dp
      )

      Column(horizontalAlignment = Alignment.End) {
        Text(text = "Hedef", color = TextWhiteTranslucent, fontSize = 12.sp)
        Text(
          text = "$currentHits / $targetHits",
          color = Color.White,
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }
  }
}
