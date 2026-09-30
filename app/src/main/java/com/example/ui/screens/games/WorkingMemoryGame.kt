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
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.random.Random

fun generateMemorySequence(level: Int): List<Int> {
  // Starts with 3 digits, increases up to 5 as level increases
  val length = when {
    level <= 2 -> 3
    level <= 5 -> 4
    else -> 5
  }
  // Unique random digits between 1 and 9
  return (1..9).shuffled().take(length)
}

@Composable
fun WorkingMemoryGame(
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
  var sequence by remember { mutableStateOf(generateMemorySequence(1)) }
  var isDisplayPhase by remember { mutableStateOf(true) }
  var displayProgress by remember { mutableFloatStateOf(1f) }
  val enteredDigits = remember { mutableStateListOf<Int>() }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  // Clean up timer pause on unmount
  DisposableEffect(Unit) {
    onDispose {
      onSetTimerPaused(false)
    }
  }

  // Display Phase Coroutine
  LaunchedEffect(round) {
    enteredDigits.clear()
    isDisplayPhase = true
    onSetTimerPaused(true) // DO NOT penalize game clock during memorization!

    val totalMs = if (sequence.size <= 3) 2400L else 3200L
    val step = 50L
    var elapsed = 0L

    while (elapsed < totalMs) {
      delay(step)
      elapsed += step
      displayProgress = 1f - (elapsed.toFloat() / totalMs)
    }

    isDisplayPhase = false
    onSetTimerPaused(false) // Resume clock for input phase!
  }

  fun checkCompletion() {
    if (enteredDigits.size == sequence.size) {
      val isCorrect = enteredDigits.toList() == sequence
      if (isCorrect) {
        feedbackText = "✓ HARİKA! (+150)"
        feedbackIsSuccess = true
        onRoundSuccess(150, 3.5f)
        round++
        sequence = generateMemorySequence(round)
      } else {
        val expectedStr = sequence.joinToString(" - ")
        val enteredStr = enteredDigits.joinToString(" - ")
        feedbackText = "✗ YANLIŞ! Doğrusu: $expectedStr"
        feedbackIsSuccess = false
        onRoundMistake(2.0f, "Girdiğin: $enteredStr, Beklenen: $expectedStr")
        if (!isRiskMode) {
          round++
          sequence = generateMemorySequence(round)
        }
      }
    }
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
          text = "Aşama $round (${sequence.size} Basamak)",
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

    // Faz Bilgilendirmesi & Geri Sayım
    if (isDisplayPhase) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(NeonGold.copy(alpha = 0.2f))
          .border(1.5.dp, NeonGold, RoundedCornerShape(14.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "👁️ SAYILARI AKLINDA TUT!",
          color = NeonGold,
          fontSize = 14.sp,
          fontWeight = FontWeight.Black
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { displayProgress },
        color = NeonGold,
        trackColor = Color.White.copy(alpha = 0.2f),
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
      )
    } else {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(Color.Black.copy(alpha = 0.6f))
          .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(14.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "👇 SAYILARI SIRAYLA TUŞLA 👇",
          color = SkyBlueAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Sayı Gösterim / Slot Kutusu
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = BluePrimary.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(24.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(24.dp))
        .padding(vertical = 18.dp, horizontal = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isDisplayPhase) {
        // Hafızaya alınacak sayılar
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          sequence.forEach { num ->
            Box(
              modifier = Modifier
                .size(54.dp)
                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = BluePrimary)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEFF6FF))
                .border(2.dp, BluePrimary, RoundedCornerShape(14.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$num",
                color = BluePrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      } else {
        // Kullanıcının girdiği sayılar (Slotlar)
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(sequence.size) { i ->
            val hasDigit = i < enteredDigits.size
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (hasDigit) BluePrimary else Color(0xFFF1F5F9))
                .border(
                  width = 2.dp,
                  color = if (hasDigit) SkyBlueAccent else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(14.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              if (hasDigit) {
                Text(
                  text = "${enteredDigits[i]}",
                  color = Color.White,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Black
                )
              } else {
                Text(text = "•", color = Color(0xFF94A3B8), fontSize = 24.sp)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Keypad (1 to 9, Sil)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 28.dp),
      contentAlignment = Alignment.Center
    ) {
      val keypadItems = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "C", "⌫", " "
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(keypadItems.size) { index ->
          val item = keypadItems[index]
          if (item == " ") {
            Spacer(modifier = Modifier.size(62.dp))
          } else {
            val isDelete = item == "⌫"
            val isClear = item == "C"

            Box(
              modifier = Modifier
                .size(62.dp)
                .shadow(6.dp, CircleShape, spotColor = if (isDelete) Color(0xFFEF4444) else BluePrimary)
                .clip(CircleShape)
                .background(
                  when {
                    isDelete -> Color(0xFFEF4444)
                    isClear -> Color(0xFF64748B)
                    isDisplayPhase -> Color(0x66FFFFFF)
                    else -> CardWhite
                  }
                )
                .border(
                  width = 2.dp,
                  color = if (isDelete || isClear) Color.White else SkyBlueAccent,
                  shape = CircleShape
                )
                .clickable(enabled = !isDisplayPhase) {
                  when (item) {
                    "⌫" -> {
                      if (enteredDigits.isNotEmpty()) {
                        enteredDigits.removeAt(enteredDigits.lastIndex)
                      }
                    }
                    "C" -> {
                      enteredDigits.clear()
                    }
                    else -> {
                      val digit = item.toIntOrNull()
                      if (digit != null && enteredDigits.size < sequence.size) {
                        enteredDigits.add(digit)
                        checkCompletion()
                      }
                    }
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item,
                color = if (isDelete || isClear) Color.White else TextDark,
                fontSize = if (isDelete || isClear) 18.sp else 24.sp,
                fontWeight = FontWeight.Black
              )
            }
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
