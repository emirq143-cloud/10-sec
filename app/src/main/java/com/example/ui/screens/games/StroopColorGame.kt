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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.CardWhite
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextWhiteTranslucent
import kotlin.random.Random

data class ColorOptionItem(
  val name: String,
  val color: Color
)

@Composable
fun StroopColorGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  val colors = remember {
    listOf(
      ColorOptionItem("Kırmızı", Color(0xFFEF4444)),
      ColorOptionItem("Mavi", Color(0xFF3B82F6)),
      ColorOptionItem("Yeşil", Color(0xFF10B981)),
      ColorOptionItem("Sarı", Color(0xFFFBBF24))
    )
  }

  val targetCount = if (isRiskMode) 5 else 3
  var completedCount by remember { mutableIntStateOf(0) }
  var targetColorIndex by remember { mutableIntStateOf(Random.nextInt(colors.size)) }

  fun pickNext() {
    targetColorIndex = (targetColorIndex + 1 + Random.nextInt(colors.size - 1)) % colors.size
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Renk Seçimi",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = "Sıradaki rengi hızlıca seç! Dikkatli ol, karıştırma!",
      color = TextWhiteTranslucent,
      fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Blue pill banner (Directly matching screenshot column 4: "Maviye bas!")
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp)
        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = BluePrimary.copy(alpha = 0.6f))
        .clip(RoundedCornerShape(26.dp))
        .background(BluePrimary)
        .border(2.5.dp, Color.White, RoundedCornerShape(26.dp))
        .padding(vertical = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "🎯 ${colors[targetColorIndex].name.uppercase()}'YE BAS!",
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Clear instruction tag pointing to color buttons
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.55f))
        .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
      Text(
        text = "👇 AŞAĞIDAKİ DOĞRU RENGE DOKUN 👇",
        color = SkyBlueAccent,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 4 Big Shiny Color Buttons (2x2 grid) with distinct high-contrast white rings
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 36.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        items(colors.size) { index ->
          val item = colors[index]
          Box(
            modifier = Modifier
              .size(115.dp)
              .shadow(18.dp, CircleShape, spotColor = item.color.copy(alpha = 0.7f))
              .clip(CircleShape)
              .background(item.color)
              .border(4.dp, Color.White, CircleShape)
              .clickable {
                if (index == targetColorIndex) {
                  completedCount++
                  if (completedCount >= targetCount) {
                    onSuccess(completedCount * 30, 100)
                  } else {
                    pickNext()
                  }
                } else {
                  if (isRiskMode) {
                    onFail("Yanlış renge dokundun!")
                  } else {
                    pickNext()
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
                )
              }
              Text(
                text = item.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }

    // Bottom Circular Timer & Progress
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
