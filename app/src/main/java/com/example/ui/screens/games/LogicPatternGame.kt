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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextWhiteTranslucent

@Composable
fun LogicPatternGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  // Pattern:
  // 🔺 -> 5
  // 🟩 -> 8   (+3)
  // 🔵 -> 12  (+4)
  // ⭐ -> ?   (+5 => 17 or options 15, 16, 20 from image with +3, +4, +4 etc.)
  val correctAnswer = 16
  val options = listOf(15, 16, 20)

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Mantık",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = "Hangi sayı gelmeli?",
      color = TextWhiteTranslucent,
      fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Crisp White Pattern Card with Vibrant Border
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp)
        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = BluePrimary.copy(alpha = 0.35f))
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(26.dp))
        .padding(vertical = 24.dp, horizontal = 28.dp)
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        PatternLine(icon = "🔺", value = "5")
        PatternLine(icon = "🟩", value = "8")
        PatternLine(icon = "🔵", value = "12")
        PatternLine(icon = "⭐", value = "?", isHighlight = true)
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Clear prominent instruction indicator
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.55f))
        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = "👇 DOĞRU SEÇENEĞE DOKUN 👇",
        color = Color(0xFF38BDF8),
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3 Highly Distinct, Prominent Choice Cards (A, B, C)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      val letters = listOf("A", "B", "C")
      options.forEachIndexed { idx, option ->
        Box(
          modifier = Modifier
            .weight(1f)
            .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = BluePrimary.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .border(2.5.dp, BluePrimary, RoundedCornerShape(20.dp))
            .clickable {
              if (option == correctAnswer) {
                onSuccess(100, 100)
              } else {
                onFail("Yanlış cevap! Doğru cevap 16 olmalıydı.")
              }
            }
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Option letter badge (A, B, C)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFEFF6FF))
                .border(1.dp, BluePrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = letters.getOrElse(idx) { "" },
                color = BluePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "$option",
              color = TextDark,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black
            )

            Text(
              text = "DOKUN ➔",
              color = Color(0xFF0284C7),
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun PatternLine(icon: String, value: String, isHighlight: Boolean = false) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    Text(text = icon, fontSize = 28.sp)
    Text(text = "➔", color = TextDarkSecondary, fontSize = 20.sp)
    Text(
      text = value,
      color = if (isHighlight) BluePrimary else TextDark,
      fontSize = 28.sp,
      fontWeight = FontWeight.ExtraBold
    )
  }
}
