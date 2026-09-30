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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextWhiteTranslucent
import kotlinx.coroutines.delay

@Composable
fun WorkingMemoryGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  val sequence = remember {
    if (isRiskMode) listOf(4, 9, 2, 7, 1) else listOf(3, 8, 1, 6)
  }
  val reverseTarget = remember { sequence.reversed() }

  var isDisplayPhase by remember { mutableStateOf(true) }
  var displayProgress by remember { mutableFloatStateOf(1f) }
  val enteredDigits = remember { mutableStateListOf<Int>() }

  LaunchedEffect(Unit) {
    val totalMs = 2200L
    val step = 50L
    var elapsed = 0L
    while (elapsed < totalMs) {
      delay(step)
      elapsed += step
      displayProgress = 1f - (elapsed.toFloat() / totalMs)
    }
    isDisplayPhase = false
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Çalışan Hafıza",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = if (isDisplayPhase) "Sayıları aklında tut!" else "TERS sırayla gir!",
      color = if (isDisplayPhase) NeonGold else Color.White,
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold
    )

    if (isDisplayPhase) {
      Spacer(modifier = Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { displayProgress },
        color = SkyBlueAccent,
        trackColor = Color.White.copy(alpha = 0.2f),
        modifier = Modifier
          .fillMaxWidth(0.5f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Crisp White Sequence Display Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(24.dp))
        .background(CardWhite)
        .padding(vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isDisplayPhase) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          sequence.forEach { num ->
            Text(
              text = "$num",
              color = TextDark,
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      } else {
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(reverseTarget.size) { i ->
            val hasDigit = i < enteredDigits.size
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (hasDigit) BluePrimary else Color(0xFFF1F5F9))
                .border(1.dp, if (hasDigit) BluePrimary else Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              if (hasDigit) {
                Text(
                  text = "${enteredDigits[i]}",
                  color = Color.White,
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Bold
                )
              } else {
                Text(text = "_", color = Color(0xFF94A3B8), fontSize = 20.sp)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (!isDisplayPhase) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.55f))
          .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "👇 TERS SIRAYLA TUŞLARA BAS 👇",
          color = SkyBlueAccent,
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Keypad: 1 to 9 (Clearly defined, high-contrast selectable keys)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 32.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(9) { index ->
          val digit = index + 1
          Box(
            modifier = Modifier
              .size(72.dp)
              .shadow(8.dp, CircleShape, spotColor = BluePrimary.copy(alpha = 0.4f))
              .clip(CircleShape)
              .background(if (!isDisplayPhase) CardWhite else Color(0x66FFFFFF))
              .border(
                width = 2.5.dp,
                color = if (!isDisplayPhase) BluePrimary else Color.White.copy(alpha = 0.4f),
                shape = CircleShape
              )
              .clickable(enabled = !isDisplayPhase) {
                if (enteredDigits.size < reverseTarget.size) {
                  val nextPos = enteredDigits.size
                  if (digit == reverseTarget[nextPos]) {
                    enteredDigits.add(digit)
                    if (enteredDigits.size == reverseTarget.size) {
                      onSuccess(120, 100)
                    }
                  } else {
                    onFail("Hatalı sıra! $digit girdin, beklenen ${reverseTarget[nextPos]} idi.")
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$digit",
              color = TextDark,
              fontSize = 26.sp,
              fontWeight = FontWeight.Black
            )
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
