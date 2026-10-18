package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
  hasUsedRevive: Boolean = false,
  onSetTimerPaused: (Boolean) -> Unit = {},
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {}
) {
  var round by rememberSaveable { mutableIntStateOf(1) }

  val sequenceListSaver = listSaver<List<Int>, Int>(
    save = { it },
    restore = { it.toList() }
  )
  var sequence by rememberSaveable(stateSaver = sequenceListSaver) {
    mutableStateOf(generateMemorySequence(1))
  }

  var isDisplayPhase by rememberSaveable { mutableStateOf(true) }
  var displayProgress by rememberSaveable { mutableFloatStateOf(1f) }

  val enteredDigitsSaver = listSaver<SnapshotStateList<Int>, Int>(
    save = { it.toList() },
    restore = { mutableStateListOf<Int>().apply { addAll(it) } }
  )
  val enteredDigits = rememberSaveable(saver = enteredDigitsSaver) { mutableStateListOf<Int>() }
  var feedbackText by rememberSaveable { mutableStateOf("") }
  var feedbackIsSuccess by rememberSaveable { mutableStateOf(true) }
  var isPeeking by remember { mutableStateOf(false) }

  // Clean up timer pause on unmount
  DisposableEffect(Unit) {
    onDispose {
      onSetTimerPaused(false)
    }
  }

  // Display Phase Coroutine - triggers ONLY when a new round starts!
  LaunchedEffect(round) {
    enteredDigits.clear()
    isDisplayPhase = true
    displayProgress = 1f
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

  // Brief Peek Coroutine - allows player to peek at numbers for 1.5s if forgotten (timer paused)
  LaunchedEffect(isPeeking) {
    if (isPeeking) {
      onSetTimerPaused(true)
      delay(1500L)
      isPeeking = false
      onSetTimerPaused(false)
    }
  }

  fun checkCompletion() {
    if (enteredDigits.size == sequence.size) {
      val isCorrect = enteredDigits.toList() == sequence
      if (isCorrect) {
        feedbackText = "✓ HARİKA! (+150)"
        feedbackIsSuccess = true
        onRoundSuccess(150, 3.5f)
        val nextRound = round + 1
        sequence = generateMemorySequence(nextRound)
        round = nextRound
      } else {
        val expectedStr = sequence.joinToString(" - ")
        val enteredStr = enteredDigits.joinToString(" - ")
        feedbackText = "✗ YANLIŞ! Doğrusu: $expectedStr"
        feedbackIsSuccess = false
        onRoundMistake(2.0f, "Girdiğin: $enteredStr, Beklenen: $expectedStr")
        if (!isRiskMode) {
          val nextRound = round + 1
          sequence = generateMemorySequence(nextRound)
          round = nextRound
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
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "👇 SAYILARI SIRAYLA TUŞLA 👇",
            color = SkyBlueAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(NeonGold.copy(alpha = 0.2f))
            .border(1.5.dp, NeonGold, RoundedCornerShape(14.dp))
            .clickable(enabled = !isPeeking) { isPeeking = true }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (isPeeking) "👁️ 1.5s..." else "👁️ Hatırla",
            color = NeonGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Sayı Gösterim / Slot Kutusu (Tam ekran uyumlu, taşma veya kırpılma yapmaz)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = BluePrimary.copy(alpha = 0.35f))
        .clip(RoundedCornerShape(22.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(22.dp))
        .padding(vertical = 14.dp, horizontal = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      val totalCount = sequence.size
      val spacing = if (totalCount >= 5) 6.dp else 10.dp
      val numFontSize = if (totalCount >= 5) 20.sp else 24.sp

      if (isDisplayPhase || isPeeking) {
        // Hafızaya alınacak sayılar (Tüm rakamlar tam sığacak şekilde weight ile esnek hizalanır)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically
        ) {
          sequence.forEach { num ->
            Box(
              modifier = Modifier
                .weight(1f, fill = false)
                .sizeIn(minWidth = 38.dp, minHeight = 38.dp, maxWidth = 52.dp, maxHeight = 52.dp)
                .aspectRatio(1f)
                .shadow(3.dp, RoundedCornerShape(12.dp), spotColor = BluePrimary)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF6FF))
                .border(2.dp, BluePrimary, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$num",
                color = BluePrimary,
                fontSize = numFontSize,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }
      } else {
        // Kullanıcının girdiği sayılar (Slotlar - son rakam asla kesilmez)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(totalCount) { i ->
            val hasDigit = i < enteredDigits.size
            Box(
              modifier = Modifier
                .weight(1f, fill = false)
                .sizeIn(minWidth = 38.dp, minHeight = 38.dp, maxWidth = 52.dp, maxHeight = 52.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (hasDigit) BluePrimary else Color(0xFFF1F5F9))
                .border(
                  width = 2.dp,
                  color = if (hasDigit) SkyBlueAccent else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(12.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              if (hasDigit) {
                Text(
                  text = "${enteredDigits[i]}",
                  color = Color.White,
                  fontSize = numFontSize,
                  fontWeight = FontWeight.Black,
                  maxLines = 1,
                  softWrap = false
                )
              } else {
                Text(text = "•", color = Color(0xFF94A3B8), fontSize = 18.sp, maxLines = 1, softWrap = false)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Keypad (1 to 9, C, 0, ⌫)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      val keypadItems = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "C", "0", "⌫"
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(keypadItems.size) { index ->
          val item = keypadItems[index]
          val isDelete = item == "⌫"
          val isClear = item == "C"

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .shadow(5.dp, RoundedCornerShape(16.dp), spotColor = if (isDelete) Color(0xFFEF4444) else BluePrimary)
              .clip(RoundedCornerShape(16.dp))
              .background(
                when {
                  isDelete -> Color(0xFFEF4444)
                  isClear -> Color(0xFF64748B)
                  isDisplayPhase || isPeeking -> Color(0x66FFFFFF)
                  else -> CardWhite
                }
              )
              .border(
                width = 2.dp,
                color = if (isDelete || isClear) Color.White else SkyBlueAccent,
                shape = RoundedCornerShape(16.dp)
              )
              .clickable(enabled = !isDisplayPhase && !isPeeking) {
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
              fontSize = if (isDelete || isClear) 18.sp else 22.sp,
              fontWeight = FontWeight.Black,
              maxLines = 1,
              softWrap = false
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
