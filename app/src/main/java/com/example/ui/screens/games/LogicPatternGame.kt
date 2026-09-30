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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextWhiteTranslucent
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlin.random.Random

data class LogicPattern(
  val lines: List<Pair<String, String>>,
  val correctAnswer: Int,
  val options: List<Int>
)

val ICON_PACKS = listOf(
  listOf("🔺", "🟩", "🔵", "⭐"),
  listOf("💎", "⚡", "🌙", "🪐"),
  listOf("🍎", "🍊", "🍇", "🍓"),
  listOf("🚗", "✈️", "🚀", "🛸"),
  listOf("🦁", "🐯", "🐻", "🐺"),
  listOf("🎯", "🏆", "🎲", "🎳")
)

fun generateRandomPattern(round: Int): LogicPattern {
  val icons = ICON_PACKS[Random.nextInt(ICON_PACKS.size)]
  val patternType = Random.nextInt(5)

  val numbers: List<Int> = when (patternType) {
    0 -> {
      // Linear step: A, A+d, A+2d, A+3d
      val start = Random.nextInt(2, 12)
      val step = Random.nextInt(3, 8)
      listOf(start, start + step, start + 2 * step, start + 3 * step)
    }
    1 -> {
      // Increasing step: A, A+d, A+d+(d+1), A+d+(d+1)+(d+2)
      val start = Random.nextInt(3, 10)
      val step = Random.nextInt(2, 5)
      val n1 = start
      val n2 = n1 + step
      val n3 = n2 + step + 1
      val n4 = n3 + step + 2
      listOf(n1, n2, n3, n4)
    }
    2 -> {
      // Doubling: A, 2A, 4A, 8A
      val start = Random.nextInt(2, 6)
      listOf(start, start * 2, start * 4, start * 8)
    }
    3 -> {
      // Alternating: A, A+s1, A+s1-s2, A+s1-s2+s1
      val start = Random.nextInt(10, 25)
      val s1 = Random.nextInt(4, 9)
      val s2 = Random.nextInt(1, 3)
      val n1 = start
      val n2 = n1 + s1
      val n3 = n2 - s2
      val n4 = n3 + s1
      listOf(n1, n2, n3, n4)
    }
    else -> {
      // Subtraction step
      val start = Random.nextInt(40, 70)
      val step = Random.nextInt(4, 9)
      listOf(start, start - step, start - 2 * step, start - 3 * step)
    }
  }

  val correct = numbers[3]
  val wrong1 = correct + if (Random.nextBoolean()) Random.nextInt(1, 4) else -Random.nextInt(1, 4)
  val wrong2 = correct + if (wrong1 > correct) -Random.nextInt(2, 5) else Random.nextInt(2, 5)
  val options = listOf(correct, wrong1, wrong2).distinct().let {
    if (it.size == 3) it.shuffled()
    else listOf(correct, correct - 2, correct + 3).shuffled()
  }

  val lines = listOf(
    Pair(icons[0], "${numbers[0]}"),
    Pair(icons[1], "${numbers[1]}"),
    Pair(icons[2], "${numbers[2]}"),
    Pair(icons[3], "?")
  )

  return LogicPattern(lines, correct, options)
}

@Composable
fun LogicPatternGame(
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
  var questionNumber by remember { mutableIntStateOf(1) }
  var currentPattern by remember { mutableStateOf(generateRandomPattern(1)) }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  fun handleSelect(chosen: Int) {
    if (chosen == currentPattern.correctAnswer) {
      feedbackText = "✓ HARİKA! (+120 Puan)"
      feedbackIsSuccess = true
      onRoundSuccess(120, 2.5f)
      questionNumber++
      currentPattern = generateRandomPattern(questionNumber)
    } else {
      feedbackText = "✗ YANLIŞ! Doğru: ${currentPattern.correctAnswer}"
      feedbackIsSuccess = false
      onRoundMistake(2.0f, "Hatalı seçim: $chosen, doğru: ${currentPattern.correctAnswer}")
      if (!isRiskMode) {
        questionNumber++
        currentPattern = generateRandomPattern(questionNumber)
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Mantık Örüntüsü",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Soru Rozeti & Geri Bildirim
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp),
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
          text = "Soru $questionNumber",
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

    Spacer(modifier = Modifier.height(14.dp))

    // Örüntü Kartı
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 26.dp)
        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = BluePrimary.copy(alpha = 0.35f))
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(26.dp))
        .padding(vertical = 20.dp, horizontal = 24.dp)
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        currentPattern.lines.forEachIndexed { index, line ->
          PatternLine(
            icon = line.first,
            value = line.second,
            isHighlight = index == 3
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "👇 DOĞRU SAYIYA DOKUN 👇",
      color = SkyBlueAccent,
      fontSize = 12.sp,
      fontWeight = FontWeight.Black,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 3 Seçenek Kartı
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      val letterBadges = listOf("A", "B", "C")
      currentPattern.options.forEachIndexed { index, option ->
        Box(
          modifier = Modifier
            .weight(1f)
            .height(96.dp)
            .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = SkyBlueAccent.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(22.dp))
            .background(CardWhite)
            .border(2.5.dp, SkyBlueAccent, RoundedCornerShape(22.dp))
            .clickable { handleSelect(option) }
            .padding(8.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(BluePrimary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = letterBadges[index],
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
            Text(
              text = "$option",
              color = TextDark,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun PatternLine(icon: String, value: String, isHighlight: Boolean = false) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(text = icon, fontSize = 28.sp)
    Text(text = "➔", color = TextDarkMuted, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Text(
      text = value,
      color = if (isHighlight) NeonOrange else TextDark,
      fontSize = if (isHighlight) 34.sp else 26.sp,
      fontWeight = FontWeight.Black
    )
  }
}
